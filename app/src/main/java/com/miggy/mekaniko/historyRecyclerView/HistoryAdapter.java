package com.miggy.mekaniko.historyRecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miggy.mekaniko.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miggy on 01/10/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolders> {

    private List<HistoryObject> itemList;
    private Context context;

    public HistoryAdapter(ArrayList<HistoryObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public HistoryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        HistoryViewHolders rcv = new HistoryViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolders holder, final int position) {
        holder.rideId.setText(itemList.get(position).getRideId());
        holder.time.setText(itemList.get(position).getTime());
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

}