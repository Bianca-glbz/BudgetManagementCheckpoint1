package com.example.budgetmanagementcheckpoint1.fragments;

import static com.example.budgetmanagementcheckpoint1.utils.DateList.months;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetmanagementcheckpoint1.R;
import com.example.budgetmanagementcheckpoint1.utils.Categories;
import com.example.budgetmanagementcheckpoint1.utils.DateList;
import com.example.budgetmanagementcheckpoint1.utils.FirebaseUtils;
import com.example.budgetmanagementcheckpoint1.utils.StatementTransaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComparisonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComparisonsFragment extends Fragment {

    private static final String TAG = "BudgetComparisonFragment";

    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private TableLayout tableLayout;

    private String selectedMonth;
    private String selectedYear;

private TextView messageView;

     private BarChart barChart;

    private Map<String, Double> budgetTargets;
    private Map<String, Map<String, ArrayList<StatementTransaction>>> transactions;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ComparisonsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComparisonsFragment newInstance(String param1, String param2) {
        ComparisonsFragment fragment = new ComparisonsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

         barChart = view.findViewById(R.id.bar_chart);
        monthSpinner = view.findViewById(R.id.month_spinner);
        yearSpinner = view.findViewById(R.id.year_spinner);
        tableLayout = view.findViewById(R.id.budget_comparison_table);

        messageView = view.findViewById(R.id.messageView);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, DateList.years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // selectedMonth = "april";
        // selectedYear = "2023";

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat= new SimpleDateFormat("MM");
        Date currentDate = new Date();
        int currentMonth = Integer.parseInt(dateFormat.format(currentDate)) -1;

        // set dropdown month  current value
        monthSpinner.setSelection(currentMonth);
        selectedMonth = months[currentMonth];

        // set dropdown year current value
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        selectedYear = year+"";
        yearSpinner.setSelection(1);

         monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = months[i];
                getBudgetData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = DateList.years[i];
                getBudgetData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

            FirebaseUtils.getTransactions(new OnSuccessListener<Map<String, Map<String, ArrayList<StatementTransaction>>>>() {
                @Override
                public void onSuccess(Map<String, Map<String, ArrayList<StatementTransaction>>> stringMapMap) {

                    transactions = stringMapMap;
                    updateTable();
                
                }
            });

        getBudgetData();

        return view;
    }

    private void getBudgetData(){
        FirebaseUtils.getBudgetTarget(selectedMonth, selectedYear, new OnSuccessListener<Map<String, Double>>() {
            @Override
            public void onSuccess(Map<String, Double> stringDoubleMap) {
                budgetTargets = stringDoubleMap;
                updateTable();
            }
        });
    }

    private void updateMessage(){


            double totalSpent = 0;
            double totalTarget = 0;
            for (Map.Entry<String, Double> entry : budgetTargets.entrySet()) {
                String category = entry.getKey();
                double target = entry.getValue();
                totalTarget += target;
                
                if(transactions == null) {
                    Toast.makeText(getContext(), "No transactions found for the selection", Toast.LENGTH_SHORT).show();
                    return;
                }


                Map<String, ArrayList<StatementTransaction>> yearData = transactions.get(selectedYear);
                    if (yearData != null) {
                        ArrayList<StatementTransaction> transactionsList = yearData.get(selectedMonth);
                        if (transactionsList != null) {
                            for (StatementTransaction transaction : transactionsList) {
                                if (transaction.getCategory().equals(category)) {
                                    totalSpent += transaction.getDebitAmount();
                                }
                            }
                        }
                    }

            }


            

            String message;
            if (totalSpent > totalTarget) {
                double overshootPercentage = (totalSpent - totalTarget) / totalTarget * 100;
                message =String.format(Locale.US, "Total Spent = %.2f", totalSpent) +
                        "\nBudget Target = " + totalTarget +
                        "\nYou overshot the budget by " + String.format(Locale.US, "%.2f", overshootPercentage) + "%!";
            } else {
                double remainingPercentage = (totalTarget - totalSpent) / totalTarget * 100;
                message = String.format(Locale.US, "Total Spent = %.2f", totalSpent) + 
                        "\nBudget Target = " + totalTarget +
                        "\nYou are " + String.format(Locale.US, "%.2f", remainingPercentage) + "% away from reaching your total budget target!";
            }

            messageView.setText(message);

    }

    private void updateTable() {

        if (budgetTargets == null || transactions == null) {
            return;
        }
        updateMessage();

        tableLayout.removeAllViews();

        // Create a new TableRow
TableRow headerRow = new TableRow(getContext());

        // Create TextViews for each column and add them to the TableRow
        TextView categoryTextView = new TextView(getContext());
        categoryTextView.setText("Category");
        categoryTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        categoryTextView.setTypeface(null, Typeface.BOLD);
        categoryTextView.setTextColor(Color.parseColor("#000000"));
        headerRow.addView(categoryTextView);

        TextView totalSpendTextView = new TextView(getContext());
        totalSpendTextView.setText("Actual");
        totalSpendTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        totalSpendTextView.setTypeface(null, Typeface.BOLD);
        totalSpendTextView.setTextColor(Color.parseColor("#000000"));
        headerRow.addView(totalSpendTextView);

        TextView targetSpendTextView = new TextView(getContext());
        targetSpendTextView.setText("Target");
        targetSpendTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        targetSpendTextView.setTypeface(null, Typeface.BOLD);
        targetSpendTextView.setTextColor(Color.parseColor("#000000"));
        headerRow.addView(targetSpendTextView);

        // Add the TableRow to the TableLayout as the first row
        tableLayout.addView(headerRow, 0);

        for (Map.Entry<String, Double> entry : budgetTargets.entrySet()) {
            String category = entry.getKey();
            Double targetAmount = entry.getValue();
            Double actualAmount = 0.0;

            Map<String, ArrayList<StatementTransaction>> yearData = transactions.get(selectedYear);
            if (yearData != null) {
                ArrayList<StatementTransaction> transactionsList = yearData.get(selectedMonth);
                if (transactionsList != null) {
                    for (StatementTransaction transaction : transactionsList) {
                        if (transaction.getCategory().equals(category)) {
                            actualAmount += transaction.getDebitAmount();
                        }
                    }
                }
            }

            TableRow row = new TableRow(getContext());
            TextView categoryView = new TextView(getContext());
            TextView actualView = new TextView(getContext());
            TextView targetView = new TextView(getContext());

            categoryView.setText(category);
            actualView.setText(String.format(Locale.getDefault(), "%.2f", actualAmount));
            targetView.setText(String.format(Locale.getDefault(), "%.2f", targetAmount));

            row.addView(categoryView);
            row.addView(actualView);
            row.addView(targetView);
            tableLayout.addView(row);
        }
        loadBarchChart();
        barChart.performClick();
    }
    

    public void loadBarchChart() {


        // Create the BarDataSet for actual spend
        BarDataSet actualDataSet = new BarDataSet(new ArrayList<>(), "Actual");
        actualDataSet.setColor(Color.GREEN);

        // Create the BarDataSet for target spend
        BarDataSet targetDataSet = new BarDataSet(new ArrayList<>(), "Target");
        targetDataSet.setColor(Color.RED);

        // Add entries to each BarDataSet
        ArrayList<String> categories = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Double> entry : budgetTargets.entrySet()) {
            String category = entry.getKey();
            Double targetAmount = entry.getValue();
            Double actualAmount = 0.0;

            Map<String, ArrayList<StatementTransaction>> yearData = transactions.get(selectedYear);
            if (yearData != null) {
                ArrayList<StatementTransaction> transactionsList = yearData.get(selectedMonth);
                if (transactionsList != null) {
                    for (StatementTransaction transaction : transactionsList) {
                        if (transaction.getCategory().equals(category)) {
                            actualAmount += transaction.getDebitAmount();
                        }
                    }
                }
            }

            categories.add(category);
            actualDataSet.addEntry(new BarEntry(i, actualAmount.floatValue()));
            targetDataSet.addEntry(new BarEntry(i, targetAmount.floatValue()));
            i++;
        }

        // Create the BarData with the two BarDataSet objects
        BarData barData = new BarData(actualDataSet, targetDataSet);

        // Set the BarData to the BarChart
        barChart.setData(barData);


    // Customize the gap between the bar chart groups (categories)
    float barWidth = 1.4f;
    float groupSpace = 0.5f;
    float barSpace = 0.05f;
    barData.setBarWidth(barWidth);
    barChart.getXAxis().setAxisMinimum(0);
    barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * (categories.size()));
    barChart.getAxisLeft().setAxisMinimum(0);
    barChart.groupBars(0, groupSpace, barSpace);

    // Show category labels below each group of bars
    barChart.getXAxis().setGranularity(2f);

        // Customize the legend
        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setFormSize(8f);
        legend.setXEntrySpace(4f);
        legend.setYEntrySpace(0f);
        legend.setWordWrapEnabled(true);

        // Set the description text and disable the description
            Description description = new Description();
            description.setText("");
            barChart.setDescription(description);
            barChart.getDescription().setEnabled(false);

            // Set the touch events
            barChart.setTouchEnabled(true);
            barChart.setDragEnabled(true);
            barChart.setScaleEnabled(true);
            barChart.setPinchZoom(false);

            // Set the background color
            barChart.setBackgroundColor(Color.WHITE);

            // Invalidate the chart to redraw it
            barChart.invalidate();

            // Animate the chart
            barChart.animateY(1000);
        }

}
