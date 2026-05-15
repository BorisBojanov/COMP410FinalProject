package com.budgetapp.dashboard;

import com.budgetapp.model.Transaction;
import com.budgetapp.service.FakeDataService;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class DashboardView {

    private ObservableList<Transaction> getFilteredTransactions(String month, String year, String category) {
        return FakeDataService.getTransactions().filtered(transaction -> {
            boolean matchesYear = transaction.getDate().startsWith(year + "-");

            boolean matchesMonth = month.equals("All") ||
                    transaction.getDate().startsWith(year + "-" + getMonthNumber(month));

            boolean matchesCategory = category.equals("All") ||
                    transaction.getCategory().equals(category);

            return matchesYear && matchesMonth && matchesCategory;
        });
    }

    private String getMonthNumber(String month) {
        return switch (month) {
            case "January" -> "01";
            case "February" -> "02";
            case "March" -> "03";
            case "April" -> "04";
            case "May" -> "05";
            case "June" -> "06";
            case "July" -> "07";
            case "August" -> "08";
            case "September" -> "09";
            case "October" -> "10";
            case "November" -> "11";
            case "December" -> "12";
            default -> "05";
        };
    }

    public VBox getView() {
        Label title = new Label("BudgetApp Dashboard");
        title.getStyleClass().add("title");

        ComboBox<String> yearFilter = new ComboBox<>();
        yearFilter.getItems().addAll("2026");
        yearFilter.setValue("2026");

        ComboBox<String> monthFilter = new ComboBox<>();
        monthFilter.getItems().addAll(
                "All",
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );
        monthFilter.setValue("All");

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll(
                "All",
                "Food",
                "Transport",
                "Subscription",
                "Groceries",
                "Shopping",
                "Entertainment",
                "Travel",
                "Technology",
                "Health",
                "Pets",
                "Books"
        );
        categoryFilter.setValue("All");

        HBox filters = new HBox(10);
        filters.getChildren().addAll(
                new Label("Year:"), yearFilter,
                new Label("Month:"), monthFilter,
                new Label("Category:"), categoryFilter
        );

        HBox summaryCards = createSummaryCards(
                monthFilter.getValue(),
                yearFilter.getValue(),
                categoryFilter.getValue()
        );

        VBox categoryPanel = createCategoryPanel(
                monthFilter.getValue(),
                yearFilter.getValue(),
                categoryFilter.getValue()
        );

        TableView<Transaction> table = createTransactionTable(
                monthFilter.getValue(),
                yearFilter.getValue(),
                categoryFilter.getValue()
        );

        BarChart<String, Number> monthlyChart = createMonthlySpendingChart(
                yearFilter.getValue(),
                categoryFilter.getValue()
        );

        VBox centerPanel = new VBox(15);
        centerPanel.getChildren().addAll(table, monthlyChart);

        BorderPane dashboard = new BorderPane();
        dashboard.setLeft(categoryPanel);
        dashboard.setCenter(centerPanel);

        Runnable refreshDashboard = () -> {
            String selectedMonth = monthFilter.getValue();
            String selectedYear = yearFilter.getValue();
            String selectedCategory = categoryFilter.getValue();

            table.setItems(getFilteredTransactions(selectedMonth, selectedYear, selectedCategory));

            summaryCards.getChildren().clear();
            summaryCards.getChildren().addAll(
                    createSummaryCards(selectedMonth, selectedYear, selectedCategory).getChildren()
            );

            dashboard.setLeft(createCategoryPanel(selectedMonth, selectedYear, selectedCategory));
            centerPanel.getChildren().set(1, createMonthlySpendingChart(selectedYear, selectedCategory));
        };

        monthFilter.setOnAction(event -> refreshDashboard.run());
        yearFilter.setOnAction(event -> refreshDashboard.run());
        categoryFilter.setOnAction(event -> refreshDashboard.run());

        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 20;");
        root.getChildren().addAll(title, filters, summaryCards, dashboard);

        return root;
    }

    private HBox createSummaryCards(String selectedMonth, String selectedYear, String selectedCategory) {
        ObservableList<Transaction> transactions =
                getFilteredTransactions(selectedMonth, selectedYear, selectedCategory);

        double totalSpending = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        double budget = selectedMonth.equals("All") ? 18000.00 : 1500.00;
        double remainingBudget = budget - totalSpending;

        HBox summaryCards = new HBox(15);
        summaryCards.getChildren().addAll(
                createCard("Spending", String.format("$%.2f", totalSpending)),
                createCard("Budget Remaining", String.format("$%.2f", remainingBudget)),
                createCard("Transactions", String.valueOf(transactions.size()))
        );

        return summaryCards;
    }

    private VBox createCard(String label, String value) {
        Label labelText = new Label(label);
        Label valueText = new Label(value);
        valueText.getStyleClass().add("card-value");

        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.getChildren().addAll(labelText, valueText);
        return card;
    }

    private VBox createCategoryPanel(String selectedMonth, String selectedYear, String selectedCategory) {
        ObservableList<Transaction> transactions =
                getFilteredTransactions(selectedMonth, selectedYear, selectedCategory);

        PieChart pieChart = new PieChart();
        VBox categoryPanel = new VBox(15);
        categoryPanel.getStyleClass().add("sidebar");
        categoryPanel.getChildren().add(new Label("Categories"));

        String[] categories = {
                "Food", "Transport", "Subscription", "Groceries", "Shopping",
                "Entertainment", "Travel", "Technology", "Health", "Pets", "Books"
        };

        for (String category : categories) {
            double total = getCategoryTotal(transactions, category);

            if (total > 0) {
                categoryPanel.getChildren().add(
                        new Label(String.format("%s: $%.2f", category, total))
                );
                pieChart.getData().add(new PieChart.Data(category, total));
            }
        }

        pieChart.setTitle("Spending by Category");
        pieChart.setPrefWidth(300);
        pieChart.setPrefHeight(300);

        categoryPanel.getChildren().add(pieChart);

        return categoryPanel;
    }

    private double getCategoryTotal(ObservableList<Transaction> transactions, String category) {
        return transactions.stream()
                .filter(transaction -> transaction.getCategory().equals(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private TableView<Transaction> createTransactionTable(
            String selectedMonth,
            String selectedYear,
            String selectedCategory
    ) {
        TableView<Transaction> table = new TableView<>();

        TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, String> merchantColumn = new TableColumn<>("Merchant");
        merchantColumn.setCellValueFactory(new PropertyValueFactory<>("merchant"));

        TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Transaction, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        table.getColumns().addAll(dateColumn, merchantColumn, categoryColumn, amountColumn);
        table.setItems(getFilteredTransactions(selectedMonth, selectedYear, selectedCategory));

        return table;
    }

    private BarChart<String, Number> createMonthlySpendingChart(String selectedYear, String selectedCategory) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount Spent");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Spending Trend");

        XYChart.Series<String, Number> spendingSeries = new XYChart.Series<>();
        spendingSeries.setName(selectedCategory.equals("All") ? "All Categories" : selectedCategory);

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (int i = 1; i <= 12; i++) {
            String monthNumber = String.format("%02d", i);

            double total = FakeDataService.getTransactions().stream()
                    .filter(transaction -> transaction.getDate().startsWith(selectedYear + "-" + monthNumber))
                    .filter(transaction ->
                            selectedCategory.equals("All") ||
                                    transaction.getCategory().equals(selectedCategory)
                    )
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            spendingSeries.getData().add(new XYChart.Data<>(months[i - 1], total));
        }

        barChart.getData().add(spendingSeries);
        barChart.setPrefHeight(250);

        return barChart;
    }
}