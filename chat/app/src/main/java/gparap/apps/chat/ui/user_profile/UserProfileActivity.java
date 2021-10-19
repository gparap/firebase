package gparap.apps.chat.ui.user_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import gparap.apps.chat.R;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView userProfileImage, editDisplayName, editEmail, editPassword;
    private EditText displayName, email, password, confirmPassword;
    private Button buttonUpdate;
    private ProgressBar progressUpdate;
    private boolean isDisplayNameEditable, isEmailEditable, isPasswordEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupToolbar();
        getWidgets();
        setListenersToMakeInputWidgetsEditable();
        displayUserProfileDetails();
        //TODO: update user profile
        //TODO: update password
    }

    private void displayUserProfileDetails() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            displayName.setText(firebaseUser.getDisplayName());
            email.setText(firebaseUser.getEmail());
        }
    }

    private void setListenersToMakeInputWidgetsEditable() {
        //make change username editable
        editDisplayName.setOnClickListener(v -> {
                    displayName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    displayName.setEnabled(true);
                    isDisplayNameEditable = true;
                }
        );

        //make change email editable
        editEmail.setOnClickListener(v -> {
                    email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    email.setEnabled(true);
                    isEmailEditable = true;
                }
        );

        //make change password editable and show password confirmation
        editPassword.setOnClickListener(v -> {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setEnabled(true);
            confirmPassword.setVisibility(View.VISIBLE);
            isPasswordEditable = true;
        });
    }

    private void getWidgets() {
        userProfileImage = findViewById(R.id.image_view_user_profile);
        editDisplayName = findViewById(R.id.image_view_edit_user_display_name);
        editEmail = findViewById(R.id.image_view_edit_user_email);
        editPassword = findViewById(R.id.image_view_edit_password);
        displayName = findViewById(R.id.edit_text_user_profile_display_name);
        email = findViewById(R.id.edit_text_user_profile_email);
        password = findViewById(R.id.edit_text_user_profile_password);
        confirmPassword = findViewById(R.id.edit_text_user_profile_confirm_password);
        buttonUpdate = findViewById(R.id.button_update_user_profile);
        progressUpdate = findViewById(R.id.progress_update_user_profile);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_user_profile);
        toolbar.setTitle(getResources().getString(R.string.title_activity_user_profile));
    }
}