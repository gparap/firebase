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
package gparap.apps.social_media.users;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import gparap.apps.social_media.R;
import gparap.apps.social_media.adapters.PostAdapter;
import gparap.apps.social_media.utils.AppConstants;
import org.junit.Before;
import org.junit.Test;

public class UserPostsActivityInstrumentedTest {
    private ActivityScenario<UserPostsActivity> activityScenario;
    String userId = "XsSLSeRQVDMjM1G5NTS6p71nE093"; //use this test user (they don't change & always have posts)

    @Before
    public void setUp() {
        //create the user intent
        Intent userPostsIntent = new Intent(ApplicationProvider.getApplicationContext(), UserPostsActivity.class);
        userPostsIntent.putExtra(AppConstants.USER_ID, userId);

        //run scenario from intent
        activityScenario = ActivityScenario.launch(userPostsIntent);
    }

    @Test
    public void isCorrect_displayUserPosts() throws InterruptedException {
        //wait a little for Firebase...
        Thread.sleep(4667);

        //get the user posts
        activityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_user_posts);
            assert recyclerView != null;
            PostAdapter adapter = (PostAdapter) recyclerView.getAdapter();
            assert adapter != null;
            assert (adapter.getItemCount() != 0);
        });
    }
}