package com.ds_bus;

import org.json.JSONException;
import org.json.simple.JSONObject;

public class BusInfo {
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

    //parse the information received from Publisher
    public BusInfo(JSONObject jsonData)
    {
        m_nRouteCode = Integer.valueOf( jsonData.get(Global.g_strRoute).toString());
        m_nLineCode = Integer.valueOf( jsonData.get(Global.g_strLine).toString());
        m_nVehicleCode = Integer.valueOf( jsonData.get(Global.g_strVehicle).toString());
        m_nRouteType = Integer.valueOf( jsonData.get(Global.g_strRouteType).toString());
        m_strLineDescription = jsonData.get(Global.g_strDesciption).toString();

        m_fLat = Float.valueOf(jsonData.get(Global.g_strLat).toString());
        m_fLon = Float.valueOf(jsonData.get(Global.g_strLon).toString());
        m_strTime = jsonData.get(Global.g_strTime).toString();
        m_nLineId = Integer.valueOf(jsonData.get(Global.g_strLineId).toString());
        m_strRouteDescription = jsonData.get(Global.g_strRouteDescription).toString();
    }
}
