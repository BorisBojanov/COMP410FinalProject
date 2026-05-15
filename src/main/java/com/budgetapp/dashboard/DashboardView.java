package com.budgetapp.dashboard;

import com.budgetapp.model.Transaction;
import com.budgetapp.service.FakeDataService;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class DashboardView {

    public VBox getView() {
        Label title = new Label("BudgetApp Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox summaryCards = new HBox(15);
        summaryCards.getChildren().addAll(
                createCard("Monthly Spending", "$847.25"),
                createCard("Budget Remaining", "$352.75"),
                createCard("Transactions", "5")
        );

        VBox categoryPanel = createCategoryPanel();
        TableView<Transaction> table = createTransactionTable();
        BarChart<String, Number> monthlyChart = createMonthlySpendingChart();

        VBox centerPanel = new VBox(15);
        centerPanel.getChildren().addAll(table, monthlyChart);

        BorderPane dashboard = new BorderPane();
        dashboard.setLeft(categoryPanel);
        dashboard.setCenter(centerPanel);

        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 20;");
        root.getChildren().addAll(title, summaryCards, dashboard);

        return root;
    }

    private VBox createCard(String label, String value) {
        Label labelText = new Label(label);
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox card = new VBox(8);
        card.setStyle("-fx-padding: 15; -fx-background-color: #eeeeee; -fx-border-color: #cccccc;");
        card.getChildren().addAll(labelText, valueText);
        return card;
    }

    private VBox createCategoryPanel() {
        VBox categoryPanel = new VBox(15);
        categoryPanel.setStyle("-fx-padding: 15; -fx-background-color: #f3f3f3;");

        PieChart pieChart = new PieChart();
        pieChart.getData().add(new PieChart.Data("Food", 6.25));
        pieChart.getData().add(new PieChart.Data("Transport", 74.20));
        pieChart.getData().add(new PieChart.Data("Subscription", 16.99));
        pieChart.getData().add(new PieChart.Data("Groceries", 92.31));
        pieChart.setTitle("Spending by Category");
        pieChart.setPrefWidth(250);
        pieChart.setPrefHeight(300);

        categoryPanel.getChildren().addAll(
                new Label("Categories"),
                new Label("Food: $6.25"),
                new Label("Transport: $74.20"),
                new Label("Subscription: $16.99"),
                new Label("Groceries: $92.31"),
                pieChart
        );

        return categoryPanel;
    }

    private TableView<Transaction> createTransactionTable() {
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
        table.setItems(FakeDataService.getTransactions());

        return table;
    }

    private BarChart<String, Number> createMonthlySpendingChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount Spent");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Spending Trend");

        XYChart.Series<String, Number> spendingSeries = new XYChart.Series<>();
        spendingSeries.setName("Spending");

        spendingSeries.getData().add(new XYChart.Data<>("Jan", 720));
        spendingSeries.getData().add(new XYChart.Data<>("Feb", 810));
        spendingSeries.getData().add(new XYChart.Data<>("Mar", 690));
        spendingSeries.getData().add(new XYChart.Data<>("Apr", 930));
        spendingSeries.getData().add(new XYChart.Data<>("May", 847));

        barChart.getData().add(spendingSeries);
        barChart.setPrefHeight(250);

        return barChart;
    }
}
