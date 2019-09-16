package com.shivam.wifidirectfinal.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.shivam.wifidirectfinal.asyncTasks.ReceiveMessageClient;
import com.shivam.wifidirectfinal.asyncTasks.ReceiveMessageServer;
import com.shivam.wifidirectfinal.utils.PeerSingleton;

public class MessageService extends Service {
	private static final String TAG = "MessageService";

	@Override
	public IBinder onBind(Intent arg0) {
		//Toast.makeText(getApplicationContext(), "Connected to.deviceName", Toast.LENGTH_LONG).show();

		return null;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Toast.makeText(getApplicationContext(), "Connected to.deviceName", Toast.LENGTH_LONG).show();

		Boolean isOwner = PeerSingleton.getInstance().isOwnner();

		//Start the AsyncTask for the server to receive messages
        if(isOwner!= null && isOwner==true){
        	Log.v(TAG, "Start the AsyncTask for the server to receive messages");
        	new ReceiveMessageServer(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        }
        else if(isOwner!= null && isOwner==false){
        	Log.v(TAG, "Start the AsyncTask for the client to receive messages");
        	new ReceiveMessageClient(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        }
		return START_STICKY;
	}
}
