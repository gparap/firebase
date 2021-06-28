package gparap.apps.blog.ui.post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import gparap.apps.blog.MainActivity;
import gparap.apps.blog.R;
import gparap.apps.blog.auth.LoginActivity;
import gparap.apps.blog.model.BlogPostModel;
import gparap.apps.blog.utils.FirebaseUtils;

public class AddBlogPostActivity extends AppCompatActivity {
    private ImageButton buttonAdd;
    private EditText postTitle, postDetails;
    private Button buttonSave;
    private ProgressBar progressBar;
    private final int ACTION_GET_CONTENT_RESULT_CODE = 999;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.add_post));
        getWidgets();

        //add an image to post
        buttonAdd.setOnClickListener(v ->
                addPostImage()
        );

        //save post
        buttonSave.setOnClickListener(v -> {
            if (isPostValidated()) {
                savePost();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //set button image as the selected image
        if (resultCode == RESULT_OK && requestCode == ACTION_GET_CONTENT_RESULT_CODE) {
            imageUri = Objects.requireNonNull(data).getData();
            buttonAdd.setImageURI(imageUri);
        }
    }

    /**
     * Saves a user's blog post to the cloud.
     * Firstly, it saves the image to cloud storage and
     * secondly, it saves the post to the database.
     */
    private void savePost() {
        FirebaseUser user = FirebaseUtils.getInstance().getUser();
        if (user != null) {
            //user is authenticated
            if (!user.isAnonymous()) {
                progressBar.setVisibility(View.VISIBLE);

                //create blog post object
                BlogPostModel blogPost = new BlogPostModel("",
                        postTitle.getText().toString().trim(),
                        postDetails.getText().toString().trim(), user.getUid()
                );

                //save post without image
                if (imageUri == null) {
                    FirebaseUtils.getInstance().saveBlogPostToDatabase(blogPost);
                    progressBar.setVisibility(View.INVISIBLE);
                    gotoMainActivity();

                } else {
                    StorageTask<UploadTask.TaskSnapshot> saveImageTask =
                            FirebaseUtils.getInstance().saveBlogPostImageToCloudStorage(
                                    AddBlogPostActivity.this, imageUri, user.getUid()
                            );
                    saveImageTask.addOnCompleteListener(task -> task.addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> downloadURL = taskSnapshot.getStorage().getDownloadUrl();
                        downloadURL.addOnCompleteListener(task1 ->
                                task1.addOnSuccessListener(uri -> {
                                    //update blog post with image url
                                    blogPost.setImageUrl(uri.toString().trim());

                                    FirebaseUtils.getInstance().saveBlogPostToDatabase(blogPost);
                                }).addOnCompleteListener(task2 -> {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    gotoMainActivity();
                                })
                        );
                    }));
                }

            } else if (user.isAnonymous()) {
                //redirect to login activity and inform user that they must be authenticated to post
                Toast.makeText(AddBlogPostActivity.this, R.string.toast_user_must_authenticate, Toast.LENGTH_LONG).show();
                startActivity(new Intent(AddBlogPostActivity.this, LoginActivity.class));
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void addPostImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, ACTION_GET_CONTENT_RESULT_CODE);
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

    private void gotoMainActivity() {
        startActivity(new Intent(AddBlogPostActivity.this, MainActivity.class));
        finish();
    }

    private void getWidgets() {
        buttonAdd = findViewById(R.id.imageButtonAddPost);
        postTitle = findViewById(R.id.editTextAddPostTitle);
        postDetails = findViewById(R.id.editTextAddPostDetails);
        buttonSave = findViewById(R.id.buttonSavePost);
        progressBar = findViewById(R.id.progressBarAddPost);
    }
}