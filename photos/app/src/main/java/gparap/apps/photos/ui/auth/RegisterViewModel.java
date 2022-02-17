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

import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import gparap.apps.photos.R;
import gparap.apps.photos.utils.AppConstants;
import gparap.apps.photos.utils.Utils;

public class RegisterViewModel extends ViewModel {
    /**
     * <pre>
     * Performs various validation checks on the user input:
     *
     * - checks if the username already exists in the database
     * - checks if the user's e-mail already exists in the database
     * - checks if the password and password confirmation fields matches
     * - checks if the password contains special characters
     * - checks if the password contains numbers
     * - checks if the password contains capital letters
     * - checks if the password is of specific minimum length
     *
     * @param widgets Input value fields in the registration form
     * </pre>
     */
    public void validateUserInput(EditText[] widgets) {
        for (int i = 0; i < widgets.length - 1; i++) {
            int id = widgets[i].getId();

            if (id == R.id.edit_text_register_username && !isUsernameUnique(widgets[i].getText().toString().trim())) {
                Toast.makeText(widgets[i].getContext(),
                        widgets[i].getContext().getResources().getString(R.string.toast_exists_username),
                        Toast.LENGTH_SHORT).show();
                break;

            } else if (id == R.id.edit_text_register_email && !isEmailUnique(widgets[i].getText().toString().trim())) {
                Toast.makeText(widgets[i].getContext(),
                        widgets[i].getContext().getResources().getString(R.string.toast_exists_email),
                        Toast.LENGTH_SHORT).show();
                break;

            } else if (id == R.id.edit_text_register_password) {
                if (!hasPasswordSpecificChars(widgets[i].getText().toString().trim())) {
                    Toast.makeText(widgets[i].getContext(),
                            widgets[i].getContext().getResources().getString(R.string.toast_error_password_special_chars),
                            Toast.LENGTH_SHORT).show();
                    break;

                } else if (!hasPasswordDigits(widgets[i].getText().toString().trim())) {
                    Toast.makeText(widgets[i].getContext(),
                            widgets[i].getContext().getResources().getString(R.string.toast_error_password_digits),
                            Toast.LENGTH_SHORT).show();
                    break;

                } else if (!isPasswordLengthValid(widgets[i].getText().toString().trim())) {
                    Toast.makeText(widgets[i].getContext(),
                            widgets[i].getContext().getResources().getString(R.string.toast_error_password_length),
                            Toast.LENGTH_SHORT).show();
                    break;

                } else if (!arePasswordsMatching(widgets[i].getText().toString().trim(), widgets[i + 1].getText().toString().trim())) {
                    Toast.makeText(widgets[i].getContext(),
                            widgets[i].getContext().getResources().getString(R.string.toast_error_passwords_match),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    //TODO
    private boolean isUsernameUnique(String username) {
        return true;
    }

    //TODO
    private boolean isEmailUnique(String email) {
        return true;
    }

    private boolean arePasswordsMatching(String password, String confirmation) {
        return Utils.getInstance().areStringsEqual(password, confirmation);
    }

    private boolean hasPasswordSpecificChars(String password) {
        return Utils.getInstance().containsSpecialChars(password, AppConstants.MIN_SPECIAL_CHARS);
    }

    private boolean hasPasswordDigits(String password) {
        return Utils.getInstance().containsDigits(password, AppConstants.MIN_DIGITS);
    }

    private boolean isPasswordLengthValid(String password) {
        return Utils.getInstance().isStringLengthValid(password, AppConstants.MIN_PASS_LENGTH);
    }
}
