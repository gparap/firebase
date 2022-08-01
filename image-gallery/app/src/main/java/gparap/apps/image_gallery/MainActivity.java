package gparap.apps.image_gallery;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    }
}