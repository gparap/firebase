package gparap.apps.social_photos;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.appcompat.widget.Toolbar;
import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

public class MainActivityInstrumentedTest {
    ActivityScenario<MainActivity> activityScenario;

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(MainActivity.class);
    }

    @Test
    @SmallTest
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("gparap.apps.socials_photos", appContext.getPackageName());
    }

    @Test
    @SmallTest
    public void isVisible_mainToolbar() {
        onView(withId(R.id.toolbar_main)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_home() {
        activityScenario.onActivity(activity -> {
            Toolbar toolbar = activity.findViewById(R.id.toolbar_main);
            assertEquals(
                    toolbar.getTitle().toString(),
                    activity.getBaseContext().getResources().getString(R.string.text_home)
            );
        });
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
        activityScenario.onActivity(activity -> {
            Toolbar toolbar = activity.findViewById(R.id.toolbar_main);
            assertEquals(
                    toolbar.getTitle().toString(),
                    activity.getBaseContext().getResources().getString(R.string.text_add)
            );
        });
    }

    @Test
    @SmallTest
    public void onClickSearch_navigateToSearchFragment() {
        onView(withId(R.id.navigation_search)).perform(click());
        activityScenario.onActivity(activity -> {
            Toolbar toolbar = activity.findViewById(R.id.toolbar_main);
            assertEquals(
                    toolbar.getTitle().toString(),
                    activity.getBaseContext().getResources().getString(R.string.text_search)
            );
        });
    }

    @Test
    @SmallTest
    public void onClickBackButton_navigateToHomeFragment() {
        onView(withId(R.id.navigation_search)).perform(click());
        pressBack();
        activityScenario.onActivity(activity -> {
            Toolbar toolbar = activity.findViewById(R.id.toolbar_main);
            assertEquals(
                    toolbar.getTitle().toString(),
                    activity.getBaseContext().getResources().getString(R.string.text_home)
            );
        });
    }
}