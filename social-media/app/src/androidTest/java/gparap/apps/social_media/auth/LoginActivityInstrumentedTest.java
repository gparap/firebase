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
package gparap.apps.social_media.auth;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gparap.apps.social_media.R;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivityInstrumentedTest {
    ActivityScenario<LoginActivity> activityScenario;
    View rootView = null;

    @Before
    public void setUp() {
        //sign-out existing user (if any)
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception ignored) {
        }

        //launches activity
        activityScenario = ActivityScenario.launch(LoginActivity.class);

        //get root view
        activityScenario.onActivity(activity -> rootView = activity.getWindow().getDecorView());
    }

    @Test
    public void isVisible_logoPlaceholder() {
        onView(ViewMatchers.withId(R.id.imageViewLoginLogoPlaceholder)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextEmail() {
        onView(withId(R.id.editTextLoginEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextPassword() {
        onView(withId(R.id.editTextLoginPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonLogin() {
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonRegister() {
        onView(withId(R.id.buttonRegisterRedirect)).check(matches(isDisplayed()));
    }

    @Test
    public void isInvisible_progressBar() {
        onView(withId(R.id.progressBarLogin)).check(matches(not(isDisplayed())));
    }

    @Test
    public void isLoginSuccessful_gotoMainActivity() throws InterruptedException {
        //type correct credentials
        onView(withId(R.id.editTextLoginEmail)).perform(typeText("gparap@dot.com"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextLoginPassword)).perform(typeText("123123"));
        closeSoftKeyboard();

        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(1667); //wait for firebase..
        onView(withId(R.id.layout_activity_main)).check(matches(isDisplayed()));
    }

    @Test
    public void isLoginUnsuccessful_displayToast() {
        //type incorrect credentials
        onView(withId(R.id.editTextLoginEmail)).perform(typeText("gparap@dot.com"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextLoginPassword)).perform(typeText("123"));
        closeSoftKeyboard();

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_wrong_credentials))
                .inRoot(withDecorView(not(rootView)))
                .check(matches(isDisplayed()));
    }

    @Test
    @Ignore("User must be already signed-in with their Google account.")
    public void isLoginWithGoogleSuccessful_gotoMainActivity() throws InterruptedException {
        onView(withId(R.id.buttonLoginWithGoogle)).perform(click());
        Thread.sleep(1667); //wait for google..
        onView(withId(R.id.layout_activity_main)).check(matches(isDisplayed()));
    }

    @Test
    public void isUsernameEmpty_displayToast() {
        //clear email first
        onView(withId(R.id.editTextLoginEmail)).perform(clearText());
        closeSoftKeyboard();

        //type anything to password
        onView(withId(R.id.editTextLoginPassword)).perform(typeText("whatever"));
        closeSoftKeyboard();

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_empty_email))
                .inRoot(withDecorView(not(rootView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void isPasswordEmpty_displayToast() {
        //clear password first
        onView(withId(R.id.editTextLoginPassword)).perform(clearText());

        //type anything to email
        onView(withId(R.id.editTextLoginEmail)).perform(typeText("whatever"));
        closeSoftKeyboard();

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_empty_password))
                .inRoot(withDecorView(not(rootView)))
                .check(matches(isDisplayed()));
    }
}