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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Objects;

import gparap.apps.social_media.data.PostModel;

public class AddPostActivity extends AppCompatActivity {
    private ImageButton imageButtonPostImage;
    private EditText editTextPostTitle, editTextPostDetails;
    private final static int REQUEST_CODE_GET_POST_IMAGE = 999;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //get widgets
        imageButtonPostImage = findViewById(R.id.imageButtonAddPost);
        editTextPostTitle = findViewById(R.id.editTextAddPostTitle);
        editTextPostDetails = findViewById(R.id.editTextAddPostDetails);
        Button buttonSavePost = findViewById(R.id.buttonSavePost);
        ProgressBar progressBar = findViewById(R.id.progressBarAddPost);

        //update the app bar title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Post");

        //add an image to the post
        imageButtonPostImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_GET_POST_IMAGE);
        });

        //save post (publish)
        buttonSavePost.setOnClickListener(v -> {
            //validate post
            if (!isPostValid()){
                return;
            }

            //show progress
            progressBar.setVisibility(View.VISIBLE);

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

                    //hide progress
                    progressBar.setVisibility(View.INVISIBLE);
                }

                //save post with image
                else {
                    //get a reference to app cloud storage
                    StorageReference cloudRef = FirebaseStorage.getInstance().getReference();

                    //get the last path segment of the image URI
                    // (BUG: getLastPathSegment() don't works as expected most of the times)
                    List<String> stringList = imageUri.getPathSegments();
                    String lastPathSegment = stringList.get(stringList.size() - 1);

                    //get a reference to the user image
                    StorageReference imageRef = cloudRef.child("social_media_app").child(user.getUid()).child(lastPathSegment);

                    //save image to storage
                    StorageTask<UploadTask.TaskSnapshot> storageTask =
                            imageRef.putFile(imageUri).addOnFailureListener(e ->
                                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
                            );

                    //save post
                    storageTask.addOnCompleteListener(task -> task.addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> downloadURL = taskSnapshot.getStorage().getDownloadUrl();
                        downloadURL.addOnCompleteListener(uriTask ->
                                uriTask.addOnSuccessListener(uri -> {
                                    //update blog post with image url
                                    post.setImageUrl(uri.toString().trim());

                                    savePostToDatabase(post);

                                    //hide progress
                                    progressBar.setVisibility(View.INVISIBLE);

                                    //inform user and return to main activity
                                    Toast.makeText(this, "Post published..", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                }).addOnFailureListener(e ->
                                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
                                )
                        );
                    }));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //set the selected image for the post
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GET_POST_IMAGE) {
            if (data != null) {
                imageUri = data.getData();
                imageButtonPostImage.setImageURI(data.getData());
            }
        }
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

    //check is the post title and details are filled-in
    private boolean isPostValid() {
        if (editTextPostTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_empty_post_title), Toast.LENGTH_SHORT).show();
            return false;
        } else if (editTextPostDetails.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_empty_post_details), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}