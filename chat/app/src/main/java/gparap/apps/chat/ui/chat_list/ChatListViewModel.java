package gparap.apps.chat.ui.chat_list;

import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import gparap.apps.chat.adapters.ChatListAdapter;
import gparap.apps.chat.data.UserModel;
import gparap.apps.chat.utils.AppConstants;

public class ChatListViewModel extends ViewModel {

    /* Retrieves the possible-to-chat users from database */
    public void getChatList(ChatListAdapter chatListAdapter, ProgressBar progressLoad) {
        ArrayList<UserModel> users = new ArrayList<>();

        //get the users database reference
        DatabaseReference usersRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                .getReference(AppConstants.DATABASE_PATH_USERS);

        //get the users from the database
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    users.add(user);
                }
                chatListAdapter.setUsers(users);
                progressLoad.setVisibility(View.INVISIBLE);
            }
        });
    }
}