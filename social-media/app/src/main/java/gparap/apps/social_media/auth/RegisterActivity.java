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
package gparap.apps.social_media.auth;

import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE_USERS;
import static gparap.apps.social_media.utils.AppConstants.INTENT_EXTRA_EMAIL;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gparap.apps.social_media.R;
import gparap.apps.social_media.data.UserModel;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPhone, editTextEmail, editTextPassword, editTextConfirmPassword;
    private String username, phone, email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getInputWidgets();

        //hide progress
        ProgressBar progress = findViewById(R.id.progressBarRegister);
        progress.setVisibility(View.INVISIBLE);

        //register a new user
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(v -> {
            getInputFields();
            if (isRegistrationValid()) {
                //show progress
                progress.setVisibility(View.VISIBLE);

                //TODO: check if user is already registered

                //create a new user
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(taskCreate -> {
                    if (taskCreate.isSuccessful()) {
                        //sign-in currently created user
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(taskLogin -> {
                            //update user's profile
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            assert firebaseUser != null;
                            firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(username).build()).addOnCompleteListener(taskUpdate -> {
                                        if (taskUpdate.isSuccessful()) {
                                            progress.setVisibility(View.INVISIBLE);
                                            Toast.makeText(this, getResources().getString(R.string.toast_register_success), Toast.LENGTH_SHORT).show();

                                            //create user data model
                                            UserModel user = new UserModel(firebaseUser.getUid(),
                                                    username, email, phone, password);

                                            //add user to the relevant database
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference usersRef = database.getReference(DATABASE_REFERENCE).child(DATABASE_REFERENCE_USERS).child(user.getId());
                                            usersRef.setValue(user);

                                            //redirect to login
                                            Intent intent = new Intent(this, LoginActivity.class);
                                            intent.putExtra(INTENT_EXTRA_EMAIL, INTENT_EXTRA_EMAIL);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                            );
                        });
                    }

                    //registration failed
                    else {
                        progress.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, this.getResources().getString(R.string.toast_registration_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getInputWidgets() {
        editTextUsername = findViewById(R.id.editTextRegisterUsername);
        editTextPhone = findViewById(R.id.editTextRegisterMobile);
        editTextEmail = findViewById(R.id.editTextRegisterEmail);
        editTextPassword = findViewById(R.id.editTextRegisterPassword);
        editTextConfirmPassword = findViewById(R.id.editTextRegisterConfirmPassword);
    }

    private void getInputFields() {
        username = editTextUsername.getText().toString();
        phone = editTextPhone.getText().toString();
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        confirmPassword = editTextConfirmPassword.getText().toString();
    }

    private boolean isRegistrationValid() {
        if (username.equals("")) {
            Toast.makeText(this, this.getResources().getString(R.string.toast_empty_username), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.equals("")) {
            Toast.makeText(this, this.getResources().getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(this, this.getResources().getString(R.string.toast_invalid_email), Toast.LENGTH_SHORT).show();
        }
        if (phone.length() < 10) {
            Toast.makeText(this, this.getResources().getString(R.string.toast_invalid_phone), Toast.LENGTH_SHORT).show();
        }
        if (password.equals("")) {
            Toast.makeText(this, this.getResources().getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confirmPassword.equals("")) {
            Toast.makeText(this, this.getResources().getString(R.string.toast_empty_password_confirm), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, this.getResources().getString(R.string.toast_unmatched_passwords), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}