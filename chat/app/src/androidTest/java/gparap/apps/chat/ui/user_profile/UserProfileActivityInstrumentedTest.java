package gparap.apps.chat.ui.user_profile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.chat.R;

public class UserProfileActivityInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(UserProfileActivity.class);
    }

    @Test
    public void isVisible_toolbar_user_profile() {
        onView(withId(R.id.toolbar_user_profile)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_image_view_user_profile() {
        onView(withId(R.id.image_view_user_profile))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void isVisible_edit_text_user_profile_display_name() {
        onView(withId(R.id.edit_text_user_profile_display_name)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_image_view_edit_user_display_name() {
        onView(withId(R.id.image_view_edit_user_display_name)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_edit_text_user_profile_email() {
        onView(withId(R.id.edit_text_user_profile_email)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_image_view_edit_user_email() {
        onView(withId(R.id.image_view_edit_user_email)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_edit_text_user_profile_password() {
        onView(withId(R.id.edit_text_user_profile_password)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_image_view_edit_password() {
        onView(withId(R.id.image_view_edit_password)).check(matches(isDisplayed()));
    }

    @Test
    public void isNotVisible_edit_text_user_profile_confirm_password() {
        onView(withId(R.id.edit_text_user_profile_confirm_password)).check(matches(not(isDisplayed())));
    }

    @Test
    public void isVisible_button_update_user_profile() {
        onView(withId(R.id.button_update_user_profile)).check(matches(isDisplayed()));
    }

    @Test
    public void isNotVisible_progress_update_user_profile() {
        onView(withId(R.id.progress_update_user_profile)).check(matches(not(isDisplayed())));
    }
}