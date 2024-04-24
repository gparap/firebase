package gparap.apps.social_media.users;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import gparap.apps.social_media.R;
import gparap.apps.social_media.adapters.PostAdapter;
import gparap.apps.social_media.data.PostModel;
import gparap.apps.social_media.utils.AppConstants;

import java.util.ArrayList;

import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE;
import static gparap.apps.social_media.utils.AppConstants.DATABASE_REFERENCE_POSTS;

public class UserPostsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        //get user id from intent
        String userId = getIntent().getStringExtra(AppConstants.USER_ID);
        if (userId.isEmpty()) {
            return;
        }

        //create a vertical LinearLayoutManager with reversed order
        LinearLayoutManager reversedLayoutManager = new LinearLayoutManager(this);
        reversedLayoutManager.setReverseLayout(true);
        reversedLayoutManager.setStackFromEnd(true);

        //setup post recycler view with adapter
        RecyclerView recyclerViewPosts = findViewById(R.id.recycler_view_user_posts);
        recyclerViewPosts.setLayoutManager(reversedLayoutManager);
        PostAdapter postAdapter = new PostAdapter();
        recyclerViewPosts.setAdapter(postAdapter);

        //fetch user posts from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference().child(DATABASE_REFERENCE).child(DATABASE_REFERENCE_POSTS);
        Task<DataSnapshot> postsSnapshotTask =  postsRef.get();
        postsSnapshotTask.addOnCompleteListener(postsTask -> {
            if (postsTask.isSuccessful()) {
                //get all database posts
                DataSnapshot postsSnapshot = postsTask.getResult();

                //search for user posts
                ArrayList<PostModel> userPostsList = new ArrayList<>();
                for (DataSnapshot postSnapshot : postsSnapshot.getChildren()) {
                    PostModel post = postSnapshot.getValue(PostModel.class);
                    if (post != null && post.getUserId().equals(userId)){
                        userPostsList.add(post);
                    }
                }

                //add user posts to adapter
                postAdapter.setPostsList(userPostsList);
            }
        });
    }
}