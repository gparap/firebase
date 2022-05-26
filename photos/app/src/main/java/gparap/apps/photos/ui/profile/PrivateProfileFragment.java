/*
 * Copyright (c) 2022 gparap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gparap.apps.photos.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import gparap.apps.photos.R;
import gparap.apps.photos.data.UserModel;
import gparap.apps.photos.ui.home.HomeFragment;

public class PrivateProfileFragment extends Fragment {

    private PrivateProfileViewModel mViewModel;
    private ViewGroup container;
    UserModel user;

    public static PrivateProfileFragment newInstance() {
        return new PrivateProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.container = container;
        //refresh layout
        if (container != null) {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.fragment_private_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PrivateProfileViewModel.class);

        // TODO: Use the ViewModel

        //close fragment and return home
        ImageView imageView = container.findViewById(R.id.image_view_profile_profile_private);
        imageView.setOnClickListener(v -> {
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.layout_fragment_profile_private, new HomeFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //get database
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        //get user
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return;
        }

        //get user profile
        DocumentReference userRef = database.collection("users").document(firebaseUser.getUid());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //converted document to a userModel POJO
                DocumentSnapshot snapshot = task.getResult();
                user = snapshot.toObject(UserModel.class);

                //display user profile
                EditText username = container.findViewById(R.id.edit_text_profile_private_username);
                username.setText(user.getUsername());
                EditText email = container.findViewById(R.id.edit_text_profile_private_email);
                email.setText(user.getEmail());
                EditText password = container.findViewById(R.id.edit_text_profile_private_password);
                password.setText(user.getPassword());

            }
            if (task.isCanceled()) {
                //TODO: Log for debug
                System.out.println(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }
}