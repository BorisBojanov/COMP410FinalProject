package com.budgetapp.parser;
/**
 * 
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
/* TransactionParser
Extracts transaction information from email content.

parserId	                    int	Attribute
regexPattern	                String	Attribute
parseEmail(email: EmailMessage)	Transaction	Method
extractDate(body: String)	    Date	Method
extractMerchant(body: String)	String	Method
extractAmount(body: String)	    double	Method
extractCurrency()               String	Method (option)
detectSubscription()          boolean	Method (option)
*/
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.budgetapp.storage.storage;
import com.budgetapp.model.Category;


public class parser {

    private Pattern amount = Pattern.compile("\\$(\\d+\\.\\d{2})");
    private Pattern date = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
    private Pattern merchant = Pattern.compile("at (.+?) on");
    private final List<Category> categories;

    public parser() {
        this.categories = storage.getInstance().getCategories();
    }

    // A simple inner class to carry the extracted fields between the two methods
    public static class ParsedTransaction {
        public double amount;
        public String date;
        public String merchant;
        public String category; // hardcode "Uncategorized" for now
    }
    /** extractTransaction
     * This method takes the raw email body as input and uses regex patterns to extract the amount, date, and merchant information.
     * 
     * @param body
     * @return
    */
    public ParsedTransaction extractTransaction(String body) {
        // Extract structured data from the email body
        // Matcher for each pattern against the body
        System.out.println("Extracting transaction data from email body:\n" + body + "\n---");

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
        parsedTransaction.category = "Uncategorized"; // default before loop

        for (Category category : this.categories) {
            String keyword = category.getRuleKeyword().toLowerCase();

            if (body.toLowerCase().contains( keyword )) {
                parsedTransaction.category = category.getCategoryName();
                // System.out.println("Matched category: " + category.getCategoryName() + " for keyword: " + category.getRuleKeyword());
                break; // Stop checking after the first match

            } else if (merchantStr.toLowerCase().contains( keyword)){ //also check merchantStr in addition to body
                parsedTransaction.category = category.getCategoryName();
                // System.out.println("Matched category: " + category.getCategoryName() + " for keyword: " + category.getRuleKeyword());
                break; // Stop checking after the first match   
            }
        }
        
        return parsedTransaction;
    }

    /** processEmail
     * This method takes the email's messageId, dateReceived, subject, sender, and body as parameters.
     * @param messageId
     * @param dateReceived
     * @param subject
     * @param sender
     * @param body
     * @return
    */
    public boolean processEmail(String messageId, String dateReceived,String subject, String sender, String body) {
        storage db = storage.getInstance();
        if (db.checkDuplicateMessage(messageId)) {
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

    /** extractDate
     * Finds the transaction date in the email body.
     * @param body
     * @return
    */
    public ParsedTransaction extractDate(String body) {
        Matcher dateMatcher = date.matcher(body);
        String dateStr = null;

        if (dateMatcher.find()){
            dateStr = dateMatcher.group(1);
            System.out.println("Extracted date: " + dateStr);
        } else {
            System.out.println("No date found in the email body.");
            return null; // Return null if date is missing
        }

        ParsedTransaction parsedTransaction = new ParsedTransaction(); // Create a new instance of the ParsedTransaction class
        parsedTransaction.date = dateStr;

        return parsedTransaction;
    }

    /** extractMerchant
     * Finds the merchant name in the email body.
     * @param body
     * @return
    */
    public ParsedTransaction extractMerchant(String body) {
        Matcher merchantMatcher = merchant.matcher(body);
        String merchantStr = null;

        if (merchantMatcher.find()){
            merchantStr = merchantMatcher.group(1);
            System.out.println("Extracted merchant: " + merchantStr);
        } else {
            System.out.println("No merchant found in the email body.");
            return null; // Return null if merchant is missing
        }

        ParsedTransaction parsedTransaction = new ParsedTransaction(); // Create a new instance of the ParsedTransaction class
        parsedTransaction.merchant = merchantStr;

        return parsedTransaction;
    }

    /** extractAmount
     * Finds the transaction amount in the email body.
     * @param body
     * @return
    */
    public ParsedTransaction extractAmount(String body) {
        Matcher amountMatcher = amount.matcher(body);
        String amountStr = null;

        if (amountMatcher.find()){
            amountStr = amountMatcher.group(1);
            System.out.println("Extracted amount: " + amountStr);
        } else {
            System.out.println("No amount found in the email body.");
            return null; // Return null if amount is missing
        }

        ParsedTransaction parsedTransaction = new ParsedTransaction(); // Create a new instance of the ParsedTransaction class
        parsedTransaction.amount = Double.parseDouble(amountStr);

        return parsedTransaction;
    }





}





