package com.example.budgetmanagementcheckpoint1.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetmanagementcheckpoint1.R;
import com.example.budgetmanagementcheckpoint1.activities.ManageTransactionsActivity;
import com.example.budgetmanagementcheckpoint1.utils.Months;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    ArrayList<StatementTransaction> transactions;

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



        transactions = new ArrayList<>();

        getData();


    }

    public void getData(){
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("transactions").document(id);
        Map<String, List<String>> monthlyTransactions = new HashMap<>();

        // get current month
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat= new SimpleDateFormat("MM");
        Date date = new Date();
        int monthNum = Integer.parseInt(dateFormat.format(date)) -1;

        String currentMonth = Months.names[monthNum];

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

                List<String> currentMonthCSV =  monthlyTransactions.get(currentMonth);
              // Log.i("TESTING // ", currentMonthCSV.get(0));

                for(int i=0; i<currentMonthCSV.size();i++){
                    StatementTransaction transaction = StatementTransaction.parseCSVstring(currentMonthCSV.get(i));
                    transactions.add(transaction);
                }

                calculateStats();
            }
        });

    }

    public void calculateStats(){
        TextView statsText = getView().findViewById(R.id.statsTitle);
        PieChart chart = getView().findViewById(R.id.piechart);

        float totalSpent = 0;

        for(int i=0; i<transactions.size();i++){
            StatementTransaction t = transactions.get(i);
            if(t.getTransactionType() == StatementTransaction.CREDIT){
                totalSpent+= t.getCreditAmount();
            }
        }


        // calculation to sum the total credit amount and calculate percentage for each category
        Map<String, Float> creditByCategory = new HashMap<>();
        float totalCredit = 0f;
        for (StatementTransaction transaction : transactions) {
            if (transaction.getTransactionType() == StatementTransaction.CREDIT) {
                String category = transaction.getCategory();
                Float credit = transaction.getCreditAmount();
                if (credit != null) {
                    Float creditInCategory = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        creditInCategory = creditByCategory.getOrDefault(category, 0f);
                    }
                    creditByCategory.put(category, creditInCategory + credit);
                    totalCredit += credit;
                }
            }
        }

// Create PieEntries for each category and calculate their percentages
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : creditByCategory.entrySet()) {
            String category = entry.getKey();
            Float credit = entry.getValue();
            float percentage = credit / totalCredit * 100f;
            String label = String.format("%s", category);
            entries.add(new PieEntry(percentage, label));
        }

// Set up the PieDataSet and PieData
        PieDataSet dataSet = new PieDataSet(entries, "Credit Distribution by Category");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(16f);
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
        chart.getLegend().setEnabled(true);
        chart.getLegend().setTextSize(14f);
        chart.animateY(1000, Easing.EaseInOutCubic);

        float finalTotalCredit = totalCredit;
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                // Show the label and value of the selected pie slice in a Toast message
                if (entry instanceof PieEntry) {
                    PieEntry pieEntry = (PieEntry) entry;
                    String label = pieEntry.getLabel();
                    float value = pieEntry.getValue();
                    float percentage = value / finalTotalCredit * 100f;
                    String message = String.format("%s: €%.2f (%.1f%%)", label, value, percentage*100);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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