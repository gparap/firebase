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
package gparap.apps.chat.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import gparap.apps.chat.R;
import gparap.apps.chat.data.model.UserModel;
import gparap.apps.chat.utils.AppConstants;

public class RegisterActivity extends AppCompatActivity {
    private EditText displayName, email, password, confirmPassword;
    private Button buttonRegister;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupToolbar();
        getWidgets();

        //hide progress
        progress.setVisibility(View.INVISIBLE);

        //register new user
        buttonRegister.setOnClickListener(v -> {
            if (validateRegistration()) {
                //show progress
                progress.setVisibility(View.VISIBLE);

                //create a new UserModel object
                UserModel user = new UserModel();
                user.setEmail(email.getText().toString().trim());
                user.setPassword(password.getText().toString().trim());
                user.setDisplayName(displayName.getText().toString().trim());

                //create a new Firebase user
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(
                        user.getEmail(),
                        user.getPassword()).addOnCompleteListener(taskCreate -> {
                    //new user created
                    if (taskCreate.isSuccessful()) {
                        //sign-in user
                        firebaseAuth.signInWithEmailAndPassword(user.getEmail(),
                                user.getPassword()).addOnCompleteListener(taskLogin -> {

                            //get currently signed-in user
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser == null) {
                                return;
                            }

                            //update user id
                            user.setId(firebaseUser.getUid());

                            //update user's display name
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.getDisplayName())
                                    .build();
                            firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(
                                    taskUpdate -> {
                                        if (taskUpdate.isSuccessful()) {
                                            progress.setVisibility(View.INVISIBLE); //hide progress
                                            Toast.makeText(this, getResources().getString(R.string.toast_register_success), Toast.LENGTH_SHORT).show();
                                            gotoLoginActivity(user.getEmail());
                                        }
                                    }
                            );
                        });
                    }

                    //cannot create new user
                    else {
                        progress.setVisibility(View.INVISIBLE); //hide progress
                        Log.d(AppConstants.TAG_CANNOT_CREATE_USER, Objects.requireNonNull(taskCreate.getException()).toString());
                    }
                });
            }
        });
    }

    private void gotoLoginActivity(String registeredEmail) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(AppConstants.REGISTERED_USER_EMAIL, registeredEmail);
        startActivity(intent);
        finish();
    }

    private boolean validateRegistration() {
        if (displayName.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_empty_display_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confirmPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_empty_confirm_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.toast_unmatched_passwords), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getWidgets() {
        displayName = findViewById(R.id.edit_text_register_display_name);
        email = findViewById(R.id.edit_text_register_email);
        password = findViewById(R.id.edit_text_register_password);
        confirmPassword = findViewById(R.id.edit_text_register_confirm_password);
        buttonRegister = findViewById(R.id.button_register);
        progress = findViewById(R.id.progress_register);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_register);
        toolbar.setTitle(getResources().getString(R.string.title_activity_register));
    }
}