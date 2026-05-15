package com.budgetapp.service;

import com.budgetapp.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FakeDataService {

    public static ObservableList<Transaction> getTransactions() {

        ObservableList<Transaction> transactions =
                FXCollections.observableArrayList();

        transactions.addAll(
		new Transaction("2026-01-04", "Starbucks", "Food", 5.95),
		new Transaction("2026-01-08", "Uber", "Transport", 22.40),
		new Transaction("2026-01-12", "Netflix", "Subscription", 16.99),
		new Transaction("2026-01-18", "Walmart", "Groceries", 88.25),
		new Transaction("2026-01-25", "Shell", "Transport", 62.10),

		new Transaction("2026-02-03", "Tim Hortons", "Food", 8.20),
		new Transaction("2026-02-09", "Amazon", "Shopping", 74.99),
		new Transaction("2026-02-14", "Spotify", "Subscription", 10.99),
		new Transaction("2026-02-20", "Costco", "Groceries", 156.30),
		new Transaction("2026-02-27", "Uber Eats", "Food", 31.60),

		new Transaction("2026-03-02", "McDonalds", "Food", 13.45),
		new Transaction("2026-03-07", "Petro Canada", "Transport", 71.25),
		new Transaction("2026-03-11", "YouTube Premium", "Subscription", 13.99),
		new Transaction("2026-03-19", "Best Buy", "Technology", 249.99),
		new Transaction("2026-03-24", "FreshCo", "Groceries", 97.80),

		new Transaction("2026-04-01", "Cineplex", "Entertainment", 22.50),
		new Transaction("2026-04-06", "DoorDash", "Food", 36.75),
		new Transaction("2026-04-13", "Esso", "Transport", 64.40),
		new Transaction("2026-04-21", "Winners", "Shopping", 58.10),
		new Transaction("2026-04-29", "Airbnb", "Travel", 420.00),
                
		new Transaction("2026-05-01", "Starbucks", "Food", 6.25),
                new Transaction("2026-05-01", "Uber", "Transport", 18.40),
                new Transaction("2026-05-02", "Netflix", "Subscription", 16.99),
                new Transaction("2026-05-02", "Walmart", "Groceries", 92.31),
                new Transaction("2026-05-03", "Shell", "Transport", 55.80),
                new Transaction("2026-05-03", "Amazon", "Shopping", 48.76),
                new Transaction("2026-05-04", "Costco", "Groceries", 135.42),
                new Transaction("2026-05-04", "Spotify", "Subscription", 10.99),
                new Transaction("2026-05-05", "Tim Hortons", "Food", 7.85),
                new Transaction("2026-05-05", "Air Canada", "Travel", 312.40),

                new Transaction("2026-05-06", "Shoppers Drug Mart", "Health", 24.99),
                new Transaction("2026-05-06", "Cineplex", "Entertainment", 18.50),
                new Transaction("2026-05-07", "Uber Eats", "Food", 32.17),
                new Transaction("2026-05-07", "Esso", "Transport", 61.20),
                new Transaction("2026-05-08", "Best Buy", "Shopping", 89.99),
                new Transaction("2026-05-08", "Apple", "Technology", 1299.99),
                new Transaction("2026-05-09", "McDonalds", "Food", 14.20),
                new Transaction("2026-05-09", "Petro Canada", "Transport", 72.45),
                new Transaction("2026-05-10", "Steam", "Entertainment", 44.99),
                new Transaction("2026-05-10", "SkipTheDishes", "Food", 26.75),

                new Transaction("2026-05-11", "Dollarama", "Shopping", 12.00),
                new Transaction("2026-05-11", "FreshCo", "Groceries", 65.90),
                new Transaction("2026-05-12", "YouTube Premium", "Subscription", 13.99),
                new Transaction("2026-05-12", "Nike", "Shopping", 110.50),
                new Transaction("2026-05-13", "Starbucks", "Food", 8.15),
                new Transaction("2026-05-13", "Uber", "Transport", 21.40),
                new Transaction("2026-05-14", "Walmart", "Groceries", 143.62),
                new Transaction("2026-05-14", "Amazon", "Shopping", 76.89),
                new Transaction("2026-05-15", "Cactus Club", "Food", 58.40),
                new Transaction("2026-05-15", "Spotify", "Subscription", 10.99),

                new Transaction("2026-05-16", "Shell", "Transport", 69.20),
                new Transaction("2026-05-16", "Cineplex", "Entertainment", 24.50),
                new Transaction("2026-05-17", "Apple Music", "Subscription", 11.99),
                new Transaction("2026-05-17", "Save On Foods", "Groceries", 122.35),
                new Transaction("2026-05-18", "DoorDash", "Food", 33.80),
                new Transaction("2026-05-18", "Amazon", "Shopping", 215.49),
                new Transaction("2026-05-19", "PetSmart", "Pets", 42.00),
                new Transaction("2026-05-19", "Esso", "Transport", 58.99),
                new Transaction("2026-05-20", "Steam", "Entertainment", 59.99),
                new Transaction("2026-05-20", "Airbnb", "Travel", 420.00),

                new Transaction("2026-05-21", "Starbucks", "Food", 5.95),
                new Transaction("2026-05-21", "Winners", "Shopping", 64.20),
                new Transaction("2026-05-22", "Uber Eats", "Food", 29.50),
                new Transaction("2026-05-22", "Costco", "Groceries", 187.44),
                new Transaction("2026-05-23", "Netflix", "Subscription", 16.99),
                new Transaction("2026-05-23", "Petro Canada", "Transport", 77.11),
                new Transaction("2026-05-24", "Best Buy", "Technology", 349.99),
                new Transaction("2026-05-24", "Tim Hortons", "Food", 9.40),
                new Transaction("2026-05-25", "Indigo", "Books", 38.75),
                new Transaction("2026-05-25", "McDonalds", "Food", 13.20)

        );

        return transactions;
    }
}