package com.budgetapp.model;

public class Transaction {
    private int tid;
    private Double amount;
    private String date;
    private String merchant;
    private String category;
    private int messageMid;


    public Transaction(int tid, double amount, String date, String merchant, String category, int messageMid) {
        this.tid = tid;
        this.amount = amount;
        this.date = date;
        this.merchant = merchant;
        this.category = category;
        this.messageMid = messageMid;
    }

    // Getters and setters for the fields
    public int getTid() {
        return tid;
    }
    public void setTid(int tid) {
        this.tid = tid;
    }

    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getMerchant() {
        return merchant;
    }
    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public int getmessageMid() {
        return messageMid;
    }
    public void setmessageMid(int messageMid) {
        this.messageMid = messageMid;
    }


}
