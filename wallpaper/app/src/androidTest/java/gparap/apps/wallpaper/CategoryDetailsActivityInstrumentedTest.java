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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import gparap.apps.wallpaper.ui.CategoryActivity;
import gparap.apps.wallpaper.ui.CategoryDetailsActivity;

@RunWith(AndroidJUnit4.class)
public class CategoryDetailsActivityInstrumentedTest {
    ActivityScenario<CategoryDetailsActivity> activityScenario;

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(CategoryDetailsActivity.class);
    }

    @Test
    public void isVisible_recyclerViewCategoryDetails() {
        onView(withId(R.id.recyclerViewCategoryDetails)).check(matches(isDisplayed()));
    }

    @Test
    public void onCategoryClick_AppBarTitleIsCategoryName() throws InterruptedException {
        //finish the previously launched activity
        activityScenario.close();
        ActivityScenario.launch(CategoryActivity.class);

        //wait for loading...
        Thread.sleep(1667);

        //click the first category (1st category is always "abstract")
        String categoryName = "abstract";
        onView(withText(categoryName)).perform(click());

        onView(withText(categoryName)).check(matches(isDisplayed()));
    }
}