package gparap.apps.social_media.adapters;

import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE_USERS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gparap.apps.social_media.R;
import gparap.apps.social_media.data.PostModel;
import gparap.apps.social_media.data.UserModel;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private ArrayList<PostModel> postsList = new ArrayList<>();
    private Context context;

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
        this.context = parent.getContext();

        //create & return the item view
        View view = LayoutInflater.from(parent.getContext()).inflate(parent.getContext()
                .getResources().getLayout(R.layout.cardview_post_thumbnail), parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        //display post image
        String imageUrl = postsList.get(position).getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Uri uri = Uri.parse(postsList.get(position).getImageUrl());
            Picasso.get().load(uri).into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        //display post title & details
        holder.postTitle.setText(postsList.get(position).getTitle());
        holder.postDetails.setText(postsList.get(position).getDetails());

        //get the FirebaseDatabase instance for the specified URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //get the username based on the user id
        DatabaseReference usersRef = firebaseDatabase.getReference(DATABASE_REFERENCE).child(DATABASE_REFERENCE_USERS).child(postsList.get(position).getUserId());
            Task<DataSnapshot> userSnapshot = usersRef.get();
            userSnapshot.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    UserModel user = task.getResult().getValue(UserModel.class);
                    if (user != null){
                        //display post creator
                        holder.postCreator.setText(String.format("%s%s",
                                context.getString(R.string.text_posted_by), user.getUsername()));
                    }
                }
            });
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
            postImage = itemView.findViewById(R.id.imageViewPost_thumbnail);
            postTitle = itemView.findViewById(R.id.textViewPostTitle_thumbnail);
            postDetails = itemView.findViewById(R.id.textViewPostDetails_thumbnail);
            postCreator = itemView.findViewById(R.id.textViewPostCreator_thumbnail);
        }
    }
}
