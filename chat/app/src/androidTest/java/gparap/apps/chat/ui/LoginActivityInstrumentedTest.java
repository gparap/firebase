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

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.chat.R;
import gparap.apps.chat.ui.auth.LoginActivity;

public class LoginActivityInstrumentedTest {
    private View decorView; //top-level window decor view

    @Before
    public void setUp() {
        ActivityScenario<LoginActivity> activityScenario = ActivityScenario.launch(LoginActivity.class);
        activityScenario.onActivity(activity ->
                decorView = activity.getWindow().getDecorView()
        );
    }

    @Test
    public void isVisible_toolbar_login() {
        onView(withId(R.id.toolbar_login)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_image_view_login_logo() {
        onView(withId(R.id.image_view_login)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_edit_text_login_email() {
        onView(withId(R.id.edit_text_login_email)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_edit_text_login_password() {
        onView(withId(R.id.edit_text_login_password)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_text_view_forgot_password() {
        onView(withId(R.id.text_view_forgot_password)).check(matches(isDisplayed()));
    }

    @Test
    public void isNotVisible_progress_login() {
        onView(withId(R.id.progress_login)).check(matches(not(isDisplayed())));
    }

    @Test
    public void isVisible_button_login() {
        onView(withId(R.id.button_login)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_text_view_user_not_registered() {
        onView(withId(R.id.text_view_user_not_registered)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_button_register() {
        onView(withId(R.id.button_goto_register)).check(matches(isDisplayed()));
    }

    @Test
    public void validateUserInput_emptyEmail_showErrorMessage() {
        //make sure email is empty
        onView(withId(R.id.edit_text_login_email)).perform(clearText());

        //attempt login
        onView(withId(R.id.button_login)).perform(click());

        onView(withText(R.string.toast_empty_login_email))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void validateUserInput_emptyPassword_showErrorMessage() {
        //make sure email is not empty and password is empty
        onView(withId(R.id.edit_text_login_email)).perform(typeText("what@ever.com"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_login_password)).perform(clearText());

        //attempt login
        onView(withId(R.id.button_login)).perform(click());

        onView(withText(R.string.toast_empty_login_password))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void userLoginIsNotSuccessful_showErrorMessage() throws InterruptedException {
        //enter wrong user credentials
        onView(withId(R.id.edit_text_login_email)).perform(typeText("wrong@email.com"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_login_password)).perform(typeText("wrong parrword"));
        closeSoftKeyboard();

        //attempt login and wait a little for response
        onView(withId(R.id.button_login)).perform(click());
        Thread.sleep(1667);

        onView(withText(R.string.toast_invalid_credentials))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

    }

    @Test
    public void userLoginIsSuccessful() throws InterruptedException {
        String email = "user@test.com";
        String password = "user@test.com";

        //create test user and wait a little for Firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password);
        Thread.sleep(1667);

        //enter user credentials
        onView(withId(R.id.edit_text_login_email)).perform(typeText(email));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_login_password)).perform(typeText(email));
        closeSoftKeyboard();

        //attempt login and wait a little for Firebase
        onView(withId(R.id.button_login)).perform(click());
        Thread.sleep(1667);

        onView(withId(R.id.layout_activity_main)).check(matches(isDisplayed()));

        //delete test user and wait a little for Firebase
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        firebaseUser.delete();
    }
}