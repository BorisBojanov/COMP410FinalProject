package com.budgetapp.model;

import com.budgetapp.storage.storage;

/* Budget
Stores monthly spending limits and spending totals for each category.

budgetId	                    int	    Attribute
monthlyLimit	                double	Attribute
amountSpent	                    double	Attribute
setLimit(limit: double)	        void	Method
updateSpending(amount: double)	void	Method
checkLimit()	                boolean	Method
*/
public class Budget {
    private int budgetId;
    private Double monthlyLimit;
    private Double amountSpent;

    // Constructor
    // when creating a new budget
    public Budget(double monthlyLimit, double amountSpent){
        this.monthlyLimit = monthlyLimit;
        this.amountSpent = amountSpent;
        storage dataBase = storage.getInstance();
        this.budgetId = dataBase.insertBudget(monthlyLimit, amountSpent); // Returns int budgetId of the newly created budget, or -1 on failure
    }

    // when loading an existing budget
    public Budget(int budgetId, double monthlyLimit, double amountSpent){
        this.budgetId = budgetId;
        this.monthlyLimit = monthlyLimit;
        this.amountSpent = amountSpent;
    }

    private void updateInDB(){
        storage dataBase = storage.getInstance();
        dataBase.updateBudget(this.budgetId, this.monthlyLimit, this.amountSpent);
    }

    public int getBudgetId() {
        return budgetId;
    }
    public void setBudgetId(int id) {
        this.budgetId = id; 
        // updateInDB();
    }


    public Double getMonthlyLimit() {
        return monthlyLimit;
    }
    public void setMonthlyLimit(Double limit) {
        this.monthlyLimit = limit;  
        updateInDB();
        System.out.println("Monthly limit set to: " + limit);
    }

    public Double getAmountSpent() {
        return amountSpent;
    }
    public void setAmountSpent(Double amount) {
        this.amountSpent = amount;
        updateInDB();
    }

    
    public void setLimit(Double limit) {
        this.monthlyLimit = limit;
        updateInDB();
        System.out.println("Monthly limit set to: " + limit);
    }

    public void updateSpending(Double amount) {
        this.amountSpent += amount;
        updateInDB();
        System.out.println("Updated amount spent: " + this.amountSpent);
    }
    
    public int checkLimitNumber(){
        Double i = this.monthlyLimit - this.amountSpent;
        int spent = this.amountSpent.compareTo(this.monthlyLimit);
        
        System.out.println("Remaining budget: " + i);

        return spent;
    }

    public boolean checkLimit() {
    boolean a = this.amountSpent > this.monthlyLimit;
    if(a){
        System.out.println("Budget limit exceeded! Amount spent: " + this.amountSpent + ", Monthly limit: " + this.monthlyLimit);
    } else {
        System.out.println("Within budget. Amount spent: " + this.amountSpent + ", Monthly limit: " + this.monthlyLimit);
    }
        return a; // true if over budget, false if within budget
    }
}
