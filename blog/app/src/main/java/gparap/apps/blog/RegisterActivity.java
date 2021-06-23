package gparap.apps.blog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email, password, passwordConfirm;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWidgets();
        buttonRegister.setOnClickListener(v -> {
            if (validateUserInput()) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.toast_welcome) + username.getText().toString(), Toast.LENGTH_SHORT).show();
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
        if (username.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_username), Toast.LENGTH_SHORT).show();
            return false;
        } else if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordConfirm.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_password_confirm), Toast.LENGTH_SHORT).show();
            return false;
        }
        //check password confirmation
        if (!password.getText().toString().isEmpty() && !passwordConfirm.getText().toString().isEmpty()
                && !passwordConfirm.getText().toString().equals(password.getText().toString())) {
            Toast.makeText(this, getString(R.string.toast_failed_password_confirm), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getWidgets() {
        username = findViewById(R.id.editTextRegisterUsername);
        email = findViewById(R.id.editTextRegisterEmail);
        password = findViewById(R.id.editTextRegisterPassword);
        passwordConfirm = findViewById(R.id.editTextPasswordConfirm);
        buttonRegister = findViewById(R.id.buttonRegister);
    }
}