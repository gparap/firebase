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
import com.google.firebase.storage.FirebaseStorage;

import gparap.apps.dating.R;

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
            email.setText(intent.getStringExtra("login_email"));
            password.setText(intent.getStringExtra("login_password"));
        }

        //register new user
        buttonRegister.setOnClickListener(view -> {
            if (validateRegistration()) {
                registerUser();
            }
        });

        //show the image picked from the user device
        getUserImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
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
        buttonPickImage.setOnClickListener(view -> getUserImageLauncher.launch("image/*"));
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

    private void registerUser() {
        //show progress
        progressBar.setVisibility(View.VISIBLE);

        //register user
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //upload user profile image
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        firebaseStorage.getReference("dating_app")
                                .child(username.getText().toString().trim()).child("profile_picture")
                                .putFile(imageUri).addOnCompleteListener(uploadTask -> {
                                    if (uploadTask.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this,
                                                getResources().getString(R.string.toast_registration_success),
                                                Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(RegisterActivity.this,
                                                getResources().getString(R.string.toast_registration_image_upload_failed),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    //hide progress
                                    progressBar.setVisibility(View.INVISIBLE);
                                });

                    } else {
                        //create error message
                        if (task.getException() != null) {
                            Toast.makeText(RegisterActivity.this,
                                    task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RegisterActivity.this,
                                    getResources().getString(R.string.toast_registration_failed),
                                    Toast.LENGTH_SHORT).show();
                        }

                        //hide progress
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}