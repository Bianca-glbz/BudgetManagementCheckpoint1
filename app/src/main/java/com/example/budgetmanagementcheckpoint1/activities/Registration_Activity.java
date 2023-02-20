package com.example.budgetmanagementcheckpoint1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.budgetmanagementcheckpoint1.Menu;
import com.example.budgetmanagementcheckpoint1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration_Activity extends AppCompatActivity {
    //Register

    private EditText PersonName, PersonPassword, PersonPhone, PersonEmailAddress;
    private Button Register;
    private final String TAG = "Registration_Activity // ";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EditText PersonName = findViewById(R.id.PersonName);
        EditText PersonPassword = findViewById(R.id.PersonPassword);
        EditText PersonPhone = findViewById(R.id.PersonPhone);
        EditText PersonEmailAddress = findViewById(R.id.PersonEmailAddress);

        Button Register = findViewById(R.id.Register);

        // Initialize Firebase Auth

        mAuth = FirebaseAuth.getInstance();

        // initialize firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //When clicking on the button the below can happen depending on input
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = PersonName.getText().toString();
                String pass = PersonPassword.getText().toString();
                String pho = PersonPhone.getText().toString();
                String ema = PersonEmailAddress.getText().toString();

                mAuth.createUserWithEmailAndPassword(ema,pass)
                        .addOnCompleteListener(Registration_Activity.this, new OnCompleteListener<AuthResult>() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUser:success");
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    Toast.makeText(Registration_Activity.this, "Authentication Successful.",
                                            Toast.LENGTH_SHORT).show();
                                    //    updateUI(user);

                                    // Create a new user with a first and last name
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name", name);
                                    user.put("phone", pho);

                                    // Add a new document with a generated ID
                                    assert firebaseUser != null;
                                    db.collection("users").document(firebaseUser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            goToTask_ListActivity();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUser:failure", task.getException());
                                    Toast.makeText(Registration_Activity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //    updateUI(null);
                                }
                            }
                        });


            }
        });
    }

    private void goToTask_ListActivity() {
        Intent i = new Intent(this, Menu.class);
        startActivity(i);
    }
}