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

import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE_POSTS;
import static gparap.apps.social_media.utils.AppConstants.POST_DETAILS;
import static gparap.apps.social_media.utils.AppConstants.POST_ID;
import static gparap.apps.social_media.utils.AppConstants.POST_IMAGE_STORAGE_ID;
import static gparap.apps.social_media.utils.AppConstants.POST_IMAGE_URL;
import static gparap.apps.social_media.utils.AppConstants.POST_TITLE;
import static gparap.apps.social_media.utils.AppConstants.POST_USER_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import gparap.apps.social_media.MainActivity;
import gparap.apps.social_media.R;
import gparap.apps.social_media.data.PostInteractionModel;
import gparap.apps.social_media.data.PostModel;
import gparap.apps.social_media.data.UserPostDislikeModel;
import gparap.apps.social_media.data.UserPostFavoriteModel;
import gparap.apps.social_media.data.UserPostLikeModel;
import gparap.apps.social_media.interfaces.UserPostInteractionCallback;
import gparap.apps.social_media.utils.AppConstants;
import gparap.apps.social_media.utils.Utils;

public class PostActivity extends AppCompatActivity {
    private PostModel post = null;
    private PostInteractionModel postInteraction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //get post extras from intent
        String username = "";
        if (getIntent() != null) {
            //get post data
            post = new PostModel.Builder()
                    .setId(getIntent().getStringExtra(AppConstants.POST_ID))
                    .setUserId(getIntent().getStringExtra(AppConstants.POST_USER_ID))
                    .setTitle(getIntent().getStringExtra(AppConstants.POST_TITLE))
                    .setDetails(getIntent().getStringExtra(AppConstants.POST_DETAILS))
                    .setImageUrl(getIntent().getStringExtra(AppConstants.POST_IMAGE_URL))
                    .setImageStorageId(getIntent().getStringExtra(AppConstants.POST_IMAGE_STORAGE_ID))
                    .build();

            //get post interactions data
            postInteraction = new PostInteractionModel(
                    Integer.parseInt(getIntent().getStringExtra("post_interaction_favorites")),
                    Integer.parseInt(getIntent().getStringExtra("post_interaction_likes")),
                    Integer.parseInt(getIntent().getStringExtra("post_interaction_dislikes")),
                    Integer.parseInt(getIntent().getStringExtra("post_interaction_comments"))
            );

            //get username
            username = getIntent().getStringExtra(AppConstants.POST_USER_NAME);
        }

        //display post texts
        assert post != null;
        ((TextView) findViewById(R.id.textViewPostTitle)).setText(post.getTitle());
        ((TextView) findViewById(R.id.textViewPostDetails)).setText(post.getDetails());
        ((TextView) findViewById(R.id.textViewPostCreator)).setText(
                String.format("%s%s", getString(R.string.text_posted_by), username)
        );

        //display post image TODO: fixed <<if (!post.getImageUrl().isEmpty())>>
        if (!post.getImageUrl().isEmpty()) {
            Picasso.get().load(post.getImageUrl()).into(((ImageView) findViewById(R.id.imageViewPost)));
        }

        //if the user that views the post is also its creator, show the related buttons
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getDisplayName(), username)) {
            //delete post
            ImageButton buttonDeletePost = findViewById(R.id.imageButton_deletePost);
            buttonDeletePost.setVisibility(View.VISIBLE);
            buttonDeletePost.setOnClickListener(v -> {
                //display a confirmation dialog
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.text_post_deleted_alert_dialog))
                        .setPositiveButton(getString(R.string.text_ok), (dialog, which) ->
                                FirebaseDatabase.getInstance().getReference().getDatabase()
                                        .getReference(DATABASE_REFERENCE)
                                        .child(DATABASE_REFERENCE_POSTS).child(post.getId())
                                        .removeValue().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(this, getString(R.string.text_post_deleted), Toast.LENGTH_SHORT).show();

                                                //delete post image from cloud storage
                                                if (!post.getImageStorageId().isEmpty()) {
                                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(DATABASE_REFERENCE);
                                                    StorageReference userRef = storageReference.child(post.getUserId());
                                                    StorageReference imageRef = userRef.child(post.getImageStorageId());
                                                    imageRef.delete();
                                                }

                                                //decrease the number of the posts this user has published
                                                Utils.getInstance().updateUserPostCounter(currentUser.getUid(), false);

                                                //close this activity and return to application posts
                                                this.startActivity(new Intent(this, MainActivity.class));
                                                this.finish();

                                            } else if (task.getException() != null) {
                                                Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }))
                        .setNegativeButton(getString(R.string.text_cancel), (dialog, which) -> dialog.dismiss())
                        .create().show();
            });

            //edit post
            ImageButton buttonEditPost = findViewById(R.id.imageButton_editPost);
            buttonEditPost.setVisibility(View.VISIBLE);
            buttonEditPost.setOnClickListener(v -> {
                //open the post for editing
                Intent intent = new Intent(this, EditPostActivity.class);
                intent.putExtra(POST_ID, post.getId());
                intent.putExtra(POST_USER_ID, post.getUserId());
                intent.putExtra(POST_TITLE, post.getTitle());
                intent.putExtra(POST_DETAILS, post.getDetails());
                intent.putExtra(POST_IMAGE_URL, post.getImageUrl());
                intent.putExtra(POST_IMAGE_STORAGE_ID, post.getImageStorageId());
                startActivity(intent);
            });

        }

        //if the user that views the post is not its creator, show & handle the interactions
        else {
            //show the interaction layout
            LinearLayout layoutPostInteractions = findViewById(R.id.layout_post_interactions);
            layoutPostInteractions.setVisibility(View.VISIBLE);

            //get the favorites view
            TextView favorites = findViewById(R.id.post_interaction_favorites);
            favorites.setText(String.valueOf(postInteraction.getFavorites()));

            //handle the favorites interaction //TODO: Refactor
            checkForExistingInteraction(currentUser, "users_posts_favorites", isInteractionExisting -> {
                //the user has added this post to its favorites
                if (isInteractionExisting) {
                    //update the view color to active
                    favorites.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_favorite_24_active, 0, 0, 0);
                }

                //the user has not added this post to its favorites (yet?)
                else {
                    //onClickListener flag for adding or removing a post to favorites
                    AtomicBoolean isAlreadyAddedToFavorites = new AtomicBoolean(false);

                    //add or remove post from user favorites
                    favorites.setOnClickListener(view -> {
                        //the post is not in user favorites, so add it
                        if (!isAlreadyAddedToFavorites.get()) {
                            //get the DatabaseReference for the specific user-post interaction
                            DatabaseReference dbRef = getDatabaseReferenceByPath("users_posts_favorites");

                            //increase the post counter
                            Utils.getInstance().updatePostInteractionCounter(post.getId(), "addToFavorite");

                            //auto-generate id
                            DatabaseReference dbRefInteraction = dbRef.push();
                            String id = dbRefInteraction.getKey();

                            //update UserPostFavorite interaction
                            assert currentUser != null;
                            UserPostFavoriteModel userFavorite = new UserPostFavoriteModel(id, currentUser.getUid(), post.getId());
                            dbRefInteraction.setValue(userFavorite);

                            //increase the favorites counter
                            postInteraction.setFavorites(postInteraction.getFavorites() + 1);
                            favorites.setText(String.valueOf(postInteraction.getFavorites()));

                            //update the view color to active
                            favorites.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_favorite_24_active, 0, 0, 0);

                            //update onClickListener flag
                            isAlreadyAddedToFavorites.set(true);
                        }

                        //TODO: the post is already in user favorites, so remove it
                    });
                }
            });

            //handle the likes interaction //TODO: Refactor, Revoke dislike
            TextView likes = findViewById(R.id.post_interaction_likes);
            likes.setText(String.valueOf(postInteraction.getLikes()));
            likes.setOnClickListener(view -> {
                //get the FirebaseDatabase instance for the specified URL
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                //get the DatabaseReference for the specific user-post interaction
                DatabaseReference dbRef = database.getReference(DATABASE_REFERENCE).child("users_posts_thumbsUp");

                //find the current user interactions (likes)
                AtomicBoolean isAlreadyLiked = new AtomicBoolean(false);
                dbRef.get().addOnCompleteListener(taskLikes -> {
                    isAlreadyLiked.set(false);
                    for (DataSnapshot dataSnapshot : taskLikes.getResult().getChildren()) {
                        String userId = dataSnapshot.child("userId").getValue(String.class);
                        String postId = dataSnapshot.child("postId").getValue(String.class);
                        assert currentUser != null;
                        if (Objects.equals(userId, currentUser.getUid())) {
                            //check if the user has already liked this post
                            assert postId != null;
                            if (postId.equals(post.getId())) {
                                isAlreadyLiked.set(true);
                                break;
                            }
                        }
                    }

                    //add post to user likes
                    if (!isAlreadyLiked.get()) {
                        //increase the post counter
                        Utils.getInstance().updatePostInteractionCounter(post.getId(), "like");

                        //auto-generate id
                        DatabaseReference dbRefInteraction = dbRef.push();
                        String id = dbRefInteraction.getKey();

                        //update UserPostLike interaction
                        assert currentUser != null;
                        UserPostLikeModel userLike = new UserPostLikeModel(id, currentUser.getUid(), post.getId());
                        dbRefInteraction.setValue(userLike);

                        //TODO: show increased interaction counter
                        //DEBUG...just for testing now
                        int tempLikeInt = Integer.parseInt(likes.getText().toString());
                        tempLikeInt++;
                        likes.setText(String.valueOf(tempLikeInt));
                    }
                });
            });

            //handle the dislikes interaction //TODO: Refactor, Revoke like
            TextView dislikes = findViewById(R.id.post_interaction_dislikes);
            dislikes.setText(String.valueOf(postInteraction.getDislikes()));
            dislikes.setOnClickListener(view -> {
                //get the FirebaseDatabase instance for the specified URL
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                //get the DatabaseReference for the specific user-post interaction
                DatabaseReference dbRef = database.getReference(DATABASE_REFERENCE).child("users_posts_thumbsDown");

                //find the current user interactions (dislikes)
                AtomicBoolean isAlreadyDisliked = new AtomicBoolean(false);
                dbRef.get().addOnCompleteListener(taskDislikes -> {
                    isAlreadyDisliked.set(false);
                    for (DataSnapshot dataSnapshot : taskDislikes.getResult().getChildren()) {
                        String userId = dataSnapshot.child("userId").getValue(String.class);
                        String postId = dataSnapshot.child("postId").getValue(String.class);
                        assert currentUser != null;
                        if (Objects.equals(userId, currentUser.getUid())) {
                            //check if the user has already disliked this post
                            assert postId != null;
                            if (postId.equals(post.getId())) {
                                isAlreadyDisliked.set(true);
                                break;
                            }
                        }
                    }

                    //add post to user dislikes
                    if (!isAlreadyDisliked.get()) {
                        //increase the post counter
                        Utils.getInstance().updatePostInteractionCounter(post.getId(), "dislike");

                        //auto-generate id
                        DatabaseReference dbRefInteraction = dbRef.push();
                        String id = dbRefInteraction.getKey();

                        //update UserPostDislike interaction
                        assert currentUser != null;
                        UserPostDislikeModel userDislike = new UserPostDislikeModel(id, currentUser.getUid(), post.getId());
                        dbRefInteraction.setValue(userDislike);

                        //TODO: show increased interaction counter
                        //DEBUG...just for testing now
                        int tempLikeInt = Integer.parseInt(dislikes.getText().toString());
                        tempLikeInt++;
                        dislikes.setText(String.valueOf(tempLikeInt));
                    }
                });
            });

            //handle the comments interaction
            //TODO ("Not implemented yet.)
        }

    }

    /**
     * Checks if a user has interacted with the post.
     *
     * @param user the current user that interacts with this post
     * @param path database reference path
     */
    private void checkForExistingInteraction(FirebaseUser user, String path, UserPostInteractionCallback callback) {
        AtomicBoolean isInteractionExisting = new AtomicBoolean(false);

        //get the FirebaseDatabase instance for the specified URL
        DatabaseReference dbRef = getDatabaseReferenceByPath(path);

        //find the current user interactions
        dbRef.get().addOnCompleteListener(task -> {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        String userId = dataSnapshot.child("userId").getValue(String.class);
                        String postId = dataSnapshot.child("postId").getValue(String.class);
                        assert user != null;
                        if (Objects.equals(userId, user.getUid())) {
                            //check if the user has already interacted with this post
                            assert postId != null;
                            if (postId.equals(post.getId())) {
                                isInteractionExisting.set(true);
                                break;
                            }
                        }
                    }

                    //invoke the callback with the existing interaction result
                    callback.onExistingInteraction(isInteractionExisting.get());
                }

        );
    }

    /**
     * Returns a DatabaseReference object based on a relative path.
     *
     * @param path the relative path of the database reference
     * @return DatabaseReference object
     */
    private DatabaseReference getDatabaseReferenceByPath(String path) {
        //get the FirebaseDatabase instance for the specified URL
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //return the DatabaseReference for the path
        return database.getReference(DATABASE_REFERENCE).child(path);
    }
}