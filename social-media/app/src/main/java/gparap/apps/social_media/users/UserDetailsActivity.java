/*
 * Copyright 2024 gparap
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
package gparap.apps.social_media.users;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import gparap.apps.social_media.R;
import gparap.apps.social_media.data.UserModel;
import gparap.apps.social_media.utils.AppConstants;

@SuppressLint("SetTextI18n")
public class UserDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        //get user details from Intent
        UserModel user = new UserModel();
        Intent intent = getIntent();
        if (intent != null) {
            user.setId(intent.getStringExtra(AppConstants.USER_ID));
            user.setUsername(intent.getStringExtra(AppConstants.USER_NAME));
            user.setEmail(intent.getStringExtra(AppConstants.USER_EMAIL));
            user.setPhone(intent.getStringExtra(AppConstants.USER_PHONE));
            user.setPassword(intent.getStringExtra(AppConstants.USER_PASSWORD));
            user.setImageUrl(intent.getStringExtra(AppConstants.USER_IMAGE_URL));
            user.setPostsCount(intent.getIntExtra(AppConstants.USER_POSTS, 0));
            user.setAboutMe(intent.getStringExtra(AppConstants.USER_ABOUT_ME));
            user.setMemberSince(intent.getStringExtra(AppConstants.USER_MEMBER_SINCE));
        }

        //display the user image
        ImageView userImage = findViewById(R.id.imageViewUserDetails);
        Picasso.get().load(user.getImageUrl()).into(userImage);

        //display the user name
        TextView userName = findViewById(R.id.textViewUserDetailsName);
        userName.setText(getResources().getString(R.string.label_user_name) + user.getUsername());

        //display the user member date
        TextView userMemberDate = findViewById(R.id.textViewUserDetailsMemberSince);
        userMemberDate.setText(getResources().getString(R.string.label_member_since) + user.getMemberSince());

        //display the user posts
        TextView userPosts = findViewById(R.id.textViewUserDetailsPosts);
        userPosts.setText(getResources().getString(R.string.label_posts_count) + user.getPostsCount());

        //display the user about text
        TextView userAboutText = findViewById(R.id.textViewUserDetailsAboutMe);
        userAboutText.setText(user.getAboutMe());
    }
}