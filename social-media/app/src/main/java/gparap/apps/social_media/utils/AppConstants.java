/*
 * Copyright 2023 gparap
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
package gparap.apps.social_media.utils;

public class AppConstants {
    public static final String DATABASE_REFERENCE                   = "social_media_app";
    public static final String DATABASE_REFERENCE_USERS             = "users";
    public static final String DATABASE_REFERENCE_POSTS             = "posts";
    public static final String DATABASE_FIELD_POST_ID               = "id";
    public static final String DATABASE_FIELD_POST_USER_ID          = "userId";
    public static final String DATABASE_FIELD_POST_TITLE            = "title";
    public static final String DATABASE_FIELD_POST_DETAILS          = "details";
    public static final String DATABASE_FIELD_POST_IMAGE_URL        = "imageUrl";
    public static final String DATABASE_FIELD_POST_IMAGE_STORAGE_ID = "imageStorageId";
    public static final String DATABASE_FIELD_POSTS_COUNT           = "posts";

    public static final String INTENT_EXTRA_EMAIL                   = "email";
    public static final String APP_BAR_TITLE_ADD_POST               = "Add Post";
    public static final String MIME_TYPE_IMAGE                      = "image/*";

    public static final int REQUEST_CODE_GET_POST_IMAGE             = 999;
    public static final int REQUEST_CODE_CAPTURE_POST_IMAGE         = 888;
    public static final int REQUEST_CODE_CAMERA_PERMISSION          = 777;

    public static final String PERMISSION_CAMERA                    = "Manifest.permission.CAMERA";
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE    = "Manifest.permission.WRITE_EXTERNAL_STORAGE";

    public static final String CONTENT_VALUE_TITLE                  = "title";
    public static final String CONTENT_VALUE_DESCRIPTION            = "desc";
    public static final String CONTENT_VALUE_AUTHOR                 = "author";

    public static final String POST_ID                              = "post_id";
    public static final String POST_USER_ID                         = "post_user_id";
    public static final String POST_USER_NAME                       = "post_user_name";
    public static final String POST_TITLE                           = "post_title";
    public static final String POST_DETAILS                         = "post_details";
    public static final String POST_IMAGE_URL                       = "post_image_url";
    public static final String POST_IMAGE_STORAGE_ID                = "post_image_storage_id";

    public static final String USER_ID                              = "user_id";
    public static final String USER_NAME                            = "user_name";
    public static final String USER_EMAIL                           = "user_email";
    public static final String USER_PHONE                           = "user_phone";
    public static final String USER_PASSWORD                        = "user_password";
    public static final String USER_IMAGE_URL                       = "user_image_url";
    public static final String USER_POSTS                           = "user_posts";
    public static final String USER_ABOUT_ME                        = "user_about_me";
    public static final String USER_MEMBER_SINCE                    = "user_member_since";
}
