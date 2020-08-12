package com.miggy.mekaniko;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminRegister extends AppCompatActivity {
    private EditText usernames, emails, passwords;
    private Button btn_registers;
    private ImageView licenses;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private DatabaseReference reference2;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);


        usernames =  findViewById(R.id.usernamed);
        emails = findViewById(R.id.email);
        passwords = findViewById(R.id.password);
        btn_registers = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();



        btn_registers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = usernames.getText().toString();
                String txt_email = emails.getText().toString();
                String txt_password = passwords.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(AdminRegister.this, "All Fields Requierd", Toast.LENGTH_SHORT).show();

                }else if (txt_password.length() < 6){
                    Toast.makeText(AdminRegister.this, "Password needs to be 6 or more characters", Toast.LENGTH_SHORT).show();

                }
                else{
                    register(txt_username, txt_email, txt_password);
                }
            }
        });

    }
    private void register (final String username, final String email, String password){

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child("Admin").child(userid);
                    HashMap<String, String> hashsMap = new HashMap<>();


                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("email", email);
                    hashMap.put("username", username);
                    hashMap.put("imageURL", "default");
                    hashMap.put("UserType", "1");







                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(AdminRegister.this, AdminPanelActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });



                }else{
                    Toast.makeText(AdminRegister.this, "Unable to register", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
