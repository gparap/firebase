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
package gparap.apps.social_media;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;

public class ProfileActivityActivityTest {
    @Before
    public void setUp() {
        //launch activity
        ActivityScenario.launch(ProfileActivity.class);
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
    public void isVisible_editTextProfileEmail() {
        onView(withId(R.id.editTextProfileEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_imageButtonProfileChangeEmail() {
        onView(withId(R.id.imageButtonProfileChangeEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextProfilePassword() {
        onView(withId(R.id.editTextProfilePassword)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_imageButtonProfileChangePassword() {
        onView(withId(R.id.imageButtonProfileChangePassword)).check(matches(isDisplayed()));
    }

    @Test
    public void isInvisible_editTextProfilePasswordConfirm() {
        onView(withId(R.id.editTextProfilePasswordConfirm)).check(matches(not(isDisplayed())));
    }

    @Test
    public void isVisible_buttonProfileUpdate() {
        onView(withId(R.id.buttonProfileUpdate)).check(matches(isDisplayed()));
    }

    @Test
    public void isInvisible_progressBarProfile() {
        onView(withId(R.id.progressBarProfile)).check(matches(not(isDisplayed())));
    }
}