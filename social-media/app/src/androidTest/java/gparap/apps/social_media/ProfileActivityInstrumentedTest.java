/*
 * Copyright 2024 gparap
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
package gparap.apps.social_media;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.filters.LargeTest;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class ProfileActivityInstrumentedTest {
    ActivityScenario<ProfileActivity> activityScenario;
    String oldUsername, newUsername;
    String oldPhone, newPhone;

    //!!! Use this default test user credentials, they don't change
    final private String testUser_email = "gp@dot.com";
    final private String testUser_password = "123123";

    @Before
    public void setUp() {
        //launch activity
        activityScenario = ActivityScenario.launch(ProfileActivity.class);
    }

    @Test
    public void isVisible_imageButtonUserProfile() {
        onView(withId(R.id.imageButtonUserProfile)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextProfileUsername() {
        onView(withId(R.id.editTextProfileUsername)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_imageButtonProfileChangeUsername() {
        onView(withId(R.id.imageButtonProfileChangeUsername)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextProfileMobile() {
        onView(withId(R.id.editTextProfileMobile)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_imageButtonProfileChangeMobile() {
        onView(withId(R.id.imageButtonProfileChangeMobile)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonProfileUpdate() {
        onView(withId(R.id.buttonProfileUpdate)).check(matches(isDisplayed()));
    }

    @Test
    public void isInvisible_progressBarProfile() {
        onView(withId(R.id.progressBarProfile)).check(matches(not(isDisplayed())));
    }

    @Test
    @LargeTest
    public void isCorrect_changeUsername() throws InterruptedException {
        //sign in with Firebase and get the username
        FirebaseAuth.getInstance().signInWithEmailAndPassword(testUser_email, testUser_password);
        Thread.sleep(1667);
        oldUsername = getEditTextValueById(R.id.editTextProfileUsername);

        //create a random int to add to the username string
        newUsername = getValueAtRandom();

        //change the username
        updateEditTextValueById(R.id.editTextProfileUsername, newUsername);

        //wait for Firebase
        Thread.sleep(4667);

        //start activity again
        activityScenario.recreate();

        //sign in with Firebase and get the username
        FirebaseAuth.getInstance().signInWithEmailAndPassword(testUser_email, testUser_password);
        Thread.sleep(1667);
        newUsername = getEditTextValueById(R.id.editTextProfileUsername);

        //assert the username has changed
        assert !Objects.equals(oldUsername, newUsername);

        //restore old username
        updateEditTextValueById(R.id.editTextProfileUsername, oldUsername);
    }

    @Test
    @LargeTest
    public void isCorrect_changePhone() throws InterruptedException {
        //sign in with Firebase and get the old phone
        FirebaseAuth.getInstance().signInWithEmailAndPassword(testUser_email, testUser_password);
        Thread.sleep(1667);
        oldPhone = getEditTextValueById(R.id.editTextProfileMobile);

        //create a random int for the phone string
        newPhone = getValueAtRandom();

        //change the phone
        updateEditTextValueById(R.id.editTextProfileMobile, newPhone);

        //wait for Firebase
        Thread.sleep(4667);

        //start activity again
        activityScenario.recreate();

        //sign in with Firebase and get the new phone
        FirebaseAuth.getInstance().signInWithEmailAndPassword(testUser_email, testUser_password);
        Thread.sleep(1667);
        newPhone = getEditTextValueById(R.id.editTextProfileMobile);

        //assert the username has changed
        assert !Objects.equals(oldPhone, newPhone);
    }

    private String getEditTextValueById(int id) {
        AtomicReference<String> value = new AtomicReference<>("");
        activityScenario.onActivity(activity -> {
            EditText editText = activity.findViewById(id);
            value.set(editText.getText().toString());
        });
        return String.valueOf(value);
    }

    private void updateEditTextValueById(int id, String newValue) {
        onView(withId(R.id.imageButtonProfileChangeUsername)).perform(click());
        onView(withId(id)).perform(click());
        onView(withId(id)).perform(clearText());
        onView(withId(id)).perform(typeText(newValue));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.buttonProfileUpdate)).perform(click());
    }

    private String getValueAtRandom() {
        Random random = new Random();
        return ("test" + random.nextInt(999999999));
    }
}