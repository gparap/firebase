package gparap.apps.chat.ui.chat_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ChatListFragment extends Fragment {
    private ViewGroup container;

    public static ChatListFragment newInstance() {
        return new ChatListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //get this Fragment's container
        this.container = container;

        //inflate chat list's layout
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //create the ViewModel for this Fragment
        ChatListViewModel viewModel = new ViewModelProvider(this).get(ChatListViewModel.class);

        //setup chat list RecyclerView with adapter
        RecyclerView recyclerViewUsers = container.findViewById(R.id.recycler_view_chat_list);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this.getContext()));
        ChatListAdapter adapterUsers = new ChatListAdapter();
        recyclerViewUsers.setAdapter(adapterUsers);

        //show loading progress
        ProgressBar progressLoad = container.findViewById(R.id.progress_load_chat_list);
        progressLoad.setVisibility(View.VISIBLE);

        //get chat list (users)
        viewModel.getChatList(adapterUsers, progressLoad);

        //swipe to refresh chat list (users)
        SwipeRefreshLayout swipeRefreshLayout = container.findViewById(R.id.layout_chat_list);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapterUsers.getUsers().clear();
                viewModel.getChatList(adapterUsers, progressLoad);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}