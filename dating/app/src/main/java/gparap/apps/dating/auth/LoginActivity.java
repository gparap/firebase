package gparap.apps.dating.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import gparap.apps.dating.MainActivity;
import gparap.apps.dating.R;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if current user is authenticated and redirect appropriately
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            setContentView(R.layout.activity_login);
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        //get widgets
        email = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        //sign in user
        buttonLogin.setOnClickListener(v->{
            //validate input
            if (validateInput()) {
                //TODO: sign in user
            }
        });

        //redirect to registration
        findViewById(R.id.buttonRegisterRedirect).setOnClickListener(v -> {
                    //pass any values the user input
                    Intent intent = new Intent(this, RegisterActivity.class);
                    intent.putExtra("login_email", email.getText().toString().trim());
                    intent.putExtra("login_password", password.getText().toString());
                    startActivity(intent);
                }
        );
    }

    private boolean validateInput() {
        //check if input is empty
        if (email.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        }

        //check if email is of correct type
        if (!email.getText().toString().trim().contains("@") || !email.getText().toString().trim().contains(".")) {
            Toast.makeText(this, getString(R.string.toast_login_email_type_error), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}