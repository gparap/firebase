package gparap.apps.photos;

import org.junit.Test;

import static org.junit.Assert.*;

import gparap.apps.photos.utils.AppConstants;
import gparap.apps.photos.utils.Utils;

public class UtilsUnitTest {
    @Test
    public void areStringsEqual_isWrong() {
        String str1 = "string1";
        String str2 = "string2";
        assertFalse(Utils.getInstance().areStringsEqual(str1, str2));
    }

    @Test
    public void areStringsEqual_isCorrect() {
        String str1 = "string";
        String str2 = "string";
        assertTrue(Utils.getInstance().areStringsEqual(str1, str2));
    }

    @Test
    public void stringContainsLessSpecialCharsThanRequested_isWrong() {
        String wrongString = "wrong";
        assertFalse("Contains no special characters",
                Utils.getInstance().containsSpecialChars(wrongString, AppConstants.MIN_SPECIAL_CHARS));

        wrongString = "wrong!";
        assertFalse("Contains less special characters than requested",
                Utils.getInstance().containsSpecialChars(wrongString, AppConstants.MIN_SPECIAL_CHARS));
    }

    @Test
    public void stringContainsRequestedNumberOfSpecialChars_isCorrect() {
        String correctString = "correct!!";
        assertTrue(Utils.getInstance().containsSpecialChars(correctString, AppConstants.MIN_SPECIAL_CHARS));

        correctString = "correct!@";
        assertTrue(Utils.getInstance().containsSpecialChars(correctString, AppConstants.MIN_SPECIAL_CHARS));
    }

    @Test
    public void stringContainsLessDigitsThanRequested_isWrong() {
        String wrongString = "wrong!";
        assertFalse("Contains no digits",
                Utils.getInstance().containsDigits(wrongString, AppConstants.MIN_DIGITS));

        wrongString = "wrong!0";
        assertFalse("Contains less digits than requested",
                Utils.getInstance().containsDigits(wrongString, AppConstants.MIN_DIGITS));
    }

    @Test
    public void stringContainsRequestedNumberOfDigits_isCorrect() {
        String correctString = "correct!!00";
        assertTrue(Utils.getInstance().containsSpecialChars(correctString, AppConstants.MIN_DIGITS));

        correctString = "correct!!01";
        assertTrue(Utils.getInstance().containsSpecialChars(correctString, AppConstants.MIN_DIGITS));
    }

    @Test
    public void stringLengthIsNotValid() {
        String wrongString = "wrong";
        assertFalse("String length is not valid",
                Utils.getInstance().isStringLengthValid(wrongString, AppConstants.MIN_PASS_LENGTH));
    }

    @Test
    public void stringLengthIsValid() {
        String correctString = "correct";
        assertTrue(Utils.getInstance().isStringLengthValid(correctString, AppConstants.MIN_PASS_LENGTH));
    }
}