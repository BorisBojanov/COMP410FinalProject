package com.budgetapp.dashboard;

import com.budgetapp.model.Transaction;
import com.budgetapp.storage.storage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class DashboardView {

    private final ObservableList<Budget> budgets = FXCollections.observableArrayList(
            new Budget("Food", 400.00),
            new Budget("Transport", 150.00),
            new Budget("Groceries", 500.00),
            new Budget("Subscription", 100.00)
    );

    private final String[] categories = {
            "Food", "Transport", "Subscription", "Groceries", "Shopping",
            "Entertainment", "Travel", "Technology", "Health", "Pets", "Books"
    };

    private ObservableList<Transaction> getStoredTransactions() {
        return FXCollections.observableArrayList(
                storage.getInstance().getTransactions()
        );
    }

    private ObservableList<Transaction> getFilteredTransactions(String month, String year, String category, String searchText) {
        return getStoredTransactions().filtered(transaction -> {
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
            default -> String.format("%02d", LocalDate.now().getMonthValue());
        };
    }

    private String getCurrentMonthName() {
        return switch (LocalDate.now().getMonthValue()) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "May";
        };
    }

    public VBox getView() {
        Label title = new Label("BudgetApp Dashboard");
        title.getStyleClass().add("title");

        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);

        String currentYear = String.valueOf(LocalDate.now().getYear());

        ComboBox<String> yearFilter = new ComboBox<>();
        yearFilter.getItems().addAll(currentYear);
        yearFilter.setValue(currentYear);

        ComboBox<String> monthFilter = new ComboBox<>();
        monthFilter.getItems().addAll(
                "All", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );
        monthFilter.setValue(getCurrentMonthName());

        Button currentMonthButton = new Button("Current Month");

        Label totalSpendLabel = new Label();
        Label totalTransactionsLabel = new Label();

        HBox filters = new HBox(10);
        filters.getStyleClass().add("card");
        filters.setAlignment(Pos.CENTER);
        filters.getChildren().addAll(
                new Label("Year:"), yearFilter,
                new Label("Month:"), monthFilter,
                currentMonthButton,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                totalSpendLabel,
                totalTransactionsLabel
        );

        ObservableList<Transaction> initialTransactions = getFilteredTransactions(
                monthFilter.getValue(),
                yearFilter.getValue(),
                "All",
                ""
        );

        updateFilterSummaryLabels(totalSpendLabel, totalTransactionsLabel, initialTransactions);

        VBox budgetProgressList = createBudgetProgressList(initialTransactions);

        Button addBudgetButton = new Button("Add/Edit Budget");
        Button deleteBudgetButton = new Button("Delete Budget");

        HBox budgetButtons = new HBox(10);
        budgetButtons.getChildren().addAll(addBudgetButton, deleteBudgetButton);

        VBox budgetPanel = new VBox(12);
        budgetPanel.getStyleClass().add("sidebar");
        budgetPanel.setPrefWidth(380);
        budgetPanel.getChildren().addAll(
                new Label("Budget Progress"),
                budgetProgressList,
                budgetButtons
        );

        PieChart pieChart = createPieChart(initialTransactions);

        HBox budgetPieRow = new HBox(20);
        budgetPieRow.setAlignment(Pos.CENTER);
        budgetPieRow.getChildren().addAll(budgetPanel, pieChart);

        BarChart<String, Number> monthlyChart = createMonthlySpendingChart(
                yearFilter.getValue(),
                "All"
        );

        TableView<Transaction> table = createTransactionTable(
                monthFilter.getValue(),
                yearFilter.getValue(),
                "All",
                ""
        );

        Button addButton = new Button("Add");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        HBox actionButtons = new HBox(10);
        actionButtons.getChildren().addAll(addButton, editButton, deleteButton);

        VBox tableSection = new VBox(10);
        tableSection.getChildren().addAll(actionButtons, table);

        VBox dashboard = new VBox(20);
        dashboard.getChildren().addAll(
                budgetPieRow,
                tableSection,
                monthlyChart
        );

        Runnable refreshDashboard = () -> {
            String selectedMonth = monthFilter.getValue();
            String selectedYear = yearFilter.getValue();

            ObservableList<Transaction> filteredTransactions = getFilteredTransactions(
                    selectedMonth,
                    selectedYear,
                    "All",
                    ""
            );

            table.setItems(filteredTransactions);
            updateFilterSummaryLabels(totalSpendLabel, totalTransactionsLabel, filteredTransactions);
            budgetProgressList.getChildren().setAll(createBudgetProgressList(filteredTransactions).getChildren());
            budgetPieRow.getChildren().set(1, createPieChart(filteredTransactions));
            dashboard.getChildren().set(2, createMonthlySpendingChart(selectedYear, "All"));
        };

        monthFilter.setOnAction(event -> refreshDashboard.run());
        yearFilter.setOnAction(event -> refreshDashboard.run());
        currentMonthButton.setOnAction(event -> {
            yearFilter.setValue(String.valueOf(LocalDate.now().getYear()));
            monthFilter.setValue(getCurrentMonthName());
            refreshDashboard.run();
        });

        addBudgetButton.setOnAction(event -> showBudgetDialog(null, refreshDashboard));

        deleteBudgetButton.setOnAction(event -> {
            if (budgets.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Budgets");
                alert.setHeaderText("No budgets to delete");
                alert.setContentText("Add a budget before trying to delete one.");
                alert.showAndWait();
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(budgets.get(0).getCategory());
            for (Budget budget : budgets) {
                dialog.getItems().add(budget.getCategory());
            }

            dialog.setTitle("Delete Budget");
            dialog.setHeaderText("Choose the budget category to delete");
            dialog.setContentText("Category:");

            dialog.showAndWait().ifPresent(category -> {
                Budget budgetToDelete = findBudgetByCategory(category);

                if (budgetToDelete != null) {
                    budgets.remove(budgetToDelete);
                    refreshDashboard.run();
                }
            });
        });

        deleteButton.setOnAction(event -> {
            Transaction selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                return;
            }

            storage.getInstance().deleteTransaction(selected.getTid());
            refreshDashboard.run();
        });

        addButton.setOnAction(event -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Transaction");

            DatePicker datePicker = new DatePicker(LocalDate.now());
            TextField merchantField = new TextField();

            ComboBox<String> categoryBox = new ComboBox<>();
            categoryBox.getItems().addAll(categories);

            TextField amountField = new TextField();

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            grid.add(new Label("Date:"), 0, 0);
            grid.add(datePicker, 1, 0);

            grid.add(new Label("Merchant:"), 0, 1);
            grid.add(merchantField, 1, 1);

            grid.add(new Label("Category:"), 0, 2);
            grid.add(categoryBox, 1, 2);

            grid.add(new Label("Amount:"), 0, 3);
            grid.add(amountField, 1, 3);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        String date = datePicker.getValue().toString();
                        String merchant = merchantField.getText();
                        String category = categoryBox.getValue();
                        double amount = Double.parseDouble(amountField.getText());

                        storage.getInstance().addManualTransaction(
                                amount,
                                date,
                                merchant,
                                category
                        );

                        refreshDashboard.run();
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText("Could not add transaction");
                        alert.setContentText("Please fill out all fields correctly.");
                        alert.showAndWait();
                    }
                }
            });
        });

        editButton.setOnAction(event -> {
            Transaction selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                return;
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Transaction");

            DatePicker datePicker = new DatePicker(LocalDate.parse(selected.getDate()));
            TextField merchantField = new TextField(selected.getMerchant());

            ComboBox<String> categoryBox = new ComboBox<>();
            categoryBox.getItems().addAll(categories);
            categoryBox.setValue(selected.getCategory());

            TextField amountField = new TextField(String.valueOf(selected.getAmount()));

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            grid.add(new Label("Date:"), 0, 0);
            grid.add(datePicker, 1, 0);

            grid.add(new Label("Merchant:"), 0, 1);
            grid.add(merchantField, 1, 1);

            grid.add(new Label("Category:"), 0, 2);
            grid.add(categoryBox, 1, 2);

            grid.add(new Label("Amount:"), 0, 3);
            grid.add(amountField, 1, 3);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        storage.getInstance().updateTransaction(
                                selected.getTid(),
                                Double.parseDouble(amountField.getText()),
                                datePicker.getValue().toString(),
                                merchantField.getText(),
                                categoryBox.getValue()
                        );

                        refreshDashboard.run();
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText("Could not edit transaction");
                        alert.setContentText("Please fill out all fields correctly.");
                        alert.showAndWait();
                    }
                }
            });
        });

        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 20;");
        root.getChildren().addAll(
                titleBox,
                filters,
                dashboard
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        return new VBox(scrollPane);
    }

    private void updateFilterSummaryLabels(
            Label totalSpendLabel,
            Label totalTransactionsLabel,
            ObservableList<Transaction> transactions
    ) {
        double totalSpend = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        totalSpendLabel.setText(String.format("Total Spend: $%.2f", totalSpend));
        totalTransactionsLabel.setText("Total Transactions: " + transactions.size());
    }

    private void showBudgetDialog(Budget existingBudget, Runnable refreshDashboard) {
        boolean editing = existingBudget != null;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add/Edit Budget");

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(categories);

        TextField amountField = new TextField();

        if (editing) {
            categoryBox.setValue(existingBudget.getCategory());
            amountField.setText(String.valueOf(existingBudget.getAmount()));
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryBox, 1, 0);

        grid.add(new Label("Monthly Budget:"), 0, 1);
        grid.add(amountField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String category = categoryBox.getValue();
                    double amount = Double.parseDouble(amountField.getText());

                    if (category == null || category.isBlank() || amount <= 0) {
                        throw new IllegalArgumentException("Invalid budget input");
                    }

                    Budget duplicateBudget = findBudgetByCategory(category);

                    if (!editing && duplicateBudget != null) {
                        duplicateBudget.setAmount(amount);
                    } else if (editing) {
                        existingBudget.setCategory(category);
                        existingBudget.setAmount(amount);
                    } else {
                        budgets.add(new Budget(category, amount));
                    }

                    refreshDashboard.run();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Could not save budget");
                    alert.setContentText("Please choose a category and enter a valid budget amount.");
                    alert.showAndWait();
                }
            }
        });
    }

    private Budget findBudgetByCategory(String category) {
        return budgets.stream()
                .filter(budget -> budget.getCategory().equals(category))
                .findFirst()
                .orElse(null);
    }

    private VBox createBudgetProgressList(ObservableList<Transaction> transactions) {
        VBox progressList = new VBox(8);

        if (budgets.isEmpty()) {
            progressList.getChildren().add(new Label("No budgets configured."));
            return progressList;
        }

        for (Budget budget : budgets) {
            progressList.getChildren().add(
                    createBudgetProgress(
                            budget.getCategory(),
                            getCategoryTotal(transactions, budget.getCategory()),
                            budget.getAmount()
                    )
            );
        }

        return progressList;
    }

    private VBox createBudgetProgress(String category, double spent, double budgetAmount) {
        double progress = budgetAmount == 0 ? 0 : spent / budgetAmount;
        double percent = progress * 100;

        Label title = new Label(String.format(
                "%s: $%.2f / $%.2f (%.0f%%)",
                category,
                spent,
                budgetAmount,
                percent
        ));

        ProgressBar progressBar = new ProgressBar(Math.min(progress, 1.0));
        progressBar.setPrefWidth(320);

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

            double total = getStoredTransactions().stream()
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

    public static class Budget {
        private String category;
        private double amount;

        public Budget(String category, double amount) {
            this.category = category;
            this.amount = amount;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }
}
