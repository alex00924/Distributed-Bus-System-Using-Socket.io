package com.broker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Consumer extends Thread {
    protected Socket m_socket;
    private String m_strIp;
    private String m_strTime;
    private String m_strCon;
    private JTable m_table;
    
    private PrintWriter m_out;
    private BufferedReader m_in;

    
    public Consumer(Socket clientSocket) {
        m_socket = clientSocket;
		try {
			m_out = new PrintWriter(m_socket.getOutputStream(), true);
			m_in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        m_strIp = ( (InetSocketAddress)m_socket.getRemoteSocketAddress() ).getAddress().toString().replace("/","");
        Date date= new Date();
        long time = date.getTime();
        m_strTime = (new Timestamp(time)).toString();
        m_strCon = "Connected";
        updateTable();
     }
    
    private void updateTable()
    {
    	m_table = Global.g_mainProcess.table_consumer;
    	
    	DefaultTableModel model = (DefaultTableModel) m_table.getModel();
    	int nCnt = model.getRowCount();
    	int  i =0;
    	
    	for( i = 0 ; i < nCnt ; i ++ ) {
    		if( model.getValueAt(i, 0).toString().contains(m_strIp) )
    			break;
    	}
    	
    	if( i < nCnt )
    		model.setValueAt("Connected", i, 2);
    	else
    	{
    		String[] arrInfo = {m_strIp, m_strTime, m_strCon};
    		model.addRow(arrInfo);
    	}
    }

    public void run() {
    	if( m_socket == null )
    		return;
    	
        //Manage Consumers
    	while(  m_socket != null && m_socket.isConnected() )
    	{
    		JSONParser jsonParser = new JSONParser();
    		try {
				String strMsg = m_in.readLine();
				if( strMsg == null || strMsg.isEmpty() )
					continue;
				System.out.println(strMsg);
				
				JSONObject jsonMsg = (JSONObject)jsonParser.parse(strMsg);
				if( jsonMsg.get(Global.g_strTitle).toString().equals(Global.g_strTitleBroker) )
				{
					sendBrokerInfo();
					System.out.println("Start Broker");
				}
				else if(jsonMsg.get(Global.g_strTitle).toString().equals(Global.g_strTitleBus))
				{
					int nBus = Integer.valueOf(jsonMsg.get(Global.g_strBusCode).toString());
					sendBusInfo(nBus);
					System.out.println("Start Bus");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
    		catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    	System.out.println("Out");
    	m_socket = null;
    	m_strCon = "DisConnected";
    	updateTable();
    }
    
    @SuppressWarnings("unchecked")
	public void sendBrokerInfo()
    {
		//send broker's information
		List<Broker> arrBrokers = Global.g_mainProcess.m_arrBroker;
		
		JSONObject brokerMsg = new JSONObject();
		brokerMsg.put(Global.g_strTitle, Global.g_strTitleBroker);
		JSONArray brokersArray = new JSONArray();
		
		for (Broker broker : arrBrokers) {
			JSONObject brokerInfoMsg = new JSONObject();
			brokerInfoMsg.put(Global.g_strIP, broker.m_strIP);
			brokerInfoMsg.put(Global.g_strPort, broker.m_nPort);
			JSONArray brokersBusLines = new JSONArray();
			for (int nId : broker.m_arrBus) {
				brokersBusLines.add(nId);
			}
			
			brokerInfoMsg.put(Global.g_strBusLines, brokersBusLines);
			brokersArray.add(brokerInfoMsg);
		}
		brokerMsg.put(Global.g_strBrokerArray, brokersArray);
    	m_out.println(brokerMsg.toJSONString());
    	m_out.flush();
    	Global.g_mainProcess.showMessage( "The brokers' informations are sent to consumer");
    }

    
    @SuppressWarnings("unchecked")
	public void sendBusInfo(int nBus)
	{
		
		List<Bus_Info> arrBus = Global.g_mainProcess.m_arrBus;
		int nCnt = 0;
		JSONArray busArray = new JSONArray();
		JSONObject busArrMsg = new JSONObject();
		busArrMsg.put(Global.g_strTitle, Global.g_strTitleBus);
		for( Bus_Info bus : arrBus)
		{
			if( bus.m_nLineId == nBus )
			{
				nCnt++;
				JSONObject busMsg = new JSONObject();
				
				busMsg.put(Global.g_strRoute, bus.m_nRouteCode);
				busMsg.put(Global.g_strLine, bus.m_nLineCode);
				busMsg.put(Global.g_strVehicle, bus.m_nVehicleCode);
				busMsg.put(Global.g_strRouteType, bus.m_nRouteType);
				busMsg.put(Global.g_strDesciption, bus.m_strLineDescription);
				
				busMsg.put(Global.g_strLat, bus.m_fLat);
				busMsg.put(Global.g_strLon, bus.m_fLon);
				busMsg.put(Global.g_strTime, bus.m_strTime);
				busMsg.put(Global.g_strLineId, bus.m_nLineId);
				busMsg.put(Global.g_strRouteDescription, bus.m_strRouteDescription);
				
				busArray.add(busMsg);
			}
		}
		if( nCnt < 1 )
		{
			Global.g_mainProcess.showMessage( "There is not bus - " + nBus + " information");
			return;
		}
		busArrMsg.put(Global.g_strBusLines, busArray);
		m_out.println(busArrMsg.toJSONString());
		m_out.flush();
    	Global.g_mainProcess.showMessage( "The Bus - " + nBus + " informations are sent to consumer");
		
	}
}
