package com.miggy.mekaniko.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miggy.mekaniko.AdminMessageActivity;
import com.miggy.mekaniko.MessageActivity;
import com.miggy.mekaniko.Model.User;
import com.miggy.mekaniko.R;

import java.util.List;

/**
 * Created by miggy on 11/14/2019.
 */

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUsers;

    public AdminAdapter(Context mContext, List<User> mUsers){

        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdminAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new AdminAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAdapter.ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AdminMessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;



        public ViewHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.username);
            profile_image = (ImageView) itemView.findViewById(R.id.profile_image);
        }
    }
}
