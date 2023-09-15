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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;

public class AddPostActivityInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(AddPostActivity.class);
    }

    @Test
    @SmallTest
    public void isVisible_imageButtonAddPost() {
        onView(withId(R.id.imageButtonAddPost)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_editTextAddPostTitle() {
        onView(withId(R.id.editTextAddPostTitle)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_editTextAddPostDetails() {
        onView(withId(R.id.editTextAddPostDetails)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_buttonSavePost() {
        onView(withId(R.id.buttonSavePost)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isNotVisible_progressBarAddPost() {
        onView(withId(R.id.progressBarAddPost)).check(matches(not(isDisplayed())));
    }
}