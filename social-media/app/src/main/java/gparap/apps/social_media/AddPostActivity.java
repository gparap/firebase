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
 */package gparap.apps.social_media;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import gparap.apps.social_media.data.PostModel;

public class AddPostActivity extends AppCompatActivity {
    private EditText editTextPostTitle, editTextPostDetails;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //get widgets
        editTextPostTitle = findViewById(R.id.editTextAddPostTitle);
        editTextPostDetails = findViewById(R.id.editTextAddPostDetails);
        Button buttonSavePost = findViewById(R.id.buttonSavePost);

        //update the app bar title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Post");

        //save post (publish)
        buttonSavePost.setOnClickListener(v -> {
            //TODO: validate post

            //get the current user
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                //create post
                PostModel post = new PostModel("",
                        user.getUid(),
                        editTextPostTitle.getText().toString().trim(),
                        editTextPostDetails.getText().toString().trim(),
                        user.getDisplayName()
                );

                //save post without image
                if (imageUri == null) {
                    savePostToDatabase(post);

                    //return to main activity
                    startActivity(new Intent(this, MainActivity.class));
                }
            }
        });
    }

    private void savePostToDatabase(PostModel post) {
        //get the FirebaseDatabase instance for the specified URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //...get the DatabaseReference for the database root node
        DatabaseReference postsRef = firebaseDatabase.getReference("social_media_app").child("posts").push();

        //write data to the database
        postsRef.child("id").setValue(postsRef.getKey());
        postsRef.child("userId").setValue(post.getUserId());
        postsRef.child("title").setValue(post.getTitle());
        postsRef.child("details").setValue(post.getDetails());
        postsRef.child("imageUrl").setValue(post.getImageUrl());
    }
}