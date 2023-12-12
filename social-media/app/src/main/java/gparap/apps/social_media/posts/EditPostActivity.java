/*
 * Copyright 2023 gparap
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
package gparap.apps.social_media.posts;

import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_DETAILS;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_FIELD_POST_TITLE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE_POSTS;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import gparap.apps.social_media.MainActivity;
import gparap.apps.social_media.R;
import gparap.apps.social_media.data.PostModel;
import gparap.apps.social_media.utils.AppConstants;

public class EditPostActivity extends AppCompatActivity {
    private PostModel post = null;
    private EditText editTextPostTitle;
    private EditText editTextPostDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        //get post extras from intent
        if (getIntent() != null) {
            post = new PostModel.Builder()
                    .setId(getIntent().getStringExtra(AppConstants.POST_ID))
                    .setUserId(getIntent().getStringExtra(AppConstants.POST_USER_ID))
                    .setTitle(getIntent().getStringExtra(AppConstants.POST_TITLE))
                    .setDetails(getIntent().getStringExtra(AppConstants.POST_DETAILS))
                    .setImageUrl(getIntent().getStringExtra(AppConstants.POST_IMAGE_URL))
                    .setImageStorageId(getIntent().getStringExtra(AppConstants.POST_IMAGE_STORAGE_ID))
                    .build();
        }

        //display post texts
        editTextPostTitle = findViewById(R.id.editText_editPostTitle);
        editTextPostTitle.setText(post.getTitle());
        editTextPostDetails = findViewById(R.id.editText_editPostDetails);
        editTextPostDetails.setText(post.getDetails());

        //display post image
        if (!post.getImageUrl().isEmpty()) {
            Picasso.get().load(post.getImageUrl()).into(((ImageView) findViewById(R.id.imageButton_editPostImage)));
        }

        //save post
        Button buttonUpdate = findViewById(R.id.buttonSave_editPost);
        buttonUpdate.setOnClickListener(v -> {
            //save edited post without image
            if (shouldUpdatePost()) {
                //get the FirebaseDatabase instance for the specified URL
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                //get the DatabaseReference for the node that should be updated
                DatabaseReference postRef = firebaseDatabase.getReference(DATABASE_REFERENCE).child(DATABASE_REFERENCE_POSTS).child(post.getId());

                //write data to the database
                if (shouldUpdatePostTitle()) {
                    postRef.child(DATABASE_FIELD_POST_TITLE).setValue(editTextPostTitle.getText().toString());
                }
                if (shouldUpdatePostDetails()) {
                    postRef.child(DATABASE_FIELD_POST_DETAILS).setValue(editTextPostDetails.getText().toString());
                }

                //finish and return to main activity
                this.finish();
                startActivity(new Intent(this, MainActivity.class));
                Toast.makeText(this, getResources().getString(R.string.toast_post_edited), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Checks if post should be updated TODO: check image url
    private boolean shouldUpdatePost() {
        String title = editTextPostTitle.getText().toString();
        String details = editTextPostDetails.getText().toString();

        if (post.getTitle().equals(title) && post.getDetails().equals(details)) {
            return false;
        }
        return !title.isEmpty() || !details.isEmpty();
    }

    private boolean shouldUpdatePostTitle() {
        return !post.getTitle().equals(editTextPostTitle.getText().toString());
    }

    private boolean shouldUpdatePostDetails() {
        return !post.getDetails().equals(editTextPostDetails.getText().toString());
    }
}