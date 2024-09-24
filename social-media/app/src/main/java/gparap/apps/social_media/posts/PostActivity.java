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
import java.util.concurrent.atomic.AtomicReference;

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

public class PostActivity extends AppCompatActivity {   //TODO: Refactor code
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
                    Integer.parseInt(getIntent().getStringExtra(AppConstants.POST_INTERACTION_FAVORITES)),
                    Integer.parseInt(getIntent().getStringExtra(AppConstants.POST_INTERACTION_LIKES)),
                    Integer.parseInt(getIntent().getStringExtra(AppConstants.POST_INTERACTION_DISLIKES)),
                    Integer.parseInt(getIntent().getStringExtra(AppConstants.POST_INTERACTION_COMMENTS))
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

        //display post image
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

            //update the favorites view
            TextView favorites = findViewById(R.id.post_interaction_favorites);
            favorites.setText(String.valueOf(postInteraction.getFavorites()));

            //handle the favorites interaction
            handleInteraction(currentUser, PostInteractionType.ADD_TO_FAVORITES);

            //update the likes view
            TextView likes = findViewById(R.id.post_interaction_likes);
            likes.setText(String.valueOf(postInteraction.getLikes()));

            //handle the likes interaction
            handleInteraction(currentUser, PostInteractionType.LIKE);

            //update the dislikes view
            TextView dislikes = findViewById(R.id.post_interaction_dislikes);
            dislikes.setText(String.valueOf(postInteraction.getDislikes()));

            //handle the dislikes interaction
            handleInteraction(currentUser, PostInteractionType.DISLIKE);

            //handle the comments interaction
            //TODO ("Not implemented yet.)
        }
    }

    /**
     * Handles the interaction of a user with a post.
     *
     * @param user            the current user that interacts with this post
     * @param interactionType the type of interaction
     */
    private void handleInteraction(FirebaseUser user, PostInteractionType interactionType) {
        String path = "";
        TextView view = null;
        int drawableId = 0;

        //get database reference relative path, text view, drawable id from interaction type
        switch (interactionType) {
            case ADD_TO_FAVORITES:
                path = AppConstants.DATABASE_REFERENCE_USERS_POSTS_FAVORITES;
                view = findViewById(R.id.post_interaction_favorites);
                drawableId = R.drawable.ic_favorite_24_active;
                break;

            case LIKE:
                path = AppConstants.DATABASE_REFERENCE_USERS_POSTS_LIKES;
                view = findViewById(R.id.post_interaction_likes);
                drawableId = R.drawable.ic_thumb_up_24_active;
                break;

            case DISLIKE:
                path = AppConstants.DATABASE_REFERENCE_USERS_POSTS_DISLIKES;
                view = findViewById(R.id.post_interaction_dislikes);
                drawableId = R.drawable.ic_thumb_down_24_active;
                break;

            case COMMENT:
                //TODO (Not implemented yet)
                break;
        }

        //handle interactions
        TextView finalView = view;
        int finalDrawableId = drawableId;
        String finalPath = path;
        checkForExistingInteraction(user, path, isInteractionExisting -> {
            //the user already has interacted with this post
            if (isInteractionExisting) {
                //update the view color to active
                finalView.setCompoundDrawablesRelativeWithIntrinsicBounds(finalDrawableId, 0, 0, 0);
                //update favorites interaction
                if (interactionType == PostInteractionType.ADD_TO_FAVORITES ||
                        interactionType == PostInteractionType.REMOVE_FROM_FAVORITES) {
                    updateTheUserPostInteraction(finalView, finalPath, interactionType, user);
                }
                //update likes interaction
                if (interactionType == PostInteractionType.LIKE ||
                        interactionType == PostInteractionType.REVOKE_LIKE) {
                    updateTheUserPostInteraction(finalView, finalPath, interactionType, user);
                }
                //update dislikes interaction
                if (interactionType == PostInteractionType.DISLIKE ||
                        interactionType == PostInteractionType.REVOKE_DISLIKE) {
                    updateTheUserPostInteraction(finalView, finalPath, interactionType, user);
                }
            }

            //the user not has interacted with this post (yet?)
            else {
                updateTheUserPostInteraction(finalView, finalPath, interactionType, user);
            }
        });
    }

    /**
     * Checks if a user has interacted with the post.
     *
     * @param user the current user that interacts with this post
     * @param path the relative path of the database reference
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

    /**
     * Handles the update of a specific user-post interaction.
     *
     * @param textView        the interaction view
     * @param path            the relative path of the database reference
     * @param interactionType the type of user-post interaction
     * @param currentUser     the current user that interacts with this post
     */
    private void updateTheUserPostInteraction(TextView textView, String path, PostInteractionType interactionType, FirebaseUser currentUser) {
        //onClickListener flags for adding or removing post interactions
        AtomicBoolean isAlreadyExistingFavorite = new AtomicBoolean(false);
        AtomicBoolean isAlreadyExistingLike = new AtomicBoolean(false);
        AtomicBoolean isAlreadyExistingDislike = new AtomicBoolean(false);

        //check if this post is already added to the user's favorites
        DatabaseReference dbRefFavorites;
        if (interactionType == PostInteractionType.ADD_TO_FAVORITES || interactionType == PostInteractionType.REMOVE_FROM_FAVORITES) {
            dbRefFavorites = getDatabaseReferenceByPath(AppConstants.DATABASE_REFERENCE_USERS_POSTS_FAVORITES);
            dbRefFavorites.get().addOnCompleteListener(task -> {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    String postId = dataSnapshot.child("postId").getValue(String.class);
                    if (Objects.equals(userId, currentUser.getUid())) {
                        //check if the user has already interacted with this post
                        assert postId != null;
                        if (postId.equals(post.getId())) {
                            isAlreadyExistingFavorite.set(true);
                            break;
                        }
                    }
                }
            });
        } else {
            dbRefFavorites = null;
        }

        //check if the user has already liked this post
        DatabaseReference dbRefLikes;
        if (interactionType == PostInteractionType.LIKE || interactionType == PostInteractionType.REVOKE_LIKE) {
            dbRefLikes = getDatabaseReferenceByPath(AppConstants.DATABASE_REFERENCE_USERS_POSTS_LIKES);
            dbRefLikes.get().addOnCompleteListener(task -> {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    String postId = dataSnapshot.child("postId").getValue(String.class);
                    if (Objects.equals(userId, currentUser.getUid())) {
                        //check if the user has already interacted with this post
                        assert postId != null;
                        if (postId.equals(post.getId())) {
                            isAlreadyExistingLike.set(true);
                            break;
                        }
                    }
                }
            });
        } else {
            dbRefLikes = null;
        }

        //check if the user has already disliked this post
        DatabaseReference dbRefDislikes;
        if (interactionType == PostInteractionType.DISLIKE || interactionType == PostInteractionType.REVOKE_DISLIKE) {
            dbRefDislikes = getDatabaseReferenceByPath(AppConstants.DATABASE_REFERENCE_USERS_POSTS_DISLIKES);
            dbRefDislikes.get().addOnCompleteListener(task -> {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    String postId = dataSnapshot.child("postId").getValue(String.class);
                    if (Objects.equals(userId, currentUser.getUid())) {
                        //check if the user has already interacted with this post
                        assert postId != null;
                        if (postId.equals(post.getId())) {
                            isAlreadyExistingLike.set(true);
                            break;
                        }
                    }
                }
            });
        } else {
            dbRefDislikes = null;
        }

        //add or remove post from user interactions
        textView.setOnClickListener(view -> {
            //get the DatabaseReference for the specific user-post interaction
            DatabaseReference dbRef = getDatabaseReferenceByPath(path);

            //increase the post counter
            Utils.getInstance().updatePostInteractionCounter(post.getId(), interactionType);

            //auto-generate id (for new interactions)
            DatabaseReference dbRefInteraction = dbRef.push();
            String id = dbRefInteraction.getKey();

            //update interaction
            switch (interactionType) {
                case ADD_TO_FAVORITES:
                    //the post is not in user interactions, so add it
                    if (!isAlreadyExistingFavorite.get()) {
                        //update interaction
                        assert currentUser != null;
                        UserPostFavoriteModel userFavorite = new UserPostFavoriteModel(id, currentUser.getUid(), post.getId());
                        dbRefInteraction.setValue(userFavorite);

                        //increase the interaction counter
                        postInteraction.setFavorites(postInteraction.getFavorites() + 1);
                        textView.setText(String.valueOf(postInteraction.getFavorites()));

                        //update the view color to active
                        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_favorite_24_active, 0, 0, 0);

                        //update onClickListener flag
                        isAlreadyExistingFavorite.set(true);
                    } else {
                        //the post is already in user interactions, so revoke it
                        dbRefFavorites.get().addOnCompleteListener(task -> {
                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                String pathId = dataSnapshot.child("id").getValue(String.class);
                                String userId = dataSnapshot.child("userId").getValue(String.class);
                                String postId = dataSnapshot.child("postId").getValue(String.class);
                                if (Objects.equals(userId, currentUser.getUid())) {
                                    //check if the user has already interacted with this post
                                    assert postId != null;
                                    if (postId.equals(post.getId())) {
                                        isAlreadyExistingFavorite.set(true);

                                        //get the interaction id
                                        AtomicReference<String> dbRefId = new AtomicReference<>(pathId);

                                        //handle the interaction
                                        dbRefFavorites.child(dbRefId.get()).removeValue().addOnCompleteListener(empty -> {
                                            //update the view color to inactive
                                            TextView textViewFavorites = findViewById(R.id.post_interaction_favorites);
                                            textViewFavorites.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_favorite_24, 0, 0, 0);

                                            //decrease the interaction counter
                                            if (postInteraction.getFavorites() > 0) {
                                                postInteraction.setFavorites(postInteraction.getFavorites() - 1);
                                                textViewFavorites.setText(String.valueOf(postInteraction.getFavorites()));
                                            }

                                            //decrease the post counter
                                            Utils.getInstance().updatePostInteractionCounter(post.getId(), PostInteractionType.REMOVE_FROM_FAVORITES);
                                        });
                                        //update onClickListener flag
                                        isAlreadyExistingFavorite.set(false);
                                        break;
                                    }
                                }
                            }
                        });
                    }
                    break;

                case LIKE:
                    if (!isAlreadyExistingLike.get()) {
                        //clean dislike interaction, if present & update onClickListener flag
                        revokeInteraction(AppConstants.DATABASE_REFERENCE_USERS_POSTS_DISLIKES, currentUser, PostInteractionType.LIKE);
                        isAlreadyExistingDislike.set(false);

                        //update interaction
                        assert currentUser != null;
                        UserPostLikeModel userLike = new UserPostLikeModel(id, currentUser.getUid(), post.getId());
                        dbRefInteraction.setValue(userLike);

                        //increase the interaction counter
                        postInteraction.setLikes(postInteraction.getLikes() + 1);
                        textView.setText(String.valueOf(postInteraction.getLikes()));

                        //update the view color to active
                        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_thumb_up_24_active, 0, 0, 0);

                        //update onClickListener flag
                        isAlreadyExistingLike.set(true);
                        break;
                    } else {
                        //the post is already in user interactions, so revoke it
                        dbRefLikes.get().addOnCompleteListener(task -> {
                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                String pathId = dataSnapshot.child("id").getValue(String.class);
                                String userId = dataSnapshot.child("userId").getValue(String.class);
                                String postId = dataSnapshot.child("postId").getValue(String.class);
                                if (Objects.equals(userId, currentUser.getUid())) {
                                    //check if the user has already interacted with this post
                                    assert postId != null;
                                    if (postId.equals(post.getId())) {
                                        //get the interaction id
                                        AtomicReference<String> dbRefId = new AtomicReference<>(pathId);

                                        //handle the interaction
                                        dbRefLikes.child(dbRefId.get()).removeValue().addOnCompleteListener(empty -> {
                                            //update the view color to inactive
                                            TextView textViewLikes = findViewById(R.id.post_interaction_likes);
                                            textViewLikes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_thumb_up_24, 0, 0, 0);

                                            //decrease the interaction counter
                                            if (postInteraction.getLikes() > 0) {
                                                postInteraction.setLikes(postInteraction.getLikes() - 1);
                                                textViewLikes.setText(String.valueOf(postInteraction.getLikes()));
                                            }

                                            //decrease the post counter
                                            Utils.getInstance().updatePostInteractionCounter(post.getId(), PostInteractionType.REVOKE_LIKE);
                                        });

                                        //update onClickListener flag
                                        isAlreadyExistingLike.set(false);
                                        break;
                                    }
                                }
                            }
                        });
                    }
                    break;

                case DISLIKE:
                    if (!isAlreadyExistingDislike.get()) {
                        //clean dislike interaction, if present & update onClickListener flag
                        revokeInteraction(AppConstants.DATABASE_REFERENCE_USERS_POSTS_LIKES, currentUser, PostInteractionType.DISLIKE);
                        isAlreadyExistingLike.set(false);

                        //update interaction
                        assert currentUser != null;
                        UserPostDislikeModel userDislike = new UserPostDislikeModel(id, currentUser.getUid(), post.getId());
                        dbRefInteraction.setValue(userDislike);

                        //increase the interaction counter
                        postInteraction.setDislikes(postInteraction.getDislikes() + 1);
                        textView.setText(String.valueOf(postInteraction.getDislikes()));

                        //update the view color to active
                        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_thumb_down_24_active, 0, 0, 0);

                        //update onClickListener flag
                        isAlreadyExistingDislike.set(true);
                        break;
                    } else {
                        //the post is already in user interactions, so revoke it
                        dbRefDislikes.get().addOnCompleteListener(task -> {
                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                String pathId = dataSnapshot.child("id").getValue(String.class);
                                String userId = dataSnapshot.child("userId").getValue(String.class);
                                String postId = dataSnapshot.child("postId").getValue(String.class);
                                if (Objects.equals(userId, currentUser.getUid())) {
                                    //check if the user has already interacted with this post
                                    assert postId != null;
                                    if (postId.equals(post.getId())) {
                                        //get the interaction id
                                        AtomicReference<String> dbRefId = new AtomicReference<>(pathId);

                                        //handle the interaction
                                        dbRefDislikes.child(dbRefId.get()).removeValue().addOnCompleteListener(empty -> {
                                            //update the view color to inactive
                                            TextView textViewDislikes = findViewById(R.id.post_interaction_dislikes);
                                            textViewDislikes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_thumb_down_24, 0, 0, 0);

                                            //decrease the interaction counter
                                            if (postInteraction.getDislikes() > 0) {
                                                postInteraction.setDislikes(postInteraction.getDislikes() - 1);
                                                textViewDislikes.setText(String.valueOf(postInteraction.getDislikes()));
                                            }

                                            //decrease the post counter
                                            Utils.getInstance().updatePostInteractionCounter(post.getId(), PostInteractionType.REVOKE_DISLIKE);
                                        });

                                        //update onClickListener flag
                                        isAlreadyExistingDislike.set(false);
                                        break;
                                    }
                                }
                            }
                        });
                    }
                    break;

                case COMMENT:
                    //TODO (Not implemented yet)
                    break;
            }
        });
    }

    /**
     * Revokes the opposite of a user-post like or dislike interaction.
     * These types of interactions cannot be present at the same time.
     *
     * @param path            the relative path of the database reference
     * @param currentUser     the type of user-post interaction
     * @param interactionType the current user that interacts with this post
     */
    private void revokeInteraction(String path, FirebaseUser currentUser, PostInteractionType interactionType) {
        //find this user-post specific interaction, if exists
        AtomicReference<String> dbRefId = new AtomicReference<>("");
        DatabaseReference dbRef = getDatabaseReferenceByPath(path);
        dbRef.get().addOnCompleteListener(task -> {
            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                String userId = dataSnapshot.child("userId").getValue(String.class);
                String postId = dataSnapshot.child("postId").getValue(String.class);
                assert userId != null;
                assert postId != null;
                if (userId.equals(currentUser.getUid()) && postId.equals(post.getId())) {
                    //get id of the database reference
                    dbRefId.set(dataSnapshot.child("id").getValue(String.class));

                    //revoke the opposite interaction
                    if (!dbRefId.get().isEmpty()) {
                        //revoke dislike interaction
                        if (interactionType == PostInteractionType.LIKE) {
                            dbRef.child(dbRefId.get()).removeValue().addOnCompleteListener(empty -> {
                                //update the view color to active
                                TextView textViewDislikes = findViewById(R.id.post_interaction_dislikes);
                                textViewDislikes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_thumb_down_24, 0, 0, 0);
                                handleInteraction(currentUser, PostInteractionType.DISLIKE);

                                //decrease the interaction counter
                                if (postInteraction.getDislikes() > 0) {
                                    postInteraction.setDislikes(postInteraction.getDislikes() - 1);
                                    textViewDislikes.setText(String.valueOf(postInteraction.getDislikes()));
                                }

                                //decrease the post counter
                                Utils.getInstance().updatePostInteractionCounter(post.getId(), PostInteractionType.REVOKE_DISLIKE);
                            });
                            break;
                        }

                        //revoke like interaction
                        else if (interactionType == PostInteractionType.DISLIKE) {
                            dbRef.child(dbRefId.get()).removeValue().addOnCompleteListener(empty -> {
                                //update the view color to active
                                TextView textViewLikes = findViewById(R.id.post_interaction_likes);
                                textViewLikes.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_thumb_up_24, 0, 0, 0);
                                handleInteraction(currentUser, PostInteractionType.LIKE);

                                //decrease the interaction counter
                                if (postInteraction.getLikes() > 0) {
                                    postInteraction.setLikes(postInteraction.getLikes() - 1);
                                    textViewLikes.setText(String.valueOf(postInteraction.getLikes()));
                                }

                                //decrease the post counter
                                Utils.getInstance().updatePostInteractionCounter(post.getId(), PostInteractionType.REVOKE_LIKE);
                            });
                            break;
                        }
                    }
                }
            }
        });
    }
}