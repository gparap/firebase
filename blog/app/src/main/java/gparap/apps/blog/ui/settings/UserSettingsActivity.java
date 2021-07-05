package gparap.apps.blog.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import gparap.apps.blog.R;
import gparap.apps.blog.model.BlogUserModel;
import gparap.apps.blog.utils.FirebaseUtils;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class UserSettingsActivity extends AppCompatActivity {
    private ImageButton buttonSetUserImage, buttonChangeUsername, buttonChangeEmail, buttonChangePassword;
    private EditText username, email, password, passwordConfirm;
    private Button buttonUpdate;
    private ProgressBar progressBar;
    private BlogUserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User Settings");
        getWidgets();
        setListenersToMakeInputWidgetsEditable();

        //get user from database
        String userId = FirebaseUtils.getInstance().getUser().getUid();
        FirebaseUtils.getInstance().getUsernameQuery(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //get current based on their id
                for (DataSnapshot task : snapshot.getChildren()) {
                    if (Objects.equals(task.child("userId").getValue(), userId)) {
                        createBlogUser(task);
                        displayUserSettings();

                        //user is found
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("DatabaseError", error.getDetails());
            }
        });
    }

    private void displayUserSettings() {
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        password.setText(user.getPassword());
    }

    @SuppressWarnings("ConstantConditions")
    private void createBlogUser(DataSnapshot task) {
        user = new BlogUserModel(
                task.child("username").getValue().toString(),
                task.child("email").getValue().toString(),
                task.child("password").getValue().toString()
        );
    }

    private void setListenersToMakeInputWidgetsEditable() {
        //make change username editable
        buttonChangeUsername.setOnClickListener(v ->
                username.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
        );

        //make change email editable
        buttonChangeEmail.setOnClickListener(v ->
                email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        );

        //make change password editable and show password confirmation
        buttonChangePassword.setOnClickListener(v -> {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordConfirm.setVisibility(View.VISIBLE);
        });
    }

    private void getWidgets() {
        buttonSetUserImage = findViewById(R.id.imageButtonUserSettingsUserImage);
        buttonChangeUsername = findViewById(R.id.imageButtonUserSettingsChangeUsername);
        buttonChangeEmail = findViewById(R.id.imageButtonUserSettingsChangeEmail);
        buttonChangePassword = findViewById(R.id.imageButtonUserSettingsChangePassword);
        username = findViewById(R.id.editTextUserSettingsUsername);
        email = findViewById(R.id.editTextUserSettingsEmail);
        password = findViewById(R.id.editTextUserSettingsPassword);
        passwordConfirm = findViewById(R.id.editTextUserSettingsPasswordConfirm);
        buttonUpdate = findViewById(R.id.buttonUserSettingsUpdate);
        progressBar = findViewById(R.id.progressBarUserSettings);
    }
}