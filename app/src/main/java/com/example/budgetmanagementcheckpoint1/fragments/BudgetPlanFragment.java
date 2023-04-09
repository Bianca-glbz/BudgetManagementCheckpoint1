package com.example.budgetmanagementcheckpoint1.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.budgetmanagementcheckpoint1.R;
import com.example.budgetmanagementcheckpoint1.utils.Categories;

import java.util.ArrayList;

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
                for (int i = 0; i <  Categories.list.length; i++) {
                    TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
                    EditText editText = (EditText) tableRow.getChildAt(1);
                    targets.set(i, Double.parseDouble(editText.getText().toString()));
                }

                // Do something with the updated target expenditures (e.g. save them to a database)
            }
        });

    }
}