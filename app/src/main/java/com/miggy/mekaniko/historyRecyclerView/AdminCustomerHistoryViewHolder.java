package com.miggy.mekaniko.historyRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.miggy.mekaniko.AdminCutomerSingleHistory;
import com.miggy.mekaniko.AdminMechanicHistorySingle;
import com.miggy.mekaniko.HistorySingleActivity;
import com.miggy.mekaniko.R;

/**
 * Created by miggy on 11/13/2019.
 */

public class AdminCustomerHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView rideId;
    public TextView time;



    public AdminCustomerHistoryViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        rideId = (TextView) itemView.findViewById(R.id.rideId);
        time = (TextView) itemView.findViewById(R.id.time);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), AdminCutomerSingleHistory.class);
        Bundle b = new Bundle();
        b.putString("rideId", rideId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
