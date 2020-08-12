package com.miggy.mekaniko;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.miggy.mekaniko.Fragments.CustomerAdminFragment;
import com.miggy.mekaniko.Fragments.MechFragment;
import com.miggy.mekaniko.Fragments.MechanicAdminFragment;
import com.miggy.mekaniko.Fragments.UsersFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminChat extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());


        viewPagerAdapter.addFragment(new MechFragment(),"Clients" );
        viewPagerAdapter.addFragment(new UsersFragment(),"Mechanics" );


        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);




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

