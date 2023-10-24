/*
 * Copyright 2023 gparap
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
package gparap.apps.social_media;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import gparap.apps.social_media.data.PostModel;
import gparap.apps.social_media.utils.AppConstants;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //get post extras from intent
        PostModel post = null;
        String username = "";
        if (getIntent() != null) {
            post = new PostModel(getIntent().getStringExtra(AppConstants.POST_ID),
                    getIntent().getStringExtra(AppConstants.POST_USER_ID),
                    getIntent().getStringExtra(AppConstants.POST_TITLE),
                    getIntent().getStringExtra(AppConstants.POST_DETAILS),
                    getIntent().getStringExtra(AppConstants.POST_IMAGE_URL));
            username = getIntent().getStringExtra(AppConstants.POST_USER_NAME);
        }

        //display post texts
        assert post != null;
        ((TextView)findViewById(R.id.textViewPostTitle)).setText(post.getTitle());
        ((TextView)findViewById(R.id.textViewPostDetails)).setText(post.getDetails());
        ((TextView)findViewById(R.id.textViewPostCreator)).setText(
                String.format("%s%s", getString(R.string.text_posted_by), username)
        );

        //display post image
        Picasso.get().load(post.getImageUrl()).into(((ImageView)findViewById(R.id.imageViewPost)));

        //if the user that views the post is also its creator, show the related buttons
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getDisplayName(), username)) {
            ImageButton buttonDeletePost = findViewById(R.id.imageButton_deletePost);
            buttonDeletePost.setVisibility(View.VISIBLE);
            ImageButton buttonEditPost = findViewById(R.id.imageButton_editPost);
            buttonEditPost.setVisibility(View.VISIBLE);
        }
    }
}