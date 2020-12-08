package com.akashkamble.slowgram.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.akashkamble.slowgram.Adapter.PhotoAdapter;
import com.akashkamble.slowgram.Adapter.PostAdapter;
import com.akashkamble.slowgram.EditProfileActivity;
import com.akashkamble.slowgram.FollowersActivity;
import com.akashkamble.slowgram.OptionsActivity;
import com.akashkamble.slowgram.R;
import com.akashkamble.slowgram.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.Post;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private TextView userName;
    private ImageView addBtn;
    private ImageView optionsBtn;
    private CircleImageView userProfile;
    private TextView TotalPosts;
    private TextView TotalFollowers;
    private TextView TotalFollowing;
    private TextView fullName;
    private TextView userBio;
    private Button editProfileBtn;
    private ImageButton myPictures;
    private ImageButton savedPictures;

    private RecyclerView recyclerViewPictures;
    private PhotoAdapter photoAdapter;
    private List<Post> mPhotoList;

    private RecyclerView recyclerViewSaved;
    private PhotoAdapter photoAdapterSaved;
    private List<Post> mySavedPosts;
    private  Toolbar toolbar;

    private FirebaseUser fUser;
    private DatabaseReference mRef;

    String profileId;
    




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_profile, container, false);

       fUser = FirebaseAuth.getInstance().getCurrentUser();
       mRef = FirebaseDatabase.getInstance().getReference();

       String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                            .getString("profileId", "none");

       if (data.equals("none")){ profileId = fUser.getUid();}
       else{profileId = data;}

       userName = view.findViewById(R.id.userNameProfileActivity);
       addBtn = view.findViewById(R.id.add);
       optionsBtn = view.findViewById(R.id.options);
       userProfile = view.findViewById(R.id.imageProfileProfileActivity);
       TotalPosts = view.findViewById(R.id.TotalPostsNumber);
       TotalFollowers = view.findViewById(R.id.TotalFollowers);
       TotalFollowing = view.findViewById(R.id.TotalFollowing);
       fullName = view.findViewById(R.id.userFullName);
       userBio = view.findViewById(R.id.bio);
       editProfileBtn = view.findViewById(R.id.editProfileBtn);
       myPictures = view.findViewById(R.id.myPictures);
       savedPictures = view.findViewById(R.id.saved);

       recyclerViewPictures = view.findViewById(R.id.recyclerViewPictures);
       recyclerViewPictures.setHasFixedSize(true);
       recyclerViewPictures.setLayoutManager(new GridLayoutManager(getContext(), 3));
       mPhotoList = new ArrayList<>();
       photoAdapter = new PhotoAdapter(getContext(), mPhotoList);
       
       recyclerViewPictures.setAdapter(photoAdapter);


       recyclerViewSaved = view.findViewById(R.id.recyclerViewSaved );
       recyclerViewSaved.setHasFixedSize(true);
       recyclerViewSaved.setLayoutManager(new GridLayoutManager(getContext(), 3) );
       mySavedPosts = new ArrayList<>();
       
       photoAdapterSaved = new PhotoAdapter(getContext(), mySavedPosts);
       recyclerViewSaved.setAdapter(photoAdapterSaved);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.ToolBarProfile);


        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPhotos();
        getSavedPosts();


        if(profileId.equals(fUser.getUid())){
            editProfileBtn.setText("Edit Profile");
        }else{
            checkFollowingStatus();
        }

        editProfileBtnOnClickListeners();

        recyclerViewPictures.setVisibility(View.VISIBLE);
        recyclerViewSaved.setVisibility(View.GONE);
        
       onClickListenersForPictures();

        TotalFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });
        TotalFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "followings");
                startActivity(intent);
            }
        });
        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });
        addBtn.setVisibility(View.GONE);




       return view;



    }

    private void onClickListenersForPictures() {
        Log.d(TAG, "onClickListenersForPictures: created");
        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewPictures.setVisibility(View.VISIBLE);
                recyclerViewSaved.setVisibility(View.GONE);
            }
        });
        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewPictures.setVisibility(View.GONE);
                recyclerViewSaved.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getSavedPosts() {
        Log.d(TAG, "getSavedPosts: Created");
        List<String> savedIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            savedIds.add(dataSnapshot.getKey());
                        }
                        FirebaseDatabase.getInstance().getReference().child("Posts")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                        mySavedPosts.clear();
                                        for (DataSnapshot snapshot1: dataSnapshot1.getChildren()){
                                            Post post = snapshot1.getValue(Post.class);
                                            for (String id : savedIds){
                                                if (post.getPostid().equals(id)){
                                                    mySavedPosts.add(post);
                                                }
                                            }
                                        }
                                        photoAdapterSaved.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        
    }

    private void myPhotos() {
        Log.d(TAG, "myPhotos: Created");
        mRef.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPhotoList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)){
                        mPhotoList.add(post);
                    }
                }

                Collections.reverse(mPhotoList);
                photoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editProfileBtnOnClickListeners() {
        Log.d(TAG, "editProfileBtnOnClickListeners: Created");
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnTxt = editProfileBtn.getText().toString();
                if (btnTxt.equals("Edit Profile")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else {
                    if (btnTxt.equals("follow")){
                        mRef.child("follow").child(fUser.getUid()).child("following")
                                .child(profileId).setValue(true);

                        mRef.child("follow").child(profileId)
                                .child("followers").child(fUser.getUid()).setValue(true);
                    }else{
                        mRef.child("follow").child(fUser.getUid()).child("following")
                                .child(profileId).removeValue();

                        mRef.child("follow").child(profileId)
                                .child("followers").child(fUser.getUid()).removeValue();

                    }



                }
            }
        });
    }

    private void checkFollowingStatus() {
        mRef.child("follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()){
                    editProfileBtn.setText("Following");
                }else{
                    editProfileBtn.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getPostCount() {
        Log.d(TAG, "getPostCount: Created");
        mRef.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getPublisher().equals(profileId)) {
                        counter++;
                    }
                }
                TotalPosts.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {
        Log.d(TAG, "getFollowersAndFollowingCount: created");
      DatabaseReference ref = mRef.child("follow").child(profileId);
      ref.child("followers").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              TotalFollowers.setText("" + snapshot.getChildrenCount());
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
      ref.child("following").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              TotalFollowing.setText("" + snapshot.getChildrenCount());
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });

    }

    private void userInfo() {
        Log.d(TAG, "userInfo: Created");
        mRef.child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                userName.setText(user.getUserName());
                Picasso.get().load(user.getImageUrl()).into(userProfile);
                fullName.setText(user.getName());
                userBio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}