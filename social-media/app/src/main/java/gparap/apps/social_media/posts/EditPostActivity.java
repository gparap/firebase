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
package gparap.apps.social_media.posts;

import static gparap.apps.social_media.utils.AppConstants.CONTENT_VALUE_AUTHOR;
import static gparap.apps.social_media.utils.AppConstants.CONTENT_VALUE_DESCRIPTION;
import static gparap.apps.social_media.utils.AppConstants.CONTENT_VALUE_TITLE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_DETAILS;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_IMAGE_STORAGE_ID;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_IMAGE_URL;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_TITLE;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import gparap.apps.social_media.MainActivity;
import gparap.apps.social_media.R;
import gparap.apps.social_media.data.PostModel;
import gparap.apps.social_media.utils.AppConstants;

public class EditPostActivity extends AppCompatActivity {
    private PostModel post = null;
    private EditText editTextPostTitle;
    private EditText editTextPostDetails;
    private ImageButton imageButtonEditPost;
    private ProgressBar progressBar;
    private Uri imageUri;
    private Boolean isUsingImageFromGallery = true;
    String lastPathSegment = null;  //the decoded last path segment of an image uri

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        //get post extras from intent
        if (getIntent() != null) {
            post = new PostModel.Builder()
                    .setId(getIntent().getStringExtra(AppConstants.POST_ID))
                    .setUserId(getIntent().getStringExtra(AppConstants.POST_USER_ID))
                    .setTitle(getIntent().getStringExtra(AppConstants.POST_TITLE))
                    .setDetails(getIntent().getStringExtra(AppConstants.POST_DETAILS))
                    .setImageUrl(getIntent().getStringExtra(AppConstants.POST_IMAGE_URL))
                    .setImageStorageId(getIntent().getStringExtra(AppConstants.POST_IMAGE_STORAGE_ID))
                    .build();
        }

        //display post texts
        editTextPostTitle = findViewById(R.id.editText_editPostTitle);
        editTextPostTitle.setText(post.getTitle());
        editTextPostDetails = findViewById(R.id.editText_editPostDetails);
        editTextPostDetails.setText(post.getDetails());

        //display post image
        if (!post.getImageUrl().isEmpty()) {
            Picasso.get().load(post.getImageUrl()).into(((ImageView) findViewById(R.id.imageButton_editPostImage)));
        }

        //add an image to the post
        imageButtonEditPost = findViewById(R.id.imageButton_editPostImage);
        imageButtonEditPost.setOnClickListener(v -> {
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
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_CAMERA_PERMISSION);
                }
            }
        });

        //save post
        Button buttonUpdate = findViewById(R.id.buttonSave_editPost);
        buttonUpdate.setOnClickListener(v -> {
            //show progress
            progressBar = findViewById(R.id.progressBar_EditPost);
            progressBar.setVisibility(View.VISIBLE);

            //save edited post without image
            if (shouldUpdatePost() && imageUri == null) {
                //get the FirebaseDatabase instance for the specified URL
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                //get the DatabaseReference for the node that should be updated
                DatabaseReference postRef = firebaseDatabase.getReference(DATABASE_REFERENCE).child(DATABASE_REFERENCE_POSTS).child(post.getId());

                //write data to the database
                if (shouldUpdatePostTitle()) {
                    postRef.child(DATABASE_FIELD_POST_TITLE).setValue(editTextPostTitle.getText().toString());
                }
                if (shouldUpdatePostDetails()) {
                    postRef.child(DATABASE_FIELD_POST_DETAILS).setValue(editTextPostDetails.getText().toString());
                }

                //finish and return to main activity
                this.finish();
                startActivity(new Intent(this, MainActivity.class));
                Toast.makeText(this, getResources().getString(R.string.toast_post_edited), Toast.LENGTH_SHORT).show();

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
                lastPathSegment = stringList.get(stringList.size() - 1);
                //fix lastPathSegment, if needed
                if (lastPathSegment.contains("/")) {    //ie: primary:Pictures/
                    List<String> lastPathSegments = Arrays.asList(lastPathSegment.split("/"));
                    lastPathSegment = lastPathSegments.get(lastPathSegments.size() - 1);
                }
                if (lastPathSegment.contains(".")) {    //ie: .jpg
                    List<String> lastPathSegments = Arrays.asList(lastPathSegment.split("\\."));
                    lastPathSegment = lastPathSegments.get(0);
                }

                //get a reference to the user image
                StorageReference imageRef = cloudRef.child(DATABASE_REFERENCE).child(post.getUserId()).child(lastPathSegment);

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
                                //get the FirebaseDatabase instance for the specified URL
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                                //get the DatabaseReference for the node that should be updated
                                DatabaseReference postRef = firebaseDatabase.getReference(DATABASE_REFERENCE).child(DATABASE_REFERENCE_POSTS).child(post.getId());

                                //update post database reference with post image url & storage id
                                postRef.child(DATABASE_FIELD_POST_IMAGE_URL).setValue(uri.toString().trim());
                                postRef.child(DATABASE_FIELD_POST_IMAGE_STORAGE_ID).setValue(lastPathSegment);

                                //update post database reference with post title & details
                                if (shouldUpdatePostTitle()) {
                                    postRef.child(DATABASE_FIELD_POST_TITLE).setValue(editTextPostTitle.getText().toString());
                                }
                                if (shouldUpdatePostDetails()) {
                                    postRef.child(DATABASE_FIELD_POST_DETAILS).setValue(editTextPostDetails.getText().toString());
                                }

                                //finish and return to main activity
                                this.finish();
                                startActivity(new Intent(this, MainActivity.class));
                                Toast.makeText(this, getResources().getString(R.string.toast_post_edited), Toast.LENGTH_SHORT).show();

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
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //set the selected image for the post
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GET_POST_IMAGE) {
            if (data != null) {
                imageUri = data.getData();
                imageButtonEditPost.setImageURI(data.getData());
            }
        }

        //set the captured image for the post
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAPTURE_POST_IMAGE) {
            if (data != null) {
                imageButtonEditPost.setImageURI(imageUri);
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

    //Checks if post should be updated
    private boolean shouldUpdatePost() {
        String title = editTextPostTitle.getText().toString();
        String details = editTextPostDetails.getText().toString();
        Uri postImage = imageUri;

        if (post.getTitle().equals(title) && post.getDetails().equals(details) && Uri.parse(post.getImageUrl()) == postImage) {
            return false;
        }
        return !title.isEmpty() || !details.isEmpty() || !(postImage == null);
    }

    private boolean shouldUpdatePostTitle() {
        return !post.getTitle().equals(editTextPostTitle.getText().toString());
    }

    private boolean shouldUpdatePostDetails() {
        return !post.getDetails().equals(editTextPostDetails.getText().toString());
    }
}