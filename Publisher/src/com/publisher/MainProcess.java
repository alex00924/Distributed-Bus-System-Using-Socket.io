package com.publisher;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import com.publisher.data.Broker;
import com.publisher.data.dataManager;

public class MainProcess extends JFrame{
	 
	private static final long serialVersionUID = 1L;
	
	public dataManager m_dataManager = null;

	//this is constructor. Init UI component, read data from file, starting connect to broker server.
	public MainProcess()
	{
		Global.g_mainProcess = this;

		initComponents();
		
		readData();
		
		runBrokers();
	}
	
	//Create sockets, send messages to brokers, receive message from broker....
	private void runBrokers()
	{
		List<Broker> arrBrokers = m_dataManager.getBroker();
		for (Broker broker : arrBrokers) {
			try {
				broker.startCommunication();
			} catch (UnknownHostException e) {
				showMessage(e.getMessage());
			} catch (IOException e) {
				showMessage(e.getMessage());
			}
		}
	}
	
	//read all data from  text file
	private void readData()
	{
		
		//read line code
		File selectedFile = selectFile("Select Bus Line Data file...");
		if( m_dataManager == null )
		{
			m_dataManager = new dataManager();
			m_dataManager.setTables(table_broker, table_bus);
		}
		m_dataManager.read_bus_line(selectedFile);
		showMessage("Read " + m_dataManager.getBusLine().size() + " bus lines from " + selectedFile.getAbsolutePath());
		
		//read route code
		selectedFile = selectFile("Select Route Code Data file...");
		if( m_dataManager == null )
			m_dataManager = new dataManager();
		m_dataManager.read_route_code(selectedFile);
		showMessage("Read " + m_dataManager.getRouteCode().size() + " routes from " + selectedFile.getAbsolutePath());
		
		
		//read bus position information
		selectedFile = selectFile("Select Bus Position Data file...");
		if( m_dataManager == null )
			m_dataManager = new dataManager();
		m_dataManager.read_bus_Info(selectedFile, model_bus);
	    showMessage("Read " + m_dataManager.getBusInfo().size() + " bus positions from " + selectedFile.getAbsolutePath());
		
		//read broker information
	    selectedFile = selectFile("Select Broker Data file...");
		if( m_dataManager == null )
			m_dataManager = new dataManager();
		m_dataManager.read_broker(selectedFile, model_broker);
	    showMessage("Read " + m_dataManager.getBroker().size() + " Brokers from " + selectedFile.getAbsolutePath());
		

	}
	
	//show file browser dialog which can select only text file
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
	
	//show message to UI-Message, this could be called from other thread
	public void showMessage(final String strMsg)
	{
		SwingUtilities.invokeLater(new Runnable() 
	    {
	      public void run()
	      {
	    	  jText_Message.append( "\n    " + strMsg);
	      }
	    });
	}


	//start program
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
		    public void run() {
		        new MainProcess().setVisible(true);   
		   }
		});
	}

	// Variables declaration - do not modify       
	String[] header_broker = {"IP Adress", "Port", "SHA1" ,"Connect State", "Bus Line"};
	String[] header_bus = {"LineCode","RouteCode","vehicleId", "latitude", "longitude", "time", "LineDescription", "RouteType", "RouteDescription", "SHA1 Key", "Send State"};
	DefaultTableModel model_broker = new DefaultTableModel(header_broker, 0);
	DefaultTableModel model_bus = new DefaultTableModel(header_bus, 0);
	public JTable table_broker = new JTable(model_broker);
	public JTable table_bus = new JTable(model_bus);
	
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JTextArea jText_Message;
	// End of variables declaration                

	//Create UI Component
	 private void initComponents() {

		 this.setTitle("Publisher");
		 
	     jSplitPane1 = new javax.swing.JSplitPane();
	     jPanel1 = new javax.swing.JPanel();
	     jLabel1 = new javax.swing.JLabel();
	     jTabbedPane1 = new javax.swing.JTabbedPane();
	     jScrollPane2 = new javax.swing.JScrollPane();
	     jScrollPane3 = new javax.swing.JScrollPane();
	     jPanel2 = new javax.swing.JPanel();
	     jLabel2 = new javax.swing.JLabel();
	     jScrollPane1 = new javax.swing.JScrollPane();
	     jText_Message = new javax.swing.JTextArea();

	     this.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	                List<Broker> arrBroker = m_dataManager.getBroker();
	                for (Broker broker : arrBroker) {
						broker.stopCommunication();
					}
	                System.exit(0);
	            }
	        });
	     setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

	     jSplitPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

	     jPanel1.setBackground(new java.awt.Color(255, 255, 255));
	     jPanel1.setPreferredSize(new java.awt.Dimension(300, 496));

	     jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
	     jLabel1.setText("Broker and Bus");
	     jLabel1.setAlignmentX(0.5F);
	     jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

	     jScrollPane2.setName("list_broker"); // NOI18N

	     jScrollPane2.setViewportView(table_broker);

	     jTabbedPane1.addTab("Brokers", jScrollPane2);

	     jScrollPane3.setName("list_bus"); // NOI18N

	     jScrollPane3.setViewportView(table_bus);

	     jTabbedPane1.addTab("Bus Information", jScrollPane3);

	     javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	     jPanel1.setLayout(jPanel1Layout);
	     jPanel1Layout.setHorizontalGroup(
	         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	         .addGroup(jPanel1Layout.createSequentialGroup()
	             .addContainerGap()
	             .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                 .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
	                 .addGroup(jPanel1Layout.createSequentialGroup()
	                     .addComponent(jLabel1)
	                     .addGap(0, 0, Short.MAX_VALUE)))
	             .addContainerGap())
	     );
	     jPanel1Layout.setVerticalGroup(
	         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	         .addGroup(jPanel1Layout.createSequentialGroup()
	             .addContainerGap()
	             .addComponent(jLabel1)
	             .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	             .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
	             .addContainerGap())
	     );

	     jLabel1.getAccessibleContext().setAccessibleName("Broker_Label");

	     jSplitPane1.setLeftComponent(jPanel1);

	     jPanel2.setBackground(new java.awt.Color(255, 255, 255));

	     jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
	     jLabel2.setText("Message");
	     jLabel2.setAlignmentX(0.5F);
	     jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

	     jText_Message.setColumns(20);
	     jText_Message.setRows(5);
	     jText_Message.setLineWrap(true);
	     jText_Message.setFont(new java.awt.Font("Yu Gothic UI", 1, 16)); // NOI18N
	     jScrollPane1.setViewportView(jText_Message);

	     javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
	     jPanel2.setLayout(jPanel2Layout);
	     jPanel2Layout.setHorizontalGroup(
	         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	         .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
	         .addGroup(jPanel2Layout.createSequentialGroup()
	             .addContainerGap()
	             .addComponent(jLabel2)
	             .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	     );
	     jPanel2Layout.setVerticalGroup(
	         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	         .addGroup(jPanel2Layout.createSequentialGroup()
	             .addContainerGap()
	             .addComponent(jLabel2)
	             .addGap(18, 18, 18)
	             .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
	     );

	     jSplitPane1.setRightComponent(jPanel2);

	     javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	     getContentPane().setLayout(layout);
	     layout.setHorizontalGroup(
	         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	         .addGroup(layout.createSequentialGroup()
	             .addGap(0, 0, 0)
	             .addComponent(jSplitPane1))
	     );
	     layout.setVerticalGroup(
	         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	         .addComponent(jSplitPane1)
	     );

	     pack();
	 }// </editor-fold>                        
	
	
}
