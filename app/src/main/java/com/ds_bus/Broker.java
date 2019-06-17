package com.ds_bus;

import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Broker {
    public String          m_strIp = "";               //broker ip address
    public int             m_nPort = 0;                //broker listening port
    private SocketManger    m_sockManager = null;
    private List<Integer>  m_arrBus = new ArrayList<>();
    public boolean          m_bConnected = false;

    public int          m_nCurBus;

    public Broker( String strIP, int nPort, SocketManger sockManager)
    {
        m_strIp = strIP;
        m_nPort = nPort;
        m_sockManager = sockManager;
    }

    public Broker(JSONObject jsonData)
    {
        //if jsonData is null, set default ip and port...
        //this is needed at the first time of connect to broker
        if( jsonData == null )
        {
            m_strIp = "192.168.200.82";
            m_nPort = 81;
            return;
        }

        m_strIp = jsonData.get(Global.g_strIP).toString();
        m_nPort = Integer.valueOf(jsonData.get(Global.g_strPort).toString());
        addBusLine(jsonData);
    }

    public void addBusLine(JSONObject jsonData)
    {
        if( jsonData == null )
            return;

        JSONArray arrBusLines = (JSONArray)jsonData.get(Global.g_strBusLines);
        int nSize = 0, i = 0;
        if( arrBusLines != null )
            nSize = arrBusLines.size();

        int nBusLine = 0;
        for(i = 0; i < nSize ; i ++ )
        {
            nBusLine = Integer.valueOf(arrBusLines.get(i).toString());
            if( !isResponse(nBusLine) )
                m_arrBus.add(nBusLine);
        }

    }

    //return true when this broker is responsible to a bus
    public boolean isResponse(int nBus)
    {
        int i = 0, nCnt = m_arrBus.size();
        for( i = 0 ; i < nCnt ; i ++ )
        {
            if( m_arrBus.get(i).equals(nBus) )
                return true;
        }

        return false;
    }
    public void getBusData(int nBus)
    {
        m_sockManager.getBusData(nBus);
    }

    public void connect()
    {

        //if not connected to the broker, create socket to connect
        if( !m_bConnected )
        {
            if( m_sockManager == null )
                m_sockManager = new SocketManger(m_strIp, m_nPort);

            m_sockManager.connectToBroker(2, this);
            return;
        }
    }
}
