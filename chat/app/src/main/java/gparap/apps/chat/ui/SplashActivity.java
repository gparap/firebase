/*
 * Copyright 2021 gparap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gparap.apps.chat.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import gparap.apps.chat.MainActivity;
import gparap.apps.chat.R;

//TODO: animation
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final long TRANSITION_DELAY_MILLIS = 1000L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //get the currently signed-in FirebaseUser or null if there is none
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //splash
        new Handler().postDelayed(() -> {
            Intent intent;

            //if there is no signed-in FirebaseUser, create an intent for LoginActivity
            if (firebaseUser == null) {
                intent = new Intent(this, LoginActivity.class);

                //if there is a signed-in FirebaseUser, create an intent for MainActivity
            } else {
                intent = new Intent(this, MainActivity.class);
            }

            //start intent and close activity
            startActivity(intent);
            this.finish();

        }, TRANSITION_DELAY_MILLIS);
    }
}