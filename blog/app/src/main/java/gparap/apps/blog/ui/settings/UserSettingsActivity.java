package gparap.apps.blog.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Objects;

import gparap.apps.blog.R;
import gparap.apps.blog.model.BlogUserModel;
import gparap.apps.blog.utils.FirebaseUtils;

@SuppressWarnings({"FieldCanBeLocal", "unused", "deprecation", "ConstantConditions"})
public class UserSettingsActivity extends AppCompatActivity {
    private ImageButton buttonSetUserImage, buttonChangeUsername, buttonChangeEmail, buttonChangePassword;
    private EditText username, email, password, passwordConfirm;
    private Button buttonUpdate;
    private ProgressBar progressBar;
    private BlogUserModel user;
    private final int ACTION_GET_CONTENT_RESULT_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User Settings");
        getWidgets();
        setListenersToMakeInputWidgetsEditable();

        //display authenticated user's profile from database
        FirebaseUser firebaseUser = FirebaseUtils.getInstance().getUser();
        if (firebaseUser != null) {
            createBlogUser(firebaseUser);
            displayUserProfile();
        }

        //pick an image from user device gallery
        buttonSetUserImage.setOnClickListener(v -> {
            Intent intentGetContent = new Intent();
            intentGetContent.setType("image/*");
            intentGetContent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intentGetContent, ACTION_GET_CONTENT_RESULT_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //start cropping activity
        if (requestCode == ACTION_GET_CONTENT_RESULT_CODE) {
            CropImage.activity(data.getData())
                    .start(this);
        }

        //get crop result
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //set user image
                buttonSetUserImage.setImageURI(result.getUri());
                buttonSetUserImage.setBackground(null);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                try {
                    throw error;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayUserProfile() {
        username.setText(user.getUsername());
        email.setText(user.getEmail());
    }

    private void createBlogUser(FirebaseUser firebaseUser) {
        user = new BlogUserModel(
                firebaseUser.getDisplayName(),
                firebaseUser.getEmail(),
                ""
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