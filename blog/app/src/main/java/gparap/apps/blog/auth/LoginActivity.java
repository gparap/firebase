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
package gparap.apps.blog.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import gparap.apps.blog.MainActivity;
import gparap.apps.blog.R;
import gparap.apps.blog.utils.FirebaseUtils;
import gparap.apps.blog.utils.GoogleUtils;

@SuppressWarnings("FieldCanBeLocal")
public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private SignInButton buttonLoginWithGoogle;
    private Button buttonLogin, buttonRegister, buttonGuest;
    private ProgressBar progressBar;
    private final int REQUEST_CODE_GOOGLE_SIGN_IN = 999;
    private static final String TAG_GOOGLE_SIGN_IN = "GOOGLE SIGN IN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        getWidgets();

        //user sign-in with Google
        buttonLoginWithGoogle.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            GoogleUtils.getInstance().signInWithGoogleAccount(this);
        });

        //user sign-in
        buttonLogin.setOnClickListener(v -> {
            if (validateInput()) {
                progressBar.setVisibility(View.VISIBLE);
                signInWithEmailAndPassword();
            }
        });

        //redirect to user registration
        buttonRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        //continue as guest
        buttonGuest.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            signInAsGuest();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(Objects.requireNonNull(account).getIdToken());
            } catch (ApiException e) {
                Log.wtf(TAG_GOOGLE_SIGN_IN, "Google sign in failed", e);
            }
        }
    }

    private void gotoBlog() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseUtils.getInstance().getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        gotoBlog();
                    } else {
                        Log.w(TAG_GOOGLE_SIGN_IN, "signInWithCredential:failure", task.getException());
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                });
    }

    private void signInAsGuest() {
        FirebaseUtils.getInstance().signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, getString(R.string.toast_login_guest), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                });
    }

    private void signInWithEmailAndPassword() {
        FirebaseUtils.getInstance().signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, getString(R.string.toast_login_successful), Toast.LENGTH_SHORT).show();
                        gotoBlog();
                    } else {
                        Toast.makeText(this, getString(R.string.toast_wrong_credentials), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                });
    }

    /**
     * Checks if e-mail address and password are empty and displays error information if they are.
     *
     * @return boolean
     */
    private boolean validateInput() {
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
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
        buttonLoginWithGoogle = findViewById(R.id.buttonLoginWithGoogle);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegisterRedirect);
        buttonGuest = findViewById(R.id.buttonGuest);
        progressBar = findViewById(R.id.progressBarLogin);
    }
}