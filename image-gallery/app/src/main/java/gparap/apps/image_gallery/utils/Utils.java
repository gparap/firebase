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

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
     * Upload image metadata to online database
     *
     * @param imageModel ImageModel holding image metadata
     */
    public void uploadImageMetadata(ImageModel imageModel) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference(AppConstants.DATABASE_REFERENCE_PATH);
        DatabaseReference childRef = databaseRef.child(imageModel.getStorageName());
        childRef.setValue(imageModel);
    }

    /**
     * Informs the user using an toast for a short period of time
     *
     * @param context application context
     * @param message message to user
     */
    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles progress visibility
     *
     * @param progress  a progressBar
     * @param isVisible progressBar's visibility
     */
    public void handleProgressVisibility(ProgressBar progress, boolean isVisible) {
        if (isVisible) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.INVISIBLE);
        }
    }
}
