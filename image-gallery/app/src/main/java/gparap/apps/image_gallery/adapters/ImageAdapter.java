/*
 * Copyright (c) 2022 gparap
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
package gparap.apps.image_gallery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gparap.apps.image_gallery.R;
import gparap.apps.image_gallery.data.ImageModel;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private ArrayList<ImageModel> images = new ArrayList<>();

    public void setImages(ArrayList<ImageModel> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate cardview from XML resources
        View view = LayoutInflater.from(parent.getContext()).inflate(
                parent.getContext().getResources().getLayout(R.layout.cardview_image), parent, false
        );

        //return a new ImageViewHolder with the previously created view
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.name.setText(images.get(position).getName());
        holder.image.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        private final TextView name;
        private final ImageView image;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            //describe items inside the recyclerView
            name = itemView.findViewById(R.id.textViewImageName);
            image = itemView.findViewById(R.id.cardViewImage);
        }
    }
}
