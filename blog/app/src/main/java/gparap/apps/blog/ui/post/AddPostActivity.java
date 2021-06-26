package gparap.apps.blog.ui.post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import gparap.apps.blog.R;

public class AddPostActivity extends AppCompatActivity {
    ImageButton buttonAdd;
    EditText postTitle, postDetails;
    Button buttonSave;
    private final int ACTION_GET_CONTENT_ReSULT_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        getWidgets();

        //add an image
        buttonAdd.setOnClickListener(v ->
                addPostImage()
        );

        //save post
        buttonSave.setOnClickListener(v -> {
            if (isPostValidated()) {
                savePostToDatabase();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //set button image as the selected image
        if (resultCode == ACTION_GET_CONTENT_ReSULT_CODE) {
            Uri selectedImage = Objects.requireNonNull(data).getData();
            buttonAdd.setImageURI(selectedImage);
        }
    }

    private void savePostToDatabase() {
        //TODO
    }

    @SuppressWarnings("deprecation")
    private void addPostImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, ACTION_GET_CONTENT_ReSULT_CODE);
    }

    /**
     * Checks is post title and post details are filled-in.
     *
     * @return boolean
     */
    private boolean isPostValidated() {
        if (postTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_post_title, Toast.LENGTH_SHORT).show();
            return false;
        } else if (postDetails.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_post_details, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getWidgets() {
        buttonAdd = findViewById(R.id.imageButtonAddPost);
        postTitle = findViewById(R.id.editTextAddPostTitle);
        postDetails = findViewById(R.id.editTextAddPostDetails);
        buttonSave = findViewById(R.id.buttonSavePost);
    }
}