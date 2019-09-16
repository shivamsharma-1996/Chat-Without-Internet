package com.shivam.wifidirectfinal.initThreads;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientInit extends Thread {
	private static final int SERVER_PORT = 8888;
	private InetAddress mServerAddr;
	public ClientInit(InetAddress serverAddr){
		mServerAddr = serverAddr;
	}

	@Override
	public void run() {
		final Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mServerAddr, SERVER_PORT),500);
			Log.d("Patch", "client socket connected");
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void interrupt() {
		super.interrupt();
	}
}
