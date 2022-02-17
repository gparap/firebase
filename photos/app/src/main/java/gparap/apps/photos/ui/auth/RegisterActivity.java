package gparap.apps.photos.ui.auth;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import gparap.apps.photos.R;
import gparap.apps.photos.utils.AppConstants;

public class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel viewModel;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);     //handle the splash screen transition
        setContentView(R.layout.activity_register);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        EditText[] widgets = getEditTextWidgets();

        //get information that the user typed before redirecting here and fill in the input fields
        email.setText(getIntent().getStringExtra(AppConstants.INTENT_EXTRA_EMAIL));
        password.setText(getIntent().getStringExtra(AppConstants.INTENT_EXTRA_PASSWORD));

        //register the new user
        findViewById(R.id.button_register).setOnClickListener(button ->
                viewModel.validateUserInput(widgets)
        );
    }

    private EditText[] getEditTextWidgets() {
        EditText username = findViewById(R.id.edit_text_register_username);
        email = findViewById(R.id.edit_text_register_email);
        password = findViewById(R.id.edit_text_register_password);
        EditText confirmation = findViewById(R.id.edit_text_register_password_confirm);
        return new EditText[]{username, email, password, confirmation};
    }
}