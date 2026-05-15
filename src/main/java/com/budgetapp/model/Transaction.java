package com.budgetapp.model;

public class Transaction {
    private final String date;
    private final String merchant;
    private final String category;
    private final double amount;

    public Transaction(String date, String merchant, String category, double amount) {
        this.date = date;
        this.merchant = merchant;
        this.category = category;
        this.amount = amount;
    }

    public String getDate() { return date; }
    public String getMerchant() { return merchant; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
}
