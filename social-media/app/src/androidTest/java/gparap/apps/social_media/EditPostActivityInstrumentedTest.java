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
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;

public class EditPostActivityInstrumentedTest {

    @Before
    public void setUp() {
        //start activity
        ActivityScenario.launch(EditPostActivity.class);
    }

    @Test
    @SmallTest
    public void isVisible_imageButton_editPostImage() {
        onView(withId(R.id.imageButton_editPostImage)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_editText_editPostTitle() {
        onView(withId(R.id.editText_editPostTitle)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_editText_editPostDetails() {
        onView(withId(R.id.editText_editPostDetails)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_buttonSaveEditedPost() {
        onView(withId(R.id.buttonSave_editPost)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isNotVisible_progressBarSaveEditedPost() {
        onView(withId(R.id.progressBar_EditPost)).check(matches(not(isDisplayed())));
    }
}