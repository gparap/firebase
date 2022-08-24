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
package gparap.apps.dating.auth;

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
import static org.hamcrest.core.Is.is;

import android.view.View;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.Test;

import gparap.apps.dating.R;

public class LoginActivityInstrumentedTest {
    private View rootView;

    @Before
    public void setUp() {
        //get scenario for this activity
        ActivityScenario<LoginActivity> activityScenario = ActivityScenario.launch(LoginActivity.class);

        //retrieve the top-level window decor view
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
    public void onButtonRegisterClick_redirectToRegistration() {
        onView(withId(R.id.buttonRegisterRedirect)).perform(click());
        onView(withId(R.id.layout_activity_register)).check(matches(isDisplayed()));
    }

    @Test
    public void emailIsEmpty_showErrorMessage() throws InterruptedException {
        //clear email text
        onView(withId(R.id.editTextLoginEmail)).perform(clearText());
        closeSoftKeyboard();

        //fill in password
        onView(withId(R.id.editTextLoginPassword)).perform(typeText("password"));
        closeSoftKeyboard();

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_empty_email))
                .inRoot(withDecorView(IsNot.not(is(rootView))))
                .check(matches(isDisplayed()));

        //wait for the toast to disappear
        Thread.sleep(Toast.LENGTH_SHORT);
    }

    @Test
    public void passwordIsEmpty_showErrorMessage() throws InterruptedException {
        //clear password text
        onView(withId(R.id.editTextLoginPassword)).perform(clearText());
        closeSoftKeyboard();

        //fill in email
        onView(withId(R.id.editTextLoginEmail)).perform(typeText("email@dot.com"));
        closeSoftKeyboard();

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_empty_password))
                .inRoot(withDecorView(IsNot.not(is(rootView))))
                .check(matches(isDisplayed()));

        //wait for the toast to disappear
        Thread.sleep(Toast.LENGTH_SHORT);
    }

    @Test
    public void email_at_SymbolMissing_showErrorMessage() {
        //clear email and password text
        onView(withId(R.id.editTextLoginEmail)).perform(clearText());
        onView(withId(R.id.editTextLoginPassword)).perform(clearText());
        closeSoftKeyboard();

        //fill in email and password
        onView(withId(R.id.editTextLoginEmail)).perform(typeText("emaildot.com"));
        onView(withId(R.id.editTextLoginPassword)).perform(typeText("password"));
        closeSoftKeyboard();

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_login_email_type_error))
                .inRoot(withDecorView(IsNot.not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void email_dot_SymbolMissing_showErrorMessage() {
        //clear email and password text
        onView(withId(R.id.editTextLoginEmail)).perform(clearText());
        onView(withId(R.id.editTextLoginPassword)).perform(clearText());
        closeSoftKeyboard();

        //fill in email and password
        onView(withId(R.id.editTextLoginEmail)).perform(typeText("email@dotcom"));
        onView(withId(R.id.editTextLoginPassword)).perform(typeText("password"));
        closeSoftKeyboard();

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_login_email_type_error))
                .inRoot(withDecorView(IsNot.not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void email_at_dot_SymbolsMissing_showErrorMessage() {
        //clear email and password text
        onView(withId(R.id.editTextLoginEmail)).perform(clearText());
        onView(withId(R.id.editTextLoginPassword)).perform(clearText());
        closeSoftKeyboard();

        //fill in email and password
        onView(withId(R.id.editTextLoginEmail)).perform(typeText("emaildotcom"));
        onView(withId(R.id.editTextLoginPassword)).perform(typeText("password"));
        closeSoftKeyboard();

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_login_email_type_error))
                .inRoot(withDecorView(IsNot.not(is(rootView))))
                .check(matches(isDisplayed()));
    }
}