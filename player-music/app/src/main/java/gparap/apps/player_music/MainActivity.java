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
package gparap.apps.player_music;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_AUDIO;

import static gparap.apps.player_music.utils.AppConstants.REQUEST_CODE_READ_EXTERNAL_STORAGE;
import static gparap.apps.player_music.utils.AppConstants.REQUEST_CODE_READ_MEDIA_AUDIO;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;

import java.io.File;
import java.util.ArrayList;

import gparap.apps.player_music.adapters.StorageFilesAdapter;
import gparap.apps.player_music.data.StorageFileModel;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<StorageFileModel> storageFiles = new ArrayList<>();
    private RecyclerView recyclerViewStorageFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializes the default FirebaseApp instance
        FirebaseApp.initializeApp(this);

        //setup recycler view with adapter for storage files
        recyclerViewStorageFiles = findViewById(R.id.recyclerViewStorageFiles);
        recyclerViewStorageFiles.setLayoutManager(new LinearLayoutManager(this));

        //TODO: Refactor code
        //determine whether the app have been granted the READ_EXTERNAL_STORAGE permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //get storage files from device (SDK >= 21 && <= 28)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    storageFiles.clear();
                    getStorageFiles(Build.VERSION_CODES.P);
                }

                //get storage files from device (SDK >= 29 && < 33)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    storageFiles.clear();
                    getStorageFiles(Build.VERSION_CODES.Q);
                }

            } else {
                //ask for READ_EXTERNAL_STORAGE permission (SDK >= 23 && <= 32)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                }
            }
        }

        //determine whether the app have been granted the READ_MEDIA_AUDIO permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                //get storage files from device
                storageFiles.clear();
                getStorageFiles(Build.VERSION_CODES.M);
            } else {
                //ask for READ_MEDIA_AUDIO permission
                requestPermissions(new String[]{READ_MEDIA_AUDIO}, REQUEST_CODE_READ_MEDIA_AUDIO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //get storage files from device (SDK >= 21 && <= 28)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                storageFiles.clear();
                getStorageFiles(Build.VERSION_CODES.P);
            }
            //get storage files from device (SDK >= 29 && < 33)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                storageFiles.clear();
                getStorageFiles(Build.VERSION_CODES.Q);
            }
        }

        if (requestCode == REQUEST_CODE_READ_MEDIA_AUDIO && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //get storage files from device (SDK >= 33)
            storageFiles.clear();
            getStorageFiles(Build.VERSION_CODES.TIRAMISU);
        }
    }

    //get storage files from the device folders "DOWNLOADS" & "MUSIC"   TODO: Refactor code
    private void getStorageFiles(int sdkVersionCode) {
        //(SDK >= 21 && <= 28)
        if (sdkVersionCode <= 28) {
            //get storage files from the device folder "DOWNLOADS"
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    //get the file name
                    String filename = file.getPath().substring(file.getPath().lastIndexOf('/') + 1);

                    //check if filename extension is of audio type (ie: mp3, ogg, etc.) and add to list
                    if (filename.contains("mp3") || filename.contains("ogg")) { //TODO: more
                        storageFiles.add(new StorageFileModel(-1L, "", filename));
                    }
                }
            }

            //get storage files from the device folder "MUSIC"
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    //get the file name
                    String filename = file.getPath().substring(file.getPath().lastIndexOf('/') + 1);

                    //check if filename extension is of audio type (ie: mp3, ogg, etc.) and add to list
                    if (filename.contains("mp3") || filename.contains("ogg")) { //TODO: more
                        storageFiles.add(new StorageFileModel(-1L, "", filename));
                    }
                }
            }
        }

        //(SDK >= 23 && < 33)
        if (sdkVersionCode >= 29) {
            //define the columns that will be returned for each row
            String[] projection = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME};

            //query against the table and return a Cursor object
            Cursor cursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null
            );

            //get audio files and add to list
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int index = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                    String filename = cursor.getString(index);
                    index = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    Long id = Long.valueOf(cursor.getString(index));
                    storageFiles.add(new StorageFileModel(id, "", filename));
                }
            }

            //free up the Cursor after use
            if (cursor != null) {
                cursor.close();
            }
        }

        //update recycler view with storage files
        StorageFilesAdapter adapter = new StorageFilesAdapter();
        adapter.storageFiles = storageFiles;
        recyclerViewStorageFiles.setAdapter(adapter);
    }
}