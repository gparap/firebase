package gparap.apps.image_gallery;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ImagePickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        //pick an image
        ImageButton buttonPickImage = findViewById(R.id.buttonImagePicker);
        buttonPickImage.setOnClickListener(v-> getContentCallback.launch("image/*"));
    }

    //register a callback for an activity result to prompt the user to pick a piece of content
    ActivityResultLauncher<String> getContentCallback =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    //handle the activity result
                    uri -> {
                        //display the image picked
                        ImageView imagePicked = findViewById(R.id.imagePicked);
                        imagePicked.setImageURI(uri);
                    });
}