package com.miggy.mekaniko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mMechanic, mCustomer, mAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMechanic = (Button) findViewById(R.id.mechanic);
        mCustomer = (Button) findViewById(R.id.customer);
        mAdmin = (Button) findViewById(R.id.admin);

        startService(new Intent(MainActivity.this, onAppKilled.class));

        mMechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MechanicLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdminLogin.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}
