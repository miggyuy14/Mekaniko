package com.miggy.mekaniko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miggy.mekaniko.Adapter.CustomerAdminAdapter;
import com.miggy.mekaniko.Model.User;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerManagenent extends AppCompatActivity {


    private CircleImageView profile_image;
    private TextView username;
    private String userID, displayNameField, displayEmailField, displayImageField;
    private TextView displayName, displayEmail;
    private CustomerAdminAdapter customerAdminAdapter;

    private EditText custonames, custoemails, custoIDs;

    private String name, email, ID, mUsers, custoLisenceField;

    private Intent intent;

    private Button mHistory, mRequirements;

    private DatabaseReference reference, validateref;
    private FirebaseAuth mAuth;
    private CustomerAdminAdapter userAdapater;

    private ImageView custolicense;
    private Button verifyCusto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_managenent);

        custolicense = (ImageView)findViewById(R.id.custoLicense);
        verifyCusto = (Button)findViewById(R.id.validate);
        custonames = (EditText)findViewById(R.id.custoname);
        custoemails = (EditText)findViewById(R.id.custoemail);
        custoIDs = (EditText)findViewById(R.id.custoID);
        mHistory = (Button)findViewById(R.id.custoHistory);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child("ActiveUser");
        reference.setValue(userid);
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intents = new Intent(CustomerManagenent.this, AdminCustomerHistory.class);
                intents.putExtra("userid", userid.toString());
                startActivity(intents);

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userid);
        validateref = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userid).child("verified");
        verifyCusto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateref.setValue("Validated");
                Toast.makeText(CustomerManagenent.this, "Validation Successful!", Toast.LENGTH_SHORT).show();
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        custonames.setText(name);
                    }
                    if (map.get("email") != null) {
                        email = map.get("email").toString();
                        custoemails.setText(email);
                    }
                    if (map.get("id") != null) {
                        ID = map.get("id").toString();
                        custoIDs.setText(ID);

                    }if (map.get("Requirement") != null) {
                        custoLisenceField = map.get("Requirement").toString();
                        Glide.with(getApplication()).load(custoLisenceField).into(custolicense);
                    }
                }else{
                    Toast.makeText(CustomerManagenent.this, "no info", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




}
