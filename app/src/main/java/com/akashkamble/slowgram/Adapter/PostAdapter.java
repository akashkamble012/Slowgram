package com.akashkamble.slowgram.Adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.akashkamble.slowgram.CommentActivity;
import com.akashkamble.slowgram.FollowersActivity;
import com.akashkamble.slowgram.Fragments.PostDetailFragment;
import com.akashkamble.slowgram.Fragments.ProfileFragment;
import com.akashkamble.slowgram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.Post;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private static final String TAG = "PostAdapter";
    private Context mContext;
    private List<Post> mPosts;
    private String postId;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Post post = mPosts.get(position);
        Picasso.get().load(post.getImageurl()).into(holder.postImage);
        holder.postItemDescription.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getImageUrl().equals("default")) {
                    holder.profileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.profileImage);
                }
                holder.postUserName.setText(user.getUserName());
                holder.author.setText(user.getName());

                isLiked(post.getPostid(), holder.likeBtn);
                numberOfLikes(post.getPostid(), holder.numberOfLikes);
                numberOfComments(post.getPostid(), holder.numberOfComments);
                isSaved(post.getPostid(), holder.saveBtn);

                holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.likeBtn.getTag().equals("like")) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                    .child(firebaseUser.getUid()).setValue(true);
                            sendNotification(post.getPostid(), post.getPublisher());

                        }else{
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                    .child(firebaseUser.getUid()).removeValue();
                        }
                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostid());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.numberOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostid());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.saveBtn.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);

                }else{
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }
            }
        });
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postId", post.getPostid()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

        holder.numberOfLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPublisher());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);
            }
        });
    }

    private void sendNotification(String postid, String publisher) {
        Log.d(TAG, "sendNotification: sending Notification");
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", publisher);
        map.put("text", "Liked your post");
        map.put("postId", postid);
        map.put("isPost", true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid())
                .push().setValue(map);

    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "ViewHolder";
        private ImageView more;
        private CircleImageView profileImage;
        private TextView postUserName;
        private ImageView postImage;
        private ImageView likeBtn, commentBtn, saveBtn;
        private TextView numberOfLikes, numberOfComments;
        private TextView author;
        private SocialTextView postItemDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            more = itemView.findViewById(R.id.more);
            profileImage = itemView.findViewById(R.id.profileImage);
            postUserName = itemView.findViewById(R.id.postUserName);
            postImage = itemView.findViewById(R.id.postImage);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            saveBtn = itemView.findViewById(R.id.saveBtn);
            numberOfComments = itemView.findViewById(R.id.numberOfComments);
            numberOfLikes = itemView.findViewById(R.id.numberOfLikes);
            author = itemView.findViewById(R.id.author);
            postItemDescription = itemView.findViewById(R.id.postItemDescription);



        }
    }
    private void isLiked(String postId, ImageView imageView){
        Log.d(TAG, "isLiked: Created");
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(firebaseUser.getUid()).exists()){
                            imageView.setImageResource(R.drawable.ic_liked);
                            imageView.setTag("liked");
                        }else {
                            imageView.setImageResource(R.drawable.ic_like);
                            imageView.setTag("like");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void numberOfLikes(String postId, TextView textView){
        Log.d(TAG, "numberOfLikes: initialized");
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        textView.setText(snapshot.getChildrenCount() +   " likes");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void numberOfComments (String postId, TextView textView) {
        Log.d(TAG, "numberOfComments: Created");
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        textView.setText("view all " + snapshot.getChildrenCount() + " comments");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void isSaved(String postid, ImageView image) {
        Log.d(TAG, "isSaved: Created");
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(postid).exists()) {
                            image.setImageResource(R.drawable.ic_saved);
                            image.setTag("saved");
                        }else {
                            image.setImageResource(R.drawable.ic_save);
                            image.setTag("save");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
