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
package gparap.apps.chat.ui.auth;

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

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import gparap.apps.chat.R;

public class RegisterActivityInstrumentedTest {
    private View decorView;

    @Before
    public void setUp() {
        ActivityScenario<RegisterActivity> activityScenario = ActivityScenario.launch(RegisterActivity.class);
        activityScenario.onActivity(activity -> {
            //get the top-level window decor view
            decorView = activity.getWindow().getDecorView();
        });
    }

    @Test
    public void isVisible_toolbar_register() {
        onView(withId(R.id.toolbar_register)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_image_view_register() {
        onView(withId(R.id.image_view_register)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_edit_text_register_display_name() {
        onView(withId(R.id.edit_text_register_display_name)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_edit_text_register_email() {
        onView(withId(R.id.edit_text_register_email)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_edit_text_register_confirm_password() {
        onView(withId(R.id.edit_text_register_confirm_password)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_button_register() {
        onView(withId(R.id.button_register)).check(matches(isDisplayed()));
    }

    @Test
    public void isNotVisible_progress_register() {
        onView(withId(R.id.progress_register)).check(matches(not(isDisplayed())));
    }

    @Test
    public void validateUserInput_emptyDisplayName_showErrorMessage() {
        //make sure display name is empty
        onView(withId(R.id.edit_text_register_display_name)).perform(clearText());

        //attempt registration
        onView(withId(R.id.button_register)).perform(click());

        onView(withText(R.string.toast_empty_display_name))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));

        delayForToastMessageToDisappear();
    }

    @Test
    public void validateUserInput_emptyEmail_showErrorMessage() {
        //make sure display name is not empty and email is empty
        onView(withId(R.id.edit_text_register_display_name)).perform(typeText("displayName"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_email)).perform(clearText());

        //attempt registration
        onView(withId(R.id.button_register)).perform(click());

        onView(withText(R.string.toast_empty_email))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));

        delayForToastMessageToDisappear();
    }

    @Test
    public void validateUserInput_emptyPassword_showErrorMessage() {
        //make sure display name and email are not empty and password is empty
        onView(withId(R.id.edit_text_register_display_name)).perform(typeText("displayName"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_email)).perform(typeText("what@ever.com"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_password)).perform(clearText());

        //attempt registration
        onView(withId(R.id.button_register)).perform(click());

        onView(withText(R.string.toast_empty_password))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));

        delayForToastMessageToDisappear();
    }

    @Test
    public void validateUserInput_emptyPasswordConfirmation_showErrorMessage() {
        //make sure display name, email and password are not empty and password confirmation is empty
        onView(withId(R.id.edit_text_register_display_name)).perform(typeText("displayName"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_email)).perform(typeText("what@ever.com"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_password)).perform(typeText("654321"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_confirm_password)).perform(clearText());

        //attempt registration
        onView(withId(R.id.button_register)).perform(click());

        onView(withText(R.string.toast_empty_confirm_password))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));

        delayForToastMessageToDisappear();
    }

    @Test
    public void validateUserInput_passwordsDoNotMatch_showErrorMessage() {
        String password = "123456";

        //make sure all fields except password confirmation are filled
        onView(withId(R.id.edit_text_register_display_name)).perform(typeText("displayName"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_email)).perform(typeText("what@ever.com"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_text_register_password)).perform(typeText("654321"));
        closeSoftKeyboard();

        //enter a wrong password confirmation
        onView(withId(R.id.edit_text_register_confirm_password)).perform(typeText(password.replace("1", "0")));
        closeSoftKeyboard();

        //attempt registration
        onView(withId(R.id.button_register)).perform(click());

        onView(withText(R.string.toast_unmatched_passwords))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));

        delayForToastMessageToDisappear();
    }

    private void delayForToastMessageToDisappear() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}