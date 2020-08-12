package com.miggy.mekaniko;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miggy.mekaniko.Fragments.AdminUserChat;
import com.miggy.mekaniko.Fragments.ChatsFragment;
import com.miggy.mekaniko.Fragments.MechFragment;
import com.miggy.mekaniko.Model.User;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MechChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    CircleImageView profile_image;
    TextView username;
    private ImageView displayImage;
    private String userID, displayNameField, displayEmailField, displayImageField;
    private TextView displayName, displayEmail;
    private DatabaseReference displayDatabase;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private DrawerLayout dl;
    private FirebaseAuth mAuth;

    private ActionBarDrawerToggle abdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_chat);
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

        displayName = (TextView) headerView.findViewById(R.id.display_username);
        displayEmail = (TextView) headerView.findViewById(R.id.display_email);
        displayImage = (ImageView) headerView.findViewById(R.id.image_display);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        displayDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

        getmechinfo();


        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        username = (TextView) findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user =  dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(MechChatActivity.this).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        MechChatActivity.ViewPagerAdapter viewPagerAdapter = new MechChatActivity.ViewPagerAdapter(getSupportFragmentManager());


        viewPagerAdapter.addFragment(new MechFragment(),"users" );
        viewPagerAdapter.addFragment(new AdminUserChat(),"admins" );

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {


        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
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
                }else{
                    Toast.makeText(MechChatActivity.this, "no info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_setting:
                Intent intent = new Intent(MechChatActivity.this, CustomerSettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_history:
                Intent intents = new Intent(MechChatActivity.this, HistoryActivity.class);
                intents.putExtra("customerOrDriver", "Drivers");
                startActivity(intents);
                break;

            case R.id.nav_chat:
                Intent intentss = new Intent(MechChatActivity.this, MechChatActivity.class);
                startActivity(intentss);
                break;

            case R.id.logout:
                disconnectDriver();

                FirebaseAuth.getInstance().signOut();
                Intent intentsss = new Intent(MechChatActivity.this, MainActivity.class);
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

}