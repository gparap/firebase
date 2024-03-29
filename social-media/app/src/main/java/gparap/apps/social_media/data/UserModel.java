/*
 * Copyright 2024 gparap
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
package gparap.apps.social_media.data;

import com.google.firebase.database.PropertyName;

/** @noinspection unused*/
public class UserModel {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String imageUrl;
    private int postsCount;
    private String aboutMe;     //TODO: update profile with user's about section
    private String memberSince; //TODO: use Date object

    @SuppressWarnings("unused")
    //!!! Do not remove, needed for
    //  com.google.firebase.database.DatabaseException: does not define a no-argument constructor
    public UserModel(){
    }
    public UserModel(String id, String username, String email, String phone, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        imageUrl = "";
        postsCount = 0;
        aboutMe = "";
        memberSince = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @PropertyName("posts")
    public int getPostsCount() {
        return postsCount;
    }

    @PropertyName("posts")
    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    @PropertyName("about")
    public String getAboutMe() {
        return aboutMe;
    }

    @PropertyName("about")
    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    @PropertyName("member_since")
    public String getMemberSince() {
        return memberSince;
    }

    @PropertyName("member_since")
    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }
}
