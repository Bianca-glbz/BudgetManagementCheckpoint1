package com.example.budgetmanagementcheckpoint1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.budgetmanagementcheckpoint1.Menu;
import com.example.budgetmanagementcheckpoint1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    static final String TAG = "Profile Activity //";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}

 //       EditText editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
 //       EditText editTextPhone = findViewById(R.id.editTextPhone2);
 //       Button ChangeDetails = findViewById(R.id.ChangeDetails);


 //       FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//    //    if (user != null) {
//            DocumentReference docRef = db.collection("users").document(user.getUid());
//            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    String name = documentSnapshot.get("name").toString();
//                    String phone = documentSnapshot.get("phone").toString();
//
//                    editTextTextPersonName.setText(name);
//                    editTextPhone.setText(phone);
//
//                }
//            });
//
//        } else {
//            Log.w(TAG, "User not logged in!");
//        }
//
//
//        ChangeDetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Map<String, Object> updatedUserData = new HashMap<>();
//                updatedUserData.put("name", editTextTextPersonName.getText().toString());
//                updatedUserData.put("phone", editTextPhone.getText().toString());
//
//                db.collection("users").document(user.getUid()).set(updatedUserData).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(Profile.this, "Detailed Updated!", Toast.LENGTH_LONG).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//            }
//        });
//
//
//
//        Profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(Profile.this, Menu.class);
//                startActivity(i);
//            }
//        });
//
//    }
//
//    }