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
package gparap.apps.social_media.data;

/**
 * This is a model class for a user post.
 */
public class PostModel {
    private String id, userId, title, details, imageUrl, imageStorageId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    /** @noinspection unused*/
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageStorageId() {
        return imageStorageId;
    }

    public void setImageStorageId(String imageStorageId) {
        this.imageStorageId = imageStorageId;
    }

    /** @noinspection unused !!! required for Firebase*/
    private PostModel() {
    }

    private PostModel(Builder builder) {
        id = builder.id;
        userId = builder.userId;
        title = builder.title;
        details = builder.details;
        imageUrl = builder.imageUrl;
        imageStorageId = builder.imageStorageId;
    }

    /**
     * This is a builder class for the user post model.
     */
    public static class Builder {
        private String id, userId, title, details, imageUrl, imageStorageId;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDetails(String details) {
            this.details = details;
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder setImageStorageId(String imageStorageId) {
            this.imageStorageId = imageStorageId;
            return this;
        }

        public PostModel build() {
            return new PostModel(this);
        }
    }
}
