package com.budgetapp.parser;
/**
 * The parser needs to do: 
 *  - Extract structured data from raw text (email body)
 *  - Persist the structured data into the database
 *  - Identify key information such as amount, date, merchant, category
 * 
 * The Parse needs to be able to find those same values from the email layer:
 *  messageId, dateReceived, subject, sender, body
 * 
 * 
*/

import com.budgetapp.storage.storage;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/*
What should your main parser method's signature look like? 
What parameters does it need, and what should it return?
*/

public class parser {

    private Pattern amount = Pattern.compile("\\$(\\d+\\.\\d{2})");
    private Pattern date = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
    private Pattern merchant = Pattern.compile("at (.+?) on");

    // A simple inner class to carry the extracted fields between the two methods
    public static class ParsedTransaction {
        public double amount;
        public String date;
        public String merchant;
        public String category; // hardcode "Uncategorized" for now
    }

    public ParsedTransaction extractTransaction(String body) {
        // Extract structured data from the email body
        // Matcher for each pattern against the body
        Matcher amountMatcher = amount.matcher(body);
        Matcher dateMatcher = date.matcher(body);
        Matcher merchantMatcher = merchant.matcher(body);

        String amountStr = null;
        String dateStr = null;
        String merchantStr = null;

        // Call m.find() on each, then m.group(1) to pull the value
        if (amountMatcher.find()){
            amountStr = amountMatcher.group(1);
            System.out.println("Extracted amount: " + amountStr);
        } else {
            System.out.println("No amount found in the email body.");
            return null; // Return null if amount is missing
        }

         if (dateMatcher.find()){
            dateStr = dateMatcher.group(1);
            System.out.println("Extracted date: " + dateStr);
        } else {
            System.out.println("No date found in the email body.");
            return null; // Return null if date is missing
        }

         if (merchantMatcher.find()){
            merchantStr = merchantMatcher.group(1);
            System.out.println("Extracted merchant: " + merchantStr);
        } else {
            System.out.println("No merchant found in the email body.");
            return null; // Return null if merchant is missing
        }
        // return a ParsedTransaction with the extracted values
        //       (or null if any required field is missing)

        ParsedTransaction parsedTransaction = new ParsedTransaction(); // Create a new instance of the ParsedTransaction class

        parsedTransaction.amount = Double.parseDouble(amountStr);
        parsedTransaction.date = dateStr;
        parsedTransaction.merchant = merchantStr;
        parsedTransaction.category = "Uncategorized";

        return parsedTransaction;
    }

    public boolean processEmail(String messageId, String dateReceived,String subject, String sender, String body) {
        // Call checkDouplicateMessages() on the storage instance first
        //       if it's a duplicate, return false immediately

        storage db = storage.getInstance();
            if (db.checkDouplicateMessages(messageId)) {
                System.out.println("Duplicate message detected. Skipping processing.");
                return false; // Skip processing if it's a duplicate
            }
    
        // Call extractTransaction(body)
        // if extraction returned null, return false
        // Extract structured data from the email body
        ParsedTransaction parsedTransaction = extractTransaction(body);
        if (parsedTransaction == null) {
            System.out.println("Failed to extract transaction data. Skipping processing.");
            return false; // Skip if extraction failed
        }        

        // Call storage.insertMessage(...) — what does it return?
        int mid = db.insertMessage(messageId, dateReceived, subject, sender, body);
        if (mid == -1) {
            System.out.println("Failed to insert message into the database. Skipping processing.");
            return false; // Skip if insertion failed
        }

        // Call storage.insertTransaction(...) using that return value
        boolean transactionInserted = db.insertTransaction(parsedTransaction.amount, parsedTransaction.date, parsedTransaction.merchant, parsedTransaction.category, mid);
        if (!transactionInserted) {
            System.out.println("Failed to insert transaction into the database.");
            return false; // Skip if transaction insertion failed
        }
        
        return true; // return true if everything succeeded
    }
}





