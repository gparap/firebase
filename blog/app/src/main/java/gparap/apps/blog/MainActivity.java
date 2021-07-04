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
package gparap.apps.blog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import gparap.apps.blog.adapter.BlogPostAdapter;
import gparap.apps.blog.auth.LoginActivity;
import gparap.apps.blog.ui.post.AddBlogPostActivity;
import gparap.apps.blog.ui.settings.UserSettingsActivity;
import gparap.apps.blog.utils.FirebaseUtils;

@SuppressWarnings({"Convert2Lambda", "FieldCanBeLocal"})
@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BlogPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add a new blog post
        FloatingActionButton fabAddPost = findViewById(R.id.fab_addPost);
        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddBlogPostActivity.class));
            }
        });

        //create and configure adapter for blog posts
        adapter = new BlogPostAdapter(FirebaseUtils.getInstance().createFirebaseRecyclerOptions());
        adapter.setContext(MainActivity.this);

        //setup recyclerView with adapter
        recyclerView = findViewById(R.id.recyclerViewBlogPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add_post:
                startActivity(new Intent(MainActivity.this, AddBlogPostActivity.class));
                break;
            case R.id.menu_item_user_settings:
                startActivity(new Intent(MainActivity.this, UserSettingsActivity.class));
                break;
            case R.id.menu_item_log_out:
                signOutUserAndReturnToLoginActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOutUserAndReturnToLoginActivity() {
        FirebaseUtils.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}