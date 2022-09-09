/*
 * Copyright (c) 2022 gparap
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
package gparap.apps.dating.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import gparap.apps.dating.R;
import gparap.apps.dating.data.UserModel;
import gparap.apps.dating.utils.AppConstants;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email, password, passwordConfirm;
    private Button buttonRegister;
    private ImageButton buttonPickImage;
    private ProgressBar progressBar;
    private ActivityResultLauncher<String> getUserImageLauncher;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWidgets();

        //get any passed values from user login
        Intent intent = getIntent();
        if (intent != null) {
            email.setText(intent.getStringExtra(AppConstants.INTENT_EXTRA_LOGIN_EMAIL));
            password.setText(intent.getStringExtra(AppConstants.INTENT_EXTRA_LOGIN_PASSWORD));
        }

        //register new user
        buttonRegister.setOnClickListener(view -> {
            if (validateRegistration()) {
                showProgress();

                //check if username already exists
                FirebaseDatabase.getInstance().getReference(AppConstants.DATABASE_NAME)
                        .child(username.getText().toString().trim()).get()
                        .addOnCompleteListener(getChildrenCountTask -> {
                            if (getChildrenCountTask.getResult().getChildrenCount() > 0) {
                                showToast(R.string.toast_registration_username_exists);
                                hideProgress();

                            } else {
                                //register user
                                if (imageUri != null) {
                                    registerUserWithProfileImage();
                                } else {
                                    registerUserWithoutProfileImage();
                                }
                            }
                        });
            }
        });

        //show the image picked from the user device
        getUserImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    //get the image URI reference
                    imageUri = result;

                    //load the image
                    Glide.with(RegisterActivity.this)
                            .load(result)
                            .centerCrop()
                            .into(buttonPickImage);

                    //clear the previous background
                    buttonPickImage.setBackground(null);
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //pick an image from the user device
        buttonPickImage.setOnClickListener(view -> getUserImageLauncher.launch(AppConstants.IMAGE_MIME_TYPE));
    }

    private void getWidgets() {
        buttonPickImage = findViewById(R.id.pickImageRegisterButton);
        buttonRegister = findViewById(R.id.buttonRegister);
        username = findViewById(R.id.editTextRegisterUsername);
        email = findViewById(R.id.editTextRegisterEmail);
        password = findViewById(R.id.editTextRegisterPassword);
        passwordConfirm = findViewById(R.id.editTextRegisterPasswordConfirm);
        progressBar = findViewById(R.id.progressBarRegister);
    }

    private boolean validateRegistration() {
        //check if input is empty
        if (username.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_username), Toast.LENGTH_SHORT).show();
            return false;
        } else if (email.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordConfirm.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_password_confirm), Toast.LENGTH_SHORT).show();
            return false;
        }
        //check password & confirmation
        if (!passwordConfirm.getText().toString().trim().equals(password.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.toast_failed_password_confirm), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showToast(int resourceId) {
        Toast.makeText(RegisterActivity.this, getResources().getString(resourceId), Toast.LENGTH_SHORT).show();
    }

    private void showToast(String msg) {
        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void uploadUserMetadata(UserModel user) {
        FirebaseDatabase.getInstance().getReference(AppConstants.DATABASE_NAME)
                .child(username.getText().toString().trim())
                .setValue(user)
                .addOnCompleteListener(uploadUserTask -> {
                    if (uploadUserTask.isSuccessful()) {
                        showToast(R.string.toast_registration_success);
                    } else {
                        showToast(R.string.toast_registration_failed);
                    }
                    hideProgress();
                });
    }

    private void registerUserWithProfileImage() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //upload user profile image to cloud
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        firebaseStorage.getReference(AppConstants.DATABASE_NAME)
                                .child(username.getText().toString().trim()).child(AppConstants.USER_PROFILE_IMAGE_NAME)
                                .putFile(imageUri)
                                .addOnCompleteListener(uploadTask -> {
                                    if (uploadTask.isSuccessful()) {
                                        //get the downloadable url for the image
                                        uploadTask.getResult().getStorage().getDownloadUrl()
                                                .addOnCompleteListener(downloadUrlTask -> {

                                                    //create the user model
                                                    UserModel user = new UserModel(
                                                            username.getText().toString().trim(),
                                                            email.getText().toString().trim(),
                                                            downloadUrlTask.getResult().toString()
                                                    );

                                                    //upload user metadata to cloud
                                                    uploadUserMetadata(user);
                                                });

                                    } else {
                                        showToast(R.string.toast_registration_image_upload_failed);
                                    }
                                    hideProgress();
                                });

                    } else {
                        if (task.getException() != null) {
                            //custom error message
                            showToast(task.getException().getLocalizedMessage());
                        } else {
                            showToast(R.string.toast_registration_failed);
                        }
                        hideProgress();
                    }
                });
    }

    private void registerUserWithoutProfileImage() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //create the user model
                        UserModel user = new UserModel(
                                username.getText().toString().trim(),
                                email.getText().toString().trim(),
                                ""
                        );

                        //upload user metadata to cloud
                        uploadUserMetadata(user);

                    } else {
                        hideProgress();
                    }
                });
    }
}