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
package gparap.apps.blog.ui.post;

import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.blog.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class AddPostActivityInstrumentedTest {
    @Before
    public void setUp() {
        ActivityScenario.launch(AddPostActivity.class);
    }

    @Test
    public void isVisible_imageView() {
        onView(withId(R.id.imageViewAddPost)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextPostTitle() {
        onView(withId(R.id.editTextAddPostTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_editTextPostDetails() {
        onView(withId(R.id.editTextAddPostDetails)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonSavePost() {
        onView(withId(R.id.buttonSavePost)).check(matches(isDisplayed()));
    }
}