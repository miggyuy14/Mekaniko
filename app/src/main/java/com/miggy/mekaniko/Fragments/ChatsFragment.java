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
import com.miggy.mekaniko.Adapter.UserAdapater;
import com.miggy.mekaniko.Model.Chat;
import com.miggy.mekaniko.Model.User;
import com.miggy.mekaniko.R;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;

    private UserAdapater userAdapater;
    private List<User> mUsers;

    private FirebaseUser fuser;
    private DatabaseReference reference;

    private List<String> userList;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if(chat.getSender().equals(fuser.getUid())){
                        userList.add(chat.getReciever());
                    }if(chat.getReciever().equals(fuser.getUid())){
                        userList.add(chat.getSender());
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void readChats(){
        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    for (String id : userList) {
                        if (user.getId().equals(id)) {
                            if (mUsers.size() != 0) {
                                for (User userl : mUsers) {
                                    if (!user.getId().equals(userl.getId())) {
                                        mUsers.add(user);
                                    }
                                }

                            } else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapater =  new UserAdapater(getContext(),mUsers);
                recyclerView.setAdapter(userAdapater);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
