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
package gparap.apps.blog;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class MainActivityInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void isVisible_recyclerViewBlogPosts() {
        onView(withId(R.id.recyclerViewBlogPosts)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonAddBlogPost() {
        onView(withId(R.id.fab_addPost)).check(matches(isDisplayed()));
    }

    @Test
    public void gotoAddNewPostActivityFromMainMenu() {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
        onView(withText(R.string.add_post)).perform(click());

        onView(withId(R.id.layout_activity_add_blog_post)).check(matches(isDisplayed()));
    }

    @Test
    public void gotoUserSettingsActivityFromMainMenu() {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
        onView(withText(R.string.user_settings)).perform(click());

        onView(withId(R.id.layout_activity_user_settings)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutUserFromMainMenu() throws InterruptedException {
        //get current user from firebase
        // if it is null, sign-in as an anonymous user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            FirebaseAuth.getInstance().signInAnonymously();
            Thread.sleep(1667);
        }

        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
        onView(withText(R.string.log_out)).perform(click());
        Thread.sleep(1667);

        onView(withId(R.id.layout_login_activity)).check(matches(isDisplayed()));
    }
}