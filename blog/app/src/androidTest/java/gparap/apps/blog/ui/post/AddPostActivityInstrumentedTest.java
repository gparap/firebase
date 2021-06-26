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

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.blog.R;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class AddPostActivityInstrumentedTest {
    ActivityScenario<AddPostActivity> activityScenario;
    View rootView;

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(AddPostActivity.class);

        //get the top-level window view
        activityScenario.onActivity(activity ->
                rootView = activity.getWindow().getDecorView()
        );
    }

    @Test
    public void isVisible_imageView() {
        onView(withId(R.id.imageButtonAddPost)).check(matches(isDisplayed()));
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

    @Test
    public void savePost_postTitleIsEmpty_showErrorMessage() {
        //clear title and save post
        onView(withId(R.id.editTextAddPostTitle)).perform(clearText());
        closeSoftKeyboard();
        onView(withId(R.id.buttonSavePost)).perform(click());

        onView(withText(R.string.toast_empty_post_title))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void savePost_postDetailsAreEmpty_showErrorMessage() {
        //fill in post title
        onView(withId(R.id.editTextAddPostTitle)).perform(typeText("any title"));
        closeSoftKeyboard();

        //clear details and save post
        onView(withId(R.id.editTextAddPostDetails)).perform(clearText());
        closeSoftKeyboard();
        onView(withId(R.id.buttonSavePost)).perform(click());

        onView(withText(R.string.toast_empty_post_details))
                .inRoot(withDecorView(not(is(rootView))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void savePost_redirectNonAuthenticatedUserToLoginActiity() throws InterruptedException {
        //sign-in anonymous user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInAnonymously();

        //fill-in post title and details
        onView(withId(R.id.editTextAddPostTitle)).perform(typeText("any title"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextAddPostDetails)).perform(typeText("any details"));
        closeSoftKeyboard();

        //try to save post and wait for firebase
        onView(withId(R.id.buttonSavePost)).perform(click());
        Thread.sleep(1667);

        onView(withId(R.id.layout_login_activity)).check(matches(isDisplayed()));
    }
}