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
package gparap.apps.blog.ui.settings;

import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.blog.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

public class UserSettingsActivityInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(UserSettingsActivity.class);
    }

    @Test
    public void isVisible_imageButtonUserSettings() {
        onView(withId(R.id.imageButtonUserSettings)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextUserSettingsUsername() {
        onView(withId(R.id.editTextUserSettingsUsername)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_imageButtonUserSettingsChangeUsername() {
        onView(withId(R.id.imageButtonUserSettingsChangeUsername)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextUserSettingsEmail() {
        onView(withId(R.id.editTextUserSettingsEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_imageButtonUserSettingsChangeEmail() {
        onView(withId(R.id.imageButtonUserSettingsChangeEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextUserSettingsPassword() {
        onView(withId(R.id.editTextUserSettingsPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_imageButtonUserSettingsChangePassword() {
        onView(withId(R.id.imageButtonUserSettingsChangePassword)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextUserSettingsPasswordConfirm() {
        onView(withId(R.id.editTextUserSettingsPasswordConfirm)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonUserSettingsUpdate() {
        onView(withId(R.id.buttonUserSettingsUpdate)).check(matches(isDisplayed()));
    }

    @Test
    public void isNotVisible_progressBarUserSettings() {
        onView(withId(R.id.progressBarUserSettings)).check(matches(not(isDisplayed())));
    }
}