/*
 * Copyright 2022 gparap
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
package gparap.apps.player_music;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {
    private ActivityScenario<MainActivity> activityScenario;

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("gparap.apps.player_music", appContext.getPackageName());
    }

    @Test
    public void isVisible_recyclerViewStorageFiles() {
        onView(withId(R.id.recyclerViewStorageFiles)).check(matches(isDisplayed()));
    }

    //!!! important !!!
    // Perform this test oly if there are audio files in the device.
    @Test
    public void onAppLoading_getAudioFilesFromDeviceStorage() throws InterruptedException {
        //wait for loading...
        Thread.sleep(267);

        activityScenario.onActivity((activity) -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recyclerViewStorageFiles);
            assert(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() > 0);
        });
    }
}