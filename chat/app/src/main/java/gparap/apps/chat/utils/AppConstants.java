package gparap.apps.chat.utils;

public class AppConstants {
    public static final String TAG_CANNOT_CREATE_USER           = "TAG_CANNOT_CREATE_USER";
    public static final String TAG_PUT_FILE_TO_STORAGE          = "TAG_PUT_FILE_TO_STORAGE";
    public static final String PUT_FILE_TO_STORAGE_OK           = "PUT_FILE_TO_STORAGE_OK";
    public static final String TAG_CHANGE_DISPLAY_NAME          = "TAG_CHANGE_DISPLAY_NAME";
    public static final String TAG_CHANGE_DISPLAY_NAME_OK       = "TAG_CHANGE_DISPLAY_NAME_OK";
    public static final String TAG_CHANGE_DISPLAY_NAME_ERROR    = "TAG_CHANGE_DISPLAY_NAME_ERROR";
    public static final String TAG_CHANGE_EMAIL                 = "TAG_CHANGE_EMAIL";
    public static final String TAG_CHANGE_EMAIL_OK              = "TAG_CHANGE_EMAIL_OK";
    public static final String TAG_CHANGE_EMAIL_ERROR           = "TAG_CHANGE_EMAIL_ERROR";
    public static final String TAG_CHANGE_PASSWORD              = "TAG_CHANGE_PASSWORD";
    public static final String TAG_CHANGE_PASSWORD_OK           = "TAG_CHANGE_PASSWORD_OK";
    public static final String TAG_CHANGE_PASSWORD_ERROR        = "TAG_CHANGE_PASSWORD_ERROR";

    public static final String REGISTERED_USER_EMAIL = "registered_user_email";
    public static final String SIGNED_IN_USER = "signed_in_user";

    /*Note from Firebase: To get a reference to a database other than a us-central1 default database,
    you must pass the database URL to getInstance() (or Kotlin+KTX database()).
    For a us-central1 default database, you can call getInstance() (or database) without arguments.*/
    public static final String DATABASE_URL = "https://social-c3823-default-rtdb.europe-west1.firebasedatabase.app/";

    public static final String DATABASE_PATH_USERS                  = "users/";
    public static final String DATABASE_PATH_PRIVATE_MESSAGES       = "privateMessages/";
    public static final String DATABASE_STORAGE_LOCATION            = "images/";
    public static final String DATABASE_CHILD_PROFILE_IMAGE_URL     = "profileImageUrl";
    public static final String DATABASE_CHILD_USER_DISPLAY_NAME     = "displayName";
    public static final String DATABASE_CHILD_USER_EMAIL            = "email";
    public static final String DATABASE_CHILD_USER_PASSWORD         = "password";

    public static final int ACTION_PICK_REQUEST_CODE = 999;
}
