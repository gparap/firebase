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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;

public class AddPostActivityInstrumentedTest {
    private View rootView;

    @Before
    public void setUp() {
        //start activity and get the top-level window view
        ActivityScenario<AddPostActivity> activityScenario = ActivityScenario.launch(AddPostActivity.class);
        activityScenario.onActivity(activity -> rootView = activity.getWindow().getDecorView());
    }

    @Test
    @SmallTest
    public void isVisible_imageButtonAddPost() {
        onView(withId(R.id.imageViewPost)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_editTextAddPostTitle() {
        onView(withId(R.id.textViewPostTitle)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_editTextAddPostDetails() {
        onView(withId(R.id.textViewPostDetails)).check(matches(isDisplayed()));
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

    @Test
    @SmallTest
    public void isPostTitleEmpty_displayError() {
        onView(withId(R.id.buttonSavePost)).perform(click());
        onView(withText(R.string.toast_empty_post_title)).inRoot(withDecorView(not(rootView)))
                .check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void arePostDetailsEmpty_displayError() {
        //make sure title is not empty
        onView(withId(R.id.textViewPostTitle)).perform(typeText("title"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.buttonSavePost)).perform(click());
        onView(withText(R.string.toast_empty_post_details)).inRoot(withDecorView(not(rootView)))
                .check(matches(isDisplayed()));
    }
}