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

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button buttonLogin, buttonRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupToolbar();
        getWidgets();

        //create ViewModel for this activity
        LoginActivityViewModel viewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);

        //display user's e-mail if they're just registered
        email.setText(getIntent().getStringExtra(AppConstants.REGISTERED_USER_EMAIL));

        //hide progress
        progressBar.setVisibility(View.INVISIBLE);

        //login existing user
        buttonLogin.setOnClickListener(v -> {
            if (viewModel.validateUserInput(email.getText().toString(), password.getText().toString())) {
                //show progress
                progressBar.setVisibility(View.VISIBLE);

                //sign-in user
                viewModel.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                UserModel user = viewModel.getSignedInUser(
                                        email.getText().toString(),
                                        password.getText().toString()
                                );
                                viewModel.greetSignedInUser(user);
                                viewModel.redirectToChat(user);
                                finish();

                            } else {
                                Toast.makeText(this, getResources().getString(R.string.toast_invalid_credentials), Toast.LENGTH_SHORT).show();
                            }

                            //hide progress
                            progressBar.setVisibility(View.INVISIBLE);
                        });
            }
        });

        //register new user
        buttonRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void getWidgets() {
        email = findViewById(R.id.edit_text_login_email);
        password = findViewById(R.id.edit_text_login_password);
        buttonLogin = findViewById(R.id.button_login);
        buttonRegister = findViewById(R.id.button_goto_register);
        progressBar = findViewById(R.id.progress_login);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_login);
        toolbar.setTitle(getResources().getString(R.string.title_activity_login));
    }
}