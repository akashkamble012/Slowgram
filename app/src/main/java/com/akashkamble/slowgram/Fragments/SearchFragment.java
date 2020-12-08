package com.akashkamble.slowgram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akashkamble.slowgram.Adapter.TagAdapter;
import com.akashkamble.slowgram.Adapter.UserAdapter;
import com.akashkamble.slowgram.MainActivity;
import com.akashkamble.slowgram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import Model.User;


public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private RecyclerView recyclerView;
    private SocialAutoCompleteTextView searchBar;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    private RecyclerView recyclerViewTags;
    private List<String> mHashTags;
    private List<String> mHashTagsCount;
    private TagAdapter tagAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);
        searchBar = view.findViewById(R.id.searchBarAuto);

        recyclerViewTags  = view.findViewById(R.id.recyclerViewTags);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));
        mHashTags = new ArrayList<>();
        mHashTagsCount = new ArrayList<>();
        tagAdapter = new TagAdapter(getContext(), mHashTags, mHashTagsCount);
        recyclerViewTags.setAdapter(tagAdapter);

            //TODO: SEARCH FEATURE FOR HASHTAGS NOT WORKING TAKE A LOOK AT IT LATER;


        readUsers();
        readTags();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        return view;
    }

    private void readTags() {
        FirebaseDatabase.getInstance().getReference().child("HashTags")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mHashTags.clear();
                        mHashTagsCount.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            mHashTags.add(dataSnapshot.getKey());
                            mHashTagsCount.add(dataSnapshot.getChildrenCount() + "");
                        }
                        tagAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void filter(String text){
        Log.d(TAG, "filter: created");
        List<String> mSearchTags = new ArrayList<>();
        List<String> mSearchTagsCount = new ArrayList<>();
        for (String s : mSearchTags){
            if (s.toLowerCase().contains(text.toLowerCase())){
                mSearchTags.add(s);
                mSearchTagsCount.add(mHashTagsCount.get(mHashTags.indexOf(s)));
            }
        }
        tagAdapter.filter(mSearchTags, mSearchTagsCount);
    }

    private void readUsers() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TextUtils.isEmpty(searchBar.getText().toString())){
                    mUsers.clear();
                    for (DataSnapshot  snapshot : dataSnapshot.getChildren() ){
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }
                    userAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUsers(String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("userName").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}