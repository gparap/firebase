/*
 * Copyright 2021 gparap
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
package gparap.apps.chat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import gparap.apps.chat.R;
import gparap.apps.chat.data.UserModel;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private Context context;
    private ArrayList<UserModel> users;

    public ArrayList<UserModel> getUsers() {
        return users;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUsers(ArrayList<UserModel> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardview_chat_list_user, parent, false);
        return new ChatListViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        //display user image if exists
        Glide.with(context).load(users.get(position).getProfileImageUrl())
                .error(context.getResources().getDrawable(R.drawable.ic_account_24))
                .placeholder(context.getResources().getDrawable(R.drawable.ic_account_24))
                .into(holder.userImage);

        //display user name
        holder.userName.setText(users.get(position).getDisplayName());
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.image_view_chat_list_user);
            userName = itemView.findViewById(R.id.image_view_chat_list_user_name);
        }
    }
}
