package com.akashkamble.slowgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.akashkamble.slowgram.Adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import Model.User;

public class FollowersActivity extends AppCompatActivity {
    private static final String TAG = "FollowersActivity";

    private String id;
    private String title;
    private List<String> idList;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    private DatabaseReference mRef;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        Toolbar toolbar = findViewById(R.id.tbbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recViewfollowers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(this, mUsers, false);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();
        switch (title) {
            case "followers":
                getFollwers();
                break;
            case "followings":
                getFollowings();
                break;
            case "likes":
                getLikes();
                break;
        }
    }

    private void getFollwers() {
        Log.d(TAG, "getFollwers: getting followers List");
        mRef.child("follow").child(id).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    idList.add(dataSnapshot.getKey());
                }

                showUsers();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getFollowings() {
        Log.d(TAG, "getFollowings: getting Followings");
        mRef.child("follow").child(id).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    idList.add(dataSnapshot.getKey());
                }

                showUsers();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes() {
        Log.d(TAG, "getLikes: getiing Likes List");
        mRef.child("Likes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    idList.add(dataSnapshot.getKey());
                }

                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUsers() {
        Log.d(TAG, "showUsers: showing users......");
        mRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for (String id : idList){
                        if (user.getId().equals(id)){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}