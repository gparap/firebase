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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;

import gparap.apps.chat.R;
import gparap.apps.chat.data.UserModel;
import gparap.apps.chat.utils.AppConstants;

public class RegisterActivityViewModel extends AndroidViewModel {
    private final WeakReference<Context> context;

    public RegisterActivityViewModel(@NonNull Application application) {
        super(application);
        this.context = new WeakReference<>(application.getApplicationContext());
    }

    public boolean validateRegistration(String displayName, String email, String password, String confirmPassword) {
        if (displayName.isEmpty()) {
            Toast.makeText(context.get(), context.get().getResources().getString(R.string.toast_empty_display_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(context.get(), context.get().getResources().getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(context.get(), context.get().getResources().getString(R.string.toast_empty_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confirmPassword.isEmpty()) {
            Toast.makeText(context.get(), context.get().getResources().getString(R.string.toast_empty_confirm_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(context.get(), context.get().getResources().getString(R.string.toast_unmatched_passwords), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public Task<AuthResult> createUserWithEmailAndPassword(String email, String password) {
        return FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password);
    }

    public UserModel getCurrentUser(String displayName, String email, String password) {
        UserModel user = new UserModel();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            user.setId(firebaseUser.getUid());
            user.setEmail(email);
            user.setPassword(password);
            user.setDisplayName(displayName);
            user.setProfileImageUrl("");
        }
        return user;
    }

    public Task<AuthResult> signInWithEmailAndPassword(String email, String password) {
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password);
    }

    public Task<Void> updateUserDisplayName(String displayName) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            return firebaseUser.updateProfile(
                    new UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()
            );
        }
        return null;
    }

    public void redirectToLogin(String registeredEmail) {
        Intent intent = new Intent(context.get(), LoginActivity.class);
        intent.putExtra(AppConstants.REGISTERED_USER_EMAIL, registeredEmail);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.get().startActivity(intent);
    }

    public void insertNewUserToTheDatabase(UserModel user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL);
        database.getReference(AppConstants.DATABASE_PATH_USERS + user.getId()).setValue(user);
    }
}
