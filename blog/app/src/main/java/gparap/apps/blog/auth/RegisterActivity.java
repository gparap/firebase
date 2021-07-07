package gparap.apps.blog.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import gparap.apps.blog.MainActivity;
import gparap.apps.blog.R;
import gparap.apps.blog.model.BlogUserModel;
import gparap.apps.blog.utils.FirebaseUtils;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email, password, passwordConfirm;
    private Button buttonRegister;
    private BlogUserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWidgets();
        buttonRegister.setOnClickListener(v -> {
            user = createUser();
            if (validateUserInput()) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        FirebaseUtils.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //update userId after user successful registration
                        if (task.getResult() != null && task.getResult().getUser() != null) {
                            user.setUserId(task.getResult().getUser().getUid());
                        }

                        //greet user and redirect to blog
                        Toast.makeText(RegisterActivity.this, getString(R.string.toast_welcome)
                                + Objects.requireNonNull(task.getResult().getUser()).getDisplayName(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.toast_registration_failed), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private boolean validateUserInput() {
        //TODO: validate e-mail
        //TODO: check if user exists in firebase

        //check if user input is empty
        if (user.getUsername().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_username), Toast.LENGTH_SHORT).show();
            return false;
        } else if (user.getEmail().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (user.getPassword().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordConfirm.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_password_confirm), Toast.LENGTH_SHORT).show();
            return false;
        }
        //check password confirmation
        if (!user.getPassword().isEmpty() && !passwordConfirm.getText().toString().isEmpty()
                && !passwordConfirm.getText().toString().equals(user.getPassword())) {
            Toast.makeText(this, getString(R.string.toast_failed_password_confirm), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private BlogUserModel createUser() {
        return new BlogUserModel(
                username.getText().toString().trim(),
                email.getText().toString().trim(),
                password.getText().toString().trim()
        );
    }

    private void getWidgets() {
        username = findViewById(R.id.editTextRegisterUsername);
        email = findViewById(R.id.editTextRegisterEmail);
        password = findViewById(R.id.editTextRegisterPassword);
        passwordConfirm = findViewById(R.id.editTextRegisterPasswordConfirm);
        buttonRegister = findViewById(R.id.buttonRegister);
    }
}