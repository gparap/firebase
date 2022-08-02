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

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import gparap.apps.image_gallery.utils.AppConstants;
import gparap.apps.image_gallery.utils.Utils;

public class ImagePickerActivity extends AppCompatActivity {
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        //pick an image
        ImageButton buttonPickImage = findViewById(R.id.buttonImagePicker);
        buttonPickImage.setOnClickListener(v -> getContentCallback.launch(AppConstants.MIME_TYPE));
    }

    //register a callback for an activity result to prompt the user to pick a piece of content
    ActivityResultLauncher<String> getContentCallback =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    //handle the activity result
                    uri -> {
                        //display the image picked
                        ImageView imagePicked = findViewById(R.id.imagePicked);
                        imagePicked.setImageURI(uri);

                        //upload the picked image to the database
                        Button buttonUploadImage = findViewById(R.id.buttonUploadImage);
                        buttonUploadImage.setOnClickListener(v -> {
                            //validate image filename
                            EditText imageFileName = findViewById(R.id.editTextImageFileName);
                            if (imageFileName.getText().toString().trim().isEmpty()) {
                                Toast.makeText(this,
                                        getResources().getString(R.string.toast_empty_image_filename),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //validate image uploading
                            if (uploadTask != null && uploadTask.isInProgress()) {
                                Toast.makeText(this,
                                        getResources().getString(R.string.toast_image_still_uploading),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //upload image
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            uploadTask = (UploadTask) storageReference
                                    .child(getChildLocation(uri))
                                    .putFile(uri)
                                    .addOnFailureListener(e -> System.out.println(e.getMessage()))
                                    .addOnCompleteListener(task -> Toast.makeText(this,
                                            getResources().getString(R.string.toast_image_uploaded_success),
                                            Toast.LENGTH_SHORT).show()
                                    );
                        });
                    });

    //returns a child location for the current storage reference
    @NonNull
    private String getChildLocation(Uri uri) {
        return AppConstants.STORAGE_CHILD_LOCATION
                + Utils.getInstance().getRandomNumber()
                + AppConstants.DOT_CHARACTER
                + Utils.getInstance().getImageFiletype(this, uri);
    }
}