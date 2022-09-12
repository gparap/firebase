package gparap.apps.dating;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;

import org.junit.Before;
import org.junit.Test;

public class MainActivityInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void onMenuOptionLogout_signOutCurrentUser() {
        Espresso.openContextualActionModeOverflowMenu();
        onView(withText(R.string.main_menu_title_logout)).perform(click());
        onView(withId(R.id.layout_login_activity)).check(matches(isDisplayed()));
    }
}