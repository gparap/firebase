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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import gparap.apps.image_gallery.data.ImageModel;
import gparap.apps.image_gallery.utils.AppConstants;
import gparap.apps.image_gallery.utils.Utils;

public class ImagePickerActivity extends AppCompatActivity {
    private UploadTask uploadTaskStorage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        //init progress
        progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.INVISIBLE);

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

                        //upload the picked image to cloud
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

                            //show progress
                            progressBar.setVisibility(View.VISIBLE);

                            //validate image uploading
                            if (uploadTaskStorage != null && uploadTaskStorage.isInProgress()) {
                                Toast.makeText(this,
                                        getResources().getString(R.string.toast_image_still_uploading),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //upload image to cloud storage and image metadata to online database
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(getChildLocation(uri));
                            uploadTaskStorage = (UploadTask) storageReference.putFile(uri);
                            Task<Uri> uriTask = uploadTaskStorage.continueWithTask(task -> {
                                System.out.println(storageReference.getDownloadUrl());
                                return storageReference.getDownloadUrl();
                            });
                            uriTask.addOnFailureListener(e -> System.out.println(e.getMessage()))
                                    .addOnCompleteListener(task -> {
                                        //successfully uploaded to cloud storage
                                        Toast.makeText(ImagePickerActivity.this,
                                                getResources().getString(R.string.toast_image_uploaded_success),
                                                Toast.LENGTH_SHORT).show();

                                        //create model object that holds the image metadata
                                        ImageModel imageModel = new ImageModel();
                                        imageModel.setName(
                                                imageFileName.getText().toString().trim()
                                                        + AppConstants.DOT_CHARACTER
                                                        + Utils.getInstance().getImageFiletype(ImagePickerActivity.this, uri)
                                        );
                                        imageModel.setUri(uriTask.getResult().toString());

                                        //upload image metadata to online database
                                        Utils.getInstance().uploadImageMetadata(imageModel);

                                        //hide progress
                                        progressBar.setVisibility(View.INVISIBLE);
                                    });
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