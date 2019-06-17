package com.ds_bus;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketManger {

    private Socket socket;
    private String m_strBrokerIP = "";          //broker Ip address
    private int    m_nBrokerPort = 0;           //broker port number
    private BufferedReader input;               //an instance for reading data
    public PrintWriter out;                    //an instance for send data

    private Broker  m_Broker;
    public  int        m_nCurBus;

    public SocketManger(String strIp, int nPort)
    {
        m_strBrokerIP = strIp;
        m_nBrokerPort = nPort;
    }

    //connect to broker with the broker ip and port.
    public void connectToBroker(int nType, Broker broker)
    {
        m_Broker = broker;

        ( new connectThread() ).execute(nType);
        /*
        InetAddress serverAddr = null;
        try {
            serverAddr = InetAddress.getByName(m_strBrokerIP);
            socket = new Socket(serverAddr, m_nBrokerPort);
            socket.setSoTimeout(1000);                              //set time out to get or connect time out.
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return socket.isConnected();
        */
    }

    //get data from broker and show the result to google map.
    public void getBusData(int nBusLine)
    {
        if( socket == null || !socket.isConnected() )
            return;

        String strSend = "";
        JSONObject msgObj = new JSONObject();

        msgObj.put(Global.g_strTitle, Global.g_strTitleBus);
        msgObj.put(Global.g_strBusCode, nBusLine);
        strSend = msgObj.toJSONString();

        out.println(strSend);
        ( new readThread() ).execute(2);
    }

    public void getBrokerData()
    {
        if( socket == null || !socket.isConnected() )
            return;

        String strSend = "";
        JSONObject msgObj = new JSONObject();

        msgObj.put(Global.g_strTitle, Global.g_strTitleBroker);
        strSend = msgObj.toString();

        out.println(strSend);
        ( new readThread() ).execute(1);
    }


    class connectThread extends AsyncTask<Integer, String, Boolean>{
        int nType = 0;  //1 : broker, 2 : bus
        @Override
        protected Boolean doInBackground(Integer... integers) {
            nType = integers[0];
            //connect for broker information
            InetAddress serverAddr = null;
            try {
                serverAddr = InetAddress.getByName(m_strBrokerIP);
                socket = new Socket(serverAddr, m_nBrokerPort);
                socket.setSoTimeout(3000);                              //set time out to get or connect time out.
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return socket.isConnected();
        }

        protected void onPostExecute(Boolean bSuccess) {
            if( !bSuccess ) {
                Toast.makeText(Global.g_MainActivity, "Can not connect to the broker, Please try again..", Toast.LENGTH_SHORT).show();
                //If can not connect, show dialog again, so user can enter correct broker information....
                if( nType == 1 )
                {
                    Global.g_MainActivity.updateUI(1, null);
                }
                return;
            }

            try {
                out = new PrintWriter(new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())), true);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            m_Broker.m_bConnected = true;
            if( nType == 1 )
            {
                //send connection type--I am consumer...
                out.println(Global.g_strIdentify_C);
                getBrokerData();
            }
            else
            {
                getBusData(m_Broker.m_nCurBus);
            }
        }
    }


    class readThread extends AsyncTask<Integer, String, String>
    {
        int nType = 0;  //1 : broker, 2 : bus
        @Override
        protected String doInBackground(Integer... integers) {
            nType = integers[0];
            String read = null;
            try {
                read = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return read;
        }

        protected void onPostExecute(String strRes) {
            Global.g_MainActivity.updateUI(nType, strRes);
        }
    }
}
