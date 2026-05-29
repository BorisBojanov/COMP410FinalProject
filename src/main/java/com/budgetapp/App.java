package com.budgetapp;

import com.budgetapp.service.EmailImportService;

public class App {
    public static void main(String[] args) {
        EmailImportService.ImportResult result = new EmailImportService().importTransactionsFromEmail();
        System.out.println(result.getMessage());
    }
}
