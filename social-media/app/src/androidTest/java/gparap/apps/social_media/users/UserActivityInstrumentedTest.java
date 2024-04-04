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

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.view.KeyEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import gparap.apps.social_media.R;

/**
 * @noinspection FieldCanBeLocal
 */
public class UserActivityInstrumentedTest {
    ActivityScenario<UserActivity> activityScenario;
    View rootView = null;

    //!!! Use this default test user, it never goes away
    final private String testUser_username = "gp";
    final private String testUser_phone = "1234567890";
    final private String testUser_email = "gp@dot.com";
    final private String testUser_password = "123123";

    @Before
    public void setUp() throws InterruptedException {
        //sign-in test user and wait for database transaction
        FirebaseAuth.getInstance().signInWithEmailAndPassword(testUser_email, testUser_password);
        Thread.sleep(1667);

        //launch activity
        activityScenario = ActivityScenario.launch(UserActivity.class);

        //get root view
        activityScenario.onActivity(activity -> rootView = activity.getWindow().getDecorView());
    }

    @Test
    @LargeTest
    public void isNotNull_applicationUsers() throws InterruptedException {
        Thread.sleep(4667); //wait for Firebase...
        activityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_users);
            int users = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
            assert (users > 0);
        });
    }

    @Test
    public void signOutUser_isSuccessful() {
        signOut();
        onView(withId(R.id.layout_activity_login)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutUser_displayToast() {
        signOut();
        onView(withText(R.string.toast_login_successful))
                .inRoot(withDecorView(not(rootView)))
                .check(matches(isDisplayed()));
    }

    @Test
    @LargeTest
    public void isCorrect_displayUserProfileDetails() throws InterruptedException {
        signOut();
        signIn();

        //open user profile
        try {
            //open from action bar icon
            onView(withId(R.id.main_menu_item_profile)).perform(click());
        } catch (Exception e) {
            //open from overflow menu
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
            onView(withText(R.string.text_profile)).perform(click());
        } finally {
            //wait for firebase..
            Thread.sleep(4667);
        }

        //assert profile details are correct
        onView(withId(R.id.editTextProfileUsername)).check(matches(withText(testUser_username)));
        onView(withId(R.id.editTextProfileMobile)).check(matches(withText(testUser_phone)));
    }

    @Test
    @LargeTest
    public void testUserSearch_searchUserByEmail() throws InterruptedException {
        //search for the default test user by e-mail
        openSearchView();
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText(testUser_email)).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        Espresso.closeSoftKeyboard();

        //get the number of users after search
        activityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_users);
            assert (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() == 1);
        });
    }

    @Test
    @LargeTest
    public void testUserSearch_searchUserByUsername() throws InterruptedException {
        //search for the default test user by username
        openSearchView();
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText(testUser_username)).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        Espresso.closeSoftKeyboard();

        //get the number of users after search
        activityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_users);
            assert (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() == 1);
        });
    }

    /**
     * Signs-out either from the appbar icon or the menu item.
     */
    private void signOut() {
        try {
            onView(withId(R.id.main_menu_item_logout)).perform(click());

        } catch (Exception e) {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
            onView(withText(R.string.text_logout)).perform(click());
        }
    }

    /**
     * Signs-in as the default test user and waits a little for Firebase.
     */
    private void signIn() throws InterruptedException {
        onView(withId(R.id.editTextLoginEmail)).perform(typeText(testUser_email));
        closeSoftKeyboard();
        onView(withId(R.id.editTextLoginPassword)).perform(typeText(testUser_password));
        closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(1667);
    }

    /** Opens SearchView from the "Search Users" menu option. */
    private void openSearchView() throws InterruptedException {
        try {
            //open from action bar icon
            onView(withId(R.id.user_menu_item_search_users)).perform(click());
        }
        catch (Exception e) {
            //open from overflow menu
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
            onView(withText(R.string.text_search_users)).perform(click());
        }finally {
            //wait for the users to be fetched from db
            Thread.sleep(1667);
        }
    }
}