package com.budgetapp.model;

import com.budgetapp.storage.storage;

/* Transaction
Stores transaction details such as date, merchant, amount, currency, and category.

transactionId	                    int	Attribute
date	                            Date	Attribute
merchant	                        String	Attribute
amount	                            double	Attribute
currency	                        String	Attribute
sourceEmailId	                    String	Attribute
editTransaction()	                void	Method
deleteTransaction()	                void	Method
updateCategory(category: Category)	void	Method

Model-View-Controller (MVC) Flow for editTransaction():
Caller
  → transaction.editTransaction()       // model method — owns the object's fields
      → storage.getInstance()           // get the shared DB connection
          → updateTransaction(tid, ...) // SQL UPDATE via PreparedStatement

*/
public class Transaction {
    private int tid;
    private String date;
    private String merchant;
    private Double amount;
    private String category;
    private int messageMid;
    private String currency;
    private String sourceEmailId;


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


    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSourceEmailId() {
        return sourceEmailId;
    }
    public void setSourceEmailId(String sourceEmailId) {
        this.sourceEmailId = sourceEmailId;
    }


    public void editTransaction() {
        storage db = storage.getInstance();
        boolean success = db.updateTransaction(this.tid, this.amount, this.date, this.merchant, this.category);
        if (success) {
            System.out.println("Transaction " + this.tid + " updated successfully.");
        } else {
            System.out.println("Failed to update tid= " + this.tid);
        }
    }

    public void deleteTransaction() {
        storage db = storage.getInstance();
        boolean success = db.deleteTransaction(this.tid);
        if (success){
            System.out.println("Deleting transaction " + this.tid);
        } else {
            System.out.println("Failed to delete tid= " + this.tid);
        }
    }

    public void updateCategory(Category category) {
        this.category = category.getCategoryName();
        System.out.println("Updated category for transaction " + this.tid + " to " + this.category);
    }
}
