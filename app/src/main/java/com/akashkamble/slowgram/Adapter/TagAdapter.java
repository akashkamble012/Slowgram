package com.akashkamble.slowgram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akashkamble.slowgram.R;

import java.lang.invoke.LambdaConversionException;
import java.net.UnknownServiceException;
import java.util.ConcurrentModificationException;
import java.util.List;

public class TagAdapter  extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mTags;
    private List<String> mTagsCount;

    public TagAdapter(Context mContext, List<String> mTags, List<String> mTagsCount) {
        this.mContext = mContext;
        this.mTags = mTags;
        this.mTagsCount = mTagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item, parent, false);

        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tag.setText("#" + mTags.get(position));
        holder.postsCount.setText(mTagsCount.get(position) + " posts");

    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "ViewHolder";

        private TextView tag, postsCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.hash_tag);
            postsCount = itemView.findViewById(R.id.numberOfPosts);
        }
    }

    public void filter(List<String> filterTags, List<String> filterTagsCount){
        this.mTags = filterTags;
        this.mTagsCount = filterTagsCount;
        notifyDataSetChanged();
    }
}
