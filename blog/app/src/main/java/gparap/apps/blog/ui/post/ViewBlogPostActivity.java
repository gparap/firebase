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
package gparap.apps.blog.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import gparap.apps.blog.MainActivity;
import gparap.apps.blog.R;
import gparap.apps.blog.model.BlogPostModel;
import gparap.apps.blog.utils.FirebaseUtils;

public class ViewBlogPostActivity extends AppCompatActivity {
    private BlogPostModel blogPost;
    private String blogPostKey;
    private ImageButton thumbUp;
    private boolean isThumbUp = false;
    private ImageButton deleteBlogPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog_post);

        //hide both image buttons
        thumbUp = findViewById(R.id.imageButtonThumbUp);
        thumbUp.setVisibility(View.GONE);
        deleteBlogPost = findViewById(R.id.imageButtonDeleteBlogPost);
        deleteBlogPost.setVisibility(View.GONE);

        //get the blog post from firebase and display it
        String blogPostTitle = getIntent().getStringExtra("blog_post_title");
        FirebaseUtils.getInstance().getBlogPostQueryByTitle(blogPostTitle).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get the blog post (it's ok, we only have one child)
                        for (DataSnapshot child : snapshot.getChildren()) {
                            blogPost = child.getValue(BlogPostModel.class);
                            blogPostKey = child.getKey();

                            //display the blog post
                            ImageView image = findViewById(R.id.imageViewBlogPost);
                            if (!blogPost.getImageUrl().isEmpty()) {
                                Picasso.with(ViewBlogPostActivity.this).load(blogPost.getImageUrl()).into(image);
                            }
                            TextView title = findViewById(R.id.textViewPostTitle);
                            title.setText(blogPost.getTitle());
                            TextView details = findViewById(R.id.textViewPostDetails);
                            details.setText(blogPost.getDetails());

                            //based on the current user, display the thumb-up or the delete button
                            //(if current user is the blog post's writer display the delete button)
                            //(if current user is the not blog post's writer display the thumb up button)
                            if (blogPost.getUserId().equals(FirebaseUtils.getInstance().getUser().getUid())) {
                                //delete the blog post and return to blog
                                deleteBlogPost.setVisibility(View.VISIBLE);
                                thumbUp.setVisibility(View.GONE);
                                deleteBlogPost.setOnClickListener(view -> {
                                    FirebaseUtils.getInstance().deleteBlogPost(blogPostKey);
                                    startActivity(new Intent(ViewBlogPostActivity.this, MainActivity.class));
                                });

                            } else {
                                //set blog post's thumb-up image color for the current user
                                thumbUp.setVisibility(View.VISIBLE);
                                deleteBlogPost.setVisibility(View.GONE);
                                FirebaseUtils.getInstance().getThumbUpInfo(blogPostKey, FirebaseUtils.getInstance().getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        setThumbUpState(snapshot.getValue() != null);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                //handle this post's thumb-up info
                                thumbUp.setOnClickListener(view -> {
                                            FirebaseUtils.getInstance().saveThumbUpInfoToDatabase(blogPostKey, FirebaseUtils.getInstance().getUser().getUid());
                                            setThumbUpState(!isThumbUp);
                                        }
                                );
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void setThumbUpState(boolean isActive) {
        if (isActive) {
            thumbUp.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_thumb_up_activated_24));
            isThumbUp = true;
        } else {
            thumbUp.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_thumb_up_default_24));
            isThumbUp = false;
        }
    }
}