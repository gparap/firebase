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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.dating.R;

public class LoginActivityInstrumentedTest {
    @Before
    public void setUp() {
        ActivityScenario.launch(LoginActivity.class);
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
}