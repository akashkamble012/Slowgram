package com.akashkamble.slowgram.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akashkamble.slowgram.Adapter.PostAdapter;
import com.akashkamble.slowgram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import Model.Post;

public class PostDetailFragment extends Fragment {
    private String postId;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseUser mUser;
    private DatabaseReference mRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
       postId = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("postId","none");
       recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewPost);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       postList = new ArrayList<>();
       postAdapter = new PostAdapter(getContext(), postList);
       recyclerView.setAdapter(postAdapter);
       mUser = FirebaseAuth.getInstance().getCurrentUser();
       mRef = FirebaseDatabase.getInstance().getReference();

       mRef.child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               postList.clear();
               postList.add(snapshot.getValue(Post.class));
               postAdapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

        return view;
    }
}