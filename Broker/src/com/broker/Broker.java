package com.broker;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Broker {
	public String m_strIP;
	public int		m_nPort;
	public List<Integer>	m_arrBus = new ArrayList<Integer>();
	
	//parse the information received from Publisher
	public Broker(JSONObject jsonData)
	{
		m_strIP = jsonData.get(Global.g_strIP).toString();
		m_nPort = Integer.valueOf(jsonData.get(Global.g_strPort).toString());
		addBusLines(jsonData);
	}
	
	//add bus lines. this is called when a publisher send data already, and other publisher send data again.
	public void addBusLines(JSONObject jsonData)
	{
		JSONArray arrBusLines = (JSONArray)jsonData.get(Global.g_strBusLines);
		for(Object busLine : arrBusLines)
		{
			m_arrBus.add(Integer.valueOf(busLine.toString()));
		}
	}
}
