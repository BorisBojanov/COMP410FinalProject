package com.budgetapp;

import com.budgetapp.model.User;
import com.budgetapp.storage.storage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleRegister() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("All fields are required.");
            return;
        }

        User newUser = new User(name, email, password); // hashes password
        int uid = storage.getInstance().insertUser(
            newUser.getName(), newUser.getEmail(), newUser.getPasswordHash()
        );

        if (uid > 0) {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Registered! You can now log in.");
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Registration failed — email may already be in use.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Main.showLogin();
        } catch (Exception e) {
            statusLabel.setText("Navigation error.");
            e.printStackTrace();
        }
    }
}
