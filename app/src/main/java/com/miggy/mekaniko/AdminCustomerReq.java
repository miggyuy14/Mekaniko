package com.miggy.mekaniko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class AdminCustomerReq extends AppCompatActivity {

    private String userID, custoLisenceField;
    private ImageView custolicense;
    private Button verifyCusto;
    private DatabaseReference reference, custoReference;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_customer_req);

        custolicense = (ImageView)findViewById(R.id.custoLicense);
        verifyCusto = (Button)findViewById(R.id.validate);
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Admin");
        intent = getIntent();
        final String userid = intent.getStringExtra("userid");

        custoReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userid);
        getCustomerRequirement();
    }

    private void getCustoID(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("ActiveUser") != null) {
                        userID = map.get("ActiveUser").toString();

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getCustomerRequirement(){
        custoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Requirement") != null) {
                        custoLisenceField = map.get("Requirement").toString();
                        Glide.with(getApplication()).load(custoLisenceField).into(custolicense);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
