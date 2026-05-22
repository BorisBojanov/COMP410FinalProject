package com.budgetapp.dashboard;

import com.budgetapp.Main;
import com.budgetapp.auth.auth;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label welcomeLabel;

    // initialize() is called automatically by FXMLLoader after @FXML fields are injected
    @FXML
    public void initialize() {
        String name = auth.getCurrentUser().getName();
        welcomeLabel.setText("Welcome, " + name + "!");
        // TODO: load transactions, charts, etc.
    }

    @FXML
    private void handleLogout() {
        auth.logout();
        try {
            Main.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
