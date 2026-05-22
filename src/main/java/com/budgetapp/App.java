package com.budgetapp;
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
public class App {
    public static void main(String[] args) {
        System.out.println("Budget App!");
        new App(); // Call the constructor to run the app logic
    }

    public App() {
        // Load credentials from .env file
        Dotenv dotenv = Dotenv.load();
        String emailReceiver = dotenv.get("GMAIL_ADDRESS");
        String appPassword   = dotenv.get("GMAIL_APP_PASSWORD");
        String bankSender    = dotenv.get("BANK_SENDER");

        // Step 1: Get storage instance (initializes DB and creates tables)
        storage db = storage.getInstance();

        // Step 2: Connect to email server
        email emailClient = new email(emailReceiver, appPassword);
        emailClient.openInbox();

        // Fetch messages from your bank
        Message[] messages = emailClient.getMessages(bankSender);

        //  Create a parser instance
        parser parser = new parser();

        if (messages == null) {
            System.out.println("No messages found from the specified sender.");
            return; // Exit if no messages to process
        }
        try {
        // 4. For each message:
        for (Message msg : messages) {
            String messageId = email.getMessageID(msg);
            String body      = email.getText(msg);
            String subject   = msg.getSubject();       // jakarta.mail.Message has this built in
            String sender    = msg.getFrom()[0].toString(); // and this
            String date      = msg.getReceivedDate().toString();
            
            //Pass to parser
            parser.processEmail(messageId, date, subject, sender, body);
        }   

        } catch (Exception e) {
            e.printStackTrace();
             // Handle exceptions that may occur during message processing
        }
        

        emailClient.close(); // Close the email connection when done
    }

}
