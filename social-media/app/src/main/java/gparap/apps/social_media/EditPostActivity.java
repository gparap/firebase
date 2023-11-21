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
package gparap.apps.social_media;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import gparap.apps.social_media.data.PostModel;
import gparap.apps.social_media.utils.AppConstants;

public class EditPostActivity extends AppCompatActivity {
    private PostModel post = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        //get post extras from intent
        if (getIntent() != null) {
            post = new PostModel(getIntent().getStringExtra(AppConstants.POST_ID),
                    getIntent().getStringExtra(AppConstants.POST_USER_ID),
                    getIntent().getStringExtra(AppConstants.POST_TITLE),
                    getIntent().getStringExtra(AppConstants.POST_DETAILS),
                    getIntent().getStringExtra(AppConstants.POST_IMAGE_URL),
                    getIntent().getStringExtra(AppConstants.POST_IMAGE_STORAGE_ID));
        }

        //display post texts
        EditText editTextPostTitle = findViewById(R.id.editText_editPostTitle);
        editTextPostTitle.setText(post.getTitle());
        EditText editTextPostDetails = findViewById(R.id.editText_editPostDetails);
        editTextPostDetails.setText(post.getDetails());

        //display post image
        if (!post.getImageUrl().isEmpty()){
            Picasso.get().load(post.getImageUrl()).into(((ImageView) findViewById(R.id.imageButton_editPostImage)));
        }
    }
}