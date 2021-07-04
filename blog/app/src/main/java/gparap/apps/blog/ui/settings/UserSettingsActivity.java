package gparap.apps.blog.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

import gparap.apps.blog.R;

public class UserSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User Settings");
    }
}