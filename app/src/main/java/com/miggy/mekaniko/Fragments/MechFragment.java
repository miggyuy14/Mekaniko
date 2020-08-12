package com.miggy.mekaniko.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miggy.mekaniko.Adapter.MechAdapter;

import com.miggy.mekaniko.Model.User;
import com.miggy.mekaniko.R;

import java.util.ArrayList;
import java.util.List;


public class MechFragment extends Fragment {

    private RecyclerView recyclerView;

    private MechAdapter mechAdapater;
    private List<User> mUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_mech, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        readUser();
        return view;


    }

    private void readUser() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Customers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    mUsers.add(user);

                }

                mechAdapater = new MechAdapter(getContext(), mUsers);
                recyclerView.setAdapter(mechAdapater);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}


