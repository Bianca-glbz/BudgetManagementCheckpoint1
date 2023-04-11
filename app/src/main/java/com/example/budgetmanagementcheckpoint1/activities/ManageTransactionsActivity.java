package com.example.budgetmanagementcheckpoint1.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageTransactionsActivity extends AppCompatActivity {

    String[] months = {"january","february","march","april","may","june","july","august","september","october","november","december"};
    String selectedMonth = "";
    String selectedYear = "";
    ActivityResultLauncher<Intent> startActivityForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_transactions);
        initButtons();
        initSpinners();
        setupActivityListner();

    }

    private void setupActivityListner(){
        startActivityForResult = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK){ //checking if result is ok
                Intent data = result.getData(); // getting the data from the file
                Uri contentUri = data.getData(); // getting the file location / uri

                try {
                    InputStream in = getContentResolver().openInputStream(contentUri); // reading the file
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    ArrayList<StatementTransaction> transactions = new ArrayList<>();
                    ArrayList<String> lines = new ArrayList<>();
                    for(String line; (line= reader.readLine()) !=null;){ // loop over each line and store in arraylist
                        lines.add(line);
                    }
                    //
                    for(int i=1; i<11;i++){
                        String [] row  =lines.get(i).split(","); //splitting lines
                        String account = row[0]; //out of the row 0 for account value
                        String date = row[1];
                        String description = row[2];
                        float debit = parseFloatOrNull(row[3]);
                        float credit = parseFloatOrNull(row[4]);
                        float balance = parseFloatOrNull(row[5]);

                        int type = row[6].equals("Debit")? StatementTransaction.DEBIT : StatementTransaction.CREDIT; //if its debit or credit
                        StatementTransaction transaction = new StatementTransaction(account,date, description,
                                type == StatementTransaction.DEBIT? debit:credit,
                                balance, type, Integer.parseInt(selectedYear));
                        transactions.add(transaction);
                    }
                    Toast.makeText(this, "Extracting transactions...", Toast.LENGTH_LONG).show();
                    uploadTransactionsToDB(transactions);

                    in.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  Toast.makeText(this, getFilePathFromUri(data), Toast.LENGTH_LONG).show();
                // csvText.setText(readCSVFile(getFilePathFromUri(data)));
            }
        });

    }

    private void initButtons() {
        Button createButton = findViewById(R.id.createTransactionBtn); // manual create
        Button uploadButton = findViewById(R.id.uploadFileButton); // upload csv
        Button editTransactionsButton = findViewById(R.id.editTransactions); // edit categories and search transactions button

        createButton.setOnClickListener(v -> startTransactionActivity());
        uploadButton.setOnClickListener(v -> openFileChooser());
        editTransactionsButton.setOnClickListener(v -> startEditTransactionsActivity());
    }

    private void startTransactionActivity() {
        Intent i = new Intent(ManageTransactionsActivity.this, TransactionActivity.class);
        i.putExtra("selectedMonth", selectedMonth);
        i.putExtra("selectedYear", selectedYear);
        startActivity(i);
    }

    private void openFileChooser(){
        Intent chooseFIle = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFIle.setType("*/*");
        chooseFIle = Intent.createChooser(chooseFIle, "Choose a file");
        startActivityForResult.launch(chooseFIle);
    }

    private void startEditTransactionsActivity() {
        Intent i = new Intent(ManageTransactionsActivity.this, EditTransactionsActivity.class);
        i.putExtra("selectedYear", selectedYear);
        startActivity(i);
    }

    private void initSpinners() {
        Spinner csvMonthSpinner = findViewById(R.id.csvMonthSPpinner);
        Spinner csvYearSpinner = findViewById(R.id.csvYearSpinner);

        setMonthSpinner(csvMonthSpinner);
        setYearSpinner(csvYearSpinner);
    }

    private void setMonthSpinner(Spinner csvMonthSpinner) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                months
        );
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        csvMonthSpinner.setAdapter(spinnerArrayAdapter);

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date currentDate = new Date();
        int currentMonth = Integer.parseInt(dateFormat.format(currentDate)) - 1;
        csvMonthSpinner.setSelection(currentMonth);
        selectedMonth = months[currentMonth];

        csvMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = months[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setYearSpinner(Spinner csvYearSpinner) {
        ArrayAdapter<String> yearSpinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                DateList.years
        );
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        csvYearSpinner.setAdapter(yearSpinnerAdapter);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        selectedYear = Integer.toString(year);
        csvYearSpinner.setSelection(1);

        csvYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = DateList.years[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public float parseFloatOrNull(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
           return -1;
        }
    }

    public void uploadTransactionsToDB(ArrayList<StatementTransaction> transactions){
        FirebaseUtils.addTransactions(selectedMonth,selectedYear,transactions);

    }


}