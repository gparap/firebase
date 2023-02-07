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
package gparap.apps.wallpaper.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Iterator;

import gparap.apps.wallpaper.R;
import gparap.apps.wallpaper.adapters.CategoryAdapter;
import gparap.apps.wallpaper.data.CategoryModel;

public class CategoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        FirebaseApp.initializeApp(this);

        //create a recycler view adapter
        CategoryAdapter categoryAdapter = new CategoryAdapter();

        //get the wallpaper categories from the database
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference categoriesRef = database.collection("wallpaper_app")
                .document("category_document")
                .collection("category_collection");

        //Display the wallpaper categories and add them to the category adapter
        categoriesRef.get().addOnCompleteListener(task -> {
            //iterate through the wallpaper categories and add them to the category adapter
            Iterator<DocumentSnapshot> iterator = task.getResult().getDocuments().iterator();
            iterator.forEachRemaining(documentSnapshot -> categoryAdapter.addCategory(
                    new CategoryModel(
                            documentSnapshot.getId(),
                            (String) documentSnapshot.get("name"),
                            (String) documentSnapshot.get("url")
                    )
            ));
            //create a recycler view for categories and set its adapter
            RecyclerView categories = findViewById(R.id.recyclerViewCategories);
            categories.setAdapter(categoryAdapter);
            categories.setLayoutManager(new GridLayoutManager(this, 2));
        });
    }
}