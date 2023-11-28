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

import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE_POSTS;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import gparap.apps.social_media.posts.AddPostActivity;

public class MainActivity extends AppCompatActivity {
    private PostAdapter postAdapter;

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
        postAdapter = new PostAdapter();
        recyclerViewPosts.setAdapter(postAdapter);

        //display all application posts (newest first)
        displayApplicationPosts(null);

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
        //search for posts
        if (item.getItemId() == R.id.main_menu_item_search) {
            //keep initial posts
            ArrayList<PostModel> initialPostsList = postAdapter.getPostsList();

            //search posts in all available fields (title, details, url)
            SearchView searchView = (SearchView) item.getActionView();
            assert searchView != null;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // TODO: > 1 keywords
                    //display all application posts (newest first) based on a search query
                    displayApplicationPosts(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //if user presses the "x" on the search, show all posts
                    if (newText.isEmpty()) {
                        postAdapter.setPostsList(initialPostsList);
                    }
                    return true;
                }
            });

            //search has ended, restore the initial posts list
            item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                    postAdapter.setPostsList(initialPostsList);
                    return true;
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays application posts from all users with or without a search query.
     * Posts are displayed in chronological order with the newest first.
     *
     * @param query search query keyword(s)
     */
    public void displayApplicationPosts(@Nullable String query) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child(DATABASE_REFERENCE_POSTS);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get all posts from database
                postAdapter.setPostsList(getApplicationPosts(snapshot, query));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Fetch users' posts from the database with or without a search query.
     *
     * @param snapshot instance containing data from a Firebase Database
     * @param query search query keyword(s)
     * @return ArrayList<PostModel>
     */
    private ArrayList<PostModel> getApplicationPosts(DataSnapshot snapshot, @Nullable String query) {
        ArrayList<PostModel> posts = new ArrayList<>();
        Iterable<DataSnapshot> children = snapshot.getChildren();
        for (DataSnapshot child : children) {
            PostModel post = child.getValue(PostModel.class);
            if (post != null){
                //get all posts
                if (query == null) {
                    posts.add(post);
                }
                //get posts based on search query
                else{
                    if (post.getTitle().contains(query) || post.getDetails().contains(query) || post.getImageUrl().contains(query)) {
                        posts.add(post);
                    }
                }
            }
        }
        return posts;
    }
}