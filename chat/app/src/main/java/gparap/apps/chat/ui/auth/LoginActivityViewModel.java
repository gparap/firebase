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
package gparap.apps.chat.ui.auth;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.WeakReference;

import gparap.apps.chat.MainActivity;
import gparap.apps.chat.R;
import gparap.apps.chat.data.UserModel;

public class LoginActivityViewModel extends AndroidViewModel {
    private final WeakReference<Context> context;

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
        context = new WeakReference<>(application.getApplicationContext());
    }

    public boolean validateUserInput(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(context.get(), context.get().getResources().getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(context.get(), context.get().getResources().getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public Task<AuthResult> signInWithEmailAndPassword(String email, String password) {
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password);
    }

    public UserModel getSignedInUser(String email, String password) {
        UserModel user = new UserModel();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            user.setId(firebaseUser.getUid());
            user.setEmail(email);
            user.setPassword(password);
            user.setDisplayName(firebaseUser.getDisplayName());
        }
        return user;
    }

    public void greetSignedInUser(UserModel user) {
        String displayName = user.getDisplayName() != null ? user.getDisplayName() : "";
        Toast.makeText(context.get(),
                context.get().getResources().getString(R.string.text_greet_user) + " " + displayName,
                Toast.LENGTH_SHORT).show();
    }

    public void redirectToChat(UserModel user) {
        Intent intent = new Intent(context.get(), MainActivity.class);
        intent.putExtra("current_user", user);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.get().startActivity(intent);
    }
}
