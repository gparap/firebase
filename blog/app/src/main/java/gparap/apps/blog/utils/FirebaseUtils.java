/*
 * Copyright 2021 gparap
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
package gparap.apps.blog.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import gparap.apps.blog.model.BlogPostModel;

/**
 * Utilities for Firebase authentication, storage and database operations.
 */
public class FirebaseUtils {
    /*Note from Firebase: To get a reference to a database other than a us-central1 default database,
    you must pass the database URL to getInstance() (or Kotlin+KTX database()).
    For a us-central1 default database, you can call getInstance() (or database) without arguments.*/
    @SuppressWarnings("FieldCanBeLocal")
    private final String databaseURL = "https://blog-d6918-default-rtdb.europe-west1.firebasedatabase.app/";
    private static FirebaseUtils instance;

    public static FirebaseUtils getInstance() {
        if (instance == null) {
            instance = new FirebaseUtils();
        }
        return instance;
    }

    private FirebaseUtils() {
    }

    public FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void signOut() {
        if (getUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    public Task<AuthResult> signInAnonymously() {
        return FirebaseAuth.getInstance().signInAnonymously();
    }

    public com.google.android.gms.tasks.Task<AuthResult> signInWithEmailAndPassword(String email, String password) {
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password);
    }

    public com.google.android.gms.tasks.Task<AuthResult> createUserWithEmailAndPassword(String email, String password) {
        return FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password);
    }

    /**
     * Deletes anonymous signed-in user from Firebase authentication.
     * (important)
     */
    public void deleteAnonymousUser() {
        FirebaseUser user = getUser();
        if (user != null && user.isAnonymous()) {
            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete();
        }
    }

    public UserProfileChangeRequest updateUserProfile(String displayName, Uri photoUri) {
        return new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(photoUri)
                .build();
    }

    public void saveBlogPostToDatabase(BlogPostModel model) {
        //get the FirebaseDatabase instance for the specified URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseURL);

        //get the DatabaseReference for the database root node
        DatabaseReference blogRef = firebaseDatabase.getReference("posts").push();

        //write data to the database
        blogRef.child("title").setValue(model.getTitle());
        blogRef.child("details").setValue(model.getDetails());
        blogRef.child("imageUrl").setValue(model.getImageUrl());
        blogRef.child("userId").setValue(model.getUserId());
        blogRef.child("username").setValue(model.getUsername());
    }

    public StorageTask<UploadTask.TaskSnapshot> saveBlogPostImageToCloudStorage(
            Context context, Uri imageUri, String userId) {
        //create a controllable Task that has a synchronized state machine
        StorageTask<UploadTask.TaskSnapshot> storageTask;

        //get a reference to app cloud storage
        StorageReference cloudRef = FirebaseStorage.getInstance().getReference();

        //get a reference to the user image
        StorageReference imageRef = cloudRef.child("images").child(userId).child(imageUri.getLastPathSegment());

        //add user image to cloud storage
        storageTask = imageRef.putFile(imageUri).addOnFailureListener(e ->
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
        );
        return storageTask;
    }

    /**
     * Creates options to configure FirebaseRecyclerAdapter.
     *
     * @return options
     */
    public FirebaseRecyclerOptions<BlogPostModel> createFirebaseRecyclerOptions() {
        return new FirebaseRecyclerOptions.Builder<BlogPostModel>()
                .setQuery(getBlogPostsQuery(), BlogPostModel.class)
                .build();
    }

    /**
     * Returns a Firebase query for all blog posts.
     *
     * @return query
     */
    private Query getBlogPostsQuery() {
        return FirebaseDatabase.getInstance(databaseURL).getReference("posts");
    }

    /**
     * Returns a Firebase query for a blog post (search by title).
     *
     * @return query
     */
    public Query getBlogPostQueryByTitle(String title) {
        return FirebaseDatabase.getInstance(databaseURL).getReference("posts")
                .orderByChild("title").equalTo(title);
    }
}
