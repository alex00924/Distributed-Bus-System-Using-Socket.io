package com.ds_bus;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Subscriber {

    List<Broker> m_arrBroker = new ArrayList<>();

    public void register(Broker broker)
    {
        m_arrBroker.add(broker);
    }

    public void connectBroker(Broker broker)
    {
        broker.connect();
    }

    //when user want to search bus, select a broker of handling this bus
    public void visualiseData( int nBus )
    {
        int i = 0, nCnt = m_arrBroker.size();
        Broker broker = null;
        for( i = 0 ; i < nCnt ; i ++ )
        {
            if(m_arrBroker.get(i).isResponse(nBus))
            {
                //if find a broker, which is responsible to this bus
                broker = m_arrBroker.get(i);
                break;
            }
        }
        //if there is no broker which is responsible to this bus, show a message
        if( broker == null ) {
            Toast.makeText(Global.g_MainActivity, "The bus is not valid...", Toast.LENGTH_SHORT).show();
            return;
        }
        broker.m_nCurBus = nBus;
        if( !broker.m_bConnected )
            connectBroker(broker);
        else
            broker.getBusData(nBus);

/*
        //if can not connect, return...
        if( !connectBroker(broker) )
            return;

        //get data from broker..
        broker.getData(nBus);
*/
    }
}
