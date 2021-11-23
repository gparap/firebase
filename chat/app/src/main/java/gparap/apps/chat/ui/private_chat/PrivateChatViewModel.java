package gparap.apps.chat.ui.private_chat;

import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

import gparap.apps.chat.adapters.ChatListAdapter;
import gparap.apps.chat.data.MessageModel;
import gparap.apps.chat.data.UserModel;
import gparap.apps.chat.utils.AppConstants;

public class PrivateChatViewModel extends ViewModel {
    String signedInUserId = "";

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

    /* Sends a private message from one user to another and stores it in the database */
    public void sendMessage(UserModel signedInUser, UserModel selectedUser, String message, ProgressBar progress) {
        //generate a pair between the chatting users (using their ids alphabetically)
        String chatPairId = "";
        if (signedInUser.getId().compareTo(selectedUser.getId()) > 0){
            chatPairId = signedInUser.getId() + selectedUser.getId();
        }else{
            chatPairId = selectedUser.getId() + signedInUser.getId();
        }

        //create a message object
        MessageModel messageObj = new MessageModel(signedInUser.getDisplayName(), selectedUser.getDisplayName(), message);

        //get the private messages database reference
        DatabaseReference msgRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                .getReference(AppConstants.DATABASE_PATH_PRIVATE_MESSAGES);

        //set the data using a Date object as a child
        msgRef.child(chatPairId).child(new Date().toString()).setValue(messageObj)
                .addOnCompleteListener(task -> progress.setVisibility(View.INVISIBLE));
    }
}