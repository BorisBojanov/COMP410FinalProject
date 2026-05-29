package com.budgetapp.storage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StorageTest {

    @Test
    void addManualTransactionStoresTransaction() {
        storage db = storage.getInstance();
        boolean inserted = db.addManualTransaction(9.99, "2026-05-10", "Test Merchant", "Shopping");

        assertTrue(inserted);
        assertTrue(db.getTransactions().stream().anyMatch(t ->
                t.getMerchant().equals("Test Merchant") && t.getAmount() == 9.99
        ));
    }

    @Test
    void budgetCanBeSavedAndFound() {
        storage db = storage.getInstance();

        assertTrue(db.upsertBudget("TestBudget", 123.45));

        assertTrue(db.getBudgets().stream().anyMatch(b ->
                b.getCategory().equals("TestBudget") && b.getAmount() == 123.45
        ));

        db.deleteBudget("TestBudget");
    }
}