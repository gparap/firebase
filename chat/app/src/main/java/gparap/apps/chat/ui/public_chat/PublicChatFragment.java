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

        //get fragment widgets
        ImageView buttonSendMsg = container.findViewById(R.id.image_view_public_chat_send_message);
        EditText msgText = container.findViewById(R.id.edit_text_public_chat_message);

        //send public message
        buttonSendMsg.setOnClickListener(view -> {
            if (msgText.getText().toString().isEmpty()) { return; }

            //create the message
            MessageModel message = new MessageModel();
            message.setSender(signedInUser.getDisplayName());
            message.setMessage(msgText.getText().toString().trim());

            if (!msgText.getText().toString().isEmpty()) {
                viewModel.sendPublicMessage(message);
            }
        });
    }
}