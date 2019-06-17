package com.publisher.data;

public class Route_Code
{
	public int		m_nRouteCode;
	public int		m_nLineCode;
	public int		m_nRouteType;			//1 : start -> end, 2 : end -> start
	public String	m_strDescription;
	
	public Route_Code(String strLine)
	{
		String[] arrInfo = strLine.split(",");
		if( arrInfo.length != 4 )
			return;
		
		m_nRouteCode = Integer.valueOf(arrInfo[0].trim());
		m_nLineCode= Integer.valueOf(arrInfo[1].trim());
		m_nRouteType = Integer.valueOf(arrInfo[2].trim());
		m_strDescription= arrInfo[3];
	}
	
}