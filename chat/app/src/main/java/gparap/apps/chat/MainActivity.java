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
package gparap.apps.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import gparap.apps.chat.adapters.ViewPagerAdapter;
import gparap.apps.chat.ui.auth.LoginActivity;
import gparap.apps.chat.ui.user_profile.UserProfileActivity;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup the options menu
        MaterialToolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                //open user profile
                case R.id.menu_item_user_profile:
                    startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                    break;

                //logout current user
                case R.id.menu_item_logout:
                    //sign-out current user
                    FirebaseAuth.getInstance().signOut();

                    //goto login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    break;
                default:
                    throw new IllegalStateException("DEBUG: this shouldn't have happened..");
            }
            return false;
        });

        //setup the ViewPager
        ViewPager viewPager = findViewById(R.id.view_pager_main);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), this));

        //setup the TabLayout with the ViewPager
        TabLayout tabLayout = findViewById(R.id.tabs_main);
        tabLayout.setupWithViewPager(viewPager);
    }
}