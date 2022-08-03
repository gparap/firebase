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

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import gparap.apps.image_gallery.data.ImageModel;

public class Utils {
    private static Utils instance = null;

    private Utils() {
    }

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    /**
     * Returns a positive random number.
     * This number will be added to the picked image filename to avoid conflicts in filenames.
     *
     * @return long
     */
    public long getRandomNumber() {
        Random random = new Random();
        return Math.abs(random.nextLong());
    }

    /**
     * Returns the filetype of a file from its MIME type
     *
     * @param context application environment
     * @param url     file immutable URI reference
     * @return String
     */
    public String getImageFiletype(Context context, Uri url) {
        //get a ContentResolver instance for the application's package
        ContentResolver contentResolver = context.getContentResolver();

        //get the MIME type of the given content URL
        String mimeType = contentResolver.getType(url);

        //return the filetype
        String[] fileType = mimeType.split("/");
        return fileType[1];
    }

    /**
     * Upload image metadata to online database
     * @param imageModel ImageModel holding image metadata
     */
    public void uploadImageMetadata(ImageModel imageModel) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference(AppConstants.DATABASE_REFERENCE_PATH);
        String key = databaseRef.push().getKey();
        if (key != null) {
            databaseRef.child(key).setValue(imageModel);
        }
    }
}
