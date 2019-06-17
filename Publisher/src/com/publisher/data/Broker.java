package com.publisher.data;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.publisher.SocketManager;
import com.publisher.Global;

public class Broker
{
	public String m_strIP;				//IP adress
	public int    m_nPort;			//Port
	public String m_strConState;		//Connecting, Connected
	public String m_strBusLine;			//the bus lines, which is managed by this broker
	public String m_strKey;				//SHA1 Key
	public List<Integer> m_arrBusLine = new ArrayList<Integer>();
	private int	 m_nRowIdx;				//the row idx of table
	private JTable m_table_broker;
	private SocketManager m_socket_manager;
	
	public Broker(String strLine, DefaultTableModel model, JTable table_broker)
	{
		m_table_broker = table_broker;
		strLine += ", , , ";
		String[] arrInfo = strLine.split(",");
		if( arrInfo.length != 5 )
			return;
		m_strIP = arrInfo[0];
		m_nPort = Integer.valueOf(arrInfo[1]);
		m_strConState = "Connecting";
		m_strBusLine = "";
		byte[] arrKey = DigestUtils.sha1(m_strIP + ":" + m_nPort);
		m_strKey = Global.byteArrayToHexString(arrKey);

		arrInfo[2] = m_strKey;
		arrInfo[3] = m_strConState;
		arrInfo[4] = m_strBusLine;

		m_nRowIdx = model.getRowCount();
		model.addRow(arrInfo);
	}
	
	public void updateConState(boolean bCon)
	{
		if( bCon )
			m_strConState = "Connected";
		else
			m_strConState = "Connecting...";
		m_table_broker.getModel().setValueAt(m_strConState, m_nRowIdx, 3);
	}
	
	public void addBusLine(Bus_Line bus_line)
	{
		int nLineCode = bus_line.m_nLineId;
		if( m_arrBusLine.contains(nLineCode))
			return;
		String str = m_table_broker.getModel().getValueAt(m_nRowIdx, 4).toString();
		if( !m_arrBusLine.isEmpty() )
			str += ", ";
		str += bus_line.m_nLineId;
		m_arrBusLine.add(nLineCode);
		m_table_broker.getModel().setValueAt(str, m_nRowIdx, 4);
	}
	
	public void startCommunication() throws UnknownHostException, IOException
	{
		if( m_socket_manager == null )
			m_socket_manager = new SocketManager(m_strIP, m_nPort, this);
		
		m_socket_manager.startCommunication();
	}
	
	public void stopCommunication()
	{
		if( m_socket_manager != null )
		{
			try {
				m_socket_manager.stopConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void sendData()
	{
		
		boolean bSend = false;
		
		//send broker's information
		List<Broker> arrBrokers = Global.g_mainProcess.m_dataManager.getBroker();
		
		JSONObject brokerMsg = new JSONObject();
		brokerMsg.put(Global.g_strTitle, Global.g_strTitleBroker);
		JSONArray brokersArray = new JSONArray();
		
		for (Broker broker : arrBrokers) {
			JSONObject brokerInfoMsg = new JSONObject();
			brokerInfoMsg.put(Global.g_strIP, broker.m_strIP);
			brokerInfoMsg.put(Global.g_strPort, broker.m_nPort);
			JSONArray brokersBusLines = new JSONArray();
			for (int nId : broker.m_arrBusLine) {
				brokersBusLines.add(nId);
			}
			
			brokerInfoMsg.put(Global.g_strBusLines, brokersBusLines);
			brokersArray.add(brokerInfoMsg);
		}
		brokerMsg.put(Global.g_strBrokerArray, brokersArray);
		bSend = m_socket_manager.sendMessage("The brokers' information", brokerMsg.toJSONString());
		if( !bSend )
		{
			Global.g_mainProcess.showMessage("Can not send brokers' information. Trying to reconnect");
			
		}
		
		List<Bus_Info> arrBus = Global.g_mainProcess.m_dataManager.getBusInfo();
		
		for( Bus_Info bus : arrBus)
		{
			//send only the bus info of which is controlled by this broker
			if( ! m_arrBusLine.contains(bus.m_nLineId) )
				continue;
			JSONObject busMsg = new JSONObject();
			busMsg.put(Global.g_strTitle, Global.g_strTitleBus);
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
			
			bSend = m_socket_manager.sendMessage("The information of Bus(" + bus.m_nVehicleCode + ")", busMsg.toJSONString());
			if( !bSend )
				break;
		}

		//If all data are sent to broker, show this message and finish to send
		if(bSend)
			Global.g_mainProcess.showMessage("Send All data to Broker(" + m_strIP + ")");
		//If can not send all data from any reason, try to resend all data
		else
			Global.g_mainProcess.showMessage("Can not send all data to Broker(" + m_strIP + "). Trying to reconnect");
			
	}
}