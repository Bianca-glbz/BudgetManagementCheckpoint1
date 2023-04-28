package com.example.budgetmanagementcheckpoint1.fragments;

import static com.example.budgetmanagementcheckpoint1.utils.DateList.months;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetmanagementcheckpoint1.R;
import com.example.budgetmanagementcheckpoint1.utils.Categories;
import com.example.budgetmanagementcheckpoint1.utils.DateList;
import com.example.budgetmanagementcheckpoint1.utils.FirebaseUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BudgetPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetPlanFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ArrayList<Double> targets; // The list of target expenditures for each category

    // The view elements
    private TableLayout tableLayout;
    private Button saveButton;
    String selectedYear, selectedMonth;
    BarChart barChart;
    TableLayout legendTable;


    public BudgetPlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BudgetPlanFragment newInstance(String param1, String param2) {
        BudgetPlanFragment fragment = new BudgetPlanFragment();
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
        return inflater.inflate(R.layout.fragment_budget, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initSpinners(view);
        barChart = view.findViewById(R.id.barchart);
        legendTable = view.findViewById(R.id.legendTable);

        // Initialize the list of targets with default values of 0.0
        targets = new ArrayList<>();
        for (int i = 0; i < Categories.list.length; i++) {
            targets.add(0.0);
        }

        // Get a reference to the TableLayout and create a TableRow for each category
        tableLayout = view.findViewById(R.id.tableLayout);
        tableLayout.setGravity(Gravity.CENTER); // Center the table in the layout

        for (int i = 0; i <  Categories.list.length; i++) {
            TableRow tableRow = new TableRow(getActivity());

            // Create a TextView for the category name and add it to the TableRow
            TextView textView = new TextView(getActivity());
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setText( Categories.list[i]);
            tableRow.addView(textView);

            // Create an EditText for the target expenditure and add it to the TableRow
            EditText editText = new EditText(getActivity());
            editText.setText(targets.get(i).toString());
            editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); // Only allow numbers with decimal places
            editText.setGravity(Gravity.CENTER);
            tableRow.addView(editText);

            // Add the TableRow to the TableLayout
            tableLayout.addView(tableRow);
        }

        // Get a reference to the Save button and set its OnClickListener
        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                 // Update the target expenditures with the values entered in the EditText views
        HashMap<String, Double> targets = new HashMap<>();
        boolean isAnyInputEmpty = false;
        for (int i = 0; i <  Categories.list.length; i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i+1);
            View childView = tableRow.getChildAt(1);
            
            // print instance of childview 
            Log.d("childview", childView.getClass().getName());



            if (childView instanceof EditText) {
                EditText editText = (EditText) childView;
                String input = editText.getText().toString();
                if (input.isEmpty()) {
                    isAnyInputEmpty = true;
                    Toast.makeText(getActivity(), "Please fill in all inputs", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    targets.put(Categories.list[i], Double.parseDouble(input));
                }
            }
        }

        if (!isAnyInputEmpty) {
            // Do something with the updated target expenditures (e.g. save them to a database)
            // targets HashMap can be used here
            Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
            FirebaseUtils.setBudgetTarget(selectedMonth,selectedYear, targets);
            // log the targets to the console
            for (Map.Entry<String, Double> entry : targets.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                Log.d("TEST // ", key + " : " + value);
            }
        }

            }
        });


    }

    private void initSpinners(View view) {
        Spinner csvMonthSpinner = view.findViewById(R.id.yearSpinner);
        Spinner csvYearSpinner = view.findViewById(R.id.monthSpinner);

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
                updateData();
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
                updateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void updateData () {
        
        FirebaseUtils.getBudgetTarget(selectedMonth, selectedYear, new OnSuccessListener<Map<String, Double>>() {
            @Override
            public void onSuccess(Map<String, Double> budgetTarget) {
                if (budgetTarget != null) {
                    for (int i = 0; i < Categories.list.length; i++) {
                        TableRow tableRow = (TableRow) tableLayout.getChildAt(i+1);
                        View childView = tableRow.getChildAt(1);
                        if (childView instanceof EditText) {
                            EditText editText = (EditText) childView;
                            editText.setText(budgetTarget.get(Categories.list[i]).toString());
                        }
                    }

                    drawChart(budgetTarget);
                }else{
                    Toast.makeText(getActivity(), "No saved data found for selected month & year", Toast.LENGTH_SHORT).show();
                    barChart.setVisibility(View.GONE);
                    // set default values
                    for (int i = 0; i < Categories.list.length; i++) {
                        TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
                        View childView = tableRow.getChildAt(1);
                        if (childView instanceof EditText) {
                            EditText editText = (EditText) childView;
                            editText.setText("0.0");
                        }
                    }




                }
            }
        });


    }

   private void drawChart(Map<String, Double> categoryData) {
    ArrayList<BarEntry> barEntries = new ArrayList<>();

    int i = 0;
    for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
        barEntries.add(new BarEntry(i++, entry.getValue().floatValue()));
    }

    // Create a BarDataSet from the BarEntry ArrayList
    BarDataSet barDataSet = new BarDataSet(barEntries, "Category Expenditure");

    // Set colors for each bar
    int[] colors = ColorTemplate.JOYFUL_COLORS;
    for (i = 0; i < barEntries.size(); i++) {
        barDataSet.addColor(colors[i % colors.length]);
    }

    // Create a BarData object from the BarDataSet and set it to the BarChart
    BarData barData = new BarData(barDataSet);
    barChart.setData(barData);

    // Set additional settings for the BarChart
    barChart.getDescription().setEnabled(false);
    barChart.setDrawValueAboveBar(true);
    barChart.setPinchZoom(false);
    barChart.setDrawBarShadow(false);
    barChart.setDrawGridBackground(false);
    barChart.getAxisLeft().setAxisMinimum(0f);
    barChart.getAxisLeft().setGranularity(1f);
    barChart.getXAxis().setGranularity(1f);
    barChart.getXAxis().setDrawGridLines(false);
    barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    barChart.animateY(1000);

    // Set the visibility of the BarChart to VISIBLE
    barChart.setVisibility(View.VISIBLE);

    // Add a Legend to the BarChart
    Legend legend = barChart.getLegend();
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

    // Set the label text for each LegendEntry based on the categoryData HashMap
    ArrayList<LegendEntry> legendEntries = new ArrayList<>();
    i = 0;
    for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
        legendEntries.add(new LegendEntry(entry.getKey(), Legend.LegendForm.SQUARE, 10f, 2f, null, colors[i % colors.length]));
        i++;
    }
    legend.setCustom(legendEntries);
}


}