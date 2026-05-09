package com.budgetapp.email;

import java.util.Properties;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.*;

/*
JakartaMail API

Providers:
    Gmail: Requires enabling IMAP in settings and generating an App Password 
        from Google Account security if two-step verification is active. 
    You typically connect to imap.gmail.com on port 993.

    Outlook/Office 365: Modern Outlook access often requires OAuth2.0 authentication 
        or using the Microsoft Graph SDK for Java for better security.
        https://learn.microsoft.com/en-us/graph/tutorials/java-email


Reading Emails
    Connect to IMAP server and authenticate: 
        Configure Properties: Define the server host, port, and security settings (like SSL/TLS). 
        Initialize a Session: Use the properties to create a Session object.
        Connect to a Store: Specify the protocol (e.g., "imaps" for secure IMAP) and connect using your credentials.
        Access a Folder: Open a specific folder, such as "INBOX", in read-only or read-write mode.
        Fetch Messages: Retrieve a list of Message objects from the folder.
        Extract Data: Loop through messages to get headers (subject, sender) and parse the body content.

    Session → Store → Folder → Message → Content


*/

public class email {
    // Class variables
    Properties properties = new Properties();

    // Constructor
    public email() {
        configureGmail(properties); // update global properties with Gmail settings
        Session session = Session.getInstance(properties);
        try {
            connectTomailServerStore(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    // Methods to configure email providers
    // Gmail configuration
    public static void configureGmail(Properties properties) {
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com"); // For Gmail
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.starttls.enable", "true"); 
    }

    // Outlook/Office 365 configuration
    public static void configureOutlook(Properties properties) {
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "outlook.office365.com"); // For Outlook/Office 365
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.starttls.enable", "true"); 
    }

    /*
    Connect to mail server and fetch emails
    
    For Gmail you cannot use your regular password. 
        You need an App Password:
            (Google Account → Security → 2-Step Verification → App Passwords). 
    Same for Outlook.
    */ 
    public static Store connectTomailServerStore(Session session){
        try{// Implementation to connect to the mail server using the provided properties and credentials
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "your@gmail.com", "your-app-password");
            return store;
        } catch (Exception e) {
            e.printStackTrace();
            // throw new Exception("Failed to connect to mail server: " + e.getMessage());
            return null; 
        }
    }
    

    public static void openFolder(Store store) {
        try{// Implementation to open a specific folder ("INBOX") and fetch messages
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);  // READ_ONLY for security requirement

        } catch (Exception e) {
            e.printStackTrace();
            // throw new Exception("Failed to open folder: " + e.getMessage());
        }
    }

    public static void fetchMessages(Folder folder) {
        try{Message[] message = folder.getMessages();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

