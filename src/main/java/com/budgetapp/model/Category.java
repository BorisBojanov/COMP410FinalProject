package com.budgetapp.model;
import com.budgetapp.storage.storage;

/* Category
Represents spending categories such as Food, Transport, or Subscriptions.

categoryId	                                int	    Attribute
name	                                    String	Attribute
ruleKeyword	                                String	Attribute
assignTransaction(transaction: Transaction)	void	Method
updateRule(keyword: String)	                void	Method
*/

public class Category {
    private int categoryId;
    private String name;
    private String ruleKeyword;

    // Constructor
    // when creating a new category
    public Category(String name, String ruleKeyword) {
        this.name = name;
        this.ruleKeyword = ruleKeyword;
        storage dataBase = storage.getInstance();
        this.categoryId = dataBase.insertCategory(name, ruleKeyword); // Returns int categoryId of the newly created category, or -1 on failure
    }

    // when loading an existing 
    public Category(int categoryId, String name, String ruleKeyword) {
        this.categoryId = categoryId;
        this.name = name;
        this.ruleKeyword = ruleKeyword;
    }

    // Getters and setters
    public int getCategoryId() {
        return this.categoryId;
    }
    public void setCategoryId(int id){
        this.categoryId = id;
    }

    public String getCategoryName(){
        return this.name;
    }
    public void setCategoryName(String name){
        this.name = name;
        storage.getInstance().updateCategoryName(this.categoryId, this.name);
    }

    public String getRuleKeyword(){
        return this.ruleKeyword;
    }
    public void setRuleKeyword(String word){
        this.ruleKeyword = word;
        storage.getInstance().updateCategoryRule(this.categoryId, this.ruleKeyword);
    }


    public void assignTransaction(Transaction transaction) {
        transaction.setCategory(this.name);
        System.out.println("Assigning transaction " + transaction.getTid() + " to category " + this.name);
    }

    public void updateRule(String keyword) {
        setRuleKeyword(keyword);
        System.out.println("Updated rule for category " + this.name + " to keyword: " + keyword);
    }

}