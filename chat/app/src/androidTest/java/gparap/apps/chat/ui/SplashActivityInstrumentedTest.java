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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.chat.R;
import gparap.apps.chat.ui.splash.SplashActivity;

public class SplashActivityInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(SplashActivity.class);
    }

    @Test
    public void userIsSignedIn_gotoMainActivity() {
        //sign-in current user first
        try {
            Task<AuthResult> task = FirebaseAuth.getInstance().signInAnonymously();
            if (task.isComplete()) {
                //wait for the transition delay
                Thread.sleep(1000L + 667L);

                onView(withId(R.id.layout_activity_main)).check(matches(isDisplayed()));
            }
        } catch (Exception ignored) {
            assert false;
        }
    }

    @Test
    public void userIsNotSignedIn_gotoLoginActivity() {
        //sign-out current user first
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            auth.addAuthStateListener(firebaseAuth -> {
                //wait for the transition delay
                try {
                    Thread.sleep(1000L + 667L);
                    onView(withId(R.id.layout_activity_login)).check(matches(isDisplayed()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ignored) {
            assert false;
        }
    }
}