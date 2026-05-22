package com.budgetapp;

import com.budgetapp.storage.storage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX entry point. The pom.xml javafx-maven-plugin points here.
 * Run with: mvn javafx:run
 *
 * Screen flow:
 *   Main → login.fxml (LoginController)
 *        → register.fxml (RegisterController) [from login screen]
 *        → dashboard.fxml (DashboardController) [after successful login]
 */
public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("Budget App");
        showLogin();
        stage.show();
    }

    /**
     * Load login.fxml into the primary stage.
     * Called on startup and after logout.
     */
    public static void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(
            Main.class.getResource("/com/budgetapp/login.fxml")
        );
        primaryStage.setScene(new Scene(loader.load(), 400, 300));
        primaryStage.setTitle("Budget App – Login");
    }

    /**
     * Load register.fxml into the primary stage.
     * Called from the LoginController "Register" link.
     */
    public static void showRegister() throws Exception {
        FXMLLoader loader = new FXMLLoader(
            Main.class.getResource("/com/budgetapp/register.fxml")
        );
        primaryStage.setScene(new Scene(loader.load(), 400, 340));
        primaryStage.setTitle("Budget App – Register");
    }

    /**
     * Load dashboard.fxml into the primary stage.
     * Called from LoginController after auth.setCurrentUser() succeeds.
     */
    public static void showDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(
            Main.class.getResource("/com/budgetapp/dashboard.fxml")
        );
        primaryStage.setScene(new Scene(loader.load(), 800, 600));
        primaryStage.setTitle("Budget App – Dashboard");
    }

    public static void main(String[] args) {
        storage.getInstance(); // initialize DB and create tables before UI starts
        launch(args);
    }
}
