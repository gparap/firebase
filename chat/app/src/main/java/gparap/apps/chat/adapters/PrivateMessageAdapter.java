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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gparap.apps.chat.R;
import gparap.apps.chat.data.MessageModel;

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.PrivateMessageViewHolder> {
    private ArrayList<MessageModel> messages = new ArrayList<>();

    public ArrayList<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<MessageModel> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PrivateMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create the ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cardview_private_message, parent, false
        );
        return new PrivateMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivateMessageViewHolder holder, int position) {
        //set the message text to be displayed
        holder.message.setText(messages.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /* Describes the private message text and metadata about its place within the RecyclerView. */
    public static class PrivateMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public PrivateMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.text_view_private_message);
        }
    }
}
