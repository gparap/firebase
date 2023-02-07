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
package gparap.apps.wallpaper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import gparap.apps.wallpaper.ui.WallpaperActivity;

@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class WallpaperActivityInstrumentedTest {
    ActivityScenario<WallpaperActivity> activityScenario;

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(WallpaperActivity.class);
    }

    @Test
    public void isVisible_recyclerViewCategoryDetails() {
        onView(withId(R.id.recyclerViewWallpapers)).check(matches(isDisplayed()));
    }

    @Test
    public void onCategoryClick_AppBarTitleIsCategoryName() throws InterruptedException {
        //finish the previous scenario
        activityScenario.close();

        //create the intent with data needed and launch scenario
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), WallpaperActivity.class);
        intent.putExtra("category_id", "1");
        intent.putExtra("category_name", "abstract");
        ActivityScenario<WallpaperActivity> newScenario = ActivityScenario.launch(intent);

        //wait for loading...
        Thread.sleep(1667);

        //1st category is always "abstract"
        String categoryName = "abstract";

        newScenario.onActivity(activity -> {
            String appBarTitle = activity.getSupportActionBar().getTitle().toString();
            assert (appBarTitle.equals(categoryName));
        });
    }

    @Test
    public void onWallpaperCategoryClicked_RecyclerViewWallpapersNotEmpty() throws InterruptedException {
        //finish the previous scenario
        activityScenario.close();

        //create the intent with data needed and launch scenario
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), WallpaperActivity.class);
        intent.putExtra("category_id", "1");
        ActivityScenario<WallpaperActivity> newScenario = ActivityScenario.launch(intent);

        //wait for loading...
        Thread.sleep(1667);

        newScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recyclerViewWallpapers);
            assert (recyclerView.getAdapter().getItemCount() > 0);
        });
    }
}