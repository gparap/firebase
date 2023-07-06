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
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import gparap.apps.social_media.adapters.UserAdapter;
import gparap.apps.social_media.auth.LoginActivity;
import gparap.apps.social_media.data.UserModel;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<UserModel> applicationUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("social_media_app").child("users");
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
                UserAdapter adapter = new UserAdapter();
                adapter.setUserList(applicationUsers);
                recyclerViewUsers.setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //logout user and redirect to login
        if (item.getItemId() == R.id.main_menu_item_logout){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, getResources().getString(R.string.toast_logout_successful), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        //redirect to user profile
        if (item.getItemId() == R.id.main_menu_item_profile){
            startActivity(new Intent(this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}