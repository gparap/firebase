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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class ImagePickerActivityInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(ImagePickerActivity.class);
    }

    @Test
    public void isVisible_editTextImageFileName() {
        onView(withId(R.id.editTextImageFileName)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonImagePicker() {
        onView(withId(R.id.buttonImagePicker)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_imagePicked() {
        onView(withId(R.id.imagePicked)).check(matches(isDisplayed()));
    }

    @Test
    public void isVisible_buttonUploadImage() {
        onView(withId(R.id.buttonUploadImage)).check(matches(isDisplayed()));
    }
}