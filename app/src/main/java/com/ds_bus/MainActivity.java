package com.ds_bus;

import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public ProgressDialog m_prgDlg;
    private View m_viewDetail;
    public Subscriber m_subscriber;
    private List<BusInfo> m_arrBus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Global.g_MainActivity = this;
        m_prgDlg = new ProgressDialog(this);
        m_prgDlg.setMessage("Loading...");

        m_subscriber = new Subscriber();

        m_viewDetail = findViewById(R.id.detail_view);
        View btn_search = findViewById(R.id.btn_search);
        final EditText txt_Bus = findViewById(R.id.txt_key);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strBus = txt_Bus.getText().toString();
                if( strBus.isEmpty() )
                {
                    Toast.makeText(MainActivity.this, "please insert bus line...", Toast.LENGTH_SHORT).show();
                    return;
                }
                int nBus = Integer.valueOf( strBus );
                m_subscriber.visualiseData(nBus);
            }
        });
        getBrokers();
//        showInfoDlg();
    }

    //read broker information from assets/Broker.txt file
    private void getBrokers()
    {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("Broker.txt")));

            // read first line
            String strLine = reader.readLine();

            //parse this broker's ip address and port number..
            String[] arrStr = strLine.split(",");
            if( arrStr == null || arrStr.length < 1 )
            {
                Toast.makeText(this, "Can not read Broker Information..", Toast.LENGTH_SHORT).show();
                return;
            }
            String strIP = arrStr[0];
            String strPort = arrStr[1];

            if( strPort == null || strPort.isEmpty() ) {
                Toast.makeText(this, "Can not read Broker Information..", Toast.LENGTH_SHORT).show();
                return;
            }

            //connect to the broker and get brokers' information...
            int nPort = Integer.valueOf(strPort);
            SocketManger sock_manager = new SocketManger(strIP, nPort);
            Broker broker = new Broker(strIP, nPort, sock_manager);
            Global.g_MainActivity.m_subscriber.register(broker);

            sock_manager.connectToBroker(1, broker);

        } catch (IOException e) {

        }
    }

    //show a dialog to get first broker info, which is used for broker information
    public void showInfoDlg()
    {
        DialogFragment newFragment = new BrokerInfoDlg();
        newFragment.show(getSupportFragmentManager(), "Brokers");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                m_viewDetail.setVisibility(View.GONE);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //get marker position. this is used to get mark information from array.
                int position = Integer.valueOf(marker.getSnippet());
                BusInfo bus = m_arrBus.get(position);
                m_viewDetail.setVisibility(View.VISIBLE);
                ( (TextView)findViewById(R.id.txt_Line) ).setText( "LINE : " + bus.m_strLineDescription);
                ( (TextView)findViewById(R.id.txt_Route) ).setText( "ROUTE : " + bus.m_strRouteDescription);
                ( (TextView)findViewById(R.id.txt_Time) ).setText( "TIME : " + bus.m_strTime);
                return false;
            }
        });
    }

    //update map according to the received data from server
    public void updateUI(int nType, String strData)
    {
        //update map ...
        if( nType == 1 )    //get Brokers information
        {
            if( strData == null || strData.isEmpty() )
            {
                Toast.makeText(this, "Can not read data from this broker, Please try again...", Toast.LENGTH_SHORT).show();
                //showInfoDlg();
                return;
            }
            m_prgDlg.dismiss();
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject jsonMsg = (JSONObject)jsonParser.parse(strData);
                //parse the message and save broker information
                JSONArray brokerArray = (JSONArray)jsonMsg.get(Global.g_strBrokerArray);
                String strIp;
                boolean bReceived = false;

                //check that this broker information is already received..
                for(Object broker : brokerArray)
                {
                    strIp = ((JSONObject)broker).get(Global.g_strIP).toString();
                    bReceived = false;
                    for(Broker old_broker : m_subscriber.m_arrBroker)
                    {
                        //received broker informatoin already, add bus informatoin
                        if( old_broker.m_strIp.equals(strIp))
                        {
                            old_broker.addBusLine((JSONObject)broker);
                            bReceived = true;
                            break;
                        }
                    }
                    if( !bReceived)
                        m_subscriber.register(new Broker((JSONObject)broker));
                }
                Toast.makeText(this, "Received broker information", Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return;
        }
        else
        {
            //if can not receive data from server, return
            if( strData == null || strData.isEmpty() )
            {
                Toast.makeText(this, "Can not read bus data from broker, Please try again...", Toast.LENGTH_SHORT).show();
                return;
            }

            m_arrBus.clear();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonMsg = null;
            try {
                jsonMsg = (JSONObject)jsonParser.parse(strData);
                //parse the message and save broker information
                JSONArray busArray = (JSONArray)jsonMsg.get(Global.g_strBusLines);
                for(Object busMsg : busArray)
                {
                    BusInfo bus = new BusInfo( (JSONObject) busMsg );
                    m_arrBus.add(bus);
                }
                updateMap();

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private void updateMap()
    {
        mMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        int i = 0;
        for( BusInfo bus : m_arrBus )
        {
            LatLng bus_loc = new LatLng(bus.m_fLat, bus.m_fLon);
            MarkerOptions newMarker = new MarkerOptions().position(bus_loc).title(bus.m_strLineDescription);
            newMarker.snippet("" + i);
            mMap.addMarker(newMarker);
            builder.include(bus_loc);
            i++;
        }

        //Move camera
        int padding = 50;
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
    }

}
