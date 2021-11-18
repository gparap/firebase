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

import gparap.apps.chat.adapters.ChatListAdapter;
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
                    if (user != null && !user.getId().equals(signedInUserId)){
                        users.add(user);
                    }
                }
                chatListAdapter.setUsers(users);
                progressLoad.setVisibility(View.INVISIBLE);
            }
        });
    }
}