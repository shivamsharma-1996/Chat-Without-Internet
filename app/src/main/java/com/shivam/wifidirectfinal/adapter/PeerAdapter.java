package com.shivam.wifidirectfinal.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shivam.wifidirectfinal.R;

/**
 * Created by Shivam Sharma on 12-08-2019.
 */
public class PeerAdapter extends RecyclerView.Adapter<PeerAdapter.PeerHolder> {
    private View.OnClickListener onClickListener;
    private Context context;
    private String[] peerList;

    public PeerAdapter(Context context, String[] peerList, View.OnClickListener onClickListener) {
        this.context = context;
        this.peerList = peerList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public PeerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_peer_list, viewGroup, false);
        v.setTag(Integer.valueOf(i));
        return new PeerHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PeerHolder peerHolder, int i) {
        peerHolder.itemView.setOnClickListener(onClickListener);
        peerHolder.setPeers(peerList[i], (i+1));
    }

    @Override
    public int getItemCount() {
        return peerList.length;
    }

    class PeerHolder extends RecyclerView.ViewHolder{
        private TextView tvPeers;

        public PeerHolder(@NonNull View itemView) {
            super(itemView);
            tvPeers = itemView.findViewById(R.id.tv_peers);
        }

        public void setPeers(String peerId, int pos){
            tvPeers.setText(peerId + "   (Peer " +  pos + ")");
        }

    }
}
