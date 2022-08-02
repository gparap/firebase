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
package gparap.apps.image_gallery.utils;

import static org.junit.Assert.*;

import android.content.Context;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import gparap.apps.image_gallery.ImagePickerActivity;

public class UtilsInstrumentedTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(ImagePickerActivity.class);
    }

    @Test
    public void getImageFiletype_isCorrect() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Uri uri = Uri.parse("content://com.android.providers.media.documents/document/image%3A46");
        String typeExpected = "jpeg";
        String typeActual = Utils.getInstance().getImageFiletype(context, uri);
        assertEquals(typeExpected, typeActual);
    }
}