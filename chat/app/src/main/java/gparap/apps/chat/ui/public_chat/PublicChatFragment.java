package gparap.apps.chat.ui.public_chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gparap.apps.chat.R;
import gparap.apps.chat.data.MessageModel;
import gparap.apps.chat.data.UserModel;
import gparap.apps.chat.utils.AppConstants;

public class PublicChatFragment extends Fragment {
    private ViewGroup container;
    private PublicChatViewModel viewModel;
    public UserModel signedInUser;

    public static PublicChatFragment newInstance() {
        return new PublicChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //get signed-in user (if exists)
        if (getActivity() != null && getActivity().getIntent() != null) {
            signedInUser = getActivity().getIntent().getParcelableExtra(AppConstants.SIGNED_IN_USER);
        }

        //get this Fragment's container
        this.container = container;

        //inflate chat list's layout
        return inflater.inflate(R.layout.fragment_public_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //create the ViewModel for this Fragment
        viewModel = new ViewModelProvider(this).get(PublicChatViewModel.class);

        displayPublicMessages();
    }


    @Override
    public void onResume() {
        super.onResume();
        displayPublicMessages();
    }

    private void displayPublicMessages() {
        //get fragment widgets
        ImageView buttonSendMsg = container.findViewById(R.id.image_view_public_chat_send_message);
        EditText msgText = container.findViewById(R.id.edit_text_public_chat_message);
        RecyclerView recyclerView = container.findViewById(R.id.recycler_view_public_chat_messages);

        //send public message
        buttonSendMsg.setOnClickListener(view -> {
            if (msgText.getText().toString().isEmpty()) { return; }

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                //get "users" reference
                DatabaseReference userRef = FirebaseDatabase.getInstance(AppConstants.DATABASE_URL)
                        .getReference(AppConstants.DATABASE_PATH_USERS + firebaseUser.getUid());

                //get the user's profile image
                userRef.child(AppConstants.DATABASE_CHILD_PROFILE_IMAGE_URL).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getValue() != null) {
                        if (!task.getResult().getValue().toString().isEmpty()) {
                            signedInUser.setProfileImageUrl(task.getResult().getValue().toString());
                        } else {
                            signedInUser.setProfileImageUrl("");
                        }

                        //create the message
                        if (msgText.getText().toString().isEmpty()) { return; }
                        MessageModel message = new MessageModel();
                        message.setSender(signedInUser.getProfileImageUrl());
                        message.setMessage(msgText.getText().toString().trim());
                        viewModel.sendPublicMessage(message);
                    }
                });
            }
        });

        //display public message
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        viewModel.displayPublicMessages(recyclerView);
    }
}