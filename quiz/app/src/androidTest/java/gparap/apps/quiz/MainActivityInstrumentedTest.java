package gparap.apps.quiz;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {
    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    @SmallTest
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("gparap.apps.quiz", appContext.getPackageName());
    }

    @Test
    @SmallTest
    public void isVisible_spinner_categories() {
        onView(withId(R.id.spinner_categories)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_layout_introductory_text() {
        onView(withId(R.id.layout_introductory_text)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_button_start_quiz() {
        onView(withId(R.id.button_start_quiz)).check(matches(isDisplayed()));
    }
}