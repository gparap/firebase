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
package gparap.apps.social_media.posts;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.AmbiguousViewMatcherException;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import gparap.apps.social_media.MainActivity;
import gparap.apps.social_media.R;
import gparap.apps.social_media.auth.LoginActivity;

/**
 * @noinspection FieldCanBeLocal
 */
public class PostActivityInstrumentedTest {
    private ActivityScenario<LoginActivity> loginActivityActivityScenario;
    private ActivityScenario<MainActivity> mainActivityScenario;

    //!!! Use this default test user credentials, they don't change
    final private String testUser_name = "gp";
    final private String testUser_email = "gp@dot.com";
    final private String testUser_password = "123123";

    Long timestamp = System.currentTimeMillis();
    private final String postTitle = String.valueOf(timestamp);
    private final String postDetails = "post details";
    private final String postBy = "posted by\u00A0";

    int count = 0;

    @Before
    public void setUp() throws InterruptedException { //TODO: refactor setUp for interaction
        //sign-out existing user (if any)
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception ignored) {
        }

        //sign-in with test user
        loginActivityActivityScenario = ActivityScenario.launch(LoginActivity.class);
        signInUser();

        //add a test post and finish current scenario
        addNewPost(postTitle, postDetails);
        loginActivityActivityScenario.close();

        //launch the main scenario and open the most recent post
        mainActivityScenario = ActivityScenario.launch(MainActivity.class);
        mainActivityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_posts);
            count = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
        });
        Thread.sleep(4667); //!!!do NOT remove this
        onView(withId(R.id.recycler_view_posts)).perform(
                RecyclerViewActions.actionOnItemAtPosition(count - 1, click()));
    }

    @Test
    public void areVisible_allPostWidgetsExceptImageView() {
        onView(withId(R.id.textViewPostTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewPostDetails)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewPostCreator)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButton_deletePost)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButton_editPost)).check(matches(isDisplayed()));

        //go back, as we have opened a post //TODO: delete added test post
        pressBack();
        try {
            onView(withId(R.id.layout_post_interactions)).check(matches(isDisplayed()));
        } catch (Exception exception) {
            if (exception instanceof AmbiguousViewMatcherException) {
                assert true;    //it's ok, there is a post interaction layout for every post
            } else {
                assert false;
            }
        }
    }

    @Test
    public void areNotVisible_allInteractionWidgets_userIsTheCreator() {
        onView(withId(R.id.post_interaction_favorites)).check(matches(not(isDisplayed())));
        onView(withId(R.id.post_interaction_likes)).check(matches(not(isDisplayed())));
        onView(withId(R.id.post_interaction_dislikes)).check(matches(not(isDisplayed())));
        onView(withId(R.id.post_interaction_comments)).check(matches(not(isDisplayed())));

        //go back, as we have opened a post //TODO: delete added test post
        pressBack();
    }

    @Test
    public void areNotVisible_allInteractionWidgets_userIsNotCreator() throws InterruptedException {
        //log out and sign in as a different test user
        //use test user: user1@dot.com with pass: 123123
        FirebaseAuth.getInstance().signOut();
        loginActivityActivityScenario = ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.editTextLoginEmail)).perform(typeText("user1@dot.com"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextLoginPassword)).perform(typeText(testUser_password));
        closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(1667); //wait for firebase..

        //launch the main scenario and open the most recent post
        mainActivityScenario = ActivityScenario.launch(MainActivity.class);
        mainActivityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_posts);
            count = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
        });
        Thread.sleep(4667); //!!!do NOT remove this
        onView(withId(R.id.recycler_view_posts)).perform(
                RecyclerViewActions.actionOnItemAtPosition(count - 1, click()));

        //test here
        onView(withId(R.id.post_interaction_favorites)).check(matches(isDisplayed()));
        onView(withId(R.id.post_interaction_likes)).check(matches(isDisplayed()));
        onView(withId(R.id.post_interaction_dislikes)).check(matches(isDisplayed()));
        onView(withId(R.id.post_interaction_comments)).check(matches(isDisplayed()));

        //go back, as we have opened a post //TODO: delete added test post
        pressBack();
    }

    @Test
    public void openPost_isPostCorrect() {
        onView(withId(R.id.textViewPostTitle)).check(matches(withText(postTitle)));
        onView(withId(R.id.textViewPostDetails)).check(matches(withText(postDetails)));
        onView(withId(R.id.textViewPostCreator)).check(matches(withText(postBy + testUser_name)));
    }

    @Test
    public void userPostInteraction_addToFavorites_updateFavoritesCounterDisplay() throws InterruptedException {
        //sign-out existing user
        FirebaseAuth.getInstance().signOut();

        //sign-in with a different test user
        loginActivityActivityScenario = ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.editTextLoginEmail)).perform(typeText("user1@dot.com"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextLoginPassword)).perform(typeText(testUser_password));
        closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(1667); //wait for firebase..

        //launch the main scenario and open the most recent post
        mainActivityScenario = ActivityScenario.launch(MainActivity.class);
        mainActivityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_posts);
            count = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
        });
        Thread.sleep(4667); //!!!do NOT remove this
        onView(withId(R.id.recycler_view_posts)).perform(
                RecyclerViewActions.actionOnItemAtPosition(count - 1, click()));

        //perform the "add to favorites" interaction
        onView(withId(R.id.post_interaction_favorites)).perform(click());
        Thread.sleep(1667); //wait for firebase..

        //test here
        onView(withId(R.id.post_interaction_favorites)).check(matches(withText("1")));

        //TODO: delete post
    }

    @Test
    public void deletePost_isPostDeleted() throws InterruptedException {
        onView(withId(R.id.imageButton_deletePost)).perform(click());
        onView(withText(R.string.text_ok)).perform(click());
        Thread.sleep(Toast.LENGTH_SHORT);
        try {
            onView(withText(postTitle)).check(matches(not(isDisplayed())));
        } catch (androidx.test.espresso.NoMatchingViewException exception) {
            //noinspection RedundantIfStatement
            if (exception.getMessage() != null && exception.getMessage().contains(postTitle)) {
                assert true;
            }
        }
    }

    private void signInUser() throws InterruptedException {
        onView(withId(R.id.editTextLoginEmail)).perform(typeText(testUser_email));
        closeSoftKeyboard();
        onView(withId(R.id.editTextLoginPassword)).perform(typeText(testUser_password));
        closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(1667); //wait for firebase..
    }

    /**
     * @noinspection SameParameterValue
     */
    private void addNewPost(String title, String details) throws InterruptedException {
        onView(withId(R.id.fab_add_post_main)).perform(click());
        onView(withId(R.id.editTextAddPostTitle)).perform(typeText(title));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextAddPostDetails)).perform(typeText(details));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.buttonSavePost)).perform(click());
        Thread.sleep(667);
    }
}