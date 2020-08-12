package com.miggy.mekaniko;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miggy.mekaniko.historyRecyclerView.AdminCustomerHistoryAdapater;
import com.miggy.mekaniko.historyRecyclerView.HistoryObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdminMehanicHistory extends AppCompatActivity {
    private RecyclerView mHistoryRecyclerView;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;

    private TextView mBalance;

    private Double Balance = 0.0;

    private FirebaseAuth auth;

    private Intent intent;

    private DatabaseReference reference, userHistoryDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mehanic_history);

        mHistoryRecyclerView = (RecyclerView) findViewById(R.id.historyRecyclerView);
        mHistoryRecyclerView.setNestedScrollingEnabled(false);
        mHistoryRecyclerView.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(AdminMehanicHistory.this);
        mHistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new AdminCustomerHistoryAdapater(getDataSetHistory(), AdminMehanicHistory.this);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);
        intent = getIntent();

        final String userid = intent.getStringExtra("userid");
        userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userid).child("history");
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child("ActiveUser");
        reference.setValue(userid);
        getUserHistoryIds();

    }
    private void getUserHistoryIds() {
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot history : dataSnapshot.getChildren()){
                        FetchRideInformation(history.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void FetchRideInformation(String rideKey) {
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("history").child(rideKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String rideId = dataSnapshot.getKey();
                    Long timestamp = 0L;
                    String distance = "";
                    Double ridePrice = 0.0;

                    if(dataSnapshot.child("timestamp").getValue() != null){
                        timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                    }

                    if(dataSnapshot.child("customerPaid").getValue() != null && dataSnapshot.child("driverPaidOut").getValue() == null){
                        if(dataSnapshot.child("distance").getValue() != null){
                            distance = dataSnapshot.child("distance").getValue().toString();
                            ridePrice = (Double.valueOf(distance) * 0.1 + 500);
                            Balance += ridePrice;
                        }
                    }


                    HistoryObject obj = new HistoryObject(rideId, getDate(timestamp));
                    resultsHistory.add(obj);
                    mHistoryAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time*1000);
        String date = DateFormat.format("MM-dd-yyyy hh:mm", cal).toString();
        return date;
    }

    private ArrayList resultsHistory = new ArrayList<HistoryObject>();
    private ArrayList<HistoryObject> getDataSetHistory() {
        return resultsHistory;
    }

    }

