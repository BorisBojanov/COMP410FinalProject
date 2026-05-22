package com.budgetapp;

import java.util.Scanner;
import com.budgetapp.model.User;
import com.budgetapp.email.email;
import com.budgetapp.parser.parser;
import com.budgetapp.storage.storage;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Message;

/**
* Budget App Main Class
* 1. Get storage instance (initializes DB and creates tables)
* 2. Connect to email server
* 3. Open INBOX folder
* 4. Fetch messages (with a filter for bank alerts)
* 5. For each message:
*    a. Extract the message ID, date, subject, sender, body
*    b. Pass those to parser.processEmail(...)
* 6. Close the email connection
**/

/*
App start
Shows menue: Login, Register, Exit

Register:
Asks for: name, email, password 
    -> creates new User(name, email, password)
Hashes password
    -> db.intertUser(name, email hashedPassword) saves to Database
    -> show some message "registered, now login"

Login:
Ask for: email, password
    -> user.login(email, password)  password is hashed and send query to DB

    returns ture: proceed to app
    returns false: show message, loop back to login..

Exit:
Safely close the app
*/
public class App {
    public static void main(String[] args) {
        System.out.println("Budget App!");
        new App(); // Call the constructor to run the app logic
    }

    public App() {
        storage db = storage.getInstance(); // Initialize database and create tables if not exist

        Scanner scanner = new Scanner(System.in);
        User session = null;

        // Main loop for login/register
        while (session == null) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    session = handleLogin(scanner);
                    break;
                case "2":
                    System.out.print("Name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();
                    registerUser(name, email, password);
                    break;
                case "3":
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        // At this point, 'session' is a logged-in User object
        System.out.println("Welcome, " + session.getName() + "!");
        // Launch email/dashboard logic here, passing 'session' if needed for user-specific data
        loadCredentials(); // Load credentials and process emails
    }
    public void registerUser(String name, String email, String password){
        User newUser = new User(name, email, password);
        storage db = storage.getInstance();
        String userName = newUser.getName();
        String userEmail = newUser.getEmail();
        String userPasswordHash = newUser.getPasswordHash();
        int uid = db.insertUser(userName, userEmail, userPasswordHash);
        
        if (uid > 0) {
            System.out.println("Registered successfully! Please log in.");
        } else {
            System.out.println("Registration failed — email may already be in use.");
        }
    }
    
    private User handleLogin(Scanner scanner){
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = User.login(email, password);  // null if credentials wrong
        if (user == null) System.out.println("Invalid email or password.");
        return user;
    }

    private void loadCredentials(){
        Dotenv dotenv = Dotenv.load();
        String emailReceiver = dotenv.get("GMAIL_ADDRESS");
        String appPassword   = dotenv.get("GMAIL_APP_PASSWORD");
        String bankSender    = dotenv.get("BANK_SENDER");

        email emailClient = new email(emailReceiver, appPassword);
        parser transactionParser = new parser();

        try {
            emailClient.openInbox();
            Message[] messages = emailClient.getMessages(bankSender);
            if (messages == null) {
                System.out.println("No messages found from the specified sender.");
                return;
            }
            for (Message msg : messages) {
                String messageId = email.getMessageID(msg);
                String body      = email.getText(msg);
                if (body == null || body.isBlank()) continue;

                String subject = msg.getSubject();
                jakarta.mail.Address[] from = msg.getFrom();
                String sender = (from != null && from.length > 0) ? from[0].toString() : "unknown";
                java.util.Date received = msg.getReceivedDate();
                String date = (received != null) ? received.toString() : msg.getSentDate() != null ? msg.getSentDate().toString() : "unknown";

                transactionParser.processEmail(messageId, date, subject, sender, body);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            emailClient.close();
        }
    }

}
