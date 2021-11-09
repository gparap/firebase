package gparap.apps.chat.ui.user_profile;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

import android.view.View;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SmallTest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import gparap.apps.chat.R;
import gparap.apps.chat.utils.AppConstants;

public class UserProfileActivityInstrumentedTest {
    ActivityScenario<UserProfileActivity> activityScenario;

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(UserProfileActivity.class);
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

    @Test
    public void isDisabled_edittingDisplayName() {
        onView(withId(R.id.edit_text_user_profile_display_name)).check(matches(not(isEnabled())));
    }

    @Test
    public void isDisabled_edittingEmainName() {
        onView(withId(R.id.edit_text_user_profile_email)).check(matches(not(isEnabled())));
    }

    @Test
    public void isDisabled_edittingPasswordName() {
        onView(withId(R.id.edit_text_user_profile_password)).check(matches(not(isEnabled())));
    }

    @Test
    public void onClickEditDisplayName_enableDisplayNameEditting() {
        onView(withId(R.id.image_view_edit_user_display_name)).perform(click());
        onView(withId(R.id.edit_text_user_profile_display_name)).check(matches(isEnabled()));
    }

    @Test
    public void onClickEditEmail_enableEmailEditting() {
        onView(withId(R.id.image_view_edit_user_email)).perform(click());
        onView(withId(R.id.edit_text_user_profile_email)).check(matches(isEnabled()));
    }

    @Test
    public void onClickEditPassword_enablePasswordEditting() {
        onView(withId(R.id.image_view_edit_password)).perform(click());
        onView(withId(R.id.edit_text_user_profile_password)).check(matches(isEnabled()));
    }

    @Test
    public void onClickEditPassword_showPasswordConfirmation() {
        onView(withId(R.id.image_view_edit_password)).perform(click());
        onView(withId(R.id.edit_text_user_profile_confirm_password)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void onHomeButtonClicked_redirectToChat() {
        onView(withContentDescription("Navigate up")).perform(click());
        onView(withId(R.id.layout_activity_main)).check(matches(isDisplayed()));
    }

    @Test
    @LargeTest
    public void displayTheCorrectUserProfileDetails() throws InterruptedException {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = null;

        //sign-out previous user (if any)
        try {
            firebaseAuth.signOut();
        } catch (Exception ignored) {
        }

        //create a test user profile
        String testUserDisplayName = "test_user_display_name";
        String testUserEmail = "test_user_@email.com";
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

            //!!! important
            //destroy the activity and create it again, so as to refresh the profile of the current user
            activityScenario.close();
            activityScenario = ActivityScenario.launch(UserProfileActivity.class);

            //get the profile details of the user that is displayed
            //and test
            activityScenario.onActivity(activity -> {
                EditText displayName = activity.findViewById(R.id.edit_text_user_profile_display_name);
                EditText email = activity.findViewById(R.id.edit_text_user_profile_email);

                assert displayName.getText().toString().equals(testUserDisplayName);
                assert email.getText().toString().equals(testUserEmail);
            });
        }catch (Exception e){
            assert false;
        }finally {
            //remove test user from database
            Objects.requireNonNull(firebaseUser).delete();
            Thread.sleep(1667);
        }
    }

    @Test
    @LargeTest
    public void updateTheUserProfileDisplayName() throws InterruptedException {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = null;

        //sign-out previous user (if any)
        try {
            firebaseAuth.signOut();
        } catch (Exception ignored) {
        }

        //create a test user profile
        String testUserDisplayName = "test_user_display_name";
        String testUserEmail = "test_user_@email.com";
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

            //!!! important
            //destroy the activity and create it again, so as to refresh the profile of the current user
            activityScenario.close();
            activityScenario = ActivityScenario.launch(UserProfileActivity.class);

            //change the user's displayName and update the profile
            String testUserDisplayNameChanged = "test_user_display_name_changed";
            onView(withId(R.id.image_view_edit_user_display_name)).perform(click());
            onView(withId(R.id.edit_text_user_profile_display_name)).perform(clearText());
            onView(withId(R.id.edit_text_user_profile_display_name)).perform(typeText(testUserDisplayNameChanged));
            closeSoftKeyboard();
            onView(withId(R.id.button_update_user_profile)).perform(click());
            Thread.sleep(1667);

            //!!! important
            //destroy the activity and create it again, so as to refresh the profile of the current user
            activityScenario.close();
            activityScenario = ActivityScenario.launch(UserProfileActivity.class);

            //get the display name of the user
            //and test
            activityScenario.onActivity(activity -> {
                EditText displayName = activity.findViewById(R.id.edit_text_user_profile_display_name);

                assert displayName.getText().toString().equals(testUserDisplayNameChanged);
            });
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
    public void updateTheUserEmail() throws InterruptedException {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = null;

        //sign-out previous user (if any)
        try {
            firebaseAuth.signOut();
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

            //!!! important
            //destroy the activity and create it again, so as to refresh the profile of the current user
            activityScenario.close();
            activityScenario = ActivityScenario.launch(UserProfileActivity.class);

            //change the user's email and update
            String testUserEmailChanged = "test_user@changed.com";
            onView(withId(R.id.image_view_edit_user_email)).perform(click());
            onView(withId(R.id.edit_text_user_profile_email)).perform(clearText());
            onView(withId(R.id.edit_text_user_profile_email)).perform(typeText(testUserEmailChanged));
            closeSoftKeyboard();
            onView(withId(R.id.button_update_user_profile)).perform(click());
            Thread.sleep(1667);

            //!!! important
            //destroy the activity and create it again, so as to refresh the profile of the current user
            activityScenario.close();
            activityScenario = ActivityScenario.launch(UserProfileActivity.class);

            //get the email of the user
            //and test
            activityScenario.onActivity(activity -> {
                EditText email = activity.findViewById(R.id.edit_text_user_profile_email);

                assert email.getText().toString().equals(testUserEmailChanged);
            });
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

    //returns the EditText's "enabled" status
    private static Matcher<View> isEnabled() {
        return new BoundedMatcher<View, EditText>(EditText.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("is Enabled: ");
            }

            @Override
            protected boolean matchesSafely(EditText item) {
                return item.isEnabled();
            }
        };
    }
}