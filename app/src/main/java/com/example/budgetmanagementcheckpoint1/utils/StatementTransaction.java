package com.example.budgetmanagementcheckpoint1.utils;

public class StatementTransaction {

    public static final int DEBIT = 1;
    public static final int CREDIT = 2;

    private String account, date, description;
    private float debitAmount, creditAmount, balance;
    private int transactionType;
    private int year;
    private String category;

    public StatementTransaction(String account, String date, String description, float amount, float balance, int transactionType, int year) {
        this.account = account;
        this.date = date;
        this.description = description;
        this.balance = balance;
        this.transactionType = transactionType;
        this.category = "unknown";
        this.year = year;

        if(transactionType == DEBIT){
            this.debitAmount = amount;
            this.creditAmount = 0;
        } else if (transactionType == CREDIT){
            this.debitAmount = 0;
            this.creditAmount = amount;
        }
    }

    public StatementTransaction(String account, String date, String description, float amount, float balance, int transactionType, String category, int year) {
        this.account = account;
        this.date = date;
        this.description = description;
        this.balance = balance;
        this.transactionType = transactionType;
        this.category = category;
        this.year = year;

        if(transactionType == DEBIT){
            this.debitAmount = amount;
            this.creditAmount = 0;
        } else if (transactionType == CREDIT){
            this.debitAmount = 0;
            this.creditAmount = amount;
        }
    }


    public String getCSVstring(){
        return "" + getAccount() + ","+getDate()+","+getDescription()+","+getDebitAmount()+','+getCreditAmount()+","+getBalance()+","+getTransactionType()+","+getCategory() + ","+getYear();
    }

    public static StatementTransaction parseCSVstring(String str){
        String[] parts = str.split(",");
        int type = Integer.parseInt(parts[6]);
        if(type == DEBIT){
            return new StatementTransaction(parts[0],parts[1],parts[2],Float.parseFloat(parts[3]),Float.parseFloat(parts[5]),DEBIT,parts[7],Integer.parseInt(parts[8]) );
        } else if (type == CREDIT){
            return new StatementTransaction(parts[0],parts[1],parts[2],Float.parseFloat(parts[4]),Float.parseFloat(parts[5]),CREDIT,parts[7], Integer.parseInt(parts[8]) );
        }
        return null;
    }

    

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(float debitAmount) {
        this.debitAmount = debitAmount;
    }

    public float getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(float creditAmount) {
        this.creditAmount = creditAmount;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }
}
