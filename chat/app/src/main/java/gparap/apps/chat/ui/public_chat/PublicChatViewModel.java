package gparap.apps.chat.ui.public_chat;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gparap.apps.chat.adapters.PublicMessageAdapter;
import gparap.apps.chat.data.MessageModel;
import gparap.apps.chat.utils.AppConstants;

public class PublicChatViewModel extends ViewModel {
    private final PublicMessageAdapter messageAdapter = new PublicMessageAdapter();

    /* Stores a user's public message on the database */
    public void sendPublicMessage(MessageModel message) {
        FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                .getReference(AppConstants.DATABASE_PATH_PUBLIC_MESSAGES)
                .push().setValue(message);
    }

    /* Retrieves public messages from the database */
    public void displayPublicMessages(RecyclerView recyclerViewMessages) {
        //get public messages database reference
        DatabaseReference msgRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                .getReference(AppConstants.DATABASE_PATH_PUBLIC_MESSAGES);

        //get public messages
        msgRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageAdapter.getMessages().clear();
                for (DataSnapshot message : snapshot.getChildren()) {
                    messageAdapter.getMessages().add(message.getValue(MessageModel.class));
                    messageAdapter.notifyDataSetChanged();
                }
                //display public messages
                recyclerViewMessages.setAdapter(messageAdapter);
                recyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}