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
package gparap.apps.social_media.auth;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.social_media.R;

public class RegisterActivityInstrumentedTest {
    ActivityScenario<RegisterActivity> activityScenario;

    @Before
    public void setUp() {
        //sign-out existing user (if any)
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception ignored) {
        }

        //launches activity
        activityScenario = ActivityScenario.launch(RegisterActivity.class);
    }

    @Test
    public void isVisible_logoPlaceholder() {
        onView(ViewMatchers.withId(R.id.imageViewRegisterLogoPlaceholder)).check(matches(isDisplayed()));
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
    public void isVisible_editTextConfirmPassword() {
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
}
