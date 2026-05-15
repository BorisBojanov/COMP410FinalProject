package com.budgetapp;

import com.budgetapp.dashboard.DashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        DashboardView dashboardView = new DashboardView();

        Scene scene = new Scene(dashboardView.getView(), 900, 550);

        stage.setTitle("BudgetApp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
