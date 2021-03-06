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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miggy.mekaniko.Adapter.MechAdapter;
import com.miggy.mekaniko.Adapter.MechanicAdminAdapter;
import com.miggy.mekaniko.Adapter.UserAdapater;
import com.miggy.mekaniko.Model.User;
import com.miggy.mekaniko.R;

import java.util.ArrayList;
import java.util.List;


public class MechanicAdminFragment extends Fragment {
    private RecyclerView recyclerView;
    TextView username;
    private MechanicAdminAdapter mechAdapter;
    private List<User> mUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mechanic_admin, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        readUser();
        return view;


    }

    private void readUser() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Drivers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    mUsers.add(user);

                }

                mechAdapter = new MechanicAdminAdapter(getContext(), mUsers);
                recyclerView.setAdapter(mechAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
