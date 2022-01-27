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
package gparap.apps.photos.ui.profile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.IsNot.not;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.photos.R;

public class PublicProfileFragmentInstrumentedTest {

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(PublicProfileFragment.class);
    }

    @Test
    @SmallTest
    public void isVisible_image_view_user_profile() {
        onView(withId(R.id.image_view_user_profile)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_label_username_profile_public() {
        onView(withId(R.id.label_username_profile_public)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isNotVisible_text_view_username_profile_public() {
        onView(withId(R.id.text_view_username_profile_public)).check(matches(not(isDisplayed())));
    }

    @Test
    @SmallTest
    public void isVisible_label_posts_profile_public() {
        onView(withId(R.id.label_posts_profile_public)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_text_view_posts_profile_public() {
        onView(withId(R.id.text_view_posts_profile_public)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_label_stars_profile_public() {
        onView(withId(R.id.label_stars_profile_public)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_text_view_stars_profile_public() {
        onView(withId(R.id.text_view_stars_profile_public)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_label_followers_profile_public() {
        onView(withId(R.id.label_followers_profile_public)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_text_view_followers_profile_public() {
        onView(withId(R.id.text_view_followers_profile_public)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_label_follows_profile_public() {
        onView(withId(R.id.label_follows_profile_public)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_text_view_follows_profile_public() {
        onView(withId(R.id.text_view_follows_profile_public)).check(matches(isDisplayed()));
    }
}