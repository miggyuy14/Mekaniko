package com.miggy.mekaniko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MechanicManagement extends AppCompatActivity {
    private EditText mechnames, mechemails, mechIDs;

    private String name, email, ID, mechLicenseField, Requirement1Url, CertificateUrl;

    private Intent intent;
    private Button mHistory;
    private DatabaseReference reference, validateref;

    private Button verifymek, mBusinessPermit, mCertificateB;

    private ImageView mechLicense, mRequirement1, mCertificate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_management);
        mCertificate = (ImageView) findViewById(R.id.Requirement2);
        mRequirement1 = (ImageView) findViewById(R.id.Requirement1);
        verifymek = (Button)findViewById(R.id.validate);
        mechLicense = (ImageView)findViewById(R.id.mekLicense);
        mechnames = (EditText)findViewById(R.id.mekname);
        mechemails = (EditText)findViewById(R.id.mekemail);
        mechIDs = (EditText)findViewById(R.id.mekID);
        mHistory = (Button)findViewById(R.id.mekHistory);
        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child("ActiveUser");
        reference.setValue(userid);
        validateref = FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(userid).child("verified");
        verifymek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateref.setValue("Validated");
                Toast.makeText(MechanicManagement.this, "Validation Successful!", Toast.LENGTH_SHORT).show();
            }
        });
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intents = new Intent(MechanicManagement.this, AdminMehanicHistory.class);
                intents.putExtra("userid", userid.toString());
                startActivity(intents);

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        mechnames.setText(name);
                    }
                    if (map.get("email") != null) {
                        email = map.get("email").toString();
                        mechemails.setText(email);
                    }
                    if (map.get("id") != null) {
                        ID = map.get("id").toString();
                        mechIDs.setText(ID);

                    }
                    if (map.get("License") != null) {
                        mechLicenseField = map.get("License").toString();
                        Glide.with(getApplication()).load(mechLicenseField).into(mechLicense);
                    } if (map.get("Requirement") != null) {
                        Requirement1Url = map.get("Requirement").toString();
                        Glide.with(getApplication()).load(Requirement1Url).into(mRequirement1);
                    }if (map.get("Certificate") != null) {
                        CertificateUrl = map.get("Certificate").toString();
                        Glide.with(getApplication()).load(CertificateUrl).into(mCertificate);
                    }else {
                        Toast.makeText(MechanicManagement.this, "no info", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    }

