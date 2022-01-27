package gparap.apps.photos.ui.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.os.Bundle;

import gparap.apps.photos.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle the splash screen transition
        SplashScreen.installSplashScreen(this);

        setContentView(R.layout.activity_login);
    }
}