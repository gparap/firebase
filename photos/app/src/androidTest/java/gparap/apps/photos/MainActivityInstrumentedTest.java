/*
 * Copyright 2022 gparap
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
package gparap.apps.photos;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.MediumTest;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

public class MainActivityInstrumentedTest {
    private Context context;

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    @SmallTest
    public void useAppContext() {
        assertEquals("gparap.apps.photos", context.getPackageName());
    }

    @Test
    @SmallTest
    public void isVisible_home() {
        onView(withId(R.id.app_bar_layout_home)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_bottomNavigationView() {
        onView(withId(R.id.bottom_navigation_view)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void onClickAdd_navigateToAddFragment() {
        onView(withId(R.id.navigation_add)).perform(click());
        onView(withId(R.id.app_bar_layout_add)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void onClickSearch_navigateToSearchFragment() {
        onView(withId(R.id.navigation_search)).perform(click());
        onView(withId(R.id.app_bar_layout_search)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void onClickBackButton_navigateToHomeFragment() {
        onView(withId(R.id.navigation_search)).perform(click());
        pressBack();
        onView(withId(R.id.app_bar_layout_home)).check(matches(isDisplayed()));
    }

    @Test
    @MediumTest
    public void userNotAuthenticated_redirectToLogin() throws InterruptedException {
        //sign-out user (if signed-in)
        FirebaseAuth.getInstance().signOut();
        Thread.sleep(300);

        onView(withId(R.id.layout_activity_login)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void onMenuIteProfileClick_redirectToPrivateProfile() {
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText(context.getString(R.string.title_menu_item_profile))).perform(click());
        onView(withId(R.id.layout_fragment_profile_private)).check(matches(isDisplayed()));
    }
}