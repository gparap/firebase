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

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;

import gparap.apps.chat.R;
import gparap.apps.chat.data.UserModel;
import gparap.apps.chat.utils.AppConstants;

public class RegisterActivity extends AppCompatActivity {
    private EditText displayName, email, password, confirmPassword;
    private Button buttonRegister;
    private ProgressBar progress;
    private RegisterActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupToolbar();
        getWidgets();

        //create ViewModel for this activity
        viewModel = new ViewModelProvider(this).get(RegisterActivityViewModel.class);

        //hide progress
        progress.setVisibility(View.INVISIBLE);

        //register a new user
        buttonRegister.setOnClickListener(v -> {
            if (viewModel.validateRegistration(
                    displayName.getText().toString().trim(),
                    email.getText().toString().trim(),
                    password.getText().toString().trim(),
                    confirmPassword.getText().toString().trim()
            )) {
                //show progress
                progress.setVisibility(View.VISIBLE);

                //create a new user
                viewModel.createUserWithEmailAndPassword(email.getText().toString().trim(),
                        password.getText().toString().trim()).addOnCompleteListener(taskCreate -> {
                    if (taskCreate.isSuccessful()) {
                        //create a user model
                        UserModel user = viewModel.getCurrentUser(
                                displayName.getText().toString().trim(),
                                email.getText().toString().trim(),
                                password.getText().toString().trim()
                        );
                        //sign-in currently created user
                        viewModel.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(taskLogin -> {
                            //update new user's profile
                            viewModel.updateUserDisplayName(user.getDisplayName()).addOnCompleteListener(taskUpdate -> {
                                        if (taskUpdate.isSuccessful()) {
                                            progress.setVisibility(View.INVISIBLE);
                                            Toast.makeText(this, getResources().getString(R.string.toast_register_success), Toast.LENGTH_SHORT).show();
                                            viewModel.redirectToLogin(user.getEmail());
                                            finish();
                                        }
                                    }
                            );
                            //add new user to the "users" database
                            viewModel.insertNewUserToTheDatabase(user);
                        });
                    }

                    //cannot create new user
                    else {
                        progress.setVisibility(View.INVISIBLE);

                        //debug log
                        if (taskCreate.getException() != null) {
                            Log.d(AppConstants.TAG_CANNOT_CREATE_USER, taskCreate.getException().toString());

                            //inform user that the e-mail they entered is invalid
                            if (taskCreate.getException() instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(this, this.getResources().getString(R.string.toast_email_badly_formatted), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
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