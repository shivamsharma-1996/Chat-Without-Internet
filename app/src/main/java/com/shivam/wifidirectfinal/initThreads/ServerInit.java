package com.shivam.wifidirectfinal.initThreads;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.shivam.wifidirectfinal.activity.LauncherActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Handler;

public class ServerInit extends Thread {
	private static final String TAG = "ServerInit";
	private static final int SERVER_PORT = 8888;
	public static ArrayList<InetAddress> clients;
	private ServerSocket serverSocket;
	public ServerInit(){
		clients = new ArrayList<>();
	}

	@Override
	public void run() {
		clients.clear();

		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			// Collect client ip's
		    while(true) {
		       Socket clientSocket = serverSocket.accept();

				if(!clients.contains(clientSocket.getInetAddress())){
		    	   clients.add(clientSocket.getInetAddress());
		    	   Log.d("Patch ServerInit0", "New client: " + clientSocket.getInetAddress().getHostAddress());
		       }
				LauncherActivity.updateClientsConnected(clients.size());
				clientSocket.close();
		    }
		} catch (IOException e) {
			Log.d("Patch ServerInit0", "client: " + "already connected");
			e.printStackTrace();
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		try {
			serverSocket.close();
			Log.v(TAG, "Server init process interrupted");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
