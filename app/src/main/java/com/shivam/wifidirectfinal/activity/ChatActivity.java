package com.shivam.wifidirectfinal.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shivam.wifidirectfinal.R;
import com.shivam.wifidirectfinal.adapter.ChatAdapter;
import com.shivam.wifidirectfinal.asyncTasks.ReceiveMessageClient;
import com.shivam.wifidirectfinal.asyncTasks.ReceiveMessageServer;
import com.shivam.wifidirectfinal.asyncTasks.SendMessageClient;
import com.shivam.wifidirectfinal.asyncTasks.SendMessageServer;
import com.shivam.wifidirectfinal.entities.Message;
import com.shivam.wifidirectfinal.service.MessageService;
import com.shivam.wifidirectfinal.utils.AppUtil;
import com.shivam.wifidirectfinal.utils.PeerSingleton;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ChatActivity";

    PeerSingleton peerSingleton = PeerSingleton.getInstance();

    private static ChatActivity context;
    //Views
    private EditText etInputMessage;
    private Button btnSend;

    //setUserName views
    private RelativeLayout rlUserNameScreen;
    private ConstraintLayout clChatScreen;
    private EditText etUserName;
    private Button btnStartChat;

    //chatList
    private static RecyclerView chatRec;
    private static ChatAdapter chatAdapter;
    private static List<Message> chatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        context = this;
        startService(new Intent(this, MessageService.class));

        init();
        setOnclickHandler();

        Boolean isOwner = PeerSingleton.getInstance().isOwnner();

        //Start the AsyncTask for the server to receive messages
       /* if(isOwner!= null && isOwner==true){
            Log.v(TAG, "Start the AsyncTask for the server to receive messages");
            new ReceiveMessageServer(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        }
        else if(isOwner!= null && isOwner==false){
            Log.v(TAG, "Start the AsyncTask for the client to receive messages");
            new ReceiveMessageClient(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        }
*/
        chatRec.scrollToPosition(chatList.size() - 1);
    }

    private void init() {
        //Views
        etInputMessage = findViewById(R.id.et_input_msg);
        btnSend = findViewById(R.id.btn_send_msg);
        rlUserNameScreen = findViewById(R.id.rl_user_name_screen);
        clChatScreen = findViewById(R.id.cl_chat_screen);
        etUserName = findViewById(R.id.et_user_name);
        btnStartChat = findViewById(R.id.btn_start_chat);

        //ChatList
        chatAdapter = new ChatAdapter(this, chatList, this);

        chatRec = findViewById(R.id.chat_list);
        chatRec.setHasFixedSize(true);
        chatRec.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        chatRec.setLayoutManager(new LinearLayoutManager(this));
        chatRec.setAdapter(chatAdapter);

        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0){
                    btnStartChat.setEnabled(true);
                    btnStartChat.setAlpha(1f);
                }else {
                    btnStartChat.setEnabled(false);
                    btnStartChat.setAlpha(0.5f);
                }
            }
        });
    }

    private void setOnclickHandler() {
        btnSend.setOnClickListener(this);
        btnStartChat.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_msg:
                sendMessage();
                break;


            case R.id.btn_start_chat:
                startChat();
                break;
        }
    }

    private void startChat() {
        //Todo: ask and confirm user for userName before taking to chatScreen
        String userName = etUserName.getText().toString();
        if(userName.isEmpty()){
            Toast.makeText(this,"userName can't be empty", Toast.LENGTH_LONG).show();
            return;
        }
        peerSingleton.setUserName(userName);

        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("Go to chatroom");
        newDialog.setMessage("Are you sure to continue with this name\n");

        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clChatScreen.setVisibility(View.VISIBLE);
                rlUserNameScreen.setVisibility(View.GONE);

                AppUtil.getInstance().hideKeyboard(ChatActivity.this);
            }
        });

        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        newDialog.show();
    }

    private void sendMessage() {
        String message = etInputMessage.getText().toString();
        Message mes = new Message(Message.TEXT_MESSAGE, message, null, peerSingleton.getUserName());

        if (!message.equals("")) {
            if (PeerSingleton.getInstance().isOwnner() != null
                    && PeerSingleton.getInstance().isOwnner()) {
                Log.e(TAG, "Message hydrated, start SendMessageServer AsyncTask");
                new SendMessageServer(ChatActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
            } else {
                Log.e(TAG, "Message hydrated, start SendMessageClient AsyncTask");
                new SendMessageClient(ChatActivity.this, PeerSingleton.getInstance().getOwnerAddress()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
            }
            etInputMessage.setText("");
        } else {
            Toast.makeText(ChatActivity.this, "Please enter a non-empty message", Toast.LENGTH_SHORT).show();
        }
    }

    // Refresh the message list
    public static void refreshList(Message message, boolean isMine) {
        message.setMine(isMine);
        message.setTime(AppUtil.getInstance().getCurrentTime());
		//Log.e(TAG, "refreshList: message is from :"+message.getSenderAddress().getHostAddress() );
		//Log.e(TAG, "refreshList: message is from :"+isMine );

        if(message.getmText().equals("reboot-chat")) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
            newDialog.setTitle("Server Disconnected");
            newDialog.setMessage("Room is closed by server. Go back and reopen the chat-screen");

            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.finish();
                }
            });

//            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });

            if (PeerSingleton.getInstance().isOwnner() != null
                    && PeerSingleton.getInstance().isOwnner() == false) {
                newDialog.show();
            }
        }else {
            chatList.add(message);
            chatAdapter.notifyDataSetChanged();
        }

        chatRec.scrollToPosition(chatList.size() - 1);
    }

    @Override
    public void onBackPressed() {
        //Todo : show dialog to confirm user before leaving chat room (disconnecting from session)
        //Toast.makeText(ChatActivity.this, "user presses back button", Toast.LENGTH_SHORT).show();
        if(clChatScreen.getVisibility() == View.VISIBLE){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Close chatroom");
            newDialog.setMessage("Are you sure you want to close this chatroom?\n"
                    + "You will no longer be able to receive messages ");

            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*if(LauncherActivity.serverInit!=null){
                        LauncherActivity.serverInit.interrupt();
                    }
                    else if(LauncherActivity.clientInit!=null){
                        LauncherActivity.clientInit.interrupt();
                    }
                    android.os.Process.killProcess(android.os.Process.myPid());*/
                    if(PeerSingleton.getInstance().isOwnner() != null
                            && PeerSingleton.getInstance().isOwnner()){

                        Message mes = new Message(Message.TEXT_MESSAGE, "reboot-chat", null, peerSingleton.getUserName());
                        new SendMessageServer(ChatActivity.this, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1500);
                }

            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            newDialog.show();
        }else {
            super.onBackPressed();
        }
    }
}
