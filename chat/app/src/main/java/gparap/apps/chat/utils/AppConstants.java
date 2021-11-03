package gparap.apps.chat.utils;

public class AppConstants {
    public static final String TAG_CANNOT_CREATE_USER = "TAG_CANNOT_CREATE_USER";
    public static final String REGISTERED_USER_EMAIL = "registered_user_email";
    /*Note from Firebase: To get a reference to a database other than a us-central1 default database,
    you must pass the database URL to getInstance() (or Kotlin+KTX database()).
    For a us-central1 default database, you can call getInstance() (or database) without arguments.*/
    public static final String DATABASE_URL = "https://social-c3823-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String DATABASE_PATH_USERS = "users/";
}
