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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gparap.apps.wallpaper.R;
import gparap.apps.wallpaper.data.WallpaperModel;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {
    private Context context;
    private ArrayList<WallpaperModel> wallpapers = new ArrayList<>();

    public ArrayList<WallpaperModel> getWallpapers() {
        return wallpapers;
    }

    public void setWallpapers(ArrayList<WallpaperModel> wallpapers) {
        this.wallpapers = wallpapers;
    }

    public void addWallpaper(WallpaperModel wallpaper) {
        wallpapers.add(wallpaper);
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new WallpaperViewHolder(LayoutInflater.from(context).inflate(
                R.layout.cardview_wallpaper, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        //load wallpaper image
        Picasso.get()
                .load(wallpapers.get(position).getUrl())
                .fit()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

    public static class WallpaperViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public WallpaperViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewWallpaper);
        }
    }
}
