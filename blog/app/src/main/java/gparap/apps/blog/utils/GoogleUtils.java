/*
 * Copyright 2021 gparap
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
package gparap.apps.blog.utils;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import gparap.apps.blog.R;

@SuppressWarnings({"FieldCanBeLocal", "deprecation"})
public class GoogleUtils {
    private static GoogleUtils instance;

    private final int REQUEST_CODE_GOOGLE_SIGN_IN = 999;

    public static GoogleUtils getInstance() {
        if (instance == null) {
            instance = new GoogleUtils();
        }
        return instance;
    }

    private GoogleUtils() {
    }

    public void signInWithGoogleAccount(AppCompatActivity activity) {
        //configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //create client for interacting with the Google Sign In API.
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions);

        //create Google Sign In intent
        Intent intentGoogleSignIn = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(intentGoogleSignIn, REQUEST_CODE_GOOGLE_SIGN_IN);
    }
}
