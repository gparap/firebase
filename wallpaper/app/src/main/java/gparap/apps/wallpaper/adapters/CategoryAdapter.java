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
package gparap.apps.wallpaper.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gparap.apps.wallpaper.R;
import gparap.apps.wallpaper.data.CategoryModel;
import gparap.apps.wallpaper.ui.WallpaperActivity;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private ArrayList<CategoryModel> categories = new ArrayList<>();

    public ArrayList<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<CategoryModel> categories) {
        this.categories = categories;
    }

    public void addCategory(CategoryModel category) {
        categories.add(category);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(
                R.layout.cardview_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        //show wallpaper category name
        holder.name.setText(categories.get(position).getName());

        //load wallpaper category image
        Picasso.get()
                .load(categories.get(position).getUrl())
                .fit()
                .into(holder.image);

        //click to enter category details activity
        holder.itemView.setOnClickListener(view->{
            Intent intent = new Intent(context, WallpaperActivity.class);
            intent.putExtra("category_id", categories.get(position).getId());
            intent.putExtra("category_name", categories.get(position).getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView image;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewCategory);
            image = itemView.findViewById(R.id.imageViewCategory);
        }
    }
}
