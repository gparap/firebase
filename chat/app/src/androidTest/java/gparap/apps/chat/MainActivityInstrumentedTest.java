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
package gparap.apps.chat;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBackUnconditionally;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import gparap.apps.chat.utils.AppConstants;

public class MainActivityInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void isVisible_Toolbar() {
        onView(withId(R.id.toolbar_main)).check(matches(isDisplayed()));
    }

    @Test
    public void onClickMainMenuUserProfile_openUserProfileActivity() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
        onView(withText(R.string.title_menu_user_profile)).perform(click());
        onView(withId(R.id.layout_activity_user_profile));
    }

    @Test
    public void signOutCurrentUserAndReturnToLoginActivity() throws InterruptedException {
        //login
        FirebaseAuth.getInstance().signInAnonymously();
        Thread.sleep(1667);

        //logout
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
        onView(withText(R.string.title_menu_logout)).perform(click());
        Thread.sleep(1667);

        onView(withId(R.id.layout_activity_login)).check(matches(isDisplayed()));
        assert (FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    @Test
    @LargeTest
    public void updateTheUserPassword() throws InterruptedException {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = null;

        //sign-out previous user (if any)
        try {
            firebaseAuth.signOut();
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
            onView(withText(R.string.title_menu_logout)).perform(click());
            Thread.sleep(1667);
        } catch (Exception ignored) {
        }

        //create a test user profile
        String testUserDisplayName = "test_user_display_name";
        String testUserEmail = "test_user@email.com";
        String testUserPassword = "123456";

        try{
            //add test user to database and sign-in
            firebaseAuth.createUserWithEmailAndPassword(testUserEmail, testUserPassword);
            Thread.sleep(1667);
            firebaseAuth.signInWithEmailAndPassword(testUserEmail, testUserPassword);
            Thread.sleep(1667);
            firebaseUser = firebaseAuth.getCurrentUser();
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(testUserDisplayName)
                    .build();
            Objects.requireNonNull(firebaseUser).updateProfile(profileChangeRequest);
            Thread.sleep(1667);

            //login to the app
            onView(withId(R.id.edit_text_login_email)).perform(typeText(testUserEmail));
            closeSoftKeyboard();
            onView(withId(R.id.edit_text_login_password)).perform(typeText(testUserPassword));
            closeSoftKeyboard();
            onView(withId(R.id.button_login)).perform(click());
            Thread.sleep(1667);

            //goto profile activity
            onView(withId(R.id.menu_item_user_profile)).perform(click());
            Thread.sleep(1667);

            //change the user's password and update
            String testUserPasswordChanged = "123123";
            onView(withId(R.id.image_view_edit_password)).perform(click());
            onView(withId(R.id.edit_text_user_profile_password)).perform(clearText());
            onView(withId(R.id.edit_text_user_profile_password)).perform(typeText(testUserPasswordChanged));
            closeSoftKeyboard();
            onView(withId(R.id.edit_text_user_profile_confirm_password)).perform(clearText());
            onView(withId(R.id.edit_text_user_profile_confirm_password)).perform(typeText(testUserPasswordChanged));
            closeSoftKeyboard();
            onView(withId(R.id.button_update_user_profile)).perform(click());
            Thread.sleep(1667);

            //goto main activity and logout
            pressBackUnconditionally();
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
            onView(withText(R.string.title_menu_logout)).perform(click());
            Thread.sleep(1667);

            //login again with the new password
            onView(withId(R.id.edit_text_login_email)).perform(typeText(testUserEmail));
            closeSoftKeyboard();
            onView(withId(R.id.edit_text_login_password)).perform(typeText(testUserPasswordChanged));
            closeSoftKeyboard();
            onView(withId(R.id.button_login)).perform(click());
            Thread.sleep(1667);

            //test here
            onView(withId(R.id.layout_activity_main)).check(matches(isDisplayed()));

        }catch (Exception e) {
            assert false;
        }
        finally {
            //remove test user from database
            Objects.requireNonNull(firebaseUser).delete();
            FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                    .getReference(AppConstants.DATABASE_PATH_USERS + firebaseUser.getUid())
                    .removeValue();
            Thread.sleep(1667);
        }
    }

    @Test
    @LargeTest
    public void onAppLoad_fetchUsersFromTheDatabase() throws InterruptedException {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = null;

        //sign-out previous user (if any)
        try {
            firebaseAuth.signOut();
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
            onView(withText(R.string.title_menu_logout)).perform(click());
            Thread.sleep(1667);
        } catch (Exception ignored) {
        }

        //create a test user profile
        String testUserDisplayName = "test_user_display_name";
        String testUserEmail = "test_user@email.com";
        String testUserPassword = "123456";

        try{
            //register test user
            onView(withId(R.id.button_goto_register)).perform(click());
            onView(withId(R.id.edit_text_register_display_name)).perform(typeText(testUserDisplayName));
            closeSoftKeyboard();
            onView(withId(R.id.edit_text_register_email)).perform(typeText(testUserEmail));
            closeSoftKeyboard();
            onView(withId(R.id.edit_text_register_password)).perform(typeText(testUserPassword));
            closeSoftKeyboard();
            onView(withId(R.id.edit_text_register_confirm_password)).perform(typeText(testUserPassword));
            closeSoftKeyboard();
            onView(withId(R.id.button_register)).perform(click());
            Thread.sleep(1667);

            //login with test user
            onView(withId(R.id.edit_text_login_password)).perform(typeText(testUserPassword));
            closeSoftKeyboard();
            onView(withId(R.id.button_login)).perform(click());
            Thread.sleep(1667);

            //test here
            onView(withText(testUserDisplayName)).check(matches(isDisplayed()));

        }catch (Exception e) {
            assert false;
        }
        finally {
            //remove test user from database
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert firebaseUser != null;
            FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                    .getReference(AppConstants.DATABASE_PATH_USERS + firebaseUser.getUid())
                    .removeValue();
            firebaseUser.delete();
            Thread.sleep(1667);
        }
    }
}