package gparap.apps.photos.ui.auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import gparap.apps.photos.R;

public class LoginActivity extends AppCompatActivity {
    private EditText email = null;
    private EditText password = null;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle the splash screen transition
        SplashScreen.installSplashScreen(this);

        setContentView(R.layout.activity_login);

        //create ViewModel for this activity
        LoginViewModel viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        //get widgets
        email = findViewById(R.id.edit_text_login_email);
        password = findViewById(R.id.edit_text_login_password);

        //login user
        Button login = findViewById(R.id.button_login);
        login.setOnClickListener(v -> {
            if (isUserInputValidated()) {
                viewModel.loginWithCredentials(email.getText().toString().trim(), password.getText().toString().trim());
            }
        });
    }

    //user should fill all input fields
    private boolean isUserInputValidated() {
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}