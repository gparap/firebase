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
package gparap.apps.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import gparap.apps.chat.MainActivity;
import gparap.apps.chat.R;
import gparap.apps.chat.data.model.UserModel;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button buttonLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupToolbar();
        getWidgets();

        //hide progress
        progressBar.setVisibility(View.INVISIBLE);

        //login
        buttonLogin.setOnClickListener(v -> {
            if (validateUserInput()) {
                //show progress
                progressBar.setVisibility(View.VISIBLE);

                //log-in user
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                Task<AuthResult> authResult = firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString());
                authResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //get user details
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        //create user model
                        assert firebaseUser != null;
                        UserModel userModel = new UserModel();
                        userModel.setId(firebaseUser.getUid());
                        userModel.setEmail(email.getText().toString());
                        userModel.setPassword(password.getText().toString());
                        userModel.setDisplayName(firebaseUser.getDisplayName());

                        //greet user
                        String displayName = firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "";
                        Toast.makeText(this, "Welcome " + displayName, Toast.LENGTH_SHORT).show();

                        //redirect to chat
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("current_user", userModel);
                        startActivity(intent);

                    }else {
                        Toast.makeText(this, getResources().getString(R.string.toast_invalid_credentials), Toast.LENGTH_SHORT).show();
                    }

                    //hide progress
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }
        });
    }

    private boolean validateUserInput() {
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_empty_login_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_empty_login_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getWidgets() {
        email = findViewById(R.id.edit_text_login_email);
        password = findViewById(R.id.edit_text_login_password);
        buttonLogin = findViewById(R.id.button_login);
        progressBar = findViewById(R.id.progress_login);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_login);
        toolbar.setTitle(getResources().getString(R.string.title_activity_login));
    }
}