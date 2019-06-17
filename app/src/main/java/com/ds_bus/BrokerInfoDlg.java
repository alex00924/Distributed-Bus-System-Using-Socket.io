package com.ds_bus;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BrokerInfoDlg extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setTitle(R.string.title_dialog);
        View layout_view = inflater.inflate(R.layout.dialog_broker_info, null);
        final EditText txt_Ip = layout_view.findViewById(R.id.txt_ip);
        final EditText txt_Port = layout_view.findViewById(R.id.txt_port);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(layout_view)
                // Add action buttons
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //connect to server and get broker information
                        String strIP = txt_Ip.getText().toString();
                        String strPort = txt_Port.getText().toString();
                        if( strIP.isEmpty() || strPort.isEmpty() ) {
                            Toast.makeText(Global.g_MainActivity, "Please insert data...", Toast.LENGTH_SHORT).show();
//                            Global.g_MainActivity.showInfoDlg();
                            return;
                        }
                        int nPort = Integer.valueOf(strPort);
                        SocketManger sock_manager = new SocketManger(strIP, nPort);
                        Broker broker = new Broker(strIP, nPort, sock_manager);
                        Global.g_MainActivity.m_subscriber.register(broker);

                        sock_manager.connectToBroker(1, broker);

                        /*
                        boolean bCon = sock_manager.connectToBroker();
                        if( !bCon ) {
                            Toast.makeText(Global.g_MainActivity, "Can not connect to the broker, Please try again..", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Global.g_MainActivity.m_prgDlg.show();
                        sock_manager.getBrokerData();
                        */
                    }
                });
        return builder.create();
    }
}
