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
package gparap.apps.wallpaper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

import gparap.apps.wallpaper.adapters.CategoryAdapter;
import gparap.apps.wallpaper.data.CategoryModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        //create a test list of categories
        ArrayList<CategoryModel> testList = new ArrayList<>();
        testList.add(new CategoryModel("1", "one", ""));
        testList.add(new CategoryModel("2", "two", ""));
        testList.add(new CategoryModel("3", "three", ""));
        testList.add(new CategoryModel("4", "four", ""));

        //create a recycler view adapter and add the categories
        CategoryAdapter categoryAdapter = new CategoryAdapter();
        categoryAdapter.setCategories(testList);

        //create a recycler view for categories and set its adapter
        RecyclerView categories = findViewById(R.id.recyclerViewCategories);
        categories.setAdapter(categoryAdapter);
        categories.setLayoutManager(new GridLayoutManager(this, 2));
    }
}