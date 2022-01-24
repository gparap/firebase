package gparap.apps.socials_photos;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

public class MainActivityInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
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
    public void isVisible_mainViewPager() {
        onView(withId(R.id.view_pager_main)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_mainBottomNavigationView() {
        onView(withId(R.id.bottom_nav_view_main)).check(matches(isDisplayed()));
    }
}