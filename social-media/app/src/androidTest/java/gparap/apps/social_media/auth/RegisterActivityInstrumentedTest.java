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
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.social_media.R;

public class RegisterActivityInstrumentedTest {
    private View decorView;
    final private String username = "test_user";
    final private String phone = "0123456789";
    final private String email = "test@dot.com";
    final private String password = "123456";
    final private String passwordConfirm = "123456";

    @Before
    public void setUp() {
        //sign-out existing user (if any)
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception ignored) {
        }

        //launch activity
        ActivityScenario<RegisterActivity> activityScenario = ActivityScenario.launch(RegisterActivity.class);

        //get root view
        activityScenario.onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @Test
    public void isVisible_imageViewRegisterPlaceholder() {
        onView(ViewMatchers.withId(R.id.imageViewRegisterPlaceholder)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextRegisterUsername() {
        onView(withId(R.id.editTextRegisterUsername)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextRegisterMobile() {
        onView(withId(R.id.editTextRegisterMobile)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextRegisterEmail() {
        onView(withId(R.id.editTextRegisterEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextRegisterPassword() {
        onView(withId(R.id.editTextRegisterPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextRegisterConfirmPassword() {
        onView(withId(R.id.editTextRegisterConfirmPassword)).check(matches(isDisplayed()));
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
    public void registerNewUser_usernameIsEmpty_showToast() throws InterruptedException {
        clearInputFields();
        fillInputFields("", phone, email, password, passwordConfirm);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_username))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
        waitForToastToFadeOut();
    }

    @Test
    public void registerNewUser_emailIseEmpty_showToast() throws InterruptedException {
        clearInputFields();
        fillInputFields(username, phone, "", password, passwordConfirm);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_email))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
        waitForToastToFadeOut();
    }

    @Test
    public void registerNewUser_emailIsMalformed_showToast() throws InterruptedException {
        clearInputFields();
        fillInputFields(username, phone, "invalid", password, passwordConfirm);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_invalid_email))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
        waitForToastToFadeOut();
    }

    @Test
    public void registerNewUser_phoneIsMalformed_showToast() throws InterruptedException {
        clearInputFields();
        fillInputFields(username, "invalid", email, password, passwordConfirm);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_invalid_phone))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
        waitForToastToFadeOut();
    }

    @Test
    public void registerNewUser_passwordIsEmpty_showToast() throws InterruptedException {
        clearInputFields();
        fillInputFields(username, phone, email, "", passwordConfirm);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_password))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
        waitForToastToFadeOut();
    }

    @Test
    public void registerNewUser_passwordConfirmationIsEmpty_showToast() throws InterruptedException {
        clearInputFields();
        fillInputFields(username, phone, email, password, "");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_password_confirm))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
        waitForToastToFadeOut();
    }

    @Test
    public void registerNewUser_passwordsDoNotMatch_showToast() throws InterruptedException {
        clearInputFields();
        fillInputFields(username, phone, email, password, "unmatched");
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_unmatched_passwords))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
        waitForToastToFadeOut();
    }

    @Test
    public void registerNewUser_registrationIsSuccessful_showToast() throws InterruptedException {
        clearInputFields();
        fillInputFields("", phone, email, password, passwordConfirm);
        onView(withId(R.id.buttonRegister)).perform(click());
        onView(withText(R.string.toast_empty_username))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
        waitForToastToFadeOut();
    }

    @Test
    public void registerNewUser_registrationIsSuccessful() throws InterruptedException {
        //register new user and wait for Firebase
        clearInputFields();
        fillInputFields(username, phone, email, password, passwordConfirm);
        onView(withId(R.id.buttonRegister)).perform(click());
        Thread.sleep(4667);

        //login with new registered user and wait for Firebase
        onView(withId(R.id.editTextLoginEmail)).perform(typeText(email));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextLoginPassword)).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(1667);

        //assert user is registered
        onView(withId(R.id.layout_activity_main)).check(matches(isDisplayed()));

        //delete test user from FirebaseAuth and FirebaseDatabase
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        firebaseUser.delete();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("social_media_app").child("users").child(username);
        usersRef.removeValue();

        //wait for Firebase to delete user
        Thread.sleep(4667);
    }

    private void clearInputFields() {
        onView(withId(R.id.editTextRegisterUsername)).perform(clearText());
        onView(withId(R.id.editTextRegisterEmail)).perform(clearText());
        onView(withId(R.id.editTextRegisterMobile)).perform(clearText());
        onView(withId(R.id.editTextRegisterPassword)).perform(clearText());
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(clearText());
    }

    private void fillInputFields(String username, String phone, String email, String password, String passwordConfirm) {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText(username));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextRegisterMobile)).perform(typeText(phone));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText(email));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText(passwordConfirm));
        Espresso.closeSoftKeyboard();
    }

    private void waitForToastToFadeOut() throws InterruptedException {
        Thread.sleep(2000);
    }
}