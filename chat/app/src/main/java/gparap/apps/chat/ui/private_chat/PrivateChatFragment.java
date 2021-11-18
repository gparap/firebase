package gparap.apps.chat.ui.private_chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    private RecyclerView recyclerViewUsers;
    private UserModel user;
    private boolean isUserChatting = false;

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
        PrivateChatViewModel viewModel = new ViewModelProvider(this).get(PrivateChatViewModel.class);

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
    }

    @Override
    public void onResume() {
        super.onResume();

        //get signed-in user (if exists)
        if (getActivity() != null && getActivity().getIntent() != null) {
            user = getActivity().getIntent().getParcelableExtra(AppConstants.SIGNED_IN_USER);
        }

        //handle the back button based on if the user is actively chatting or not
        if (this.getView() != null) {
            this.getView().setFocusableInTouchMode(true);
            this.getView().requestFocus();
            this.getView().setOnKeyListener((v, keyCode, event) -> {
                if (isUserChatting) {
                    //close private chat and diplay chat list
                    recyclerViewUsers.setVisibility(View.VISIBLE);
                    isUserChatting = false;
                    return true;

                } else {
                    return false;
                }
            });
        }
    }

    @Override
    public void onClickChatListUser(UserModel user) {
        //hide chat list because we will be chatting with the selected user
        recyclerViewUsers.setVisibility(View.INVISIBLE);
        isUserChatting = true;

        //DEBUG
        Toast.makeText(container.getContext(), user.getDisplayName(), Toast.LENGTH_SHORT).show();
    }
}