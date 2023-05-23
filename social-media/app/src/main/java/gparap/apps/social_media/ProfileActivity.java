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

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import gparap.apps.social_media.data.UserModel;

public class ProfileActivity extends AppCompatActivity {
    private ImageButton imageButtonUserProfile, imageButtonProfileChangeUsername, imageButtonProfileChangeMobile,
            imageButtonProfileChangeEmail, imageButtonProfileChangePassword;
    private EditText editTextProfileUsername, editTextProfileMobile, editTextProfileEmail,
            editTextProfilePassword, editTextProfilePasswordConfirm;
    private Button buttonProfileUpdate;
    private ProgressBar progressBarProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.text_profile));
        getWidgets();

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //display basic user details from authentication
            editTextProfileUsername.setText(user.getDisplayName());
            editTextProfileEmail.setText(user.getEmail());

            //get more user details from database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("social_media_app")
                    .child("users").child(Objects.requireNonNull(user.getDisplayName()));
            Task<DataSnapshot> snapshotTask = userRef.get();
            snapshotTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    UserModel dbUser = task.getResult().getValue(UserModel.class);
                    if (dbUser != null) {
                        editTextProfileMobile.setText(dbUser.getPhone());
                        editTextProfilePassword.setText(dbUser.getPassword());
                    }
                }
            });
        }
    }

    private void getWidgets() {
        imageButtonUserProfile = findViewById(R.id.imageButtonUserProfile);
        imageButtonProfileChangeUsername = findViewById(R.id.imageButtonProfileChangeUsername);
        imageButtonProfileChangeMobile = findViewById(R.id.imageButtonProfileChangeMobile);
        imageButtonProfileChangeEmail = findViewById(R.id.imageButtonProfileChangeEmail);
        imageButtonProfileChangePassword = findViewById(R.id.imageButtonProfileChangePassword);
        editTextProfileUsername = findViewById(R.id.editTextProfileUsername);
        editTextProfileMobile = findViewById(R.id.editTextProfileMobile);
        editTextProfileEmail = findViewById(R.id.editTextProfileEmail);
        editTextProfilePassword = findViewById(R.id.editTextProfilePassword);
        editTextProfilePasswordConfirm = findViewById(R.id.editTextProfilePasswordConfirm);
        buttonProfileUpdate = findViewById(R.id.buttonProfileUpdate);
        progressBarProfile = findViewById(R.id.progressBarProfile);
    }
}