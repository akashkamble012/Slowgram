package com.akashkamble.slowgram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.akashkamble.slowgram.Adapter.PostAdapter;
import com.akashkamble.slowgram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.Post;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerViewPost;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followingList;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerViewPost = view.findViewById(R.id.recyclerVIewPosts);
        recyclerViewPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPost.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);

        recyclerViewPost.setAdapter(postAdapter);

        followingList = new ArrayList<>();
        checkFollowingUser();
        return view;
    }

    private void checkFollowingUser() {
        Log.d(TAG, "checkFollowingUser: Created");
        FirebaseDatabase.getInstance().getReference().child("follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followingList.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            followingList.add(dataSnapshot.getKey());
                        }
                        followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        readPosts();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void readPosts() {
        Log.d(TAG, "readPosts: created");
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postList.clear();
                        for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                            Post post = dataSnapshot.getValue(Post.class);

                            for (String id : followingList){
                                if (post.getPublisher().equals(id)){
                                    postList.add(post);
                                }
                            }

                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}