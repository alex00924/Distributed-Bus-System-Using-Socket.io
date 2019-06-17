package com.publisher.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class dataManager {

	private List<Bus_Info> m_busInfo= new ArrayList<Bus_Info>();
	private List<Bus_Line> m_busLine= new ArrayList<Bus_Line>();
	private List<Route_Code> m_route= new ArrayList<Route_Code>();
	private List<Broker> m_broker= new ArrayList<Broker>();

	
	private JTable m_table_broker;
	private JTable m_table_bus;
	
	public void setTables(JTable table_broker, JTable table_bus)
	{
		m_table_broker = table_broker;
		m_table_bus = table_bus;
	}
	
	public void read_bus_Info(File file, DefaultTableModel model)
	{
		m_busInfo.clear();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			
			while (line != null) {
				Bus_Info bus_pos = new Bus_Info(line, model, m_table_bus, this);
				m_busInfo.add(bus_pos);
//				m_busPos.put(bus_pos.m_nVehicleCode, bus_pos);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void read_bus_line(File file)
	{
		m_busLine.clear();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();

			while (line != null) {
				Bus_Line bus_line = new Bus_Line(line);
				m_busLine.add(bus_line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void read_route_code(File file)
	{
		m_route.clear();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();

			while (line != null) {
				Route_Code route_code = new Route_Code(line);
				m_route.add(route_code);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void read_broker(File file, DefaultTableModel model)
	{
		m_broker.clear();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();

			while (line != null) {
				Broker broker = new Broker(line, model, m_table_broker);
				m_broker.add(broker);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setVehicle2Broker();
	}
	
	private void setVehicle2Broker()
	{
		//sort the brokers according to his sha1 key
		Collections.sort(m_broker, new Comparator<Broker>() {

			@Override
			public int compare(Broker o1, Broker o2) {
				return o1.m_strKey.compareTo(o2.m_strKey);
			}
		});

		int i = 0, k = 0 ; 
		int nCnt = m_busLine.size();
		int nBrok_Cnt = m_broker.size();
		
		//all lines are controled by larger sha1key broker.
		for( i = 1 ; i < nCnt; i ++ )
		{
			for( k = 0 ; k < nBrok_Cnt-1 ; k ++ )
			{
				if( m_busLine.get(i).m_strKey.compareTo( m_broker.get(k).m_strKey ) < 0)
					break;
			}
			m_broker.get(k).addBusLine(m_busLine.get(i));
		}
	}

	public List<Bus_Info> getBusInfo()
	{
		return m_busInfo;
	}
	public List<Bus_Line> getBusLine()
	{
		return m_busLine;
	}
	public List<Route_Code> getRouteCode()
	{
		return m_route;
	}
	public List<Broker> getBroker()
	{
		return m_broker;
	}
	
	
	
	public Bus_Line getBusLine(int nCode)
	{
		for (Bus_Line line : m_busLine) {
			if( line.m_nLineCode == nCode )
				return line;
		}
		return null;
	}
	
	public Route_Code getRoute(int nCode)
	{
		for (Route_Code route : m_route) {
			if( route.m_nRouteCode== nCode )
				return route;
		}
		return null;
	}

	
}
