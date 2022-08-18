package gparap.apps.dating.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import gparap.apps.dating.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email, password, passwordConfirm;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWidgets();

        //register new user
        buttonRegister.setOnClickListener(view->{
            if (validateRegistration()) {
                //TODO: Register user
            }
        });
    }

    private void getWidgets() {
        buttonRegister = findViewById(R.id.buttonRegister);
        username = findViewById(R.id.editTextRegisterUsername);
        email = findViewById(R.id.editTextRegisterEmail);
        password = findViewById(R.id.editTextRegisterPassword);
        passwordConfirm = findViewById(R.id.editTextRegisterPasswordConfirm);
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
}