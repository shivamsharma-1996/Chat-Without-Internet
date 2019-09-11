package com.shivam.wifidirectfinal;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Views
    private TextView tvConnectionStatus;
    private Button btnWifiOnOff;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setConnectionState();
        init();
        setOnclickHandler();
    }

    private void init() {
        tvConnectionStatus = findViewById(R.id.tv_connection_state);
        btnWifiOnOff = findViewById(R.id.btn_wifi_onOff);

        //Wifi
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);



    }

    private void setOnclickHandler() {
        btnWifiOnOff.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_wifi_onOff:
                switchWifi();
        }
    }

    private void switchWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        } else {
            wifiManager.setWifiEnabled(true);
        }
    }


    private void setConnectionState() {
        if (wifiManager.isWifiEnabled()) {
            btnWifiOnOff.setText("Turn off Wifi");
        } else {
            btnWifiOnOff.setText("Turn on Wifi");
        }
    }
}
