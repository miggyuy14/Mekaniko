package com.miggy.mekaniko;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MechanicLoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mEmail, mPassword;
    private Button mLogin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseauthlistener;
    private String customerOrDriver;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        mAuth = FirebaseAuth.getInstance();


        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);


        findViewById(R.id.PasswordReset).setOnClickListener(this);
        findViewById(R.id.registration).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);

    }

    private void userLogin() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (email.isEmpty()) {
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Please enter a valid email");
            mEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mPassword.setError("Password is required");
            mPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mPassword.setError("Minimum lenght of password should be 6");
            mPassword.requestFocus();
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                        Intent intent = new Intent(MechanicLoginActivity.this, MechanicMapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();



                }else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MechanicMapActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registration:
                finish();
                startActivity(new Intent(this, RegisterActivity2.class));
                break;

            case R.id.login:
                userLogin();
                break;

            case R.id.PasswordReset:
                finish();
                startActivity(new Intent(this, PasswordResetActivity.class));
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseauthlistener);
    }

}
