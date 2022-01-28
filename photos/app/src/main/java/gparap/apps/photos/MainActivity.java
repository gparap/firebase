package gparap.apps.photos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import gparap.apps.photos.ui.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle the splash screen transition
        SplashScreen.installSplashScreen(this);

        //check if user is authenticated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            //redirect to authentication
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        setContentView(R.layout.activity_main);

        //setup navigation component
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            //setup bottom navigation view
            BottomNavigationView bottomNavView = findViewById(R.id.bottom_navigation_view);
            NavigationUI.setupWithNavController(bottomNavView, navController);
        }
    }
}