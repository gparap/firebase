/*
 * Copyright 2022 gparap
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
package gparap.apps.player_music.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gparap.apps.player_music.R;
import gparap.apps.player_music.data.StorageFileModel;

public class StorageFilesAdapter extends RecyclerView.Adapter<StorageFilesAdapter.StorageFilesViewHolder> {
    public ArrayList<StorageFileModel> storageFiles = new ArrayList<>();

    @NonNull
    @Override
    public StorageFilesAdapter.StorageFilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate view
        View storageFileView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cardview_storage_file, parent, false
        );
        //create view
        return new StorageFilesViewHolder(storageFileView);
    }

    @Override
    public void onBindViewHolder(@NonNull StorageFilesAdapter.StorageFilesViewHolder holder, int position) {
        holder.storageFilename.setText(storageFiles.get(position).getFilename());
    }

    @Override
    public int getItemCount() {
        return storageFiles.size();
    }

    public static class StorageFilesViewHolder extends RecyclerView.ViewHolder {
        public TextView storageFilename;

        public StorageFilesViewHolder(@NonNull View itemView) {
            super(itemView);
            storageFilename = itemView.findViewById(R.id.textViewStorageFilename);
        }
    }
}
