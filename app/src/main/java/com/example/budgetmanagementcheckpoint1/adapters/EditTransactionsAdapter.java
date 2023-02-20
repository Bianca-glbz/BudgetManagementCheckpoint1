package com.example.budgetmanagementcheckpoint1.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetmanagementcheckpoint1.R;
import com.example.budgetmanagementcheckpoint1.utils.StatementTransaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
// class which decides what happens to each item
    public class EditTransactionsAdapter extends RecyclerView.Adapter<EditTransactionsAdapter.RecipestViewHolder> {
        private List<StatementTransaction> transactions;
        private  Activity activity;

        // the view holder for each item with the title, image and like button
        public class RecipestViewHolder extends RecyclerView.ViewHolder {
            public TextView type, date, description, amount;
            public Spinner categoryDropdown;

            public RecipestViewHolder(View view) {
                super(view);
                type =view.findViewById(R.id.transactionType);
                date = view.findViewById(R.id.transactionDate);
                description = view.findViewById(R.id.transactionDescription);
                amount = view.findViewById(R.id.transactionAmount);
                categoryDropdown = view.findViewById(R.id.categorySpinner);

            }
        }

        public EditTransactionsAdapter(List<StatementTransaction> list, Activity activity) {
            this.transactions = list;
            this.activity = activity;
        }

        @Override
        public RecipestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_edit_item, parent, false);

            return new RecipestViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecipestViewHolder holder, int position) {
            // get the class at this position and fill in the view with data
            StatementTransaction transaction = transactions.get(position);

            holder.type.setText(transaction.getTransactionType() == StatementTransaction.DEBIT? "DEBIT":"CREDIT");
            holder.date.setText(transaction.getDate());
            holder.description.setText(String.format("DESCRIPTION: %s", transaction.getDescription()));
            holder.amount.setText(String.format("EUR %s", transaction.getTransactionType() == StatementTransaction.DEBIT ? transaction.getDebitAmount() : transaction.getCreditAmount()));

            String[] categories ={"unknown", "travel", "entertainment","groceries","transport"};

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (activity, android.R.layout.simple_spinner_item,
                            categories); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            holder.categoryDropdown.setAdapter(spinnerArrayAdapter);


            int currentCateogry = Arrays.asList(categories).indexOf(transaction.getCategory());

            // use current month as default selection
            holder.categoryDropdown.setSelection(currentCateogry);

            holder.categoryDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    transaction.setCategory(categories[i]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });



        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }
    }

