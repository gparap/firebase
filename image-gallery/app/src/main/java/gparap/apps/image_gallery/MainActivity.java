package gparap.apps.image_gallery;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import gparap.apps.image_gallery.adapters.ImageAdapter;
import gparap.apps.image_gallery.data.ImageModel;

public class MainActivity extends AppCompatActivity {

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

        //test list of image models
        ArrayList<ImageModel> testModels = new ArrayList<>();
        ImageModel imageModel1 = new ImageModel();
        imageModel1.setName("name1");
        imageModel1.setUri("url1");
        ImageModel imageModel2 = new ImageModel();
        imageModel2.setName("name2");
        imageModel2.setUri("url2");
        ImageModel imageModel3 = new ImageModel();
        imageModel3.setName("name3");
        imageModel3.setUri("url3");
        testModels.add(imageModel1);
        testModels.add(imageModel2);
        testModels.add(imageModel3);

        //create RecyclerView with Adapter for images
        RecyclerView recyclerView = findViewById(R.id.recyclerView_imageGallery);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageAdapter imageAdapter = new ImageAdapter();
        imageAdapter.setImages(testModels);
        recyclerView.setAdapter(imageAdapter);
    }
}