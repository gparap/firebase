package gparap.apps.chat.ui.private_chat;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import gparap.apps.chat.adapters.ChatListAdapter;
import gparap.apps.chat.adapters.PrivateMessageAdapter;
import gparap.apps.chat.data.MessageModel;
import gparap.apps.chat.data.UserModel;
import gparap.apps.chat.utils.AppConstants;

public class PrivateChatViewModel extends ViewModel {
    private String signedInUserId = "";
    private ArrayList<MessageModel> messages = new ArrayList<>();
    private final PrivateMessageAdapter messageAdapter = new PrivateMessageAdapter();
    private RecyclerView recyclerViewMessages;

    /* Retrieves the possible-to-chat users from database */
    public void getChatList(ChatListAdapter chatListAdapter, ProgressBar progressLoad) {
        ArrayList<UserModel> users = new ArrayList<>();

        //get the id of the signed-in user
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            signedInUserId = firebaseUser.getUid();
        }

        //get the users database reference
        DatabaseReference usersRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                .getReference(AppConstants.DATABASE_PATH_USERS);

        //get all users from the database (except for the signed-in one)
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user != null && !user.getId().equals(signedInUserId)) {
                        users.add(user);
                    }
                }
                chatListAdapter.setUsers(users);
                progressLoad.setVisibility(View.INVISIBLE);
            }
        });
    }

    /* Gets the private chat messages for a specific chatting pair */
    public void displayPrivateMessages(RecyclerView recyclerViewMessages, UserModel signedInUser, UserModel selectedUser) {
        this.recyclerViewMessages = recyclerViewMessages;

        //get database reference for the chatting pair
        DatabaseReference msgRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                .getReference(AppConstants.DATABASE_PATH_PRIVATE_MESSAGES);

        //generate a pair between the chatting users (using their ids alphabetically)
        String chatPairId = "";
        if (signedInUser.getId().compareTo(selectedUser.getId()) > 0) {
            chatPairId = signedInUser.getId() + selectedUser.getId();
        } else {
            chatPairId = selectedUser.getId() + signedInUser.getId();
        }

        //get the private messages of the chatting pair
        DatabaseReference pairRef = msgRef.child("/" + chatPairId);
        pairRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    messages.add(dataSnapshot.getValue(MessageModel.class));
                }
                messageAdapter.setMessageReceiver(selectedUser);
                messageAdapter.setMessages(messages);
                recyclerViewMessages.setAdapter(messageAdapter);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /* Sends a private message from one user to another and stores it in the database */
    public void sendMessage(UserModel signedInUser, UserModel selectedUser, String message, ProgressBar progress) {
        //generate a pair between the chatting users (using their ids alphabetically)
        String chatPairId = "";
        if (signedInUser.getId().compareTo(selectedUser.getId()) > 0) {
            chatPairId = signedInUser.getId() + selectedUser.getId();
        } else {
            chatPairId = selectedUser.getId() + signedInUser.getId();
        }

        //create a message object
        MessageModel messageObj = new MessageModel(signedInUser.getDisplayName(), selectedUser.getDisplayName(), message);

        //get the private messages database reference
        DatabaseReference msgRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                .getReference(AppConstants.DATABASE_PATH_PRIVATE_MESSAGES);

        //set the data using a Date object as a child
        msgRef.child(chatPairId).child(new Date().toString()).setValue(messageObj)
                .addOnCompleteListener(task -> {
                    progress.setVisibility(View.INVISIBLE);
                    displayPrivateMessages(recyclerViewMessages, signedInUser, selectedUser);
                });
    }
}