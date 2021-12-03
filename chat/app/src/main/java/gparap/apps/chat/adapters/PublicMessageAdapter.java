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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import gparap.apps.chat.R;
import gparap.apps.chat.data.MessageModel;

public class PublicMessageAdapter extends RecyclerView.Adapter<PublicMessageAdapter.PublicMessageViewHolder> {
    private Context context;
    private ArrayList<MessageModel> messages = new ArrayList<>();

    public ArrayList<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<MessageModel> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public PublicMessageAdapter.PublicMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        //create ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_public_message, parent, false);
        return new PublicMessageViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull PublicMessageAdapter.PublicMessageViewHolder holder, int position) {
        //set message text
        holder.message.setText(messages.get(position).getMessage());

        //set message sender image
        Glide.with(context).load(messages.get(position).getSender())
                .error(context.getResources().getDrawable(R.drawable.ic_account_24))
                .placeholder(context.getResources().getDrawable(R.drawable.ic_account_24))
                .into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    /* Describes the public message text and metadata about its place within the RecyclerView. */
    public static class PublicMessageViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView userImage;
        public TextView message;

        public PublicMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_public_chat_user_image);
            userImage = itemView.findViewById(R.id.image_view_public_chat_user);
            message = itemView.findViewById(R.id.text_view_public_message);
        }
    }
}
