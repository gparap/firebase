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

/** Describes a user's "like" interaction with a specific post. */
public class UserPostDislikeModel {
    private String id;
    private String userId;
    private String postId;
    private Long timestamp; //for sorting

    @SuppressWarnings("unused")
    //!!! Do not remove, no-argument constructor is needed for firebase
    public UserPostDislikeModel(){
    }

    public UserPostDislikeModel(String id, String userId, String postId) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        //TODO: timestamp upon creation
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
