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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import gparap.apps.blog.MainActivity;
import gparap.apps.blog.R;
import gparap.apps.blog.auth.LoginActivity;

public class AddPostActivity extends AppCompatActivity {
    private ImageButton buttonAdd;
    private EditText postTitle, postDetails;
    private Button buttonSave;
    private ProgressBar progressBar;
    private final int ACTION_GET_CONTENT_RESULT_CODE = 999;
    private Uri imageUri;
    /*Note from Firebase: To get a reference to a database other than a us-central1 default database,
    you must pass the database URL to getInstance() (or Kotlin+KTX database()).
    For a us-central1 default database, you can call getInstance() (or database) without arguments.*/
    @SuppressWarnings("FieldCanBeLocal")
    private final String databaseURL = "https://blog-d6918-default-rtdb.europe-west1.firebasedatabase.app/";

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

    private void savePostToDatabase(String imageDownloadUrl, String userId) {
        //get the FirebaseDatabase instance for the specified URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseURL);

        //get the DatabaseReference for the database root node
        DatabaseReference postsRef = firebaseDatabase.getReference().child("posts");

        //get the DatabaseReference for an auto-generated node
        DatabaseReference postRef = postsRef.push();

        //write data to the database
        postRef.child("title").setValue(postTitle.getText().toString().trim());
        postRef.child("details").setValue(postDetails.getText().toString().trim());
        postRef.child("image").setValue(imageDownloadUrl.trim());
        postRef.child("user_id").setValue(userId);
    }

    private StorageTask<UploadTask.TaskSnapshot> saveImageToCloudStorage(String userId) {
        StorageTask<UploadTask.TaskSnapshot> storageTask;

        //get a reference to app cloud storage
        StorageReference cloudRef = FirebaseStorage.getInstance().getReference();

        //get a reference to the user image
        StorageReference imageRef = cloudRef.child("images").child(userId).child(imageUri.getLastPathSegment());

        //add user image to cloud storage
        storageTask = imageRef.putFile(imageUri).addOnFailureListener(e ->
                Toast.makeText(AddPostActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
        );
        return storageTask;
    }

    /**
     * Saves a user's blog post to the cloud.
     * Firstly, it saves the image to cloud storage and
     * secondly, it saves the post to the database.
     */
    private void savePost() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is authenticated
            if (!user.isAnonymous()) {
                progressBar.setVisibility(View.VISIBLE);

                //save post without image
                if (imageUri == null) {
                    savePostToDatabase("", user.getUid());
                    progressBar.setVisibility(View.INVISIBLE);
                    gotoMainActivity();
                } else {
                    StorageTask<UploadTask.TaskSnapshot> saveImageTask = saveImageToCloudStorage(user.getUid());
                    saveImageTask.addOnCompleteListener(task -> task.addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> downloadURL = taskSnapshot.getStorage().getDownloadUrl();
                        downloadURL.addOnCompleteListener(task1 ->
                                task1.addOnSuccessListener(uri ->
                                        savePostToDatabase(uri.toString(), user.getUid()))
                                        .addOnCompleteListener(task2 -> {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            gotoMainActivity();
                                        })
                        );
                    }));
                }
            } else if (user.isAnonymous()) {
                //redirect to login activity and inform user that they must be authenticated to post
                Toast.makeText(AddPostActivity.this, R.string.toast_user_must_authenticate, Toast.LENGTH_LONG).show();
                startActivity(new Intent(AddPostActivity.this, LoginActivity.class));
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
        startActivity(new Intent(AddPostActivity.this, MainActivity.class));
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