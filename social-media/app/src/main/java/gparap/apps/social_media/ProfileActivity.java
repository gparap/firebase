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
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
    private ImageButton imageButtonUserProfile, imageButtonProfileChangeUsername, imageButtonProfileChangeMobile;
    private EditText editTextProfileUsername, editTextProfileMobile;
    private Button buttonProfileUpdate;
    private ProgressBar progressBarProfile;
    private final int GET_CONTENT_REQUEST_CODE = 999;
    UserModel dbUser;
    Uri userProfileImageUriData = null;
    private boolean isUsernameChanged, isMobileChanged, isImageChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.text_profile));
        getWidgets();
        setEditTextsEditable();

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //display basic user details from authentication
            editTextProfileUsername.setText(user.getDisplayName());

            //get more user details from database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("social_media_app")
                    .child("users").child(Objects.requireNonNull(user.getUid()));
            Task<DataSnapshot> snapshotTask = userRef.get();
            snapshotTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dbUser = task.getResult().getValue(UserModel.class);
                    if (dbUser != null) {
                        editTextProfileMobile.setText(dbUser.getPhone());

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
            //update user profile with image
            if (isImageChanged) {
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

                        //update user's display name (aka username)
                        if (isUsernameChanged) {
                            user.updateProfile(new UserProfileChangeRequest.Builder()
                                    .setDisplayName(editTextProfileUsername.getText().toString())
                                    .build()).addOnCompleteListener(taskUsername -> {
                                if (taskUsername.isSuccessful()) {
                                    //update user reference in database
                                    dbUser.setUsername(editTextProfileUsername.getText().toString());
                                    Task<Void> dbTask = usersRef.setValue(dbUser);
                                    dbTask.addOnCompleteListener(t -> {
                                        if (t.isSuccessful()) {
                                            System.out.println("Username updated.");
                                        }
                                    });
                                }
                            });
                        }
                        //update user's phone
                        if (isMobileChanged) {
                            //update user reference in database
                            dbUser.setPhone(editTextProfileMobile.getText().toString());
                            Task<Void> dbTask = usersRef.setValue(dbUser);
                            dbTask.addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    System.out.println("User phone updated.");
                                }
                            });
                        }
                    }
                });

                //update user profile without image
            } else {
                //update user's display name (aka username)
                if (isUsernameChanged) {
                    user.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(editTextProfileUsername.getText().toString())
                            .build()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //update user reference in database
                            dbUser.setUsername(editTextProfileUsername.getText().toString());
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference usersRef = database.getReference("social_media_app").child("users").child(dbUser.getId());
                            Task<Void> dbTask = usersRef.setValue(dbUser);
                            dbTask.addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    System.out.println("Username updated.");
                                }
                            });
                        }
                    });
                }
                //update user's phone
                if (isMobileChanged) {
                    //update user reference in database
                    dbUser.setPhone(editTextProfileMobile.getText().toString());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("social_media_app").child("users").child(dbUser.getId());
                    Task<Void> task = usersRef.setValue(dbUser);
                    task.addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            System.out.println("User phone updated.");
                        }
                    });
                }
            }
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
                    isImageChanged = true;
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void getWidgets() {
        imageButtonUserProfile = findViewById(R.id.imageButtonUserProfile);
        imageButtonProfileChangeUsername = findViewById(R.id.imageButtonProfileChangeUsername);
        imageButtonProfileChangeMobile = findViewById(R.id.imageButtonProfileChangeMobile);
        editTextProfileUsername = findViewById(R.id.editTextProfileUsername);
        editTextProfileMobile = findViewById(R.id.editTextProfileMobile);
        buttonProfileUpdate = findViewById(R.id.buttonProfileUpdate);
        progressBarProfile = findViewById(R.id.progressBarProfile);
    }

    private void setEditTextsEditable() {
        imageButtonProfileChangeUsername.setOnClickListener(v -> {
            editTextProfileUsername.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            isUsernameChanged = true;
            imageButtonProfileChangeUsername.setBackgroundTintList(getColorStateList(R.color.purple_500));

        });
        imageButtonProfileChangeMobile.setOnClickListener(v -> {
            editTextProfileMobile.setInputType(InputType.TYPE_CLASS_PHONE);
            isMobileChanged = true;
            imageButtonProfileChangeMobile.setBackgroundTintList(getColorStateList(R.color.purple_500));
        });
    }
}