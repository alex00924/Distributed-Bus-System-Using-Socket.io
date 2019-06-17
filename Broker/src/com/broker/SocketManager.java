package com.broker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketManager {
	ServerSocket serverSocket = null;
    
	public int m_nServerPort = 80;
	public SocketManager(int nPort)
	{
		m_nServerPort = nPort;
		try {
			serverSocket = new ServerSocket(m_nServerPort);
			(new ListenThread()).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopServer()
	{
		if( serverSocket != null ) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class ListenThread extends Thread
	{
		public void run() {
			Global.g_mainProcess.showMessage("Server started...");
			
			while (true) {
	            try {
	            	
	            	if( serverSocket == null || serverSocket.isClosed() )
	            		break;
	            	
	            	Socket socket = serverSocket.accept();
	                //read first msg, which can identify publisher or consumer
	                BufferedReader inp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                String strMsg = inp.readLine();
	                if( strMsg.equals(Global.g_strIdentify_P) )
	                {
	                	//start receiving from publisher
	                	(new Publisher(socket)).start();
	                }
	                else if(strMsg.equals(Global.g_strIdentify_C))
	                {
	                	(new Consumer(socket)).start();
	                }
	                
	                
	                
	            } catch (IOException e) {
	                System.out.println("I/O error: " + e);
	            }
	            
	        }
		}
	}
	
}
