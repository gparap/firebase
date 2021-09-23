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

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import gparap.apps.blog.R;
import gparap.apps.blog.model.BlogPostModel;
import gparap.apps.blog.utils.FirebaseUtils;

public class ViewBlogPostActivity extends AppCompatActivity {
    private BlogPostModel blogPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog_post);

        //get the blog post from firebase and display it
        String blogPostTitle = getIntent().getStringExtra("blog_post_title");
        FirebaseUtils.getInstance().getBlogPostQueryByTitle(blogPostTitle).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get the blog post (it's ok, we only have one child)
                        for (DataSnapshot child : snapshot.getChildren()) {
                            blogPost = child.getValue(BlogPostModel.class);
                            break;
                        }
                        //display the blog post
                        ImageView image = findViewById(R.id.imageViewBlogPost);
                        if (!blogPost.getImageUrl().isEmpty()) {
                            Picasso.with(ViewBlogPostActivity.this).load(blogPost.getImageUrl()).into(image);
                        }
                        TextView title = findViewById(R.id.textViewPostTitle);
                        title.setText(blogPost.getTitle());
                        TextView details = findViewById(R.id.textViewPostDetails);
                        details.setText(blogPost.getDetails());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }
}