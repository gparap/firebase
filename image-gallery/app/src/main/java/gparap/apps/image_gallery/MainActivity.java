package gparap.apps.image_gallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import gparap.apps.image_gallery.adapters.ImageAdapter;
import gparap.apps.image_gallery.data.ImageModel;
import gparap.apps.image_gallery.utils.AppConstants;
import gparap.apps.image_gallery.utils.Utils;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<ImageModel> images = new ArrayList<>();
    private final ImageAdapter imageAdapter = new ImageAdapter();
    private ProgressBar progressMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init progress
        progressMain = findViewById(R.id.progressMain);
        Utils.getInstance().handleProgressVisibility(progressMain, true);

        //pick image
        FloatingActionButton fabImagePicker = findViewById(R.id.fab_imagePicker);
        fabImagePicker.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImagePickerActivity.class);
            startActivity(intent);
            finish();
        });

        //read data from the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child(
                AppConstants.DATABASE_REFERENCE_PATH
        );
        databaseRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Utils.getInstance().showToast(this,
                        getResources().getString(R.string.toast_error_loading_images));

            } else {
                //put the images fetched from the database inside the images list
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    ImageModel imageModel = dataSnapshot.getValue(ImageModel.class);
                    images.add(imageModel);
                }

                //update Adapter dataset with images
                imageAdapter.setImages(images);
            }
            Utils.getInstance().handleProgressVisibility(progressMain, false);
        });

        //create RecyclerView with Adapter for images
        RecyclerView recyclerView = findViewById(R.id.recyclerView_imageGallery);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(imageAdapter);

        //handle the long click of an image to delete it from the database
        imageAdapter.setOnItemLongClickListener(position -> {
            ImageModel imageToDelete = imageAdapter.getImages().get(position);

            //display a confirmation dialog
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_delete_forever_24)
                    .setTitle(getResources().getString(R.string.text_delete_image))
                    .setMessage(getResources().getString(R.string.text_delete_message))
                    .setNegativeButton(
                            getResources().getString(R.string.text_cancel),
                            (dialog_cancel, which) -> this.closeContextMenu())
                    .setPositiveButton(
                            getResources().getString(R.string.text_ok),
                            (dialog_ok, which) -> deleteImage(imageToDelete))
                    .create();
            dialog.show();
        });
    }

    //Removes an image and its metadata from online storage
    private void deleteImage(ImageModel image) {
        Utils.getInstance().handleProgressVisibility(progressMain, true);

        //get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference(AppConstants.DATABASE_REFERENCE_PATH);

        //delete image metadata from database
        DatabaseReference childRef = databaseRef.child(image.getStorageName());
        Task<Void> deleteImageMetadataTask = childRef.removeValue();
        deleteImageMetadataTask.addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Utils.getInstance().showToast(this,
                        getResources().getString(R.string.toast_error_deleting_image));
            }

            //delete image from storage
            else {
                //get storage reference
                FirebaseStorage storageRef = FirebaseStorage.getInstance();
                StorageReference storageChildRef = storageRef.getReference(
                        AppConstants.STORAGE_CHILD_LOCATION + image.getStorageName()
                );

                Task<Void> deleteImageTask = storageChildRef.delete();
                deleteImageTask.addOnCompleteListener(task1 -> {
                    if (!task.isSuccessful()) {
                        Utils.getInstance().showToast(this,
                                getResources().getString(R.string.toast_error_deleting_image));
                    } else {
                        Utils.getInstance().showToast(this,
                                getResources().getString(R.string.toast_image_deleted_success));
                    }

                    //update image adapter
                    images.remove(image);
                    imageAdapter.setImages(images);
                });
            }
            Utils.getInstance().handleProgressVisibility(progressMain, false);
        });
    }
}