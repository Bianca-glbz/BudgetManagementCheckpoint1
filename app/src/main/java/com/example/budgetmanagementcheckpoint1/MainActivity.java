package com.example.budgetmanagementcheckpoint1;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.budgetmanagementcheckpoint1.activities.Login_Activity;
import com.example.budgetmanagementcheckpoint1.activities.Registration_Activity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //declaring signup and signin buttons for MainActivity

    private Button SignInMain, SignUpMain;
    private final String TAG = "MainActivity // ";
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Finding screen By ID's
        SignInMain = (Button) findViewById(R.id.SignInMain);
        SignUpMain = (Button) findViewById(R.id.SignUpMain);




        //When clicking SignInMain Button go to Login class
        SignInMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, Login_Activity.class);
                startActivity(i);
            }
        });
        //When clicking SignUpMain Button go to Registration class
        SignUpMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Registration_Activity.class);
                startActivity(intent);
            }
        });
    }

}