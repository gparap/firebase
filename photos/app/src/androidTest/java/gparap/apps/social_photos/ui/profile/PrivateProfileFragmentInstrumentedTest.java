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
package gparap.apps.social_photos.ui.profile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.IsNot.not;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.social_photos.R;

public class PrivateProfileFragmentInstrumentedTest {

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(PrivateProfileFragment.class);
    }

    @Test
    @SmallTest
    public void isVisible_image_button_profile_private_user_image() {
        onView(withId(R.id.image_button_profile_private_user_image)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_edit_text_profile_private_username() {
        onView(withId(R.id.edit_text_profile_private_username)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_image_button_profile_private_change_username() {
        onView(withId(R.id.image_button_profile_private_change_username)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_edit_text_profile_private_email() {
        onView(withId(R.id.edit_text_profile_private_email)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_image_button_profile_private_change_email() {
        onView(withId(R.id.image_button_profile_private_change_email)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_edit_text_profile_private_password() {
        onView(withId(R.id.edit_text_profile_private_password)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_image_button_profile_private_change_password() {
        onView(withId(R.id.image_button_profile_private_change_password)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isNotVisible_edit_text_profile_private_password_confirm() {
        onView(withId(R.id.edit_text_profile_private_password_confirm)).check(matches(not(isDisplayed())));
    }

    @Test
    @SmallTest
    public void isVisible_button_profile_private_update() {
        onView(withId(R.id.button_profile_private_update)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isNotVisible_progress_bar_profile_private() {
        onView(withId(R.id.progress_bar_profile_private)).check(matches(not(isDisplayed())));
    }

}