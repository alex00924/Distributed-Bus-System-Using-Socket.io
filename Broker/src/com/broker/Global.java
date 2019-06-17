package com.broker;

public class Global {
	
	public static MainProcess g_mainProcess;
	
	public static String	g_strIdentify_P = "PUBLISHER";		           //message sender identify.
	public static String	g_strIdentify_B = "BROKER";		    	       //message sender identify.
	public static String	g_strIdentify_C = "CONSUMER";		           //message sender identify.

	public static String    g_strTitleBroker = "BROKER INFORMATION";       //message header, which can realize the message is broker information or bus line.
	public static String    g_strTitleBus = "BUS INFORMATION";             //message header, which can realize the message is broker information or bus line.
	
	public static String	g_strFrom = "FROM";					//message header 
	public static String	g_strTitle = "TITLE";				//message header 
	
	
	public static String	g_strIP = "IP";						//message header 
	public static String	g_strPort = "PORT";					//message header 
	public static String	g_strBusLines = "BUS_LINES";		//message header 
	
	public static String	g_strRoute = "ROUTE_CODE";			//message header 
	public static String	g_strLine = "LINE_CODE";			//message header 
	public static String	g_strVehicle = "VEHICLE_ID";		//message header 
	public static String	g_strRouteType = "ROUTE_TYPE";		//message header 
	public static String	g_strDesciption = "DESCRIPTION";	//message header 
	public static String	g_strLat = "LATITUTE";				//message header 
	public static String	g_strLon = "LONGITUTE";				//message header 
	public static String	g_strTime = "TIME";					//message header 
	public static String	g_strLineId = "LINE_ID";			//message header 
	public static String	g_strRouteDescription = "ROUTE_DESCRIPTION";	//message header 
	
	public static String	g_strBrokerArray = "BROKER_ARRAY";
	
	public static String 	g_strBusCode = "BUS_CODE";			//bus code is requested by consumer
	
	public static String byteArrayToHexString(byte[] b) {
	  String result = "";
	  for (int i=0; i < b.length; i++) {
	    result +=
	          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
	  }
	  return result;
	}
	
	
}
