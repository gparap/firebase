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
package gparap.apps.photos.utils;

public final class Utils {
    private static Utils instance;

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    private Utils() {
    }

    public boolean areStringsEqual(String str1, String str2) {
        return str1.equals(str2);
    }

    /**
     * Checks if a string contains a predefined number of special characters ie. !@#$ etc.
     *
     * @param str    String under examination
     * @param length how many special characters are requested
     * @return boolean based on if the string contains equal or more special characters
     */
    public boolean containsSpecialChars(String str, int length) {
        int containedChars = 0;

        //get spacial characters and parameter as string arrays
        String[] specialChars = AppConstants.SPECIAL_CHARS.split("");
        String[] paramString = str.split("");

        //find out how many predefined special characters has the string
        for (String s : paramString) {
            if (containedChars == length) {
                break;
            }
            for (String specialChar : specialChars) {
                if (s.equals(specialChar)) {
                    containedChars += 1;
                }
            }
        }

        return (containedChars >= length);
    }

    /**
     * Checks if a string contains a predefined number of digits
     *
     * @param str    String under examination
     * @param length how many special characters are requested
     * @return boolean based on if the string contains equal or more special characters
     */
    public boolean containsDigits(String str, int length) {
        int containedDigits = 0;

        //get digits and parameter as string arrays
        String[] digits = AppConstants.NUMBERS.split("");
        String[] paramString = str.split("");

        //find out how many digits has the string
        for (String s : paramString) {
            if (containedDigits == length) {
                break;
            }
            for (String digit : digits) {
                if (s.equals(digit)) {
                    containedDigits += 1;
                }
            }
        }

        return (containedDigits >= length);
    }

    /**
     * Checks if a string length is valid
     *
     * @param str    String under examination
     * @param length requested string length
     * @return boolean based on if the string length is equal or bigger than the requested
     */
    public boolean isStringLengthValid(String str, int length) {
        return str.length() >= length;
    }
}
