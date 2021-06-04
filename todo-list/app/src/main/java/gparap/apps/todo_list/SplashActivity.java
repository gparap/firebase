package gparap.apps.todo_list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    public static final int SPLASH_DELAY_MILLS = 1667;

    @SuppressWarnings("Convert2Lambda")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //display progress bar
        ProgressBar progressBar = findViewById(R.id.progressBarSplash);
        progressBar.setVisibility(View.VISIBLE);

        //animate widgets

        animateWidgets();

        //splash
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //user is new
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                //user is signed-in
                else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        }, SPLASH_DELAY_MILLS);
    }

    private void animateWidgets() {
        //get widgets
        TextView appName = findViewById(R.id.textViewAppName);
        ImageView appLogo = findViewById(R.id.imageViewLogo);

        //load animations
        Animation animDown = AnimationUtils.loadAnimation(this, R.anim.anim_downwards);
        Animation animUp = AnimationUtils.loadAnimation(this, R.anim.anim_upwards);

        //animate widgets
        appName.setAnimation(animDown);
        appLogo.setAnimation(animUp);
    }
}