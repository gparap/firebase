/*
 * Copyright 2021 gparap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gparap.apps.blog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import gparap.apps.blog.R;
import gparap.apps.blog.model.BlogPostModel;

public class BlogPostAdapter extends FirebaseRecyclerAdapter<BlogPostModel, BlogPostAdapter.BlogPostViewHolder> {
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Initializes an Adapter that listens to a Firebase query
     *
     * @param options options to configure a FirebaseRecyclerAdapter
     */
    public BlogPostAdapter(@NonNull @NotNull FirebaseRecyclerOptions<BlogPostModel> options) {
        super(options);
    }

    /**
     * Binds blog post item views with the FirebaseRecyclerAdapter.
     *
     * @param blogPostViewHolder a blog post item ViewHolder
     * @param i                  the item's position in RecyclerView
     * @param model              a blog post object
     */
    @Override
    protected void onBindViewHolder(@NonNull @NotNull BlogPostAdapter.BlogPostViewHolder blogPostViewHolder, int i, @NonNull @NotNull BlogPostModel model) {
        blogPostViewHolder.setPostImage(model.getImageUrl(), context);
        blogPostViewHolder.setPostTitle(model.getTitle());
        blogPostViewHolder.setPostDetails(model.getDetails());
        blogPostViewHolder.setPostblogger(model.getUsername());
    }

    @NonNull
    @NotNull
    @Override
    public BlogPostViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //inflate and return the cardview
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_blog_post, parent, false);
        return new BlogPostViewHolder(view);
    }

    /**
     * Describes a blog post item view and metadata about its place within the RecyclerView.
     */
    public static class BlogPostViewHolder extends RecyclerView.ViewHolder {
        private final ImageView postImage;
        private final TextView postTitle;
        private final TextView postDetails;
        private final TextView postblogger;

        public BlogPostViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            //get item views
            postImage = itemView.findViewById(R.id.cardview_post_image);
            postTitle = itemView.findViewById(R.id.cardview_post_title);
            postDetails = itemView.findViewById(R.id.cardview_post_details);
            postblogger = itemView.findViewById(R.id.cardview_post_username);
        }

        public void setPostImage(String imageUrl, Context context) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.with(context).load(imageUrl).into(postImage);
            }
        }

        public void setPostTitle(String title) {
            postTitle.setText(title);
        }

        public void setPostDetails(String details) {
            postDetails.setText(details);
        }

        public void setPostblogger(String blogger) {
            postblogger.setText(blogger);
        }
    }
}
