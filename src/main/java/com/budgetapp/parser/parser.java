package com.budgetapp.parser;

import com.budgetapp.service.CategoryService;
import com.budgetapp.storage.storage;

import java.time.LocalDate;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class parser {

    private final Pattern amount = Pattern.compile("\\$\\s*(\\d+(?:,\\d{3})*(?:\\.\\d{2})?)");
    private final Pattern isoDate = Pattern.compile("(\\d{4}[-/]\\d{2}[-/]\\d{2})");
    private final Pattern merchantAtOn = Pattern.compile("(?i)\\bat\\s+(.+?)\\s+on\\b");
    private final Pattern merchantLabel = Pattern.compile("(?i)(merchant|vendor|store)\\s*[:\\-]\\s*([^\\n\\r]+)");

    public static class ParsedTransaction {
        public double amount;
        public String date;
        public String merchant;
        public String category;
    }

    public ParsedTransaction extractTransaction(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }

        Matcher amountMatcher = amount.matcher(body);
        Matcher dateMatcher = isoDate.matcher(body);
        Matcher merchantAtMatcher = merchantAtOn.matcher(body);
        Matcher merchantLabelMatcher = merchantLabel.matcher(body);

        if (!amountMatcher.find()) {
            System.out.println("No amount found in the email body.");
            return null;
        }

        String amountStr = amountMatcher.group(1).replace(",", "");

        String dateStr;
        if (dateMatcher.find()) {
            dateStr = dateMatcher.group(1).replace("/", "-");
        } else {
            dateStr = LocalDate.now().toString();
        }

        String merchantStr;
        if (merchantAtMatcher.find()) {
            merchantStr = merchantAtMatcher.group(1).trim();
        } else if (merchantLabelMatcher.find()) {
            merchantStr = merchantLabelMatcher.group(2).trim();
        } else {
            merchantStr = "Unknown Merchant";
        }

        ParsedTransaction parsedTransaction = new ParsedTransaction();
        parsedTransaction.amount = Double.parseDouble(amountStr);
        parsedTransaction.date = dateStr;
        parsedTransaction.merchant = merchantStr;
        parsedTransaction.category = CategoryService.categorize(merchantStr, parsedTransaction.amount);

        return parsedTransaction;
    }

    public boolean processEmail(String messageId, String dateReceived, String subject, String sender, String body) {
        storage db = storage.getInstance();

        if (messageId == null || messageId.isBlank()) {
            messageId = "generated-" + Math.abs((String.valueOf(dateReceived) + subject + sender + body).hashCode());
        }

        if (db.checkDouplicateMessages(messageId)) {
            System.out.println("Duplicate message detected. Skipping processing.");
            return false;
        }

        ParsedTransaction parsedTransaction = extractTransaction(body);
        if (parsedTransaction == null) {
            System.out.println("Failed to extract transaction data. Skipping processing.");
            return false;
        }

        int mid = db.insertMessage(messageId, dateReceived, subject, sender, body);
        if (mid == -1) {
            System.out.println("Failed to insert message into the database. Skipping processing.");
            return false;
        }

        return db.insertTransaction(
                parsedTransaction.amount,
                parsedTransaction.date,
                parsedTransaction.merchant,
                parsedTransaction.category,
                mid
        );
    }
}
