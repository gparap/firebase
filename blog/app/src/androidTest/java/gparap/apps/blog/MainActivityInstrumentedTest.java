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

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

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

    @Test
    @LargeTest
    public void clickMainMenuSettings_displayAuthenticatedUserSettings() throws InterruptedException {
        //!!! before the test we should make sure that a test-user must exist in Firebase,
        //!!!   with the following settings:
        String username = "test_user_0";
        String email = "test_user_0@mail.com";
        String password = "123456";

        logoutAndLoginWithTestUser(email, password);
        openMainMenuSettings();

        onView(withId(R.id.editTextUserSettingsUsername)).check(matches(withText(username)));
        onView(withId(R.id.editTextUserSettingsEmail)).check(matches(withText(email)));
    }

    @Test
    @LargeTest
    public void clickMainMenuSettings_changeAuthenticatedUserSettings() throws InterruptedException {
        //!!! before the test we should make sure that a test-user must exist in Firebase,
        //!!!   with the following settings:
        String username = "test_user_0";
        String email = "test_user_0@mail.com";
        String password = "123456";

        //changed user settings
        String usernameChanged = "test_user_changed";
        String emailChanged = "test_user_changed@mail.com";

        logoutAndLoginWithTestUser(email, password);
        openMainMenuSettings();

        //change user settings (username, email) and wait for Firebase
        onView(withId(R.id.imageButtonUserSettingsChangeUsername)).perform(click());
        onView(withId(R.id.editTextUserSettingsUsername)).perform(typeText(usernameChanged));
        closeSoftKeyboard();
        onView(withId(R.id.imageButtonUserSettingsChangeEmail)).perform(click());
        onView(withId(R.id.editTextUserSettingsEmail)).perform(typeText(emailChanged));
        closeSoftKeyboard();
        onView(withId(R.id.buttonUserSettingsUpdate)).perform(click());
        Thread.sleep(1667);

        openMainMenuSettings();

        onView(withId(R.id.editTextUserSettingsUsername)).check(matches(withText(usernameChanged)));
        onView(withId(R.id.editTextUserSettingsEmail)).check(matches(withText(emailChanged)));

        //restore test user
        onView(withId(R.id.imageButtonUserSettingsChangeUsername)).perform(click());
        onView(withId(R.id.editTextUserSettingsUsername)).perform(typeText(username));
        closeSoftKeyboard();
        onView(withId(R.id.imageButtonUserSettingsChangeEmail)).perform(click());
        onView(withId(R.id.editTextUserSettingsEmail)).perform(typeText(email));
        closeSoftKeyboard();
        onView(withId(R.id.buttonUserSettingsUpdate)).perform(click());
        Thread.sleep(1667);
    }

    @Test
    @LargeTest
    public void clickRecyclerViewItem_openBlogPost() throws InterruptedException {
        //make sure there is at least on test blog post
        logoutAndLoginWithTestUser("test_user_0@mail.com", "123456");
        onView(withId(R.id.fab_addPost)).perform(click());
        onView(withId(R.id.editTextAddPostTitle)).perform(typeText("test title"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextAddPostDetails)).perform(typeText("test details"));
        closeSoftKeyboard();
        onView(withId(R.id.buttonSavePost)).perform(click());
        pressBack();
        Thread.sleep(1667);

        //click test blog post
        onView(withId(R.id.recyclerViewBlogPosts))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("test title")), click()));

        onView(withText("test title")).check(matches(isDisplayed()));

        //remove test blog post from database
        final String databaseURL = "https://blog-d6918-default-rtdb.europe-west1.firebasedatabase.app/";
        Query query = FirebaseDatabase.getInstance(databaseURL).getReference("posts")
                .orderByChild("title").equalTo("test title");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    child.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void logoutAndLoginWithTestUser(String email, String password) throws InterruptedException {
        //log-out current user and wait for Firebase
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
        onView(withText(R.string.log_out)).perform(click());
        Thread.sleep(1667);

        //log-in with test-user and wait for Firebase
        onView(withId(R.id.editTextLoginEmail)).perform(typeText(email));
        closeSoftKeyboard();
        onView(withId(R.id.editTextLoginPassword)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(1667);
    }

    private void openMainMenuSettings() throws InterruptedException {
        //goto menu settings and wait for Firebase
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
        onView(withText(R.string.user_settings)).perform(click());
        Thread.sleep(1667);
    }
}