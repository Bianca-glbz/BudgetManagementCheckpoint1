package com.example.budgetmanagementcheckpoint1.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.budgetmanagementcheckpoint1.R;
import com.example.budgetmanagementcheckpoint1.activities.EditTransactionsActivity;
import com.example.budgetmanagementcheckpoint1.activities.ManageTransactionsActivity;
import com.example.budgetmanagementcheckpoint1.activities.TransactionActivity;
import com.example.budgetmanagementcheckpoint1.utils.DateList;
import com.example.budgetmanagementcheckpoint1.utils.FirebaseUtils;
import com.example.budgetmanagementcheckpoint1.utils.StatementTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

     String[] months = {"january","february","march","april","may","june","july","august","september","october","november","december"};
    String selectedMonth = "";
    String selectedYear = "";
    ActivityResultLauncher<Intent> startActivityForResult;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Button manageTransactionsButton = getView().findViewById(R.id.transactionsButton);

        // manageTransactionsButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         Intent i = new Intent(getActivity(), ManageTransactionsActivity.class );
        //         getActivity().startActivity(i);
        //     }
        // });

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
                    InputStream in = getActivity().getContentResolver().openInputStream(contentUri); // reading the file
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
                    Toast.makeText(getActivity(), "Extracting transactions...", Toast.LENGTH_LONG).show();
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
        Button createButton = getView().findViewById(R.id.createTransactionBtn); // manual create
        Button uploadButton =  getView().findViewById(R.id.uploadFileButton); // upload csv
        Button editTransactionsButton =  getView().findViewById(R.id.editTransactions); // edit categories and search transactions button

        createButton.setOnClickListener(v -> startTransactionActivity());
        uploadButton.setOnClickListener(v -> openFileChooser());
        editTransactionsButton.setOnClickListener(v -> startEditTransactionsActivity());
    }

    private void startTransactionActivity() {
        Intent i = new Intent(getActivity(), TransactionActivity.class);
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
        Intent i = new Intent(getActivity(), EditTransactionsActivity.class);
        i.putExtra("selectedYear", selectedYear);
        i.putExtra("selectedMonth", selectedMonth);
        startActivity(i);
    }

    private void initSpinners() {
        Spinner csvMonthSpinner =  getView().findViewById(R.id.homeMonthSpinner);
        Spinner csvYearSpinner =  getView().findViewById(R.id.homeYearSpinner);

        setMonthSpinner(csvMonthSpinner);
        setYearSpinner(csvYearSpinner);
    }

    private void setMonthSpinner(Spinner csvMonthSpinner) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                getActivity(),
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
                getActivity(),
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
        FirebaseUtils.addTransactions(selectedMonth,selectedYear,transactions, getActivity());

    }
}