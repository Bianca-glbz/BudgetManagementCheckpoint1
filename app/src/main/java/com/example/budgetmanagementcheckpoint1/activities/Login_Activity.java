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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Activity extends AppCompatActivity {
    //Login

    private EditText emailInput, passwordInput;
    private Button ContinueSignIn;
    private final String TAG = "Login_Activity // ";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button ContinueSignIn = findViewById(R.id.ContinueSignIn);

        // Initialize Firebase Auth

        mAuth = FirebaseAuth.getInstance();
       FirebaseUser user= mAuth.getCurrentUser();
       if(user != null){
           Toast.makeText(Login_Activity.this, "Login Successful!.",
                   Toast.LENGTH_LONG).show();

           // updateUI(user);
           goToTaskList_Activity();
       }

        //When clicking on the button the below can happen depending on input
        ContinueSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(Login_Activity.this, "Login Successful!.",
                                            Toast.LENGTH_LONG).show();

                                    // updateUI(user);
                                    goToTaskList_Activity();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(Login_Activity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }
                            }
                        });

            }
        });
    }
    private void goToTaskList_Activity() {
        Intent i = new Intent(this, Menu.class);
        startActivity(i);

    }
}

