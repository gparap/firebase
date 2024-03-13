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
package gparap.apps.social_media.users;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.IsNot.not;

import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.social_media.R;

public class UserDetailsActivityInstrumentedTest {
    @Before
    public void setUp() {
        ActivityScenario.launch(UserDetailsActivity.class);
    }

    @Test
    public void isNotVisible_imageViewUserDetails() {
        onView(withId(R.id.imageViewUserDetails)).check(matches(not(isDisplayed())));
    }

    @Test
    public void isVisible_textViewUserDetailsName() {
        onView(withId(R.id.textViewUserDetailsName)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_textViewUserDetailsMemberSince() {
        onView(withId(R.id.textViewUserDetailsMemberSince)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_textViewUserDetailsPosts() {
        onView(withId(R.id.textViewUserDetailsPosts)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_textViewUserDetailsAboutMe() {
        onView(withId(R.id.textViewUserDetailsAboutMe)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonUserDetailsViewPosts() {
        onView(withId(R.id.buttonUserDetailsViewPosts)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonUserDetailsMessageUser() {
        onView(withId(R.id.buttonUserDetailsMessageUser)).check(matches(isDisplayed()));
    }
}