package gparap.apps.social_media.adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gparap.apps.social_media.R;
import gparap.apps.social_media.data.UserModel;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<UserModel> userList = new ArrayList<>();

    /** @noinspection unused*/
    public ArrayList<UserModel> getUserList() {
        return userList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUserList(ArrayList<UserModel> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create & return the item view
        View view = LayoutInflater.from(parent.getContext()).inflate(parent.getContext()
                .getResources().getLayout(R.layout.cardview_user), parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        //get user image resource identifier
        Uri uri = Uri.parse(userList.get(position).getImageUrl());

        //display user
        Picasso.get().load(uri).into(holder.userImage);
        holder.userName.setText(userList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userImage;
        private final TextView userName;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.image_view_app_user);
            userName = itemView.findViewById(R.id.text_view_app_user);
        }
    }
}
