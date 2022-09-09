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
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import gparap.apps.dating.MainActivity;
import gparap.apps.dating.R;
import gparap.apps.dating.utils.AppConstants;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if current user is authenticated and redirect appropriately
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            setContentView(R.layout.activity_login);
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        //get widgets
        email = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPassword);
        progress = findViewById(R.id.progressBarLogin);

        //sign-in user with e-mail and password
        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            if (validateInput()) {
                //show progress
                progress.setVisibility(View.VISIBLE);

                //sign-in
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                        email.getText().toString().trim(), password.getText().toString().trim()
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, getString(R.string.toast_login_successful), Toast.LENGTH_SHORT).show();

                        //redirect to main activity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } else {
                        Toast.makeText(this, getString(R.string.toast_login_failed), Toast.LENGTH_SHORT).show();
                    }

                    //hide progress
                    progress.setVisibility(View.INVISIBLE);
                });
            }
        });

        //redirect to registration
        findViewById(R.id.buttonRegisterRedirect).setOnClickListener(v -> {
                    //pass any values the user input
                    Intent intent = new Intent(this, RegisterActivity.class);
                    intent.putExtra(AppConstants.INTENT_EXTRA_LOGIN_EMAIL, email.getText().toString().trim());
                    intent.putExtra(AppConstants.INTENT_EXTRA_LOGIN_PASSWORD, password.getText().toString());
                    startActivity(intent);
                }
        );
    }

    private boolean validateInput() {
        //check if input is empty
        if (email.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        }

        //check if email is of correct type
        if (!email.getText().toString().trim().contains(AppConstants.SYMBOL_SIGN_AT) ||
                !email.getText().toString().trim().contains(AppConstants.SYMBOL_SIGN_DOT)) {
            Toast.makeText(this, getString(R.string.toast_login_email_type_error), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}