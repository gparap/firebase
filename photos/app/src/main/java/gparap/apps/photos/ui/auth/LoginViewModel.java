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
package gparap.apps.photos.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginViewModel extends ViewModel {
    private final LoginRepository repository = new LoginRepository();

    /**
     * Signs-in user to database with e-mail and password credentials.
     *
     * @param userEmail    user's email
     * @param userPassword user's password
     */
    public Task<AuthResult> signInUser(@NonNull String userEmail, @NonNull String userPassword) {
        return repository.signInWithEmailAndPassword(userEmail, userPassword);
    }
}
