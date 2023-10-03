package gparap.apps.social_media.adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gparap.apps.social_media.R;
import gparap.apps.social_media.data.PostModel;
import gparap.apps.social_media.data.UserModel;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private ArrayList<PostModel> postsList = new ArrayList<>();

    public ArrayList<PostModel> getPostsList() {
        return postsList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPostsList(ArrayList<PostModel> postsList) {
        this.postsList = postsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create & return the item view
        View view = LayoutInflater.from(parent.getContext()).inflate(parent.getContext()
                .getResources().getLayout(R.layout.cardview_post), parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        //display post image
        String imageUrl = postsList.get(position).getImageUrl();
        if (!imageUrl.isEmpty()) {
            Uri uri = Uri.parse(postsList.get(position).getImageUrl());
            Picasso.get().load(uri).into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        //display post title & details
        holder.postTitle.setText(postsList.get(position).getTitle());
        holder.postDetails.setText(postsList.get(position).getDetails());

        //TODO: username instead of id
        //display post creator
        holder.postCreator.setText(postsList.get(position).getUserId());
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final ImageView postImage;
        private final TextView postTitle, postDetails, postCreator;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.imageViewPost);
            postTitle = itemView.findViewById(R.id.textViewPostTitle);
            postDetails = itemView.findViewById(R.id.textViewPostDetails);
            postCreator = itemView.findViewById(R.id.textViewPostCreator);
        }
    }
}
