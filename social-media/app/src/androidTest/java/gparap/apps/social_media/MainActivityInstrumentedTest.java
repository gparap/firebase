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

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SmallTest;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

public class MainActivityInstrumentedTest {
    private ActivityScenario<MainActivity> activityScenario;

    @Before
    public void setUp() {
        //sign-out existing user (if any)
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception ignored) {
        }

        //launch scenario
        activityScenario = ActivityScenario.launch(MainActivity.class);
    }

    @Test
    @SmallTest
    public void onClickFabAddPost_redirectToAddPostActivity() throws InterruptedException {
        signInUser();
        onView(withId(R.id.fab_add_post_main)).perform(click());
        onView(withId(R.id.layout_activity_add_post)).check(matches(isDisplayed()));
    }

    @Test
    @LargeTest
    public void onLoad_recyclerViewIsNotEmpty() throws InterruptedException {
        signInUser();

        //add a post (title only)
        onView(withId(R.id.fab_add_post_main)).perform(click());
        onView(withId(R.id.textViewPostTitle)).perform(typeText("title"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.buttonSavePost)).perform(click());
        Thread.sleep(1667); //wait for firebase..

        //test if recycler view is not empty
        activityScenario.onActivity(activity -> {
            RecyclerView recyclerViewPosts = activity.findViewById(R.id.recycler_view_posts);
            int posts = Objects.requireNonNull(recyclerViewPosts.getAdapter()).getItemCount();
            assert (posts != 0);
        });
    }

    private void signInUser() throws InterruptedException {
        onView(withId(R.id.editTextLoginEmail)).perform(typeText("gparap@dot.com"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextLoginPassword)).perform(typeText("123123"));
        closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(1667); //wait for firebase..
    }
}