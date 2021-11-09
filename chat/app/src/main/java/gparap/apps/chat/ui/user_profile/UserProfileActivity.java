package gparap.apps.chat.ui.user_profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;

import gparap.apps.chat.R;
import gparap.apps.chat.data.UserModel;
import gparap.apps.chat.utils.AppConstants;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView userProfileImage, editDisplayName, editEmail, editPassword;
    private EditText displayName, email, password, confirmPassword;
    private Button buttonUpdate;
    private ProgressBar progressUpdate;
    private boolean isProfileImageChanged;
    private final UserModel user = new UserModel();
    private UserProfleActivityViewModel viewModel;
    private Uri profileImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupToolbar();
        getWidgets();
        setListenersToMakeInputWidgetsEditable();

        //create ViewModel for this activity
        viewModel = new ViewModelProvider(this).get(UserProfleActivityViewModel.class);

        //display user profile details
        displayName.setText(viewModel.getDisplayName());
        email.setText(viewModel.getEmail());

        //pick and set user profile image
        userProfileImage = findViewById(R.id.image_view_user_profile);
        userProfileImage.setOnClickListener(view ->
                pickUserProfileImage()
        );

        //update user profile
        buttonUpdate.setOnClickListener(view -> {
            if (isProfileImageChanged) {
                progressUpdate.setVisibility(View.VISIBLE);
                if (viewModel.updateUserProfileImage(profileImageUri, progressUpdate)){
                    isProfileImageChanged = true;
                }
            }
            if (isDisplayNameChanged()) {
                displayProgress();
                viewModel.updateUserDisplayName(displayName.getText().toString().trim(), isProfileImageChanged, progressUpdate);
                user.setDisplayName(displayName.getText().toString());
            }
            if (isEmailChanged()) {
                displayProgress();
                viewModel.updateUserEmail(email.getText().toString().trim(), user, isProfileImageChanged, progressUpdate);
            }
            if (password.isEnabled() && isPasswordConfirmed()) {
                displayProgress();
                viewModel.updateUserPassword(password.getText().toString().trim(), user, isProfileImageChanged, progressUpdate);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            //image picker result
            if (requestCode == AppConstants.ACTION_PICK_REQUEST_CODE) {
                userProfileImage.setImageBitmap(viewModel.getUserProfileImageBitmap(data.getData()));
                isProfileImageChanged = true;
                profileImageUri = data.getData();
            }
        }
    }

    private boolean isPasswordConfirmed() {
        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.toast_unmatched_passwords), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private boolean isEmailChanged() {
        //hold the old value of the email
        user.setEmail(viewModel.getEmail());

        return user.getEmail() != null && !user.getEmail().equals(email.getText().toString());
    }

    private boolean isDisplayNameChanged() {
        return user.getDisplayName() != null && !user.getDisplayName().equals(displayName.getText().toString());
    }

    private void displayProgress() {
        if (!isProfileImageChanged) {
            progressUpdate.setVisibility(View.VISIBLE);
        }
    }

    private void pickUserProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, AppConstants.ACTION_PICK_REQUEST_CODE);
    }

    private void setListenersToMakeInputWidgetsEditable() {
        //make change username editable
        editDisplayName.setOnClickListener(v -> {
                    displayName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    displayName.setEnabled(true);

                    //hold the old displayName value
                    user.setDisplayName(displayName.getText().toString());
                }
        );

        //make change email editable
        editEmail.setOnClickListener(v -> {
                    email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    email.setEnabled(true);
                }
        );

        //make change password editable and show password confirmation
        editPassword.setOnClickListener(v -> {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setEnabled(true);
            confirmPassword.setVisibility(View.VISIBLE);
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