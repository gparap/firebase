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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import gparap.apps.blog.model.BlogPostModel;
import gparap.apps.blog.model.BlogUserModel;

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

    /**
     * Returns the currently signed-in FirebaseUser.
     *
     * @return FirebaseUser
     */
    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
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

    public void saveBlogUserToDatabase(BlogUserModel model) {
        //get the FirebaseDatabase instance for the specified URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseURL);

        //get the DatabaseReference for the database root node
        DatabaseReference postsRef = firebaseDatabase.getReference().child("users");

        //get the DatabaseReference for an auto-generated node
        DatabaseReference postRef = postsRef.push();

        //write data to the database
        postRef.child("username").setValue(model.getUsername());
        postRef.child("email").setValue(model.getEmail());
        postRef.child("password").setValue(model.getPassword());
        postRef.child("imageUrl").setValue(model.getImageUrl());
        postRef.child("userId").setValue(model.getUserId());
    }

    public void saveBlogPostToDatabase(BlogPostModel model) {
        getUsernameQuery(model.getUserId()).addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //get all users from the database and find the user who is posting
                for (DataSnapshot task : snapshot.getChildren()) {
                    if (task.child("userId").getValue().equals(model.getUserId())) {
                        //update blog post object with username
                        model.setUsername(task.child("username").getValue().toString());

                        //get the FirebaseDatabase instance for the specified URL
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseURL);

                        //get the DatabaseReference for the database root node
                        DatabaseReference postsRef = firebaseDatabase.getReference().child("posts");

                        //get the DatabaseReference for an auto-generated node
                        DatabaseReference postRef = postsRef.push();

                        //write data to the database
                        postRef.child("title").setValue(model.getTitle());
                        postRef.child("details").setValue(model.getDetails());
                        postRef.child("image").setValue(model.getImageUrl());
                        postRef.child("userId").setValue(model.getUserId());
                        postRef.child("username").setValue(model.getUsername());

                        //user who is posting is found
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("DatabaseError", error.getDetails());
            }
        });
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
     * Returns a Firebase query for a username based on userId.
     *
     * @return query
     */
    public Query getUsernameQuery(@SuppressWarnings("unused") String userId) {
        return FirebaseDatabase.getInstance(databaseURL).getReference("users");
    }

    /**
     * Returns a Firebase query for blog posts.
     *
     * @return query
     */
    private Query getBlogPostsQuery() {
        return FirebaseDatabase.getInstance(databaseURL).getReference().child("posts");
    }
}
