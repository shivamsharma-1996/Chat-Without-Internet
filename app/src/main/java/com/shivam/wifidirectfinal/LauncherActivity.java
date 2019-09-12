package com.shivam.wifidirectfinal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shivam.wifidirectfinal.adapter.PeerAdapter;
import com.shivam.wifidirectfinal.receiver.WifiDirectBroadcastReceiver;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener {

    //Views
    public TextView tvConnectionStatus;
    public TextView tvGroupStatus;
    public Button btnWifiOnOff;
    public Button btnFindGroups, btnFormGroup, btnRemoveGroup;

    //peers listview
    RecyclerView peerRec;
    PeerAdapter peerAdapter;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    List<WifiP2pDevice> peers = new ArrayList<>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;
    public WifiP2pManager.PeerListListener peerListListener;
    public WifiP2pManager.ConnectionInfoListener connectionInfoListener;

    //Broadcast receiver to maintain all custom actions
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setOnclickHandler();
        setConnectionState();
        Log.d("Patch", String.valueOf(wifiManager.getConfiguredNetworks()));

    }

    private void init() {
        //views
        tvConnectionStatus = findViewById(R.id.tv_connection_state);
        btnWifiOnOff = findViewById(R.id.btn_wifi_onOff);
        btnFindGroups = findViewById(R.id.btn_find_group);
        btnFormGroup = findViewById(R.id.btn_form_group);
        tvGroupStatus = findViewById(R.id.tv_group_status);
        btnRemoveGroup = findViewById(R.id.btn_remove_group);

        //Wifi
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        //creating an Intent-filter
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peersList) {
                if (!peersList.getDeviceList().equals(peers)) {
                    peers.clear();
                    peers.addAll(peersList.getDeviceList());

                    deviceNameArray = new String[peersList.getDeviceList().size()];
                    deviceArray = new WifiP2pDevice[peersList.getDeviceList().size()];
                    int index = 0;
                    for (WifiP2pDevice device : peersList.getDeviceList()) {
                        deviceNameArray[index] = device.deviceName;
                        deviceArray[index] = device;
                        index++;
                    }

                    peerAdapter = new PeerAdapter(LauncherActivity.this, deviceNameArray, LauncherActivity.this);
                    peerRec.setAdapter(peerAdapter);
                    //Toast.makeText(LauncherActivity.this, "clicked to peer", Toast.LENGTH_LONG).show();
                    discoveryCompleted();
                }

                if (peers.size() == 0) {
                    Toast.makeText(LauncherActivity.this, "No Device Found!!!", Toast.LENGTH_LONG).show();
                    tvGroupStatus.setText("Group Status : None");
                    discoveryCompleted();
                }

            }
        };

        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                final InetAddress groupOwnerAddress = info.groupOwnerAddress;

                if (info.groupFormed && info.isGroupOwner) {
                    tvGroupStatus.setText("Group Status : Owner");
//                    serverClass = new ServerClass();
//                    serverClass.start();
                } else if (info.groupFormed) {
                    tvGroupStatus.setText("Group Status : Client");
//                    clientClass = new ClientClass(groupOwnerAddress);
//                    clientClass.start();
                }else {
                    //group not formed
                    tvGroupStatus.setText("Group Status : None");
                }
            }
        };


        //Peer list
        peerRec = findViewById(R.id.peer_list);
        peerRec.setHasFixedSize(true);
        peerRec.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        peerRec.setLayoutManager(new LinearLayoutManager(this));
        peerRec.setAdapter(peerAdapter);
    }

    private void setOnclickHandler() {
        btnWifiOnOff.setOnClickListener(this);
        btnFormGroup.setOnClickListener(this);
        btnFindGroups.setOnClickListener(this);
        btnRemoveGroup.setOnClickListener(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_wifi_onOff:
                switchWifi();
                break;
            case R.id.peer_rel:
                WifiP2pDevice wifiP2pDevice = deviceArray[(Integer) view.getTag()];
                connectPeerToPeerOrGroup(wifiP2pDevice);
                break;
            case R.id.btn_form_group:
                createGroup();
                break;
            case R.id.btn_find_group:
                findGroups();
                break;
            case R.id.btn_remove_group:
                removeGroupIfFormed();
                break;
        }
    }

    private void removeGroupIfFormed() {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(LauncherActivity.this, "Group is removed", Toast.LENGTH_SHORT).show();
                tvGroupStatus.setText("Group Status : None");
                peers.clear();
                peerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(LauncherActivity.this, "Failed to remove Group, try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findGroups() {
        btnFindGroups.setEnabled(false);
        btnFindGroups.setAlpha(0.5f);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                btnFindGroups.setEnabled(true);
                btnFindGroups.setText("Discovering Groups ...");
            }

            @Override
            public void onFailure(int reason) {
                btnFindGroups.setText("Discovery failed");
                btnFindGroups.setAlpha(1f);
                btnFindGroups.setEnabled(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnFindGroups.setText("Find Groups");
                    }
                }, 1500);
            }
        });
    }

    private void createGroup() {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Device is ready to accept incoming connections from peers.
                Toast.makeText(LauncherActivity.this, "Group created", Toast.LENGTH_SHORT).show();
                tvGroupStatus.setText("Group Status : Owner");
            }

            @Override
            public void onFailure(int reason) {
                //tvGroupStatus.setText("Group Status : None");
                Toast.makeText(LauncherActivity.this, "Can't create group due to wifi not available or already connected to a group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void connectPeerToPeerOrGroup(final WifiP2pDevice wifiP2pDevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiP2pDevice.deviceAddress;

        mManager.connect(mChannel, config,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connected to " + wifiP2pDevice.deviceName, Toast.LENGTH_LONG).show();
                        tvConnectionStatus.setText("Connected to : " + wifiP2pDevice.deviceName);
                        tvConnectionStatus.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void discoveryCompleted(){
        //change button state
        btnFindGroups.setText("Discovery completed");
        btnFindGroups.setEnabled(true);
        btnFindGroups.setAlpha(1f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnFindGroups.setText("Find Groups");
            }
        }, 1500);
    }

    public void resetDiscoveryButton(){
        btnFindGroups.setText("Find Groups");
        btnFindGroups.setEnabled(true);
        btnFindGroups.setAlpha(1f);
    }
}
