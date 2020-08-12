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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miggy.mekaniko.Fragments.AdminChatFragment;
import com.miggy.mekaniko.Fragments.AdminUserChat;
import com.miggy.mekaniko.Fragments.ChatsFragment;
import com.miggy.mekaniko.Fragments.UsersFragment;
import com.miggy.mekaniko.Model.User;


import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main2Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    private ImageView displayImage;
    private String userID, displayNameField, displayEmailField, displayImageField;
    private TextView displayName, displayEmail;
    private DatabaseReference displayDatabase;

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dl = findViewById(R.id.dl);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dl, toolbar,
                R.string.open, R.string.close);
        dl.addDrawerListener(toggle);
        toggle.syncState();





        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(firebaseUser.getUid());
        displayName = (TextView) headerView.findViewById(R.id.display_username);
        displayEmail = (TextView) headerView.findViewById(R.id.display_email);
        displayImage = (ImageView) headerView.findViewById(R.id.image_display);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        displayDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);
        getCustomInfo();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());


        viewPagerAdapter.addFragment(new UsersFragment(),"users" );
        viewPagerAdapter.addFragment(new AdminUserChat(),"admins" );

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void getCustomInfo() {
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
                    Toast.makeText(Main2Activity.this, "no info", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(Main2Activity.this, CustomerSettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_history:
                Intent intents = new Intent(Main2Activity.this, HistoryActivity.class);
                intents.putExtra("customerOrDriver", "Customers");
                startActivity(intents);
                break;

            case R.id.nav_chat:
                Intent intentss = new Intent(Main2Activity.this, Main2Activity.class);
                startActivity(intentss);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intentssss = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intentssss);
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

}
