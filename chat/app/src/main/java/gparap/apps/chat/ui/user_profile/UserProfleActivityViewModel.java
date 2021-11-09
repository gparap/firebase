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
package gparap.apps.chat.ui.user_profile;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import gparap.apps.chat.data.UserModel;
import gparap.apps.chat.utils.AppConstants;

public class UserProfleActivityViewModel extends AndroidViewModel {
    private final WeakReference<Context> context;

    public String getDisplayName() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            return firebaseUser.getDisplayName();
        }
        return "";
    }

    public String getEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            return firebaseUser.getEmail();
        }
        return "";
    }

    public UserProfleActivityViewModel(@NonNull Application application) {
        super(application);
        context = new WeakReference<>(application.getApplicationContext());
    }

    public Bitmap getUserProfileImageBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.get().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void updateUserDisplayName(String displayName, boolean isProfileImageChanged, View progressUpdate) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {

            //create a request to update user profile information
            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
            builder.setDisplayName(displayName);
            UserProfileChangeRequest userProfileChangeRequest = builder.build();

            //update user's display name in users database reference and in profile information
            DatabaseReference userRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                    .getReference(AppConstants.DATABASE_PATH_USERS + firebaseUser.getUid());
            Task<Void> task = userRef.child(AppConstants.DATABASE_CHILD_USER_DISPLAY_NAME).setValue(displayName);
            task.addOnCompleteListener(view -> {
                if (task.isSuccessful()) {
                    firebaseUser.updateProfile(userProfileChangeRequest);
                    Log.d(AppConstants.TAG_CHANGE_DISPLAY_NAME, AppConstants.TAG_CHANGE_DISPLAY_NAME_OK);

                } else {
                    Log.d(AppConstants.TAG_CHANGE_DISPLAY_NAME, AppConstants.TAG_CHANGE_DISPLAY_NAME_ERROR);
                }

                if (!isProfileImageChanged) {
                    progressUpdate.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void updateUserEmail(String email, UserModel user, boolean isProfileImageChanged, View progressUpdate) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.updateEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //update user's email in users database reference
                    DatabaseReference userRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                            .getReference(AppConstants.DATABASE_PATH_USERS + firebaseUser.getUid());
                    userRef.child(AppConstants.DATABASE_CHILD_USER_EMAIL).setValue(email).addOnCompleteListener(taskEmail -> {
                        if (taskEmail.isSuccessful()) {
                            Log.d(AppConstants.TAG_CHANGE_EMAIL, AppConstants.TAG_CHANGE_EMAIL_OK);

                            //hold the new email value
                            user.setEmail(email);

                        } else {
                            Log.d(AppConstants.TAG_CHANGE_EMAIL, AppConstants.TAG_CHANGE_EMAIL_ERROR);

                            //!!! we must revert the change and restore the old user email
                            firebaseUser.updateEmail(user.getEmail());
                        }
                    });

                } else {
                    Log.d(AppConstants.TAG_CHANGE_EMAIL, AppConstants.TAG_CHANGE_EMAIL_ERROR);
                }

                if (!isProfileImageChanged) {
                    progressUpdate.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void updateUserPassword(String password, UserModel user, boolean isProfileImageChanged, View progressUpdate) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.updatePassword(password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //get the users database reference
                    DatabaseReference userRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                            .getReference(AppConstants.DATABASE_PATH_USERS + firebaseUser.getUid());

                    //!!!   hold the value of the old password in case something goes terribly wrong
                    userRef.addValueEventListener(getValueEventListener(user));

                    //update user's password in users database reference
                    userRef.child(AppConstants.DATABASE_CHILD_USER_PASSWORD).setValue(password).addOnCompleteListener(taskPassword -> {
                        if (taskPassword.isSuccessful()) {
                            Log.d(AppConstants.TAG_CHANGE_PASSWORD, AppConstants.TAG_CHANGE_PASSWORD_OK);

                            //hold the new password value
                            user.setPassword(password);

                        } else {
                            Log.d(AppConstants.TAG_CHANGE_PASSWORD, AppConstants.TAG_CHANGE_PASSWORD_ERROR);

                            //!!! we must revert the change and restore the old user password
                            firebaseUser.updatePassword(user.getEmail());
                        }
                    });

                } else {
                    Log.d(AppConstants.TAG_CHANGE_PASSWORD, AppConstants.TAG_CHANGE_PASSWORD_ERROR);
                }

                if (!isProfileImageChanged) {
                    progressUpdate.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @NonNull
    private ValueEventListener getValueEventListener(UserModel user) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user.setPassword(Objects.requireNonNull(snapshot.getValue(UserModel.class)).getPassword());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public boolean updateUserProfileImage(Uri profileImageUri, View progressUpdate) {
        AtomicBoolean isProfileImageChanged = new AtomicBoolean(false);
        FirebaseStorage.getInstance().getReference(
                AppConstants.DATABASE_STORAGE_LOCATION + UUID.randomUUID().toString())
                .putFile(profileImageUri).addOnCompleteListener(taskUpload -> {
            if (taskUpload.isSuccessful()) {
                Log.d(AppConstants.TAG_PUT_FILE_TO_STORAGE, AppConstants.PUT_FILE_TO_STORAGE_OK);

                //update user profile image url in database
                if (taskUpload.getResult() != null) {
                    taskUpload.getResult().getStorage().getDownloadUrl().addOnCompleteListener(taskUrl -> {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            //get "users" reference
                            DatabaseReference userRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                                    .getReference(AppConstants.DATABASE_PATH_USERS + firebaseUser.getUid());

                            //update "profileImageUrl" child
                            if (taskUrl.getResult() != null) {
                                userRef.child(AppConstants.DATABASE_CHILD_PROFILE_IMAGE_URL).setValue((taskUrl.getResult()).toString());
                            }
                        }
                    });
                    isProfileImageChanged.set(true);
                }

            } else {
                if (taskUpload.getException() != null) {
                    Log.d(AppConstants.TAG_PUT_FILE_TO_STORAGE, taskUpload.getException().getLocalizedMessage());
                }
            }
            progressUpdate.setVisibility(View.INVISIBLE);
            isProfileImageChanged.set(true);
        });
        return isProfileImageChanged.get();
    }
}
