package com.budgetapp.service;

import com.budgetapp.model.Transaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FakeDataService {

    public static ObservableList<Transaction> getTransactions() {

        return FXCollections.observableArrayList(

                new Transaction("2026-05-01", "Starbucks", "Food", 6.25),
                new Transaction("2026-05-02", "Uber", "Transport", 18.40),
                new Transaction("2026-05-03", "Netflix", "Subscription", 16.99),
                new Transaction("2026-05-04", "Walmart", "Groceries", 92.31),
                new Transaction("2026-05-05", "Shell", "Transport", 55.80)

        );
    }
}
