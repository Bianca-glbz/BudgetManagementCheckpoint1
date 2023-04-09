package com.example.budgetmanagementcheckpoint1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.budgetmanagementcheckpoint1.R;
import com.example.budgetmanagementcheckpoint1.utils.Categories;
import com.example.budgetmanagementcheckpoint1.utils.StatementTransaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionActivity extends AppCompatActivity {


    FirebaseFirestore db;
    int selectedType = 0;
    int currentCateogry = Arrays.asList(Categories.list).indexOf(0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        db = FirebaseFirestore.getInstance();

        String selectedMonth = getIntent().getStringExtra("selectedMonth");
        String selectedYear = getIntent().getStringExtra("selectedYear");

        //transaction added manually
        EditText descriptionInput = findViewById(R.id.descriptionInput);
        EditText amountInput = findViewById(R.id.amountInput);
        Spinner categoriesDropdwon = findViewById(R.id.categoryDropdown);
        EditText dateInput = findViewById(R.id.date);
        Spinner typeDropdown = findViewById(R.id.typeDropdown);

        Button addButton = findViewById(R.id.addTransactionBtn);

        // Transaction type drop down
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        new String[]{"DEBIT","CREDIT"}); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        typeDropdown.setAdapter(spinnerArrayAdapter);
        typeDropdown.setSelection(selectedType);
        typeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType = i+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // category dropdown

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        Categories.list); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        categoriesDropdwon.setAdapter(categoriesAdapter);

        categoriesDropdwon.setSelection(currentCateogry);

        categoriesDropdwon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentCateogry = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("TransactionActivity // ", "just testing!");

                // Create a new user with a first and last name

                StatementTransaction transaction = new StatementTransaction("n/a",dateInput.getText().toString(),descriptionInput.getText().toString(),Float.parseFloat(amountInput.getText().toString()),
                        -1,selectedType,Categories.list[currentCateogry],Integer.parseInt(selectedYear));

                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String[] months = {"january","february","march","april","may","june","july","august","september","october","november","december"};

//                @SuppressLint("SimpleDateFormat")
//                DateFormat dateFormat= new SimpleDateFormat("MM");
//                Date currentDate = new Date();
//                String currentMonth = months[Integer.parseInt(dateFormat.format(currentDate)) -1];



                DocumentReference docRef = db.collection("transactions").document(id);
                Map<String, Object> monthlyTransactions = new HashMap<>();

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        for(String month: documentSnapshot.getData().keySet()){
                            List<String> transactionsCSV = (List<String>) documentSnapshot.get(month);

                            ArrayList<String> monthTransaction = new ArrayList<>();
                            for(int i=0; i<transactionsCSV.size();i++){
                                monthTransaction.add((transactionsCSV.get(i)));
                            }
                            monthlyTransactions.put(month, monthTransaction);

                        }

                        List<String> transactionsCSV = (List<String>) monthlyTransactions.get(selectedMonth);
                        transactionsCSV.add(0, transaction.getCSVstring());
                        monthlyTransactions.put(selectedMonth, transactionsCSV);

                        db.collection("transactions").document(id).set(monthlyTransactions).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(TransactionActivity.this, "Transaction added!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TransactionActivity.this, "Upload Failed!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        });

                    }
                });


            }
        });


    }
}