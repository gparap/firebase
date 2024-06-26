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

import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE_USERS;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gparap.apps.social_media.ProfileActivity;
import gparap.apps.social_media.R;
import gparap.apps.social_media.adapters.UserAdapter;
import gparap.apps.social_media.auth.LoginActivity;
import gparap.apps.social_media.data.UserModel;

public class UserActivity extends AppCompatActivity {
    private final ArrayList<UserModel> applicationUsers = new ArrayList<>();
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        FirebaseApp.initializeApp(this);

        //check if user is authenticated. If not, redirect to login page
        new Handler(getMainLooper()).post(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(this, LoginActivity.class));
                this.finish();
            }
        });

        //get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //get application users from the database (except current)
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(DATABASE_REFERENCE).child(DATABASE_REFERENCE_USERS);
        Task<DataSnapshot> snapshotTask = usersRef.get();
        snapshotTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Iterable<DataSnapshot> snapshotUsers = task.getResult().getChildren();
                for (DataSnapshot snapshotUser: snapshotUsers) {
                    UserModel dbUser = snapshotUser.getValue(UserModel.class);
                    if (dbUser != null && currentUser != null){
                        if (!dbUser.getId().equals(currentUser.getUid())){
                            applicationUsers.add(dbUser);
                        }
                    }
                }

                //setup recycler view with adapter for application users
                RecyclerView recyclerViewUsers = findViewById(R.id.recycler_view_users);
                recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
                userAdapter = new UserAdapter();
                userAdapter.setUserList(applicationUsers);
                recyclerViewUsers.setAdapter(userAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //logout user and redirect to login
        if (item.getItemId() == R.id.user_menu_item_logout){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, getResources().getString(R.string.toast_logout_successful), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        //redirect to user profile
        if (item.getItemId() == R.id.user_menu_item_profile){
            startActivity(new Intent(this, ProfileActivity.class));
        }
        //search for users
        if (item.getItemId() == R.id.user_menu_item_search_users) {
            //keep initial users
            ArrayList<UserModel> initialUsersList = userAdapter.getUserList();

            //search users in all relevant fields (e-mail, username)
            SearchView searchView = (SearchView) item.getActionView();
            assert searchView != null;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // TODO: > 1 keywords
                    //display all application users (newest first) based on a search query
                    displayApplicationUsers(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //if user presses the "x" on the search, show all users
                    if (newText.isEmpty()) {
                        userAdapter.setUserList(initialUsersList);
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
                    userAdapter.setUserList(initialUsersList);
                    return true;
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays application users with or without a search query.
     * Users are displayed in chronological order with the newest first.
     *
     * @param query search query keyword(s)
     */
    public void displayApplicationUsers(@Nullable String query) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child(DATABASE_REFERENCE_USERS);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get all users from database
                userAdapter.setUserList(getApplicationPosts(snapshot, query));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Fetch users from the database with or without a search query.
     *
     * @param snapshot instance containing data from a Firebase Database
     * @param query search query keyword(s)
     * @return ArrayList<UserModel>
     */
    private ArrayList<UserModel> getApplicationPosts(DataSnapshot snapshot, @Nullable String query) {
        ArrayList<UserModel> users = new ArrayList<>();
        Iterable<DataSnapshot> children = snapshot.getChildren();
        for (DataSnapshot child : children) {
            UserModel user = child.getValue(UserModel.class);
            if (user != null){
                //get all users
                if (query == null) {
                    users.add(user);
                }
                //get users based on search query
                else{
                    if (user.getEmail().contains(query) || user.getUsername().contains(query)) {
                        users.add(user);
                    }
                }
            }
        }
        return users;
    }
}