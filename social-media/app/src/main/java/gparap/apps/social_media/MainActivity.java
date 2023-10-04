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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gparap.apps.social_media.adapters.PostAdapter;
import gparap.apps.social_media.auth.LoginActivity;
import gparap.apps.social_media.data.PostModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        //check if user is authenticated, if not redirect to login page TODO: auto-login
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        //create a vertical LinearLayoutManager with reversed order
        LinearLayoutManager reversedLayoutManager = new LinearLayoutManager(this);
        reversedLayoutManager.setReverseLayout(true);
        reversedLayoutManager.setStackFromEnd(true);

        //setup post recycler view with adapter
        RecyclerView recyclerViewPosts = findViewById(R.id.recycler_view_posts);
        recyclerViewPosts.setLayoutManager(reversedLayoutManager);
        PostAdapter postAdapter = new PostAdapter();
        recyclerViewPosts.setAdapter(postAdapter);

        //display application posts (newest first) from all users
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("social_media_app").child("posts");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<PostModel> postsList = new ArrayList<>();

                //get all posts from database
                Iterable<DataSnapshot> children = snapshot.getChildren();
                for (DataSnapshot child : children) {
                    PostModel post = child.getValue(PostModel.class);
                    postsList.add(post);
                }
                postAdapter.setPostsList(postsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //redirect to the activity that adds a new post
        findViewById(R.id.fab_add_post_main).setOnClickListener(v ->
                startActivity(new Intent(this, AddPostActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //logout user and redirect to login
        if (item.getItemId() == R.id.main_menu_item_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, getResources().getString(R.string.toast_logout_successful), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        //redirect to user profile
        if (item.getItemId() == R.id.main_menu_item_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}