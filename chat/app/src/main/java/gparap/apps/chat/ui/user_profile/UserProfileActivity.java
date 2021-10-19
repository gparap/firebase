package gparap.apps.chat.ui.user_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.appbar.MaterialToolbar;

import gparap.apps.chat.R;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView user_profileImage, editDisplayName, editEmail, editPassword;
    private EditText displayName, email, password, confirmPassword;
    private Button buttonUpdate;
    private ProgressBar progressUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupToolbar();
        getWidgets();
    }

    private void getWidgets() {
        user_profileImage = findViewById(R.id.image_view_user_profile);
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