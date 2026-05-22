package com.budgetapp;

import com.budgetapp.auth.auth;
import com.budgetapp.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    // These names must match fx:id values in login.fxml exactly
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            errorLabel.setText("Email and password are required.");
            return;
        }

        User user = User.login(email, password);

        if (user != null) {
            auth.setCurrentUser(user);   // store the logged-in user globally
            try {
                Main.showDashboard();
            } catch (Exception e) {
                errorLabel.setText("Failed to load dashboard.");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid email or password.");
        }
    }

    @FXML
    private void goToRegister() {
        try {
            Main.showRegister();
        } catch (Exception e) {
            errorLabel.setText("Navigation error.");
            e.printStackTrace();
        }
    }
}
