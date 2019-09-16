package com.shivam.wifidirectfinal.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shivam.wifidirectfinal.R;
import com.shivam.wifidirectfinal.activity.ChatActivity;
import com.shivam.wifidirectfinal.asyncTasks.SendMessageServer;
import com.shivam.wifidirectfinal.entities.Message;
import com.shivam.wifidirectfinal.utils.PeerSingleton;

import java.util.List;

/**
 * Created by Shivam Sharma on 12-08-2019.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ConversationHolder> {
    private View.OnClickListener onClickListener;
    private Context context;
    private List<Message> chatList;
    public ChatAdapter(Context context, List<Message> chatList, View.OnClickListener onClickListener) {
        this.context = context;
        this.chatList = chatList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_list, viewGroup, false);
        v.setTag(Integer.valueOf(i));
        return new ConversationHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationHolder conversationHolder, int i) {
        String chatName = PeerSingleton.getInstance().getUserName();
        if(/*chatName.equals(chatList.get(i).getChatName())*/ chatList.get(i).isMine() == true){
            conversationHolder.setGravity(Gravity.END);
        }else {
            conversationHolder.setGravity(Gravity.START);
        }

        conversationHolder.setUserName(chatList.get(i).getChatName());
        conversationHolder.itemView.setOnClickListener(onClickListener);
        conversationHolder.setMessage(chatList.get(i).getmText());
        conversationHolder.setSentTime(chatList.get(i).getTime());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ConversationHolder extends RecyclerView.ViewHolder{
        private RelativeLayout chatBox;
        private TextView tvUserName, tvMessage, tvSentTime;

        public ConversationHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvMessage = itemView.findViewById(R.id.tv_message);
            chatBox = itemView.findViewById(R.id.rl_chat_box);
            tvSentTime = itemView.findViewById(R.id.tv_time);
        }

        public void setUserName(String userName) {
            this.tvUserName.setText(userName);
        }

        public void setMessage(String message){
            tvMessage.setText(message);
        }

        public void setSentTime(String sentTime) {
            this.tvSentTime.setText(sentTime);
        }


        public void setGravity(int gravity){
            chatBox.setGravity(gravity);
            if(gravity == Gravity.END){
                tvMessage.setBackground(ContextCompat.getDrawable(context,R.drawable.message_text_background));
                tvMessage.setTextColor(Color.WHITE);
            }else {
                tvMessage.setBackground(ContextCompat.getDrawable(context,R.drawable.message_text_background2));
                tvMessage.setTextColor(Color.BLACK);
            }
        }
    }
}
