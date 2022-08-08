package gparap.apps.image_gallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import gparap.apps.image_gallery.adapters.ImageAdapter;
import gparap.apps.image_gallery.data.ImageModel;
import gparap.apps.image_gallery.utils.AppConstants;

public class MainActivity extends AppCompatActivity {
    ArrayList<ImageModel> images = new ArrayList<>();
    ImageAdapter imageAdapter = new ImageAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //pick image
        FloatingActionButton fabImagePicker = findViewById(R.id.fab_imagePicker);
        fabImagePicker.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImagePickerActivity.class);
            startActivity(intent);
        });

        //read data from the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child(
                AppConstants.DATABASE_REFERENCE_PATH
        );
        databaseRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                //inform the user that cannot fetch images from the database
                Toast.makeText(MainActivity.this,
                        getResources().getString(R.string.toast_error_loading_images),
                        Toast.LENGTH_SHORT
                ).show();

            } else {
                //put the images fetched from the database inside the images list
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    ImageModel imageModel = dataSnapshot.getValue(ImageModel.class);
                    images.add(imageModel);
                }

                //update Adapter dataset with images
                imageAdapter.setImages(images);
            }
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
        System.out.println(image.getName());
        System.out.println(image.getUri());
        //TODO: delete image from storage
        //TODO: delete image metadata from database
    }
}