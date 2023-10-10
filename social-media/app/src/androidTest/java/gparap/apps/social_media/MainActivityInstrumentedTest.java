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
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.KeyEvent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SmallTest;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

/** @noinspection FieldCanBeLocal*/
public class MainActivityInstrumentedTest {
    private ActivityScenario<MainActivity> activityScenario;

    //!!! Use this default test user credentials, they don't change
    final private String testUser_email = "gp@dot.com";
    final private String testUser_password = "123123";

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

    @Test
    public void testSearch() throws InterruptedException {
        signInUser();

        //assert that there are at least 2 posts with only one containing the keyword
        addNewPost("title to find", "details...");
        addNewPost("title", "details...");

        //search for the post
        onView(withId(R.id.main_menu_item_search)).perform(click());
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText("find")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        Espresso.closeSoftKeyboard();

        //get the number of posts after search
        activityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_posts);
            assert (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() > 0);
        });
    }

    private void signInUser() throws InterruptedException {
        onView(withId(R.id.editTextLoginEmail)).perform(typeText(testUser_email));
        closeSoftKeyboard();
        onView(withId(R.id.editTextLoginPassword)).perform(typeText(testUser_password));
        closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(1667); //wait for firebase..
    }

    /** @noinspection SameParameterValue*/
    private void addNewPost(String title, String details) throws InterruptedException {
        onView(withId(R.id.fab_add_post_main)).perform(click());
        onView(withId(R.id.textViewPostTitle)).perform(typeText(title));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.textViewPostDetails)).perform(typeText(details));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.buttonSavePost)).perform(click());
        Thread.sleep(667);
    }
}