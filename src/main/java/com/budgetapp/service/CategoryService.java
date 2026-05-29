package com.budgetapp.service;

public class CategoryService {

    public static String categorize(String merchant, double amount) {
        if (merchant == null || merchant.isBlank()) {
            return categorizeByAmount(amount);
        }

        String m = merchant.toLowerCase();

        if (containsAny(m, "starbucks", "tim hortons", "mcdonald", "restaurant", "cafe", "coffee", "pizza")) {
            return "Food";
        }
        if (containsAny(m, "uber", "lyft", "transit", "bus", "train", "gas", "shell", "petro", "esso")) {
            return "Transport";
        }
        if (containsAny(m, "netflix", "spotify", "prime", "disney", "subscription", "apple.com", "google")) {
            return "Subscription";
        }
        if (containsAny(m, "walmart", "superstore", "safeway", "costco", "grocery", "sobeys")) {
            return "Groceries";
        }
        if (containsAny(m, "amazon", "best buy", "shop", "store", "mall")) {
            return "Shopping";
        }
        if (containsAny(m, "cineplex", "movie", "theatre", "game", "entertainment")) {
            return "Entertainment";
        }
        if (containsAny(m, "air canada", "westjet", "hotel", "airbnb", "travel")) {
            return "Travel";
        }
        if (containsAny(m, "pharmacy", "clinic", "doctor", "health", "dental")) {
            return "Health";
        }

        return categorizeByAmount(amount);
    }

    private static String categorizeByAmount(double amount) {
        if (amount <= 25) {
            return "Food";
        }
        if (amount <= 80) {
            return "Shopping";
        }
        if (amount <= 200) {
            return "Groceries";
        }
        return "Uncategorized";
    }

    private static boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
