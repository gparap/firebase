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
package gparap.apps.blog.auth;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.blog.R;

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

public class RegisterActivityInstrumentedTest {
    private View rootView;

    @Before
    public void setUp() {
        //launch activity and retrieve the top-level window decor view
        ActivityScenario<RegisterActivity> activityScenario = ActivityScenario.launch(RegisterActivity.class);
        activityScenario.onActivity(activity -> rootView = activity.getWindow().getDecorView());
    }

    @Test
    public void isVisible_imageLogo() {
        onView(ViewMatchers.withId(R.id.imageViewRegisterLogo)).check(matches(isDisplayed()));
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
        clearPreviousText(R.id.editTextRegisterUsername);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_username))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void emailIsEmpty_showErrorMessage() {
        //fill in username first
        typeNewText(R.id.editTextRegisterUsername, "username");

        clearPreviousText(R.id.editTextRegisterEmail);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_email))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void passwordIsEmpty_showErrorMessage() {
        //fill in username, email
        typeNewText(R.id.editTextRegisterUsername, "username");
        typeNewText(R.id.editTextRegisterEmail, "email");

        clearPreviousText(R.id.editTextRegisterPassword);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_password))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void passwordConfirmationIsEmpty_showErrorMessage() {
        //fill in username, email and password
        typeNewText(R.id.editTextRegisterUsername, "username");
        typeNewText(R.id.editTextRegisterEmail, "email");
        typeNewText(R.id.editTextRegisterPassword, "password");

        clearPreviousText(R.id.editTextRegisterPasswordConfirm);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_password_confirm))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void passwordAndConfirmationDoNotMatch_showErrorMessage() {
        //fill in username, email
        typeNewText(R.id.editTextRegisterUsername, "username");
        typeNewText(R.id.editTextRegisterEmail, "email");

        clearPreviousTextAndTypeNewText(R.id.editTextRegisterPassword, "password");
        clearPreviousTextAndTypeNewText(R.id.editTextRegisterPasswordConfirm, "do no match");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_failed_password_confirm))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void userRegistration_success() throws InterruptedException {
        //fill-in the input fields for a test user
        typeNewText(R.id.editTextRegisterUsername, "username");
        typeNewText(R.id.editTextRegisterEmail, "username@email.com");
        typeNewText(R.id.editTextRegisterPassword, "123456");
        typeNewText(R.id.editTextRegisterPasswordConfirm, "123456");

        //register user and wait for firebase
        onView(withId(R.id.buttonRegister)).perform(click());
        Thread.sleep(1677);

        onView(withId(R.id.layout_activity_main)).check(matches(isDisplayed()));

        //delete test user and wait for firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        firebaseUser.delete();
        Thread.sleep(1677);
    }

    @Test
    public void userRegistration_failed() throws InterruptedException {
        //!!! test user (user1@com.com) exists in firebase
        String existingUser = "user1@com.com";

        //fill-in the input fields for a test user that matches existing user
        typeNewText(R.id.editTextRegisterUsername, "username");
        typeNewText(R.id.editTextRegisterEmail, existingUser);
        typeNewText(R.id.editTextRegisterPassword, "123456");
        typeNewText(R.id.editTextRegisterPasswordConfirm, "123456");

        //register user and wait for firebase
        onView(withId(R.id.buttonRegister)).perform(click());
        Thread.sleep(1677);

        onView(withText(R.string.toast_registration_failed))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    private void clearPreviousText(int viewId) {
        onView(withId(viewId)).perform(clearText());
        closeSoftKeyboard();
    }

    private void clearPreviousTextAndTypeNewText(int viewId, String text) {
        onView(withId(viewId)).perform(clearText());
        onView(withId(viewId)).perform(typeText(text));
        closeSoftKeyboard();
    }

    private void typeNewText(int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
        closeSoftKeyboard();
    }
}