package gparap.apps.chat.ui.private_chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import gparap.apps.chat.R;
import gparap.apps.chat.adapters.ChatListAdapter;
import gparap.apps.chat.data.UserModel;
import gparap.apps.chat.utils.AppConstants;

public class PrivateChatFragment extends Fragment implements ChatListAdapter.ChatListUserCallback {
    private ViewGroup container;
    private PrivateChatViewModel viewModel;
    private RecyclerView recyclerViewUsers;
    private UserModel signedInUser;
    private UserModel selectedUser;
    private boolean isUserChatting = false;
    private View privateChatView;
    private EditText signedInUserMessage;
    private ImageView imageSendMessage;
    private ProgressBar progressSendMessage;
    private RecyclerView recyclerViewMessages;

    public static PrivateChatFragment newInstance() {
        return new PrivateChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //get this Fragment's container
        this.container = container;

        //inflate chat list's layout
        return inflater.inflate(R.layout.fragment_private_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //create the ViewModel for this Fragment
        viewModel = new ViewModelProvider(this).get(PrivateChatViewModel.class);

        //setup chat list RecyclerView with adapter
        recyclerViewUsers = container.findViewById(R.id.recycler_view_chat_list);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this.getContext()));
        ChatListAdapter adapterUsers = new ChatListAdapter();
        adapterUsers.setUserCallback(this);
        recyclerViewUsers.setAdapter(adapterUsers);

        //show loading progress
        ProgressBar progressLoad = container.findViewById(R.id.progress_load_chat_list);
        progressLoad.setVisibility(View.VISIBLE);

        //get chat list (users)
        viewModel.getChatList(adapterUsers, progressLoad);

        //swipe to refresh chat list (users)
        SwipeRefreshLayout swipeRefreshLayout = container.findViewById(R.id.layout_chat_list);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapterUsers.getUsers().clear();
            viewModel.getChatList(adapterUsers, progressLoad);
            swipeRefreshLayout.setRefreshing(false);
        });

        //hide the private chat
        privateChatView = container.findViewById(R.id.layout_view_private_chat);
        privateChatView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        //get signed-in user (if exists)
        if (getActivity() != null && getActivity().getIntent() != null) {
            signedInUser = getActivity().getIntent().getParcelableExtra(AppConstants.SIGNED_IN_USER);
        }

        //get private chat view widgets
        signedInUserMessage = container.findViewById(R.id.edit_text_private_chat_primary_user);
        imageSendMessage = container.findViewById(R.id.image_view_private_chat_send_message);
        progressSendMessage = container.findViewById(R.id.progress_send_private_message);
        recyclerViewMessages = container.findViewById(R.id.recycler_view_private_chat_messages);
    }

    @Override
    public void onClickChatListUser(UserModel user) {
        handleBackButton();

        //hide the users chat list and show the private chat
        // because we will be chatting with the selected user
        recyclerViewUsers.setVisibility(View.INVISIBLE);
        privateChatView.setVisibility(View.VISIBLE);
        isUserChatting = true;
        selectedUser = user;

        //display private messages on the RecyclerView
        if (recyclerViewUsers.getVisibility() == View.INVISIBLE) {
            recyclerViewMessages.setLayoutManager(new LinearLayoutManager(container.getContext()));
            viewModel.displayPrivateMessages(recyclerViewMessages, signedInUser, selectedUser);
        }

        //send private message
        imageSendMessage.setOnClickListener(view -> {
            if (!signedInUserMessage.getText().toString().isEmpty()) {
                progressSendMessage.setVisibility(View.VISIBLE);
                viewModel.sendMessage(signedInUser, selectedUser, signedInUserMessage.getText().toString(), progressSendMessage);
                handleBackButton();
            }
        });
    }

    /* Handles the back button based on if the user is actively chatting or not */
    private void handleBackButton() {
        if (this.getView() != null) {
            this.getView().setFocusableInTouchMode(true);
            this.getView().requestFocus();
            this.getView().setOnKeyListener((v, keyCode, event) -> {
                if (isUserChatting) {
                    //close private chat and diplay chat list
                    recyclerViewUsers.setVisibility(View.VISIBLE);
                    isUserChatting = false;
                    privateChatView.setVisibility(View.INVISIBLE);
                    return true;

                } else {
                    return false;
                }
            });
        }
    }
}