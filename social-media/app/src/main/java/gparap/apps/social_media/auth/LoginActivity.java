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

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import gparap.apps.social_media.MainActivity;
import gparap.apps.social_media.R;
import gparap.apps.social_media.utils.AppConstants;

@SuppressWarnings({"FieldCanBeLocal", "deprecation"})
public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button buttonLogin;
    private ProgressBar progressBar;

    //for interacting with the Google Sign In API
    private SignInButton buttonLoginWithGoogle;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.text_login));
        getWidgets();

        //user login
        buttonLogin.setOnClickListener(v -> {
            if (isLoginInputValid()) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                                email.getText().toString().trim(), password.getText().toString().trim())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                //display info to user and redirect to main activity
                                Toast.makeText(this, getString(R.string.toast_login_successful), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();

                            } else {
                                Toast.makeText(this, getString(R.string.toast_wrong_credentials), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        });
            }
        });

        //configure options for Google authentication
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //create a client object for Google authentication
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //user login using Google authentication
        buttonLoginWithGoogle.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            startActivityForResult(googleSignInClient.getSignInIntent(), AppConstants.REQUEST_CODE_GOOGLE_AUTH);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //start the Google authentication flow
        if (requestCode == AppConstants.REQUEST_CODE_GOOGLE_AUTH) {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(accountTask -> {
                if (accountTask.isSuccessful()) {
                    try {
                        //get the basic account information of the signed in Google user
                        GoogleSignInAccount googleAccount = accountTask.getResult(ApiException.class);

                        //get the ID token of the user's Google account
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);

                        //authenticate user
                        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(resultTask -> {
                            if (resultTask.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);

                                //redirect to main activity
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        });

                    } catch (ApiException e) {
                        Toast.makeText(this, getResources().getString(R.string.toast_login_google_failed), Toast.LENGTH_SHORT).show();

                        //debug
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(this, getResources().getString(R.string.toast_login_google_failed), Toast.LENGTH_SHORT).show();
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isLoginInputValid() {
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(this, getString(R.string.toast_invalid_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getWidgets() {
        email = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBarLogin);
        buttonLoginWithGoogle = findViewById(R.id.buttonLoginWithGoogle);
    }
}