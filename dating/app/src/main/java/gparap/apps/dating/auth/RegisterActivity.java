package gparap.apps.dating.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import gparap.apps.dating.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email, password, passwordConfirm;
    private Button buttonRegister;
    private ProgressBar progressBar;

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
    }

    private void getWidgets() {
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
                        Toast.makeText(RegisterActivity.this,
                                getResources().getString(R.string.toast_registration_success), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(RegisterActivity.this,
                                getResources().getString(R.string.toast_registration_failed), Toast.LENGTH_SHORT).show();
                    }

                    //hide progress
                    progressBar.setVisibility(View.INVISIBLE);
                });
    }
}