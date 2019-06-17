package com.publisher.data;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.codec.digest.DigestUtils;

import com.publisher.Global;

//Bus Position
public class Bus_Info
{
	public int 		m_nLineCode;
	public int 		m_nRouteCode;
	public int 		m_nVehicleCode;
	public float 	m_fLat;
	public float 	m_fLon;
	public String 	m_strTime;
	public int		m_nLineId;
	public int		m_nRouteType;
	public String	m_strLineDescription;
	public String	m_strRouteDescription;
	public String	m_strKey;				//SHA1 key of Line code
	private JTable  m_table_bus;
	private dataManager m_data_manager;
	public Bus_Info(String strLine, DefaultTableModel model, JTable table_bus, dataManager data_manager)
	{
		m_data_manager = data_manager;
		m_table_bus = table_bus;
		
		strLine = strLine + ", , , , , ";
		String[] arrInfo = strLine.split(",");
		if( arrInfo.length != 11 )
			return;
		try
		{
			m_nLineCode = Integer.valueOf(arrInfo[0].trim());
			m_nRouteCode= Integer.valueOf(arrInfo[1].trim());
			m_nVehicleCode = Integer.valueOf(arrInfo[2].trim());
			m_fLat = Float.valueOf(arrInfo[3].trim());
			m_fLon = Float.valueOf(arrInfo[4].trim());
			m_strTime = arrInfo[5];
			
			Bus_Line bus_line= m_data_manager.getBusLine(m_nLineCode);
			if( bus_line != null )
			{
				arrInfo[6] = bus_line.m_strDescription;
				m_nLineId = bus_line.m_nLineId;
				m_strLineDescription = arrInfo[6];
			}
			
			Route_Code route = m_data_manager.getRoute(m_nRouteCode);
			if( route != null )
			{
				m_nRouteType = route.m_nRouteType;
				m_strRouteDescription = route.m_strDescription;
				
				arrInfo[7] = String.valueOf(m_nRouteType);
				arrInfo[8] = m_strRouteDescription;
			}
			
			byte[] arrKey = DigestUtils.sha1(String.valueOf(m_nLineCode));
			m_strKey = Global.byteArrayToHexString(arrKey);
			arrInfo[9] = m_strKey;
			arrInfo[10] = "Sending...";
			model.addRow(arrInfo);
		}
		catch(Exception e)
		{
			int nn = 0;
			nn = 1;
		}
	}
	
}
