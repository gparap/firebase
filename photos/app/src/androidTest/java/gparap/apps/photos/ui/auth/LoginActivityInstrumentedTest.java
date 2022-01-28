/*
 * Copyright 2022 gparap
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
package gparap.apps.photos.ui.auth;

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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.photos.R;

public class LoginActivityInstrumentedTest {
    private View decorView = null;

    @Before
    public void setUp() {
        ActivityScenario<LoginActivity> activityScenario = ActivityScenario.launch(LoginActivity.class);

        //get the top-level window decor view
        activityScenario.onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @Test
    @SmallTest
    public void isVisible_image_view_app_logo_login() {
        onView(withId(R.id.image_view_app_logo_login)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_text_view_title_login() {
        onView(withId(R.id.text_view_title_login)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_edit_text_login_email() {
        onView(withId(R.id.edit_text_login_email)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_edit_text_login_password() {
        onView(withId(R.id.edit_text_login_password)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_text_view_forgot_password() {
        onView(withId(R.id.text_view_forgot_password)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_button_login() {
        onView(withId(R.id.button_login)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isNotVisible_progress_login() {
        onView(withId(R.id.progress_login)).check(matches(not(isDisplayed())));
    }

    @Test
    @SmallTest
    public void emptyEmail_showErrorMessage() {
        //make sure email is empty before login
        onView(withId(R.id.edit_text_login_email)).perform(clearText());
        onView(withId(R.id.button_login)).perform(click());

        onView(withText(R.string.toast_empty_email))
                .inRoot(withDecorView(not(is(decorView))))
                .check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void emptyPassword_showErrorMessage() {
        //make sure email is not empty and password is empty before login
        onView(withId(R.id.edit_text_login_password)).perform(clearText());
        onView(withId(R.id.edit_text_login_email)).perform(typeText("wft"));
        closeSoftKeyboard();
        onView(withId(R.id.button_login)).perform(click());

        onView(withText(R.string.toast_empty_password))
                .inRoot(withDecorView(not(is(decorView))))
                .check(matches(isDisplayed()));
    }
}