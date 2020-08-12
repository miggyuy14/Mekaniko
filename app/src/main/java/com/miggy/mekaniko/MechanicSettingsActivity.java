package com.miggy.mekaniko;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MechanicSettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private EditText mNameField, mPhoneField;

    private Button mBack, mConfirm, mBusinessPermit, mCertificateB;

    GoogleApiClient mGoogleApiClient;

    private ImageView mProfileImage, mRequirement1, mCertificate;

    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;

    private String userID;
    private String mName;
    private String mPhone;
    private String mCar;




    private StorageReference mStorage;
    private String mProfileImageUrl, Requirement1Url, CertificateUrl;

    private  Uri resultUri ,resultUri2, resultUri3;

    private TextView mValidId;

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    private ImageView displayImage;
    private String displayNameField, displayEmailField, displayImageField;
    private TextView displayName, displayEmail;
    private DatabaseReference displayDatabase, reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_settings);

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);

        mValidId = (TextView) findViewById(R.id.validid);

        mCertificate = (ImageView) findViewById(R.id.Requirement2);
        mRequirement1 = (ImageView) findViewById(R.id.Requirement1);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        mBusinessPermit = (Button) findViewById(R.id.businesspermit);
        mCertificateB = (Button) findViewById(R.id.Certificate);
        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dl = findViewById(R.id.dl);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dl, toolbar,
                R.string.open, R.string.close);
        dl.addDrawerListener(toggle);
        toggle.syncState();

        mStorage = FirebaseStorage.getInstance().getReference();






        //RecyclerView



        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        displayDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);
        displayName = (TextView) headerView.findViewById(R.id.display_username);
        displayEmail = (TextView) headerView.findViewById(R.id.display_email);
        displayImage = (ImageView) headerView.findViewById(R.id.image_display);
        getUserInfo();
        getmechinfo();
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mBusinessPermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
        mCertificateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });

    }

    private void getmechinfo() {
        displayDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("username") != null) {
                        displayNameField = map.get("username").toString();
                        displayName.setText(displayNameField);
                    }
                    if (map.get("email") != null) {
                        displayEmailField = map.get("email").toString();
                        displayEmail.setText(displayEmailField);
                    }
                    if (map.get("profileImageUrl") != null) {
                        displayImageField = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(displayImageField).into(displayImage);
                    }
                } else {
                    Toast.makeText(MechanicSettingsActivity.this, "no info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_setting:
                Intent intent = new Intent(MechanicSettingsActivity.this, CustomerSettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_history:
                Intent intents = new Intent(MechanicSettingsActivity.this, HistoryActivity.class);
                intents.putExtra("customerOrDriver", "Drivers");
                startActivity(intents);
                break;

            case R.id.nav_chat:
                Intent intentss = new Intent(MechanicSettingsActivity.this, MechChatActivity.class);
                startActivity(intentss);
                break;

            case R.id.logout:
                disconnectDriver();

                FirebaseAuth.getInstance().signOut();
                Intent intentsss = new Intent(MechanicSettingsActivity.this, MainActivity.class);
                startActivity(intentsss);
                finish();
                break;
        }

        return true;
    }

    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void disconnectDriver() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("mechsAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }


    private void getUserInfo() {
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                    }
                    if (map.get("phone") != null) {
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                    if (map.get("profileImageUrl") != null) {
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                        mValidId.setVisibility(View.GONE);
                    }if (map.get("Requirement") != null) {
                        Requirement1Url = map.get("Requirement").toString();
                        Glide.with(getApplication()).load(Requirement1Url).into(mRequirement1);
                    }if (map.get("Certificate") != null) {
                        CertificateUrl = map.get("Certificate").toString();
                        Glide.with(getApplication()).load(CertificateUrl).into(mCertificate);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();


        Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);


        mDriverDatabase.updateChildren(userInfo);

        if (resultUri != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", downloadUrl.toString());
                    mDriverDatabase.updateChildren(newImage);

                    finish();
                    return;
                }
            });
        } if (resultUri2 != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Business_Permit").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri2);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put("Requirement", downloadUrl.toString());
                    mDriverDatabase.updateChildren(newImage);

                    finish();
                    return;
                }
            });
        }
        if (resultUri3 != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Certificate").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri3);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put("Certificate", downloadUrl.toString());
                    mDriverDatabase.updateChildren(newImage);

                    finish();
                    return;
                }
            });
        }else {
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri2 = imageUri;
            mRequirement1.setImageURI(resultUri2);
        }
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri3 = imageUri;
            mRequirement1.setImageURI(resultUri3);
        }
        }



}