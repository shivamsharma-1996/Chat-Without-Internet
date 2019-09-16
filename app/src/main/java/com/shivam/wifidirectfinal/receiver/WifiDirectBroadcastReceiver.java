package com.shivam.wifidirectfinal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;
import android.widget.Toast;
import com.shivam.wifidirectfinal.activity.LauncherActivity;
import com.shivam.wifidirectfinal.utils.PeerSingleton;

/**
 * Created by Shivam Sharma on 09-09-2019.
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private LauncherActivity mActivity;


    public WifiDirectBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, LauncherActivity mActivity) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = mActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context, "Wifi is ON", Toast.LENGTH_LONG).show();
                mActivity.btnWifiOnOff.setText("Turn off Wifi");
                mActivity.tvConnectionStatus.setVisibility(View.GONE);
                mActivity.tvGroupStatus.setText("Group Status : None");
                mActivity.resetDiscoveryButton();
            }else {
                Toast.makeText(context, "Wifi is OFF", Toast.LENGTH_LONG).show();
                mActivity.btnWifiOnOff.setText("Turn on Wifi");
                mActivity.tvConnectionStatus.setVisibility(View.GONE);
                if(PeerSingleton.getInstance().getGroupFormed()){
                    mActivity.tvGroupStatus.setText("Group Status : Destroyed");

                     if(LauncherActivity.serverInit!=null && LauncherActivity.serverInit.isAlive()){
                        LauncherActivity.serverInit.interrupt();
                         LauncherActivity.serverInit =  null;
                    }
                    else if(LauncherActivity.clientInit!=null && LauncherActivity.clientInit.isAlive()){
                        LauncherActivity.clientInit.interrupt();
                         LauncherActivity.clientInit =  null;

                     }
                }
                mActivity.resetDiscoveryButton();
            }

        }else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){

            if(mManager!= null){
                mManager.requestPeers(mChannel, mActivity.peerListListener);
            }
        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){

            if(mManager!=null){
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if(networkInfo.isConnected()){
                    mManager.requestConnectionInfo(mChannel, mActivity.connectionInfoListener);
                }else {
                    mActivity.tvConnectionStatus.setText("Device Disconnected");
                }
            }
        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }
}
