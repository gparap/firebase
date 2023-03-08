/*
 * Copyright 2023 gparap
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
package gparap.apps.social_media;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

public class MainActivityActivityTest {
    ActivityScenario<MainActivity> activityScenario;
    View rootView = null;

    @Before
    public void setUp() throws InterruptedException {
        //sign-in test user and wait for database transaction
        FirebaseAuth.getInstance().signInWithEmailAndPassword("gparap@dot.com", "123123");
        Thread.sleep(1667);

        //launch activity
        activityScenario = ActivityScenario.launch(MainActivity.class);

        //get root view
        activityScenario.onActivity(activity -> rootView = activity.getWindow().getDecorView());
    }

    @Test
    public void signOutUser_isSuccessful() {
        signOut();
        onView(withId(R.id.layout_activity_login)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutUser_displayToast() {
        signOut();
        onView(withText(R.string.toast_login_successful))
                .inRoot(withDecorView(not(rootView)))
                .check(matches(isDisplayed()));
    }

    /**
     * Signs-out either from the appbar icon or the menu item.
     */
    private void signOut() {
        try {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
            onView(withText(R.string.text_logout)).perform(click());
        } catch (Exception e) {
            onView(withId(R.id.main_menu_item_logout)).perform(click());
        }
    }
}