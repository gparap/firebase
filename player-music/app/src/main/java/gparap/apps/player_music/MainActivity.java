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

import static gparap.apps.player_music.utils.AppConstants.REQUEST_CODE_READ_EXTERNAL_STORAGE;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializes the default FirebaseApp instance
        FirebaseApp.initializeApp(this);

        //determine whether the app have been granted the READ_EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //TODO(SDK >= 23 && < 33))

        } else {
            //ask for READ_EXTERNAL_STORAGE permission (SDK >= 23 && < 33)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            }

            //TODO: MANAGE_EXTERNAL_STORAGE (SDK >= 33)
            //do not ask for READ_EXTERNAL_STORAGE permission (SDK >= 33)
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                //TODO(SDK >= 33)
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            //TODO (SDK >= 21)
        }
    }
}