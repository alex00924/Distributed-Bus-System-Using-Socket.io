package com.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Publisher extends Thread{
	protected Socket m_socket;
	private PrintWriter m_out;
    private BufferedReader m_in;

	public Publisher(Socket clientSocket) {
		m_socket = clientSocket;
		try {
			m_out = new PrintWriter(m_socket.getOutputStream(), true);
			m_in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopCommunication()
	{
		try {
			if( m_in != null )
				m_in.close();
			if( m_out != null )
				m_out.close();
			if( m_socket != null )
				m_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Manage Publisher
    public void run() {
        while( m_socket != null && m_socket.isConnected() )
        {
        	try {
        		parseMsg(m_in.readLine());
        		
			} catch (IOException e) {
				break;
			}
        }
    }
    
    private void parseMsg(String strMsg)
    {
    	if( strMsg == null || strMsg.isEmpty() )
    		return;
    	
    	JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonMsg = (JSONObject)jsonParser.parse(strMsg);
			
			//Broker's Information
			if( jsonMsg.get(Global.g_strTitle).toString().equals(Global.g_strTitleBroker) )
			{
				JSONArray brokerArray = (JSONArray)jsonMsg.get(Global.g_strBrokerArray);
				String strIp;
				boolean bReceived = false;
				
				//check that this broker information is already received..
				for(Object broker : brokerArray)
				{
					strIp = ((JSONObject)broker).get(Global.g_strIP).toString();
					bReceived = false;
					for(Broker old_broker : Global.g_mainProcess.m_arrBroker)
					{
						if( old_broker.m_strIP.equals(strIp))
						{
							old_broker.addBusLines((JSONObject)broker);
							bReceived = true;
							break;
						}
					}
					if( !bReceived)
						Global.g_mainProcess.m_arrBroker.add(new Broker((JSONObject)broker));
				}
				//reply "I received your brokers information"
				sendResMessage(1, "");
			}
			else if(jsonMsg.get(Global.g_strTitle).toString().equals(Global.g_strTitleBus))
			{
				Bus_Info bus = new Bus_Info(jsonMsg);
				Global.g_mainProcess.m_arrBus.add(bus);
				
				//reply "I received your bus information"
				sendResMessage(0, bus.m_nVehicleCode + "");
			}
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // msg title : Broker's information, Bus Pos information
    //nType : 1 -> Brokers Information, 2 -> Vehicle information
    public void sendResMessage(int nType, String strMsg){
        //send reply message to Publisher
    	m_out.println("OK");
    	if( nType == 1 )
    		strMsg = "Brokers' Information Received.";
    	else
    		strMsg = "Vehicle(" + strMsg + ") Informatoin received.";
    	Global.g_mainProcess.showMessage(strMsg);
    }
    
}
