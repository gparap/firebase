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

import static gparap.apps.social_media.utils.AppConstants.APP_BAR_TITLE_ADD_POST;
import static gparap.apps.social_media.utils.AppConstants.CONTENT_VALUE_AUTHOR;
import static gparap.apps.social_media.utils.AppConstants.CONTENT_VALUE_DESCRIPTION;
import static gparap.apps.social_media.utils.AppConstants.CONTENT_VALUE_TITLE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_DETAILS;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_ID;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_IMAGE_URL;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_TITLE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_USER_ID;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE_POSTS;
import static gparap.apps.social_media.utils.AppConstants.MIME_TYPE_IMAGE;
import static gparap.apps.social_media.utils.AppConstants.PERMISSION_CAMERA;
import static gparap.apps.social_media.utils.AppConstants.PERMISSION_WRITE_EXTERNAL_STORAGE;
import static gparap.apps.social_media.utils.AppConstants.REQUEST_CODE_CAMERA_PERMISSION;
import static gparap.apps.social_media.utils.AppConstants.REQUEST_CODE_CAPTURE_POST_IMAGE;
import static gparap.apps.social_media.utils.AppConstants.REQUEST_CODE_GET_POST_IMAGE;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

@SuppressWarnings("deprecation")
public class AddPostActivity extends AppCompatActivity {
    private ImageButton imageButtonPostImage;
    private EditText editTextPostTitle, editTextPostDetails;
    private Uri imageUri;
    private Boolean isUsingImageFromGallery = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //get widgets
        imageButtonPostImage = findViewById(R.id.imageViewPost_thumbnail);
        editTextPostTitle = findViewById(R.id.textViewPostTitle_thumbnail);
        editTextPostDetails = findViewById(R.id.textViewPostDetails_thumbnail);
        Button buttonSavePost = findViewById(R.id.buttonSavePost);
        ProgressBar progressBar = findViewById(R.id.progressBarAddPost);

        //update the app bar title
        Objects.requireNonNull(getSupportActionBar()).setTitle(APP_BAR_TITLE_ADD_POST);

        //add an image to the post
        imageButtonPostImage.setOnClickListener(v -> {
            //using the device image gallery
            if (isUsingImageFromGallery) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(MIME_TYPE_IMAGE);
                startActivityForResult(intent, REQUEST_CODE_GET_POST_IMAGE);
            }

            //using the device camera
            else {
                //request camera permission
                if ((ContextCompat.checkSelfPermission(this, PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, PERMISSION_WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

                    //add captured image values
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.Media.TITLE, editTextPostTitle.getText().toString());
                    contentValues.put(MediaStore.Images.Media.DESCRIPTION, editTextPostDetails.getText().toString());
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    //capture image
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        startActivityForResult(intent, REQUEST_CODE_CAPTURE_POST_IMAGE);
                    } catch (Exception ignored) {
                    }

                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_CAMERA_PERMISSION);
                }
            }
        });

        //save post (publish)
        buttonSavePost.setOnClickListener(v -> {
            //validate post
            if (!isPostValid()) {
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
                    StorageReference imageRef = cloudRef.child(DATABASE_REFERENCE).child(user.getUid()).child(lastPathSegment);

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
                                    Toast.makeText(this, getResources().getString(R.string.toast_post_published), Toast.LENGTH_SHORT).show();
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

        //set the captured image for the post
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAPTURE_POST_IMAGE) {
            if (data != null) {
                imageButtonPostImage.setImageURI(imageUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE, CONTENT_VALUE_TITLE);             //TODO: add the details
            contentValues.put(MediaStore.Images.Media.DESCRIPTION, CONTENT_VALUE_DESCRIPTION); //  of the post here and
            contentValues.put(MediaStore.Images.Media.AUTHOR, CONTENT_VALUE_AUTHOR);           //  the user id
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            try {
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_POST_IMAGE);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_post_menu_use_gallery) {
            //set image from gallery checked
            item.setChecked(true);
            isUsingImageFromGallery = true;
        } else {
            //set image from camera checked
            item.setChecked(true);
            isUsingImageFromGallery = false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePostToDatabase(PostModel post) {
        //get the FirebaseDatabase instance for the specified URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //...get the DatabaseReference for the database root node
        DatabaseReference postsRef = firebaseDatabase.getReference(DATABASE_REFERENCE).child(DATABASE_REFERENCE_POSTS).push();

        //write data to the database
        postsRef.child(DATABASE_FIELD_POST_ID).setValue(postsRef.getKey());
        postsRef.child(DATABASE_FIELD_POST_USER_ID).setValue(post.getUserId());
        postsRef.child(DATABASE_FIELD_POST_TITLE).setValue(post.getTitle());
        postsRef.child(DATABASE_FIELD_POST_DETAILS).setValue(post.getDetails());
        if (post.getImageUrl() == null) {
            postsRef.child(DATABASE_FIELD_POST_IMAGE_URL).setValue("");
        } else {
            postsRef.child(DATABASE_FIELD_POST_IMAGE_URL).setValue(post.getImageUrl());
        }
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