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

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.Test;

import gparap.apps.dating.R;

public class LoginActivityInstrumentedTest {
    private View rootView;

    /* Use this test user for convenience (exists by default) */
    private final String testEmail = "gparap@dot.com";
    @SuppressWarnings("FieldCanBeLocal")
    private final String testPassword = "123123";
    private final String wrongPassword = "password";

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
        //clear email text and fill in password
        clearExistingText(R.id.editTextLoginEmail);
        typeInputText(R.id.editTextLoginPassword, wrongPassword);

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_empty_email))
                .inRoot(withDecorView(IsNot.not(is(rootView))))
                .check(matches(isDisplayed()));

        //wait for the toast to disappear
        Thread.sleep(Toast.LENGTH_SHORT);
    }

    @Test
    public void passwordIsEmpty_showErrorMessage() throws InterruptedException {
        //clear password text and fill in email
        clearExistingText(R.id.editTextLoginPassword);
        typeInputText(R.id.editTextLoginEmail, "email@dot.com");

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
        clearExistingText(R.id.editTextLoginEmail);
        clearExistingText(R.id.editTextLoginPassword);

        //fill in email and password
        typeInputText(R.id.editTextLoginEmail, "email_dot.com");
        typeInputText(R.id.editTextLoginPassword, wrongPassword);

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_login_email_type_error))
                .inRoot(withDecorView(IsNot.not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void email_dot_SymbolMissing_showErrorMessage() {
        //clear email and password text
        clearExistingText(R.id.editTextLoginEmail);
        clearExistingText(R.id.editTextLoginPassword);

        //fill in email and password
        typeInputText(R.id.editTextLoginEmail, "email@dotcom");
        typeInputText(R.id.editTextLoginPassword, wrongPassword);

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_login_email_type_error))
                .inRoot(withDecorView(IsNot.not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void email_at_dot_SymbolsMissing_showErrorMessage() {
        //clear email and password text
        clearExistingText(R.id.editTextLoginEmail);
        clearExistingText(R.id.editTextLoginPassword);

        //fill in email and password
        typeInputText(R.id.editTextLoginEmail, "email_dot_com");
        typeInputText(R.id.editTextLoginPassword, wrongPassword);

        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.string.toast_login_email_type_error))
                .inRoot(withDecorView(IsNot.not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void onButtonLoginClick_loginSuccessful() throws InterruptedException {
        //clear email and password text
        clearExistingText(R.id.editTextLoginEmail);
        clearExistingText(R.id.editTextLoginPassword);

        //sign-in test user with email and password
        typeInputText(R.id.editTextLoginEmail, testEmail);
        typeInputText(R.id.editTextLoginPassword, testPassword);
        try {
            onView(withId(R.id.buttonLogin)).perform(click());

            //wait a little to sign-in
            Thread.sleep(1667);

            //test if we are redirected to main activity
            onView(withId(R.id.layout_activity_main)).check(matches(isDisplayed()));

            //sign-out test user
            signOut();

        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            //sign-out test user
            signOut();
        }
    }

    @Test
    public void onButtonLoginClick_loginFailed() {
        //clear email and password text
        clearExistingText(R.id.editTextLoginEmail);
        clearExistingText(R.id.editTextLoginPassword);

        //try to  sign-in test user with email and wrong password
        typeInputText(R.id.editTextLoginEmail, testEmail);
        typeInputText(R.id.editTextLoginPassword, wrongPassword);
        onView(withId(R.id.buttonLogin)).perform(click());

        //test if error message is displayed
        onView(withText(R.string.toast_login_failed))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    private void clearExistingText(int viewId) {
        onView(withId(viewId)).perform(clearText());
        closeSoftKeyboard();
    }

    private void typeInputText(int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
        closeSoftKeyboard();
    }

    private void signOut() throws InterruptedException {
        try {
            FirebaseAuth.getInstance().signOut();
            Thread.sleep(1667);
        } catch (Exception ignored) {} finally {
            Thread.sleep(1667);
        }
    }
}