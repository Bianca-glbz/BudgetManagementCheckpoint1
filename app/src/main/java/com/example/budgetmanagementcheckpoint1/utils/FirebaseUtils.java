package com.example.budgetmanagementcheckpoint1.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUtils {

    private static final String TAG = "FirebaseUtils";
    private static final String COLLECTION_NAME = "transactions";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    public static ArrayList<StatementTransaction> getTransactionsFrom(String month, String year, Map<String, Map<String, ArrayList<StatementTransaction>>> transactionsMap){
        Log.i(TAG,"requested transactions for : "+month +" "+ year);
        Log.i(TAG, "requested from data: "+ transactionsMap.toString());

        if(transactionsMap.get(year) == null){
            return null;
        }

        return transactionsMap.get(year).get(month);
    }

    public static void getTransactions(OnSuccessListener<Map<String, Map<String, ArrayList<StatementTransaction>>>> listener) {
        String userId = auth.getUid();
        Log.i(TAG,"TESTING TO SEE IF IT WORKS: "+ userId);

        Map<String, Map<String, ArrayList<StatementTransaction>>> transactionsMap = new HashMap<>();

        db.collection(COLLECTION_NAME).document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.i(TAG,"TESTING TO SEE IF IT WORKS: ");
                Map<String, Object> data = documentSnapshot.getData();
                Log.i(TAG,"Firebase DATA: "+ data.toString());
                // {2023: {}, 2024: {} ...}

                for(Map.Entry<String, Object> yearData: data.entrySet()){
                    String currentYear = yearData.getKey();
                    Map<String, List<String>> allMonthsData = (Map<String, List<String>>) yearData.getValue();
                    // {january: [], feg: [], ...}

                    Log.i(TAG,"Allmonths DATA: "+ allMonthsData.toString());

                    Map<String, ArrayList<StatementTransaction>> currentYearTransactions = new HashMap<>();

                    for(Map.Entry<String, List<String>> monthData: allMonthsData.entrySet()) {
                        String currentMonth = monthData.getKey();
                        ArrayList<StatementTransaction> currentMonthTransactions = new ArrayList<>();
                        List<String> monthTransactions = monthData.getValue();

                        for(int i =0; i<monthTransactions.size(); i++){
                            String transactionCSV = monthTransactions.get(i);
                            StatementTransaction transactionObj = StatementTransaction.parseCSVstring(transactionCSV);
                            currentMonthTransactions.add(transactionObj);
                        }

                        currentYearTransactions.put(currentMonth, currentMonthTransactions);
                    }

                    transactionsMap.put(currentYear, currentYearTransactions);
                }

                listener.onSuccess(transactionsMap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.toString());
                listener.onSuccess(null);
            }
        });
    }


    public static void addTransaction(int month, int year, StatementTransaction transaction) {
        CollectionReference colRef = db.collection(COLLECTION_NAME).document(auth.getUid()).collection(Integer.toString(year)).document(Integer.toString(month)).collection("transactions");
        colRef.add(transaction.getCSVstring())
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Transaction added"))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding transaction", e));
    }

    public static void addTransactions(String month, String year, List<StatementTransaction> transactions) {

        DocumentReference docRef = db.collection("transactions").document(auth.getUid());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> data = document.getData();
                    if(data != null && data.containsKey(year)){
                        Map<String, Object> yearData = (Map<String, Object>) data.get(year);
                        if(yearData.containsKey(month)){
                            List<String> monthData = (List<String>) yearData.get(month);
                            for(StatementTransaction transaction : transactions){
                                monthData.add(transaction.getCSVstring());
                            }
                            yearData.put(month, monthData);
                        }else{
                            List<String> monthData = new ArrayList<>();
                            for(StatementTransaction transaction : transactions){
                                monthData.add(transaction.getCSVstring());
                            }
                            yearData.put(month, monthData);
                        }
                        data.put(year, yearData);
                    }else{
                        Map<String, Object> yearData = new HashMap<>();
                        List<String> monthData = new ArrayList<>();
                        for(StatementTransaction transaction : transactions){
                            monthData.add(transaction.getCSVstring());
                        }
                        yearData.put(month, monthData);
                        data.put(year, yearData);
                    }
                    docRef.set(data);
                } else {
                    Map<String, Object> yearData = new HashMap<>();
                    List<String> monthData = new ArrayList<>();
                    for(StatementTransaction transaction : transactions){
                        monthData.add(transaction.getCSVstring());
                    }
                    yearData.put(month, monthData);
                    Map<String, Object> data = new HashMap<>();
                    data.put(year, yearData);
                    docRef.set(data);
                }
            }
        });
    }


    public static void updateTransactions(Map<String, Map<String, ArrayList<StatementTransaction>>> transactionsMap, OnSuccessListener<Void> listener) {
        String userId = auth.getUid();
        Map<String, Map<String, ArrayList<String>>> transactionsCSVMap = new HashMap<>();

        for(Map.Entry<String, Map<String, ArrayList<StatementTransaction>>> yearData: transactionsMap.entrySet()) {
            String currentYear = yearData.getKey();
            Map<String, ArrayList<String>> allMonthsCSVData = new HashMap<>();

            for(Map.Entry<String, ArrayList<StatementTransaction>> monthData: yearData.getValue().entrySet()) {
                String currentMonth = monthData.getKey();
                ArrayList<String> currentMonthCSVTransactions = new ArrayList<>();

                for(StatementTransaction transactionObj: monthData.getValue()) {
                    String transactionCSV = transactionObj.getCSVstring();
                    currentMonthCSVTransactions.add(transactionCSV);
                }

                allMonthsCSVData.put(currentMonth, currentMonthCSVTransactions);
            }

            transactionsCSVMap.put(currentYear, allMonthsCSVData);
        }

        db.collection(COLLECTION_NAME).document(userId).set(transactionsCSVMap).addOnSuccessListener(listener);
    }


    public static void deleteTransaction(int month, int year, String transactionCSV) {
        CollectionReference colRef = db.collection(COLLECTION_NAME).document(auth.getUid()).collection(Integer.toString(year)).document(Integer.toString(month)).collection("transactions");
        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getData().containsValue(transactionCSV)) {
                        document.getReference().delete();
                        break;
                    }
                }
            }
        });
    }

    public interface TransactionListener {
        void onTransactionsReceived(ArrayList<String> transactions);
    }
}

