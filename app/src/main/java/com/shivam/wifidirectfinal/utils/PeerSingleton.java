package com.shivam.wifidirectfinal.utils;

import java.net.InetAddress;

/**
 * Created by Shivam Sharma on 11-09-2019.
 */
public class PeerSingleton {

    private static PeerSingleton instance = null;
    private static final String TAG = "PeerSingleton";
    private Boolean isOwner;
    private InetAddress ownerAddress;
    private Boolean isGroupFormed = false;
    private String userName;

    public static PeerSingleton getInstance() {
        if (instance == null) {
            instance = new PeerSingleton();
        }
        return instance;
    }

    private PeerSingleton() {
    }

    public Boolean isOwnner() {
        return isOwner;
    }

    public void setOwnner(Boolean ownner) {
        isOwner = ownner;
    }

    public InetAddress getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(InetAddress ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public Boolean getGroupFormed() {
        return isGroupFormed;
    }

    public void setGroupFormed(Boolean groupFormed) {
        isGroupFormed = groupFormed;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
