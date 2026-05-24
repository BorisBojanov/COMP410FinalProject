package com.budgetapp;

import com.budgetapp.dashboard.DashboardView;
import com.budgetapp.storage.storage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // Initialize database + insert sample data
        storage.getInstance().seedSampleData();

        DashboardView dashboardView = new DashboardView();

        Scene scene = new Scene(dashboardView.getView(), 900, 550);

        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );

        stage.setTitle("BudgetApp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}