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
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.dating.R;

public class RegisterActivityInstrumentedTest {
    private View rootView;

    @Before
    public void setUp() {
        //get scenario for this activity
        ActivityScenario<RegisterActivity> activityScenario = ActivityScenario.launch(RegisterActivity.class);

        //retrieve the top-level window decor view
        activityScenario.onActivity(activity -> rootView = activity.getWindow().getDecorView());
    }

    @Test
    public void isVisible_addImageButton() {
        onView(ViewMatchers.withId(R.id.imageViewRegisterButton)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextUsername() {
        onView(withId(R.id.editTextRegisterUsername)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextEmail() {
        onView(withId(R.id.editTextRegisterEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextPassword() {
        onView(withId(R.id.editTextRegisterPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextPasswordConfirmation() {
        onView(withId(R.id.editTextRegisterPasswordConfirm)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonRegister() {
        onView(withId(R.id.buttonRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void isInvisible_progressBar() {
        onView(withId(R.id.progressBarRegister)).check(matches(not(isDisplayed())));
    }


    @Test
    public void usernameIsEmpty_showErrorMessage() {
        clearExistingText(R.id.editTextRegisterUsername);
        closeSoftKeyboard();
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_username))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void emailIsEmpty_showErrorMessage() {
        //fill in username first
        typeInputText(R.id.editTextRegisterUsername, "username");

        clearExistingText(R.id.editTextRegisterEmail);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_email))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void passwordIsEmpty_showErrorMessage() {
        //fill in username, email
        typeInputText(R.id.editTextRegisterUsername, "username");
        typeInputText(R.id.editTextRegisterEmail, "email");

        clearExistingText(R.id.editTextRegisterPassword);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_password))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void passwordConfirmationIsEmpty_showErrorMessage() {
        //fill in username, email and password
        typeInputText(R.id.editTextRegisterUsername, "username");
        typeInputText(R.id.editTextRegisterEmail, "email");
        typeInputText(R.id.editTextRegisterPassword, "password");

        clearExistingText(R.id.editTextRegisterPasswordConfirm);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_password_confirm))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void passwordAndConfirmationDoNotMatch_showErrorMessage() {
        //fill in username, email
        typeInputText(R.id.editTextRegisterUsername, "username");
        typeInputText(R.id.editTextRegisterEmail, "email");

        //fill in password and confirmation
        clearExistingText(R.id.editTextRegisterPassword);
        typeInputText(R.id.editTextRegisterPassword, "password");
        clearExistingText(R.id.editTextRegisterPasswordConfirm);
        typeInputText(R.id.editTextRegisterPassword, "not matching");

        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_failed_password_confirm))
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
}