package gparap.apps.chat.ui.public_chat;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.FirebaseDatabase;

import gparap.apps.chat.data.MessageModel;
import gparap.apps.chat.utils.AppConstants;

public class PublicChatViewModel extends ViewModel {

    /* Stores a user's public message on the database */
    public void sendPublicMessage(MessageModel message) {
        FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                .getReference(AppConstants.DATABASE_PATH_PUBLIC_MESSAGES)
                .push().setValue(message);
    }
}