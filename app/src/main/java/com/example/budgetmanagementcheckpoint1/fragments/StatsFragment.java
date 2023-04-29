package com.example.budgetmanagementcheckpoint1.fragments;

import static com.example.budgetmanagementcheckpoint1.utils.DateList.months;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetmanagementcheckpoint1.R;
import com.example.budgetmanagementcheckpoint1.utils.DateList;
import com.example.budgetmanagementcheckpoint1.utils.FirebaseUtils;
import com.example.budgetmanagementcheckpoint1.utils.StatementTransaction;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    Map<String, Map<String, ArrayList<StatementTransaction>>> transactionsData;
    Map<String, List<StatementTransaction>> monthlyTransactions;
    ArrayList<StatementTransaction> transactions;
    String selectedMonth, selectedYear;


    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
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
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        monthlyTransactions = new HashMap<>();
        transactions = new ArrayList<>();

        setupDropdowns();
        getData();
    }

    public void setupDropdowns(){

        Spinner csvMonthSPinner = getView().findViewById(R.id.monthDropDown);
        Spinner csvYearSpinner = getView().findViewById(R.id.yearDropDown);

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat= new SimpleDateFormat("MM");
        Date currentDate = new Date();
        int currentMonth = Integer.parseInt(dateFormat.format(currentDate)) -1;


        // month dropdown adapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,
                        months); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        csvMonthSPinner.setAdapter(spinnerArrayAdapter);

        // year dropdown adapter
        ArrayAdapter<String> yearSpinnerAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item, DateList.years
                ); //selected item will look like a spinner set from XML
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        csvYearSpinner.setAdapter(yearSpinnerAdapter);

        // set dropdown month  current value
        csvMonthSPinner.setSelection(currentMonth);
        selectedMonth = months[currentMonth];

        // set dropdown year current value
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        selectedYear = year+"";
        csvYearSpinner.setSelection(1);

        refreshChartsWithData();

        csvMonthSPinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = months[i];
                refreshChartsWithData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        csvYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = DateList.years[i];
                refreshChartsWithData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getData(){

        FirebaseUtils.getTransactions(new OnSuccessListener<Map<String, Map<String, ArrayList<StatementTransaction>>>>() {
            @Override
            public void onSuccess(Map<String, Map<String, ArrayList<StatementTransaction>>> data) {
                transactionsData = data;
                refreshChartsWithData();
            }
        });
    }

    public void refreshChartsWithData(){
    if(transactionsData == null) {
        return;
    }
        List<StatementTransaction> currentMonthData =  FirebaseUtils.getTransactionsFrom(selectedMonth, selectedYear, transactionsData);

        transactions.clear();

        if(currentMonthData == null){
            Toast.makeText(getContext(),"No data found for the selection.", Toast.LENGTH_LONG).show();
            calculateStats();
            showTable();
            return;
        }

        

        for(int i=0; i<currentMonthData.size();i++){
            transactions.add(currentMonthData.get(i));
        }

        calculateStats();
        showTable();
    }

    public void showTable(){
        TableLayout categoryTable = getView().findViewById(R.id.categoryTable);
        categoryTable.removeAllViews();

        // Calculation to sum the total debit amount and calculate percentage for each category
        Map<String, Float> debitByCategory = new HashMap<>();
        float totalDebit = 0f;
        for (StatementTransaction transaction : transactions) {
            if (transaction.getTransactionType() == StatementTransaction.DEBIT) {
                String category = transaction.getCategory();
                Float debit = transaction.getDebitAmount();
                if (debit != null) {
                    Float debitInCategory = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        debitInCategory = debitByCategory.getOrDefault(category, 0f);
                    }
                    debitByCategory.put(category, debitInCategory + debit);
                    totalDebit += debit;
                }
            }
        }

// Create a new TableRow for the headings
        TableRow headingsRow = new TableRow(getContext());

// Create a TextView for the category heading
        TextView categoryHeading = new TextView(getContext());
        categoryHeading.setText("Category");
        categoryHeading.setTextColor(Color.BLACK);
        categoryHeading.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1f));
        categoryHeading.setTypeface(null, Typeface.BOLD);

        headingsRow.addView(categoryHeading);

// Create a TextView for the amount heading
        TextView amountHeading = new TextView(getContext());
        amountHeading.setText("Amount Spent");
        amountHeading.setTextColor(Color.BLACK);
        amountHeading.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1f));
        amountHeading.setGravity(Gravity.END);
        amountHeading.setTypeface(null, Typeface.BOLD);

        headingsRow.addView(amountHeading);

// Add the headings TableRow to the TableLayout
        categoryTable.addView(headingsRow);

        for (Map.Entry<String, Float> entry : debitByCategory.entrySet()) {
            String category = entry.getKey();
            Float debit = entry.getValue();

            // Create a new TableRow
            TableRow row = new TableRow(getContext());

            // Create a TextView for the category name
            TextView categoryText = new TextView(getContext());
            categoryText.setText(category);
            categoryText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1f));
            row.addView(categoryText);

            // Create a TextView for the debit amount
            TextView debitText = new TextView(getContext());
            debitText.setText(String.format("€%.2f", debit));
            debitText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1f));
            debitText.setGravity(Gravity.END);
            row.addView(debitText);

            // Add the TableRow to the TableLayout
            categoryTable.addView(row);
        }
    }

    public void calculateStats() {
        PieChart chart = getView().findViewById(R.id.piechart);

        // Calculation to sum the total debit amount and calculate percentage for each category
        Map<String, Float> debitByCategory = new HashMap<>();
        float totalDebit = 0f;
        for (StatementTransaction transaction : transactions) {
            if (transaction.getTransactionType() == StatementTransaction.DEBIT) {
                String category = transaction.getCategory();
                Float debit = transaction.getDebitAmount();
                if (debit != null) {
                    Float debitInCategory = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        debitInCategory = debitByCategory.getOrDefault(category, 0f);
                    }
                    debitByCategory.put(category, debitInCategory + debit);
                    totalDebit += debit;
                }
            }
        }

        // Create PieEntries for each category and calculate their percentages
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : debitByCategory.entrySet()) {
            String category = entry.getKey();
            Float debit = entry.getValue();
            float percentage = (debit / totalDebit);
            String label = String.format("%s", category);
            entries.add(new PieEntry(percentage, label));
        }

       // Set up the PieDataSet and PieData
PieDataSet dataSet = new PieDataSet(entries, "Debit Distribution by Category");
dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
dataSet.setValueTextSize(12f);
dataSet.setValueTextColor(Color.WHITE);
dataSet.setValueFormatter(new PercentFormatter());
dataSet.setDrawValues(false);
dataSet.setLabel("");

PieData data = new PieData(dataSet);
chart.setData(data);

// Customize the chart appearance and enable the legend
chart.getDescription().setEnabled(false);
chart.setHoleRadius(0f);
chart.setTransparentCircleRadius(0f);
chart.animateY(1000, Easing.EaseInOutCubic);

// Set the visibility of the PieChart to VISIBLE
chart.setVisibility(View.VISIBLE);

// Add a Legend to the PieChart
Legend legend = chart.getLegend();
legend.setForm(Legend.LegendForm.SQUARE);
legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
legend.setDrawInside(false);
legend.setWordWrapEnabled(true);
legend.setXEntrySpace(10f);
legend.setYEntrySpace(10f);
legend.setYOffset(2f);
legend.setTextSize(14f);
legend.setMaxSizePercent(0.4f);



        float finalTotalDebit = totalDebit;
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                // Show the label and value of the selected pie slice in a Toast message
                if (entry instanceof PieEntry) {
                    PieEntry pieEntry = (PieEntry) entry;
                    String label = pieEntry.getLabel();
                    Float debit = ((PieEntry) entry).getValue();
                    float percentage = (debit / finalTotalDebit) * 100f;
                    String message = String.format("%s: €%.2f (%.1f%%)", label, debit, percentage*100);
                   // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // Hide the details when no pie slice is selected
            }
        });

        // Refresh the chart to show the updated data
        chart.invalidate();
    }

}