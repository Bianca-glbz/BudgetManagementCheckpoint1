package com.example.budgetmanagementcheckpoint1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.budgetmanagementcheckpoint1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TransactionActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        db = FirebaseFirestore.getInstance();

        //transaction added manually
        EditText descriptionInput = findViewById(R.id.descriptionInput);
        EditText amountInput = findViewById(R.id.amountInput);
        EditText cateogryInput = findViewById(R.id.categoryInput);
        EditText typeInput = findViewById(R.id.typeInput);
        Button addButton = findViewById(R.id.addTransactionBtn);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("TransactionActivity // ", "just testing!");

                // Create a new user with a first and last name
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("description", descriptionInput.getText().toString());
                transaction.put("amount", Float.parseFloat(amountInput.getText().toString()));
                transaction.put("category", cateogryInput.getText().toString());
                transaction.put("type", typeInput.getText().toString());

                // Add a new document with a generated ID- MANUALLY ADDING
                db.collection("transactions")
                        .add(transaction)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.w("TransactionActivity // ", "Added successfullyt");

                                Toast.makeText(TransactionActivity.this, "DocumentSnapshot added with ID: " + documentReference.getId(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TransactionActivity // ", "Error adding document", e);
                                Toast.makeText(TransactionActivity.this, "Error adding document ", Toast.LENGTH_LONG).show();

                            }
                        });

            }
        });


    }
}