package com.broker;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

public class MainProcess extends javax.swing.JFrame {

	private SocketManager m_sockManger;
	private static final long serialVersionUID = 2L;
	public List<Broker> m_arrBroker = new ArrayList<Broker>();
	public List<Bus_Info> m_arrBus = new ArrayList<Bus_Info>();
	public List<Publisher> m_arrPublisher = new ArrayList<Publisher>();
	
	public MainProcess()
	{
		Global.g_mainProcess =  this;
		initComponents();
		int nPort = 0;
		String strPort;
		String strIp = "";
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			strIp = inetAddress.getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		do
		{
			File selectedFile = selectFile("Select Broker Data...");
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(selectedFile));
				String line = reader.readLine();

				while (line != null) {
					String[] arrInfo = line.split(",");
					if( strIp.equals(arrInfo[0]) )
					{
						nPort = Integer.valueOf(arrInfo[1]);
						break;
					}
					line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if( nPort < 1 )
			{
				JOptionPane.showMessageDialog(this,"Select Correct File...","Alert",JOptionPane.WARNING_MESSAGE);     
			}
		}while(nPort < 1);
		
		if( m_sockManger == null )
			m_sockManger = new SocketManager(nPort);
	}
	
	private File selectFile(String strTitle)
	{
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setDialogTitle(strTitle);
		fileChooser.addChoosableFileFilter(new FileFilter() {
			 
		    public String getDescription() {
		        return "Text Files (*.txt)";
		    }
		 
		    public boolean accept(File f) {
		        if (f.isDirectory()) {
		            return true;
		        } else {
		            return f.getName().toLowerCase().endsWith(".txt");
		        }
		    }
		});
		fileChooser.setAcceptAllFileFilterUsed(false);
		URL location = MainProcess.class.getProtectionDomain().getCodeSource().getLocation();
		fileChooser.setCurrentDirectory(new File(location.getFile()));
		int result = fileChooser.showOpenDialog(this);
		if (result != JFileChooser.APPROVE_OPTION)
		{
			JOptionPane.showMessageDialog(this,"You must select file...","Alert",JOptionPane.WARNING_MESSAGE);     
			selectFile(strTitle);
		}
	    return fileChooser.getSelectedFile();
	}
	
	public void showMessage(final String strMsg)
	{
		SwingUtilities.invokeLater(new Runnable() 
	    {
	      public void run()
	      {
	    	  txt_msg.append( "\n    " + strMsg);
	      }
	    });
	}
	
	
	  private void initComponents() {

		  this.setTitle("Broker");
		  
	        jSplitPane1 = new javax.swing.JSplitPane();
	        jPanel1 = new javax.swing.JPanel();
	        jLabel1 = new javax.swing.JLabel();
	        jTabbedPane1 = new javax.swing.JTabbedPane();
	        jScrollPane2 = new javax.swing.JScrollPane();
	        
	        jPanel2 = new javax.swing.JPanel();
	        jLabel2 = new javax.swing.JLabel();
	        jScrollPane1 = new javax.swing.JScrollPane();
	        txt_msg = new javax.swing.JTextArea();

	        this.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	                //Stop publisher
	            	for (Publisher publisher : m_arrPublisher) {
	                	publisher.stopCommunication();
					}
	                
	            	//stop consumer
	            	
	            	//stop server socket
	            	m_sockManger.stopServer();
	                System.exit(0);
	            }
	        });
	        
	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

	        jPanel1.setMinimumSize(new java.awt.Dimension(400, 100));
	        jPanel1.setPreferredSize(new java.awt.Dimension(500, 582));

	        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
	        jLabel1.setText("Consumers");
	        jScrollPane2.setViewportView(table_consumer);

	        jTabbedPane1.addTab("Consumers", jScrollPane2);

	        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	        jPanel1.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jLabel1)
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jLabel1)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
	                .addGap(18, 18, 18))
	        );

	        jTabbedPane1.getAccessibleContext().setAccessibleName("tab_consumer");

	        jSplitPane1.setLeftComponent(jPanel1);

	        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
	        jLabel2.setText("Messages");

	        txt_msg.setColumns(20);
	        txt_msg.setRows(5);
	        jScrollPane1.setViewportView(txt_msg);

	        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
	        jPanel2.setLayout(jPanel2Layout);
	        jPanel2Layout.setHorizontalGroup(
	            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel2Layout.createSequentialGroup()
	                .addGap(39, 39, 39)
	                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
	        );
	        jPanel2Layout.setVerticalGroup(
	            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel2Layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jLabel2)
	                .addGap(27, 27, 27)
	                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
	                .addContainerGap())
	        );

	        jSplitPane1.setRightComponent(jPanel2);

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 994, Short.MAX_VALUE)
	                .addContainerGap())
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(jSplitPane1)
	        );

	        pack();
	    }// </editor-fold>                        

	  public static void main(String args[]) {
	               /* Create and display the form */
	        java.awt.EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                new MainProcess().setVisible(true);
	            }
	        });
	    }


	  // Variables declaration - do not modify
		String[] header_broker = {"IP Adress", "Connected Time", "Connect State"};
		DefaultTableModel model_broker = new DefaultTableModel(header_broker, 0);

	    private javax.swing.JLabel jLabel1;
	    private javax.swing.JLabel jLabel2;
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JPanel jPanel2;
	    private javax.swing.JScrollPane jScrollPane1;
	    private javax.swing.JScrollPane jScrollPane2;
	    private javax.swing.JSplitPane jSplitPane1;
	    private javax.swing.JTabbedPane jTabbedPane1;
	    public  javax.swing.JTable table_consumer = new JTable(model_broker);
	    private javax.swing.JTextArea txt_msg;
}
