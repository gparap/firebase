package gparap.apps.dating.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import gparap.apps.dating.MainActivity;
import gparap.apps.dating.R;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;

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
        username = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPassword);

        //TODO: sign in user

        //redirect to registration
        findViewById(R.id.buttonRegisterRedirect).setOnClickListener(v -> {
                    //pass any values the user input
                    Intent intent = new Intent(this, RegisterActivity.class);
                    intent.putExtra("login_username", username.getText().toString().trim());
                    intent.putExtra("login_password", password.getText().toString());
                    startActivity(intent);
                }
        );
    }
}