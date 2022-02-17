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
import static org.hamcrest.core.IsNot.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.photos.R;

public class RegisterActivityInstrumentedTest {
    private View decorView;

    @Before
    public void setUp() {
        ActivityScenario<RegisterActivity> activityScenario = ActivityScenario.launch(RegisterActivity.class);
        activityScenario.onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @Test
    @SmallTest
    public void isVisible_image_view_app_logo_register() {
        onView(withId(R.id.image_view_app_logo_register)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_text_view_title_register() {
        onView(withId(R.id.text_view_title_register)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_edit_text_register_email() {
        onView(withId(R.id.edit_text_register_email)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_edit_text_register_password() {
        onView(withId(R.id.edit_text_register_password)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_edit_text_register_password_confirm() {
        onView(withId(R.id.edit_text_register_password_confirm)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_button_register() {
        onView(withId(R.id.button_register)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isNotVisible_progress_register() {
        onView(withId(R.id.progress_register)).check(matches(not(isDisplayed())));
    }

    @Test
    @LargeTest
    public void passwordNotContainsRequestedNumberOfSpecialChars_showErrorMessage() {
        //fill the username and password fields
        onView(withId(R.id.edit_text_register_username)).perform(typeText("user"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_email)).perform(typeText("email@com"));
        closeSoftKeyboard();

        //clear password if not empty and type an invalid one (about the special characters)
        onView(withId(R.id.edit_text_register_password)).perform(clearText());
        onView(withId(R.id.edit_text_register_password)).perform(typeText("pass00"));
        onView(withId(R.id.button_register)).perform(click());

        onView(withText(R.string.toast_error_password_special_chars))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        waitForToastToFade();
    }

    @Test
    @LargeTest
    public void passwordNotContainsRequestedNumberOfDigits_showErrorMessage() {
        //fill the username and password fields
        onView(withId(R.id.edit_text_register_username)).perform(typeText("user"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_email)).perform(typeText("email@com"));
        closeSoftKeyboard();

        //clear password if not empty and type an invalid one (about the digits)
        onView(withId(R.id.edit_text_register_password)).perform(clearText());
        onView(withId(R.id.edit_text_register_password)).perform(typeText("pass!!"));
        onView(withId(R.id.button_register)).perform(click());

        onView(withText(R.string.toast_error_password_digits))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        waitForToastToFade();
    }

    @Test
    @LargeTest
    public void passwordLengthIsNotValid_showErrorMessage() {
        //fill the username and password fields
        onView(withId(R.id.edit_text_register_username)).perform(typeText("user"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_email)).perform(typeText("email@com"));
        closeSoftKeyboard();

        //clear password if not empty and type an invalid length one
        onView(withId(R.id.edit_text_register_password)).perform(clearText());
        onView(withId(R.id.edit_text_register_password)).perform(typeText("p!!00"));
        onView(withId(R.id.button_register)).perform(click());

        onView(withText(R.string.toast_error_password_length))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        waitForToastToFade();
    }

    @Test
    @LargeTest
    public void passwordAndConfirmationDoNotMatch_showErrorMessage() {
        //fill the username and password fields
        onView(withId(R.id.edit_text_register_username)).perform(typeText("user"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_email)).perform(typeText("email@com"));
        closeSoftKeyboard();

        //clear password and confirmation if not empty
        onView(withId(R.id.edit_text_register_password)).perform(clearText());
        onView(withId(R.id.edit_text_register_password_confirm)).perform(clearText());

        //type a correct password with invalid confirmation
        onView(withId(R.id.edit_text_register_password)).perform(typeText("pass!@09"));
        onView(withId(R.id.edit_text_register_password_confirm)).perform(typeText("incorrect"));
        onView(withId(R.id.button_register)).perform(click());

        onView(withText(R.string.toast_error_passwords_match))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        waitForToastToFade();
    }

    //wait for the toast message to fade out based on NotificationManagerService.java (LONG_DELAY)
    private void waitForToastToFade() {
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}