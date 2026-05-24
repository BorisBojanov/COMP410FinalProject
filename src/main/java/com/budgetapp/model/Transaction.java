package com.budgetapp.model;

public class Transaction {

    private int tid;
    private int messageId;

    private final String date;
    private final String merchant;
    private final String category;
    private final double amount;

    public Transaction(
            int tid,
            double amount,
            String date,
            String merchant,
            String category,
            int messageId
    ) {
        this.tid = tid;
        this.amount = amount;
        this.date = date;
        this.merchant = merchant;
        this.category = category;
        this.messageId = messageId;
    }

    public int getTid() {
        return tid;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getDate() {
        return date;
    }

    public String getMerchant() {
        return merchant;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}