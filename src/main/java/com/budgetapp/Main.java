package com.budgetapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.chart.PieChart;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label title = new Label("BudgetApp Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox summaryCards = new HBox(15);
        summaryCards.getChildren().addAll(
                createCard("Monthly Spending", "$847.25"),
                createCard("Budget Remaining", "$352.75"),
                createCard("Transactions", "5")
        );

        VBox categoryPanel = new VBox(15);
	categoryPanel.setStyle("-fx-padding: 15; -fx-background-color: #f3f3f3;");

	Label categoryTitle = new Label("Categories");

	Label food = new Label("Food: $6.25");
	Label transport = new Label("Transport: $74.20");
	Label subscription = new Label("Subscription: $16.99");
	Label groceries = new Label("Groceries: $92.31");

	PieChart pieChart = new PieChart();
	pieChart.getData().add(new PieChart.Data("Food", 6.25));
	pieChart.getData().add(new PieChart.Data("Transport", 74.20));
	pieChart.getData().add(new PieChart.Data("Subscription", 16.99));
	pieChart.getData().add(new PieChart.Data("Groceries", 92.31));
	pieChart.setTitle("Spending by Category");
	pieChart.setPrefWidth(250);
	pieChart.setPrefHeight(300);

	categoryPanel.getChildren().addAll(
        	categoryTitle,
        	food,
        	transport,
        	subscription,
        	groceries,
        	pieChart
);
	

        TableView<Transaction> table = createTransactionTable();

        BorderPane dashboard = new BorderPane();
        dashboard.setLeft(categoryPanel);
        dashboard.setCenter(table);

        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 20;");
        root.getChildren().addAll(title, summaryCards, dashboard);

        Scene scene = new Scene(root, 900, 550);

        stage.setTitle("BudgetApp");
        stage.setScene(scene);
        stage.show();
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

        table.getItems().addAll(
                new Transaction("2026-05-01", "Starbucks", "Food", 6.25),
                new Transaction("2026-05-02", "Uber", "Transport", 18.40),
                new Transaction("2026-05-03", "Netflix", "Subscription", 16.99),
                new Transaction("2026-05-04", "Walmart", "Groceries", 92.31),
                new Transaction("2026-05-05", "Shell", "Transport", 55.80)
        );

        return table;
    }

    public static void main(String[] args) {
        launch();
    }

    public static class Transaction {
        private final String date;
        private final String merchant;
        private final String category;
        private final double amount;

        public Transaction(String date, String merchant, String category, double amount) {
            this.date = date;
            this.merchant = merchant;
            this.category = category;
            this.amount = amount;
        }

        public String getDate() { return date; }
        public String getMerchant() { return merchant; }
        public String getCategory() { return category; }
        public double getAmount() { return amount; }
    }
}
