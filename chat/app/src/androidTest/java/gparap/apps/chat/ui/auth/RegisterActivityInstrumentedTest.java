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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.IsNot.not;

import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.chat.R;

public class RegisterActivityInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(RegisterActivity.class);
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
}