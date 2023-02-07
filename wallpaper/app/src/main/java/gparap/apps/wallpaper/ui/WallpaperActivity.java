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

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Iterator;
import java.util.Objects;

import gparap.apps.wallpaper.R;
import gparap.apps.wallpaper.adapters.WallpaperAdapter;
import gparap.apps.wallpaper.data.WallpaperModel;

public class WallpaperActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        context = this;

        //get category details from intent
        String categoryId = getIntent().getStringExtra("category_id");
        String categoryName = getIntent().getStringExtra("category_name");

        //update activity title
        if (categoryName != null && !categoryName.isEmpty()) {
            getSupportActionBar().setTitle(categoryName);
        }

        //create a recycler view adapter
        WallpaperAdapter wallpaperAdapter = new WallpaperAdapter();

        //get the wallpapers of the selected category from the database
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference wallpapersRef = database.collection("wallpaper_app")
                .document("wallpaper_document")
                .collection("wallpaper_collection");

        //Display the wallpapers of the selected category and add them to the adapter
        wallpapersRef.get().addOnCompleteListener(task -> {
            //iterate through the wallpapers
            Iterator<DocumentSnapshot> iterator = task.getResult().getDocuments().iterator();
            iterator.forEachRemaining(documentSnapshot -> {
                //find wallpapers of selected category and add them to the adapter
                if (Objects.equals(documentSnapshot.getString("category_id"), categoryId)) {
                    wallpaperAdapter.addWallpaper(new WallpaperModel(
                            categoryId,
                            documentSnapshot.getString("name"),
                            documentSnapshot.getString("url")
                    ));
                }
            });
            //create recycler view for wallpapers and set its adapter
            RecyclerView wallpapers = findViewById(R.id.recyclerViewWallpapers);
            wallpapers.setLayoutManager(new GridLayoutManager(WallpaperActivity.this, 2));
            wallpapers.setAdapter(wallpaperAdapter);
        });
    }
}