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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.Random;

import gparap.apps.social_media.data.UserModel;

public class ProfileActivity extends AppCompatActivity {
    private ImageButton imageButtonUserProfile, imageButtonProfileChangeUsername, imageButtonProfileChangeMobile,
            imageButtonProfileChangeEmail, imageButtonProfileChangePassword;
    private EditText editTextProfileUsername, editTextProfileMobile, editTextProfileEmail,
            editTextProfilePassword, editTextProfilePasswordConfirm;
    private Button buttonProfileUpdate;
    private ProgressBar progressBarProfile;
    private final int GET_CONTENT_REQUEST_CODE = 999;
    UserModel dbUser;
    Uri userProfileImageUriData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.text_profile));
        getWidgets();

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //display basic user details from authentication
            editTextProfileUsername.setText(user.getDisplayName());
            editTextProfileEmail.setText(user.getEmail());

            //get more user details from database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("social_media_app")
                    .child("users").child(Objects.requireNonNull(user.getUid()));
            Task<DataSnapshot> snapshotTask = userRef.get();
            snapshotTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dbUser = task.getResult().getValue(UserModel.class);
                    if (dbUser != null) {
                        editTextProfileMobile.setText(dbUser.getPhone());
                        editTextProfilePassword.setText(dbUser.getPassword());

                        //display user profile image
                        if (!dbUser.getImageUrl().isEmpty()) {
                            Uri uri = Uri.parse(dbUser.getImageUrl());
                            Picasso.get().load(uri).into(imageButtonUserProfile);
                        }
                    }
                }
            });
        }

        //set user profile image
        imageButtonUserProfile = findViewById(R.id.imageButtonUserProfile);
        imageButtonUserProfile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GET_CONTENT_REQUEST_CODE);
        });

        //update user profile
        buttonProfileUpdate = findViewById(R.id.buttonProfileUpdate);
        buttonProfileUpdate.setOnClickListener(v -> {
            //add image to database storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("social_media_app").child(dbUser.getId());
            StorageReference childRef = storageRef.child(String.valueOf(Math.abs((new Random()).nextLong())));
            UploadTask uploadTask = childRef.putFile(userProfileImageUriData);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                //continue with the task to get the download URL
                return childRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();

                    //update user reference in database
                    dbUser.setImageUrl(downloadUrl.toString());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("social_media_app").child("users").child(dbUser.getId());
                    usersRef.setValue(dbUser);
                }
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //display selected avatar and keep uri data
        if (requestCode == GET_CONTENT_REQUEST_CODE) {
            Bitmap bitmap;
            try {
                if (data != null) {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    imageButtonUserProfile.setImageBitmap(bitmap);
                    userProfileImageUriData = data.getData();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void getWidgets() {
        imageButtonUserProfile = findViewById(R.id.imageButtonUserProfile);
        imageButtonProfileChangeUsername = findViewById(R.id.imageButtonProfileChangeUsername);
        imageButtonProfileChangeMobile = findViewById(R.id.imageButtonProfileChangeMobile);
        imageButtonProfileChangeEmail = findViewById(R.id.imageButtonProfileChangeEmail);
        imageButtonProfileChangePassword = findViewById(R.id.imageButtonProfileChangePassword);
        editTextProfileUsername = findViewById(R.id.editTextProfileUsername);
        editTextProfileMobile = findViewById(R.id.editTextProfileMobile);
        editTextProfileEmail = findViewById(R.id.editTextProfileEmail);
        editTextProfilePassword = findViewById(R.id.editTextProfilePassword);
        editTextProfilePasswordConfirm = findViewById(R.id.editTextProfilePasswordConfirm);
        buttonProfileUpdate = findViewById(R.id.buttonProfileUpdate);
        progressBarProfile = findViewById(R.id.progressBarProfile);
    }
}