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
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import gparap.apps.wallpaper.ui.CategoryActivity;

@RunWith(AndroidJUnit4.class)
public class CategoryActivityInstrumentedTest {
    ActivityScenario<CategoryActivity> activityScenario;

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(CategoryActivity.class);
    }

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("gparap.apps.wallpaper", appContext.getPackageName());
    }

    @Test
    public void isVisible_RecyclerViewCategories() {
        onView(withId(R.id.recyclerViewCategories)).check(matches(isDisplayed()));
    }

    @Test
    public void afterAppLoaded_RecyclerViewCategoriesNotEmpty() {
        activityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recyclerViewCategories);
            assert (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() > 0);
        });
    }
}