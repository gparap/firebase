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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import gparap.apps.chat.R;
import gparap.apps.chat.data.MessageModel;
import gparap.apps.chat.data.UserModel;

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.PrivateMessageViewHolder> {
    private Context context;
    private ArrayList<MessageModel> messages = new ArrayList<>();
    private UserModel messageReceiver;

    public ArrayList<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<MessageModel> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public UserModel getMessageReceiver() {
        return messageReceiver;
    }

    public void setMessageReceiver(UserModel messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    @NonNull
    @Override
    public PrivateMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        //create the ViewHolder
        View view = LayoutInflater.from(context).inflate(
                R.layout.cardview_private_message, parent, false
        );
        return new PrivateMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivateMessageViewHolder holder, int position) {
        //get the id of the signed-in user (the sender)
        String displayName = "";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            displayName = firebaseUser.getDisplayName();
        }

        //set the message text to be displayed
        assert displayName != null;
        if (displayName.equals(messages.get(position).getSender())) {
            holder.messageSender.setVisibility(View.VISIBLE);
            holder.messageReceiver.setVisibility(View.INVISIBLE);
            holder.messageSender.setText(messages.get(position).getMessage());

            //do not display the image of the sender
            holder.cardView.setVisibility(View.INVISIBLE);
            holder.userImage.setVisibility(View.INVISIBLE);

        } else {
            holder.messageSender.setVisibility(View.INVISIBLE);
            holder.messageReceiver.setVisibility(View.VISIBLE);
            holder.messageReceiver.setText(messages.get(position).getMessage());

            //display the image placeholder of the receiver
            holder.cardView.setVisibility(View.VISIBLE);
            holder.userImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(messageReceiver.getProfileImageUrl()).into(holder.userImage);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /* Describes the private message text and metadata about its place within the RecyclerView. */
    public static class PrivateMessageViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView userImage;
        public TextView messageReceiver;
        public TextView messageSender;

        public PrivateMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_private_chat_user_image);
            userImage = itemView.findViewById(R.id.image_view_private_chat_user);
            messageReceiver = itemView.findViewById(R.id.text_view_private_message_receiver);
            messageSender = itemView.findViewById(R.id.text_view_private_message_sender);
        }
    }
}
