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
package gparap.apps.social_media.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gparap.apps.social_media.data.PostModel;
import gparap.apps.social_media.data.UserModel;
import gparap.apps.social_media.posts.PostInteractionType;

import static gparap.apps.social_media.utils.AppConstants.*;

public class Utils {
    private static Utils instance;

    private Utils() {
    }

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    /**
     * Updates the number of the posts this user has published.
     *
     * @param userId     The current signed-in user of the application
     * @param isAddition Flag to tell if we have addition (or deletion) of post
     */
    public void updateUserPostCounter(String userId, Boolean isAddition) {
        //get the FirebaseDatabase instance for the specified URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //get the DatabaseReference for the specific user
        DatabaseReference userRef = firebaseDatabase.getReference(DATABASE_REFERENCE).child(DATABASE_REFERENCE_USERS).child(userId);

        //update the number of the posts
        userRef.get().addOnCompleteListener(task -> {
            DataSnapshot userSnapshot = task.getResult();
            UserModel user = userSnapshot.getValue(UserModel.class);
            if (user != null) {
                int posts = user.getPostsCount();
                if (isAddition) {
                    posts += 1;
                } else {
                    posts -= 1;
                }
                userRef.child(DATABASE_FIELD_POSTS_COUNT).setValue(posts);
            }
        });
    }

    /**
     * Updates the number of times the post is specifically marked by a user interaction.
     *
     * @param postId            The current post the is been viewed by a user of the application.
     * @param interactionType   A specific interaction like add to favorites, like, dislike, etc.
     */
    public void updatePostInteractionCounter(String postId, PostInteractionType interactionType) {
        //get the FirebaseDatabase instance for the specified URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //get the DatabaseReference for the specific post
        DatabaseReference postRef = firebaseDatabase.getReference(DATABASE_REFERENCE).child(DATABASE_REFERENCE_POSTS).child(postId);

        //update the interaction counter of the post
        postRef.get().addOnCompleteListener(task -> {
            DataSnapshot postSnapshot = task.getResult();
            PostModel post = postSnapshot.getValue(PostModel.class);
            if (post != null) {
                int interactions;
                switch (interactionType) {
                    case ADD_TO_FAVORITES:
                        interactions = post.getFavorites();
                        interactions += 1;
                        postRef.child(DATABASE_FIELD_POST_FAVORITES_COUNT).setValue(interactions);
                        break;
                    case REMOVE_FROM_FAVORITES:
                        interactions = post.getFavorites();
                        interactions -= 1;
                        postRef.child(DATABASE_FIELD_POST_FAVORITES_COUNT).setValue(interactions);
                        break;
                    case LIKE:
                        interactions = post.getLikes();
                        interactions += 1;
                        postRef.child(DATABASE_FIELD_POST_LIKES_COUNT).setValue(interactions);
                        break;
                    case DISLIKE:
                        interactions = post.getDislikes();
                        interactions += 1;
                        postRef.child(DATABASE_FIELD_POST_DISLIKES_COUNT).setValue(interactions);
                        break;
                    case COMMENT:
                        //TODO (Not implemented yet)
                        break;
                }
            }
        });
    }
}
