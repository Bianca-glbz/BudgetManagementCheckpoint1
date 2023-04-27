package com.example.budgetmanagementcheckpoint1.fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.budgetmanagementcheckpoint1.R;
import com.example.budgetmanagementcheckpoint1.utils.DateList;
import com.example.budgetmanagementcheckpoint1.utils.StatementTransaction;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
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

    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                          Bundle savedInstanceState) {
    //     // Inflate the layout for this fragment
    //     return inflater.inflate(R.layout.fragment_settings, container, false);
    // }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        monthSpinner = view.findViewById(R.id.month_spinner);
        yearSpinner = view.findViewById(R.id.year_spinner);
        tableLayout = view.findViewById(R.id.budget_comparison_table);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, DateList.months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, DateList.years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

//        Button submitButton = view.findViewById(R.id.);
//        submitButton.setOnClickListener(v -> {
//            selectedMonth = monthSpinner.getSelectedItem().toString();
//            selectedYear = yearSpinner.getSelectedItem().toString();
//
//            getBudgetTarget(selectedMonth, selectedYear, new OnSuccessListener<Map<String, Double>>() {
//                @Override
//                public void onSuccess(Map<String, Double> map) {
//                    budgetTargets = map;
//                    updateTable();
//                }
//            });
//
//            getTransactions(new OnSuccessListener<Map<String, Map<String, ArrayList<StatementTransaction>>>>() {
//                @Override
//                public void onSuccess(Map<String, Map<String, ArrayList<StatementTransaction>>> map) {
//                    transactions = map;
//                    updateTable();
//                }
//            });
//        });

        return view;
    }

    private void updateTable() {
        if (budgetTargets == null || transactions == null) {
            return;
        }

        tableLayout.removeAllViews();

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
            TextView progressView = new TextView(getContext());

            categoryView.setText(category);
            actualView.setText(String.format(Locale.getDefault(), "%.2f", actualAmount));
            targetView.setText(String.format(Locale.getDefault(), "%.2f", targetAmount));

            double progress = actualAmount / targetAmount * 100;
            progressView.setText(String.format(Locale.getDefault(), "%.2f%%", progress));
            if (progress >= 100)
                progressView.setTextColor(ContextCompat.getColor(getContext(), com.google.android.material.R.color.design_default_color_on_primary));
            else {
                progressView.setTextColor(ContextCompat.getColor(getContext(), R.color.purple_500));
            }

            row.addView(categoryView);
            row.addView(actualView);
            row.addView(targetView);
            row.addView(progressView);
            tableLayout.addView(row);
        }
    }
}
