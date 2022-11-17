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

import android.content.ContentUris;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import gparap.apps.player_music.R;
import gparap.apps.player_music.data.StorageFileModel;

public class StorageFilesAdapter extends RecyclerView.Adapter<StorageFilesAdapter.StorageFilesViewHolder> {
    public ArrayList<StorageFileModel> storageFiles = new ArrayList<>();
    private Context context;
    private MediaPlayer mediaPlayer;

    @NonNull
    @Override
    public StorageFilesAdapter.StorageFilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //get target context
        context = parent.getContext();
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

        //TODO: refactor code & check files
        //play the audio file
        holder.playButton.setOnClickListener(view -> {
            //get the URI of the storage file inside te device (SDK >= 29)
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, storageFiles.get(position).getId()
                );
            }

            //create a MediaPlayer object and set its attributes
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            );
            try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mediaPlayer.setDataSource(context, uri);
                }else{
                    mediaPlayer.setDataSource(storageFiles.get(position).getFilepath());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setScreenOnWhilePlaying(true);

            //play the storage file
            try {
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });



        //stop the audio file and release resources
        holder.stopButton.setOnClickListener(view->{
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        });
    }

    @Override
    public int getItemCount() {
        return storageFiles.size();
    }

    public static class StorageFilesViewHolder extends RecyclerView.ViewHolder {
        public TextView storageFilename;
        public ImageButton playButton;
        public ImageButton stopButton;

        public StorageFilesViewHolder(@NonNull View itemView) {
            super(itemView);
            storageFilename = itemView.findViewById(R.id.textViewStorageFilename);
            playButton = itemView.findViewById(R.id.imageButtonPlayStorageFile);
            stopButton = itemView.findViewById(R.id.imageButtonStopStorageFile);
        }
    }
}
