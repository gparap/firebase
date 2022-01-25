package gparap.apps.social_photos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup navigation component
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            //setup bottom navigation view
            BottomNavigationView bottomNavView = findViewById(R.id.bottom_navigation_view);
            NavigationUI.setupWithNavController(bottomNavView, navController);

//            //setup toolbar with navController
//            Toolbar toolbar = findViewById(R.id.toolbar_main);
//            NavigationUI.setupWithNavController(toolbar, navController);
        }
    }
}