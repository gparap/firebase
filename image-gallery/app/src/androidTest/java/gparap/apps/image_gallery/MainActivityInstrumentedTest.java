/*
 * Copyright (c) 2022 gparap
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
package gparap.apps.image_gallery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {
    ActivityScenario<MainActivity> activityScenario;

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("gparap.apps.image_gallery", appContext.getPackageName());
    }

    @Test
    public void isNotVisible_recyclerView_imageGallery() {
        onView(withId(R.id.recyclerView_imageGallery)).check(matches(not(isDisplayed())));
    }

    @Test
    public void isVisible_floatingActionButton_imagePicker() {
        onView(withId(R.id.fab_imagePicker)).check(matches(isDisplayed()));
    }

    @Test
    public void onClickImagePickerButton_redirectToImagePickerActivity() {
        onView(withId(R.id.fab_imagePicker)).perform(click());
        onView(withId(R.id.layout_activity_image_picker)).check(matches(isDisplayed()));
    }

    /* !!! Assert that the database in not empty !!! */
    @Test
    public void loadImagesFromDatabase_recyclerViewIsNotEmpty() throws InterruptedException {
        //wait for image loading
        Thread.sleep(1667);

        //test here
        activityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recyclerView_imageGallery);
            assert recyclerView.getChildCount() > 0;
        });
    }

    /* !!! Assert that the database in not empty !!! */
    @Test
    public void deleteImageFromDatabase_imageDeletedSuccessfully() throws InterruptedException {
        //wait for image loading
        Thread.sleep(1667);

        //get the number of images in the RecyclerView
        int imagesCountBefore;
        imagesCountBefore = getRecyclerViewItemsCount();

        //delete an image from RecyclerView using long click
        onView(withId(R.id.recyclerView_imageGallery)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, longClick())
        );

        //confirm long click on dialog
        onView(withText(R.string.text_ok)).perform(click());

        //wait for image to delete
        Thread.sleep(1667);

        //get the number of images in the RecyclerView
        int imagesCountAfter;
        imagesCountAfter = getRecyclerViewItemsCount();

        //test here
        assert (imagesCountAfter < imagesCountBefore);
    }

    /* Returns the number of images in the RecyclerView */
    private int getRecyclerViewItemsCount() {
        AtomicInteger itemsCount = new AtomicInteger(-1);
        activityScenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recyclerView_imageGallery);
            itemsCount.set(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount());
        });
        return itemsCount.get();
    }
}