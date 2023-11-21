/*
 * Copyright 2023 gparap
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
package gparap.apps.social_media;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static gparap.apps.social_media.utils.AppConstants.POST_DETAILS;
import static gparap.apps.social_media.utils.AppConstants.POST_ID;
import static gparap.apps.social_media.utils.AppConstants.POST_IMAGE_STORAGE_ID;
import static gparap.apps.social_media.utils.AppConstants.POST_IMAGE_URL;
import static gparap.apps.social_media.utils.AppConstants.POST_TITLE;
import static gparap.apps.social_media.utils.AppConstants.POST_USER_ID;

import android.content.Intent;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class EditPostActivityInstrumentedTest {
    private ActivityScenario<EditPostActivity> activityScenario;

    //use this test post (create in Firebase, if not exists)
    String postId = "-NhaEp_FXSMVjab1L4kG";
    String postUserId = "XsSLSeRQVDMjM1G5NTS6p71nE093";
    String postTitle = "test post title";
    String postDetails = "test post details";
    String postImageUrl = "";
    String postImageStorageId = "";

    @Before
    public void setUp() {
        //create the edit post intent
        Intent editPostIntent = new Intent(ApplicationProvider.getApplicationContext(), EditPostActivity.class);
        editPostIntent.putExtra(POST_ID, postId);
        editPostIntent.putExtra(POST_USER_ID, postUserId);
        editPostIntent.putExtra(POST_TITLE, postTitle);
        editPostIntent.putExtra(POST_DETAILS, postDetails);
        editPostIntent.putExtra(POST_IMAGE_URL, postImageUrl);
        editPostIntent.putExtra(POST_IMAGE_STORAGE_ID, postImageStorageId);

        //start activity scenario with intent
        activityScenario = ActivityScenario.launch(editPostIntent);
    }

    @Test
    @SmallTest
    public void isVisible_imageButton_editPostImage() {
        onView(withId(R.id.imageButton_editPostImage)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_editText_editPostTitle() {
        onView(withId(R.id.editText_editPostTitle)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_editText_editPostDetails() {
        onView(withId(R.id.editText_editPostDetails)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isVisible_buttonSaveEditedPost() {
        onView(withId(R.id.buttonSave_editPost)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void isNotVisible_progressBarSaveEditedPost() {
        onView(withId(R.id.progressBar_EditPost)).check(matches(not(isDisplayed())));
    }

    @Test
    @SmallTest
    public void openPostForEditing_postIsCorrect() {
        activityScenario.onActivity(activity -> {
            EditText editTextPostTile = activity.findViewById(R.id.editText_editPostTitle);
            assert editTextPostTile.getText().toString().equals(postTitle);
            EditText editTextPostDetails = activity.findViewById(R.id.editText_editPostDetails);
            assert editTextPostDetails.getText().toString().equals(postDetails);
        });
    }
}