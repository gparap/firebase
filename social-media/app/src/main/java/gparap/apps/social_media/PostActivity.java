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

import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE_POSTS;
import static gparap.apps.social_media.utils.AppConstants.POST_DETAILS;
import static gparap.apps.social_media.utils.AppConstants.POST_ID;
import static gparap.apps.social_media.utils.AppConstants.POST_IMAGE_STORAGE_ID;
import static gparap.apps.social_media.utils.AppConstants.POST_IMAGE_URL;
import static gparap.apps.social_media.utils.AppConstants.POST_TITLE;
import static gparap.apps.social_media.utils.AppConstants.POST_USER_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import gparap.apps.social_media.data.PostModel;
import gparap.apps.social_media.utils.AppConstants;

public class PostActivity extends AppCompatActivity {
    private PostModel post = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //get post extras from intent
        String username = "";
        if (getIntent() != null) {
            post = new PostModel(getIntent().getStringExtra(AppConstants.POST_ID),
                    getIntent().getStringExtra(AppConstants.POST_USER_ID),
                    getIntent().getStringExtra(AppConstants.POST_TITLE),
                    getIntent().getStringExtra(AppConstants.POST_DETAILS),
                    getIntent().getStringExtra(AppConstants.POST_IMAGE_URL),
                    getIntent().getStringExtra(AppConstants.POST_IMAGE_STORAGE_ID));
            username = getIntent().getStringExtra(AppConstants.POST_USER_NAME);
        }

        //display post texts
        assert post != null;
        ((TextView) findViewById(R.id.textViewPostTitle)).setText(post.getTitle());
        ((TextView) findViewById(R.id.textViewPostDetails)).setText(post.getDetails());
        ((TextView) findViewById(R.id.textViewPostCreator)).setText(
                String.format("%s%s", getString(R.string.text_posted_by), username)
        );

        //display post image TODO: fixed <<if (!post.getImageUrl().isEmpty())>>
        if (!post.getImageUrl().isEmpty()){
            Picasso.get().load(post.getImageUrl()).into(((ImageView) findViewById(R.id.imageViewPost)));
        }

        //if the user that views the post is also its creator, show the related buttons
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getDisplayName(), username)) {
            //delete post
            ImageButton buttonDeletePost = findViewById(R.id.imageButton_deletePost);
            buttonDeletePost.setVisibility(View.VISIBLE);
            buttonDeletePost.setOnClickListener(v -> {
                //display a confirmation dialog
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.text_post_deleted_alert_dialog))
                        .setPositiveButton(getString(R.string.text_ok), (dialog, which) ->
                                FirebaseDatabase.getInstance().getReference().getDatabase()
                                        .getReference(DATABASE_REFERENCE)
                                        .child(DATABASE_REFERENCE_POSTS).child(post.getId())
                                        .removeValue().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(this, getString(R.string.text_post_deleted), Toast.LENGTH_SHORT).show();

                                                //delete post image from cloud storage
                                                if (!post.getImageStorageId().isEmpty()) {
                                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(DATABASE_REFERENCE);
                                                    StorageReference userRef = storageReference.child(post.getUserId());
                                                    StorageReference imageRef = userRef.child(post.getImageStorageId());
                                                    imageRef.delete();
                                                }

                                                //close this activity and return to application posts
                                                this.startActivity(new Intent(this, MainActivity.class));
                                                this.finish();

                                            } else if (task.getException() != null) {
                                                Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }))
                        .setNegativeButton(getString(R.string.text_cancel), (dialog, which) -> dialog.dismiss())
                        .create().show();
            });

            //edit post
            ImageButton buttonEditPost = findViewById(R.id.imageButton_editPost);
            buttonEditPost.setVisibility(View.VISIBLE);
            buttonEditPost.setOnClickListener(v -> {
                //open the post for editing
                Intent intent = new Intent(this, EditPostActivity.class);
                intent.putExtra(POST_ID, post.getId());
                intent.putExtra(POST_USER_ID, post.getUserId());
                intent.putExtra(POST_TITLE, post.getTitle());
                intent.putExtra(POST_DETAILS, post.getDetails());
                intent.putExtra(POST_IMAGE_URL, post.getImageUrl());
                intent.putExtra(POST_IMAGE_STORAGE_ID, post.getImageStorageId());
                startActivity(intent);
            });
        }
    }
}