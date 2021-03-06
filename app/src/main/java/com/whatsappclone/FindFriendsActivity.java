package com.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mtoolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        FindFriendsRecyclerList= findViewById(R.id.find_friends_recycle_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        mtoolbar= findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UsersRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder findFriendViewHolder, final int i, @NonNull Contacts contacts)
            {
                findFriendViewHolder.userName.setText(contacts.getName());
                findFriendViewHolder.userStatus.setText(contacts.getStatus());
                Picasso.get().load(contacts.getImage()).into(findFriendViewHolder.profileimage);

                findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String visit_user_id= getRef(i).getKey();

                        Intent profileintent= new Intent(FindFriendsActivity.this,ProfileActivity.class);
                        profileintent.putExtra("Visit_User_ID", visit_user_id);
                        startActivity(profileintent);
                    }
                });


            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
               FindFriendViewHolder viewHolder= new FindFriendViewHolder(view);
               return viewHolder;
            }
        };
        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView  profileimage;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userName= itemView.findViewById(R.id.user_profile_name);
            userStatus= itemView.findViewById(R.id.user_status);
            profileimage= itemView.findViewById(R.id.users_profile_image);

        }
    }
}
