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
package gparap.apps.location_places;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import gparap.apps.location_places.data.CreditsModel;
import gparap.apps.location_places.utils.AppConstants;

public class MainActivity extends AppCompatActivity {
    int userCredits = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        //get user credits
        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("location-places");
        collectionRef.document(AppConstants.USER).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userCredits = Integer.parseInt(String.valueOf(Objects.requireNonNull(document.getData()).get("credits")));
                    if (userCredits == 0) {
                        System.out.println("Not enough credits");
                        setContentView(R.layout.activity_empty);
                        Objects.requireNonNull(getSupportActionBar()).hide();
                        return;
                    } else {
                        //TODO: work with credits
                        userCredits++;
                        userCredits--;
                    }
                    System.out.println("User Credits = " + document.getData());
                } else {
                    System.out.println("No such user");
                }
            } else {
                System.out.println(Objects.requireNonNull(task.getException()).getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        //update user credits
        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("location-places");
        CreditsModel credits = new CreditsModel(userCredits);
        collectionRef.document(AppConstants.USER).set(credits).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("User credits updated.");
            }
        });
    }
}