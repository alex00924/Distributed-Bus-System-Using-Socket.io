package com.publisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;

import com.publisher.data.Broker;

public class SocketManager {
	private Socket m_clientSocket;
    private PrintWriter m_out;
    private BufferedReader m_in;
    
    private String m_IP;
    private int m_port;
    
    private Broker m_broker;		//a broker to manage this socket
    
    public SocketManager(String ip, int port, Broker broker)
    {
    	m_broker = broker;
    	m_IP = ip;
    	m_port = port;
    }
 
    public void startCommunication() {
    	(new Connect_Thread()).start();
    }
 
    // msg title : Broker's information, Bus Pos information
    public boolean sendMessage(String msgTitle, String msg){
        //send msg to broker
    	m_out.println(msg);
        String resp;
		try {
			resp = m_in.readLine();
			//receive reply and show it to message view
	        if( resp.equals("OK") )
	        	Global.g_mainProcess.showMessage(msgTitle + " is sent successfly to " + m_broker.m_strIP);
	        else
	        {
	        	Global.g_mainProcess.showMessage(msgTitle + " is not sent to " + m_broker.m_strIP);
	        	return false;
	        }
		} catch (IOException e) {
			Global.g_mainProcess.showMessage("Can not send data to " + m_broker.m_strIP);
			return false;
		}
		return true;
    }
 
    public void stopConnection() throws IOException {
    	if( m_in != null )
    		m_in.close();
    	if( m_out != null )
    		m_out.close();
    	if( m_clientSocket != null )
    		m_clientSocket.close();
    }
    
	public void monitorBroker()
    {
    	while(true)
    	{
    		
    		JSONObject aliveMsg = new JSONObject();
    		aliveMsg.put(Global.g_strTitle, Global.g_strAlive);
    		m_out.println(aliveMsg);
    		try {
				String resp = m_in.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
    		//if broker is disconnected, try to reconnect
    		if( m_clientSocket == null || !m_clientSocket.isConnected() )
    		{
    			try {
    				Global.g_mainProcess.showMessage("Broker(" + m_broker.m_strIP + ") is disconnected.");
    				m_broker.startCommunication();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			break;
    		}
    		
    		try {
        		Connect_Thread.sleep(5000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}	
    	}
    	
    }
    
    public boolean startReceive()
    {
    	if( m_clientSocket.isConnected() )
    		return false;
    	//Start Receive
    	(new Receive_Thread()).start();
    	return true;
    }
    
    private class Receive_Thread extends Thread
    {
    	 public void run() {
              
             String inputLine;
             try {
				while ( m_clientSocket.isConnected() && (inputLine = m_in.readLine()) != null) {
					//reply broker's message
					
				 }
			} catch (IOException e) {
				try {
					//if error occur, close connection
					stopConnection();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
    	 }
    }
    
    private class Connect_Thread extends Thread
    {
    	 public void run() {
    		 Global.g_mainProcess.showMessage("Connecting to  " + m_IP + " : " + m_port);
    		 InetSocketAddress sockAdress = new InetSocketAddress(m_IP, m_port);
    		 while(true)
    		 {
    			 try
    			 {
    				if( m_clientSocket != null )
    					 m_clientSocket.close();
    				m_clientSocket = new Socket();
    				//m_clientSocket.setSoTimeout(1000);		//set read time out to 1s
					m_clientSocket .connect(sockAdress);
					if( m_clientSocket.isConnected() )
						break;
					sleep(5000);
				}
    			catch (UnknownHostException e) {
				}
    			catch (IOException e) {
				}
    			catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		 }
    		 
    		 Global.g_mainProcess.showMessage("Connected to  " + m_IP + " : " + m_port);
    		 m_broker.updateConState(true);
    		 
    		 try {
				m_out = new PrintWriter(m_clientSocket.getOutputStream(), true);
		        m_in = new BufferedReader(new InputStreamReader(m_clientSocket.getInputStream()));
		    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		 
    		 //send msg that this is publisher
    		 m_out.println(Global.g_strIdentify_P);
    		 
    		 //start sending data to broker
    		 m_broker.sendData();
    		
    		 //monitor if the broker is online, if not connect, then try to reconnect.
//    		 monitorBroker();
 			
    		 //start receive, this is not needed
//    		 startReceive();
    	 }
    }
}
