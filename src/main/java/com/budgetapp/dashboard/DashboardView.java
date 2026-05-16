package com.budgetapp.dashboard;

import com.budgetapp.model.Transaction;
import com.budgetapp.service.FakeDataService;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class DashboardView {

    private ObservableList<Transaction> getFilteredTransactions(String month, String year, String category, String searchText) {
        return FakeDataService.getTransactions().filtered(transaction -> {
            boolean matchesYear = transaction.getDate().startsWith(year + "-");

            boolean matchesMonth = month.equals("All") ||
                    transaction.getDate().startsWith(year + "-" + getMonthNumber(month));

            boolean matchesCategory = category.equals("All") ||
                    transaction.getCategory().equals(category);

            boolean matchesSearch = searchText == null ||
                    searchText.isBlank() ||
                    transaction.getMerchant().toLowerCase().contains(searchText.toLowerCase());

            return matchesYear && matchesMonth && matchesCategory && matchesSearch;
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

        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);

        ComboBox<String> yearFilter = new ComboBox<>();
        yearFilter.getItems().addAll("2026");
        yearFilter.setValue("2026");

        ComboBox<String> monthFilter = new ComboBox<>();
        monthFilter.getItems().addAll(
                "All", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );
        monthFilter.setValue("All");

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll(
                "All", "Food", "Transport", "Subscription", "Groceries",
                "Shopping", "Entertainment", "Travel", "Technology",
                "Health", "Pets", "Books"
        );
        categoryFilter.setValue("All");

        TextField merchantSearch = new TextField();
        merchantSearch.setPromptText("Search merchant...");

        VBox filters = new VBox(10);
        filters.getStyleClass().add("card");
        filters.setPrefWidth(240);
        filters.getChildren().addAll(
                new Label("Year:"), yearFilter,
                new Label("Month:"), monthFilter,
                new Label("Category:"), categoryFilter,
                new Label("Merchant:"), merchantSearch
        );

        VBox summaryCards = createSummaryCards(
                monthFilter.getValue(),
                yearFilter.getValue(),
                categoryFilter.getValue(),
                merchantSearch.getText()
        );

        HBox filterSummaryRow = new HBox(20);
	filterSummaryRow.setAlignment(Pos.CENTER);
        filterSummaryRow.getChildren().addAll(filters, summaryCards);

        ObservableList<Transaction> initialTransactions = getFilteredTransactions(
                monthFilter.getValue(),
                yearFilter.getValue(),
                categoryFilter.getValue(),
                merchantSearch.getText()
        );

        VBox budgetProgressPanel = createBudgetProgressPanel(initialTransactions);
        PieChart pieChart = createPieChart(initialTransactions);

        HBox budgetPieRow = new HBox(20);
        budgetPieRow.getChildren().addAll(budgetProgressPanel, pieChart);

        BarChart<String, Number> monthlyChart = createMonthlySpendingChart(
                yearFilter.getValue(),
                categoryFilter.getValue()
        );

        VBox categoriesPanel = createCategoryListPanel(initialTransactions);

        TableView<Transaction> table = createTransactionTable(
                monthFilter.getValue(),
                yearFilter.getValue(),
                categoryFilter.getValue(),
                merchantSearch.getText()
        );

        HBox categoryTableRow = new HBox(20);
        categoryTableRow.getChildren().addAll(categoriesPanel, table);

        VBox dashboard = new VBox(20);
        dashboard.getChildren().addAll(
                budgetPieRow,
                monthlyChart,
                categoryTableRow
        );

        Runnable refreshDashboard = () -> {
            String selectedMonth = monthFilter.getValue();
            String selectedYear = yearFilter.getValue();
            String selectedCategory = categoryFilter.getValue();
            String searchText = merchantSearch.getText();

            ObservableList<Transaction> filteredTransactions = getFilteredTransactions(
                    selectedMonth,
                    selectedYear,
                    selectedCategory,
                    searchText
            );

            table.setItems(filteredTransactions);

            summaryCards.getChildren().clear();
            summaryCards.getChildren().addAll(
                    createSummaryCards(
                            selectedMonth,
                            selectedYear,
                            selectedCategory,
                            searchText
                    ).getChildren()
            );

            budgetPieRow.getChildren().set(0, createBudgetProgressPanel(filteredTransactions));
            budgetPieRow.getChildren().set(1, createPieChart(filteredTransactions));
            dashboard.getChildren().set(1, createMonthlySpendingChart(selectedYear, selectedCategory));
            categoryTableRow.getChildren().set(0, createCategoryListPanel(filteredTransactions));
        };

        monthFilter.setOnAction(event -> refreshDashboard.run());
        yearFilter.setOnAction(event -> refreshDashboard.run());
        categoryFilter.setOnAction(event -> refreshDashboard.run());
        merchantSearch.textProperty().addListener((obs, oldValue, newValue) -> refreshDashboard.run());

        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 20;");
        root.getChildren().addAll(
                titleBox,
                filterSummaryRow,
                dashboard
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        return new VBox(scrollPane);
    }

    private VBox createSummaryCards(String selectedMonth, String selectedYear, String selectedCategory, String searchText) {
        ObservableList<Transaction> transactions =
                getFilteredTransactions(selectedMonth, selectedYear, selectedCategory, searchText);

        double totalSpending = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        double budget = selectedMonth.equals("All") ? 18000.00 : 1500.00;
        double remainingBudget = budget - totalSpending;

        VBox summaryCards = new VBox(15);
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
        card.setPrefWidth(240);
        card.getChildren().addAll(labelText, valueText);

        return card;
    }

    private VBox createBudgetProgressPanel(ObservableList<Transaction> transactions) {
        VBox panel = new VBox(12);
        panel.getStyleClass().add("sidebar");
        panel.setPrefWidth(320);

        panel.getChildren().addAll(
                new Label("Budget Progress"),
                createBudgetProgress("Food", getCategoryTotal(transactions, "Food"), 400.00),
                createBudgetProgress("Transport", getCategoryTotal(transactions, "Transport"), 150.00),
                createBudgetProgress("Groceries", getCategoryTotal(transactions, "Groceries"), 500.00),
                createBudgetProgress("Subscription", getCategoryTotal(transactions, "Subscription"), 100.00)
        );

        return panel;
    }

    private VBox createBudgetProgress(String category, double spent, double budget) {
        double progress = budget == 0 ? 0 : spent / budget;

        Label title = new Label(String.format("%s: $%.2f / $%.2f", category, spent, budget));

        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setPrefWidth(250);

        if (progress < 0.75) {
            progressBar.getStyleClass().add("safe");
        } else if (progress <= 1.0) {
            progressBar.getStyleClass().add("warning");
        } else {
            progressBar.getStyleClass().add("danger");
        }

        VBox box = new VBox(5);
        box.getChildren().addAll(title, progressBar);

        return box;
    }

    private PieChart createPieChart(ObservableList<Transaction> transactions) {
    PieChart pieChart = new PieChart();

    pieChart.setTitle("Spending by Category");
    pieChart.setLegendVisible(false);

    pieChart.setPrefWidth(500);
    pieChart.setPrefHeight(320);

    double grandTotal = transactions.stream()
            .mapToDouble(Transaction::getAmount)
            .sum();

    String[] categories = {
            "Food", "Transport", "Subscription", "Groceries", "Shopping",
            "Entertainment", "Travel", "Technology", "Health", "Pets", "Books"
    };

    for (String category : categories) {
        double total = getCategoryTotal(transactions, category);

        if (total > 0) {

            double percent = grandTotal == 0
                    ? 0
                    : (total / grandTotal) * 100;

            pieChart.getData().add(
                    new PieChart.Data(
                            String.format("%s %.1f%%", category, percent),
                            total
                    )
            );
        }
    }

    return pieChart;
}

    private VBox createCategoryListPanel(ObservableList<Transaction> transactions) {
        VBox categoryPanel = new VBox(12);
        categoryPanel.getStyleClass().add("sidebar");
        categoryPanel.setPrefWidth(280);

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
            }
        }

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
            String selectedCategory,
            String searchText
    ) {
        TableView<Transaction> table = new TableView<>();

        TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(130);

        TableColumn<Transaction, String> merchantColumn = new TableColumn<>("Merchant");
        merchantColumn.setCellValueFactory(new PropertyValueFactory<>("merchant"));
        merchantColumn.setPrefWidth(190);

        TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setPrefWidth(150);

        TableColumn<Transaction, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setPrefWidth(120);

        table.getColumns().addAll(dateColumn, merchantColumn, categoryColumn, amountColumn);
        table.setItems(getFilteredTransactions(selectedMonth, selectedYear, selectedCategory, searchText));

        table.setPrefWidth(610);
        table.setMaxWidth(610);
        table.setPrefHeight(350);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    private BarChart<String, Number> createMonthlySpendingChart(String selectedYear, String selectedCategory) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount Spent");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Spending Trend");
	barChart.setLegendVisible(false);

        XYChart.Series<String, Number> spendingSeries = new XYChart.Series<>();
        spendingSeries.setName(selectedCategory.equals("All") ? "All Categories" : selectedCategory);

        String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

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
        barChart.setPrefHeight(320);

        return barChart;
    }
}
