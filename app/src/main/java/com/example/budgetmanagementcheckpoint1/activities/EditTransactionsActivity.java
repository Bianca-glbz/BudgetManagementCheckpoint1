package com.example.budgetmanagementcheckpoint1.activities;

import static com.example.budgetmanagementcheckpoint1.utils.DateList.months;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.budgetmanagementcheckpoint1.R;
import com.example.budgetmanagementcheckpoint1.adapters.EditTransactionsAdapter;
import com.example.budgetmanagementcheckpoint1.utils.DateList;
import com.example.budgetmanagementcheckpoint1.utils.FirebaseUtils;
import com.example.budgetmanagementcheckpoint1.utils.StatementTransaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditTransactionsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String selectedMonth ="";
    EditTransactionsAdapter adapter;
    ArrayList<StatementTransaction> transactions;
    Map<String, Map<String, ArrayList<StatementTransaction>>> transactionsData;
    Spinner monthDropdown;
    RecyclerView transactionsREcyclerView;
    String selectedYear = "2023";
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transactions);

        searchView = findViewById(R.id.searchView2);
        monthDropdown = findViewById(R.id.monthSpinner);
        transactionsREcyclerView = findViewById(R.id.transactionsREcyclerView);
        Button saveButton = findViewById(R.id.saveChangesButton);

        selectedYear = getIntent().getStringExtra("selectedYear");
        selectedMonth = getIntent().getStringExtra("selectedMonth");

        db = FirebaseFirestore.getInstance();

        transactionsData = new HashMap<>();
        transactions = new ArrayList<>();

        // get data from firebase
        FirebaseUtils.getTransactions(data -> {
            transactionsData = data;
            setupDropdowns();
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadTransactionsToDB(transactions);

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTransactions(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTransactions(newText);
                return false;
            }
        });

    }

    private void filterTransactions(String query) {

        if(query.equals("")){
                adapter = new EditTransactionsAdapter(transactions, EditTransactionsActivity.this );
                transactionsREcyclerView.setAdapter(adapter);
                transactionsREcyclerView.setLayoutManager(new LinearLayoutManager(EditTransactionsActivity.this));
            return;
        }
    ArrayList<StatementTransaction> filteredTransactions = new ArrayList<>();
    for (StatementTransaction transaction : transactions) {
        if (transaction.getDescription().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
            filteredTransactions.add(transaction);
        }
    }

     adapter = new EditTransactionsAdapter(filteredTransactions, EditTransactionsActivity.this );
                transactionsREcyclerView.setAdapter(adapter);
                transactionsREcyclerView.setLayoutManager(new LinearLayoutManager(EditTransactionsActivity.this));
    //adapter.filterList(filteredTransactions);
}

    public void setupDropdowns(){
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        months); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        monthDropdown.setAdapter(spinnerArrayAdapter);

        // get current month
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat= new SimpleDateFormat("MM");
        Date date = new Date();
        int month = Integer.parseInt(dateFormat.format(date)) -1;

        // use current month as default selection
        
        if(selectedMonth.equals("")){
            selectedMonth = months[month].toLowerCase(Locale.ROOT);
        }
        int monthNum = Arrays.asList(months).indexOf(selectedMonth);
        monthDropdown.setSelection(monthNum);

      //  transactions = FirebaseUtils.getTransactionsFrom(selectedMonth, "2023", transactionsData);

        adapter = new EditTransactionsAdapter(transactions, EditTransactionsActivity.this );
        transactionsREcyclerView.setAdapter(adapter);
        transactionsREcyclerView.setLayoutManager(new LinearLayoutManager(EditTransactionsActivity.this));

        monthDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = months[i].toLowerCase(Locale.ROOT).toLowerCase(Locale.ROOT);

                if(transactions.size() > 0){
                    transactions.clear();
                }
                ArrayList<StatementTransaction> monthTransactions = FirebaseUtils.getTransactionsFrom(selectedMonth, selectedYear, transactionsData);

                if(monthTransactions == null){
                    Toast.makeText(EditTransactionsActivity.this, "No data found for selected month", Toast.LENGTH_LONG).show();
                    transactions = new ArrayList<>();
                }else{
                    transactions = new ArrayList<>(monthTransactions);
                }

                adapter = new EditTransactionsAdapter(transactions, EditTransactionsActivity.this );
                transactionsREcyclerView.setAdapter(adapter);
                transactionsREcyclerView.setLayoutManager(new LinearLayoutManager(EditTransactionsActivity.this));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void uploadTransactionsToDB(ArrayList<StatementTransaction> transactions){

        if(transactions.size() == 0) return;
        Map<String, ArrayList<StatementTransaction>> yearTransactions = transactionsData.get(selectedYear);
        yearTransactions.put(selectedMonth, transactions);
        //transactionsData.put(selectedYear, yearTransactions);

        FirebaseUtils.updateTransactions(transactionsData, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditTransactionsActivity.this,"Transactions saved!",Toast.LENGTH_LONG).show();
            }

        });

    }
}