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
package gparap.apps.social_media.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gparap.apps.social_media.R;
import gparap.apps.social_media.data.UserModel;
import gparap.apps.social_media.users.UserDetailsActivity;
import gparap.apps.social_media.utils.AppConstants;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<UserModel> userList = new ArrayList<>();
    private Context context;

    /**
     * @noinspection unused
     */
    public ArrayList<UserModel> getUserList() {
        return userList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUserList(ArrayList<UserModel> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //get target context
        context = parent.getContext();

        //create & return the item view
        View view = LayoutInflater.from(parent.getContext()).inflate(parent.getContext()
                .getResources().getLayout(R.layout.cardview_user), parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        //get user image resource identifier
        Uri uri = Uri.parse(userList.get(position).getImageUrl());

        //display user
        Picasso.get().load(uri).into(holder.userImage);
        holder.userName.setText(userList.get(position).getUsername());

        //display user details in new activity
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserDetailsActivity.class);
            intent.putExtra(AppConstants.USER_ID, userList.get(position).getId());
            intent.putExtra(AppConstants.USER_NAME, userList.get(position).getUsername());
            intent.putExtra(AppConstants.USER_EMAIL, userList.get(position).getEmail());
            intent.putExtra(AppConstants.USER_PHONE, userList.get(position).getPhone());
            intent.putExtra(AppConstants.USER_PASSWORD, userList.get(position).getPassword());
            intent.putExtra(AppConstants.USER_IMAGE_URL, userList.get(position).getImageUrl());
            intent.putExtra(AppConstants.USER_POSTS, userList.get(position).getPostsCount());
            intent.putExtra(AppConstants.USER_ABOUT_ME, userList.get(position).getAboutMe());
            intent.putExtra(AppConstants.USER_MEMBER_SINCE, userList.get(position).getMemberSince());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userImage;
        private final TextView userName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.image_view_app_user);
            userName = itemView.findViewById(R.id.text_view_app_user);
        }
    }
}
