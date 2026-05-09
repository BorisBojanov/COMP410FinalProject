package com.budgetapp.email;

import java.util.Properties;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.FromStringTerm;
import jakarta.mail.search.SearchTerm;
import jakarta.mail.search.SubjectTerm;
import jakarta.mail.BodyPart;


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
        try {
            configureGmail(properties); // update global properties with Gmail settings
            Session session = Session.getInstance(properties);
            connectTomailServerStore(session);
            Store store = connectTomailServerStore(session);
            Folder f = openFolder(store, "INBOX");
            SearchTerm filter = filterMessages("someBankAlerts@Cibc_DT_RBC.com");
            // filterMessages("alerts@td.com", "transaction");
            fetchMessages(f, filter);

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
        properties.put("mail.imaps.connectiontimeout", "10000"); // Optional: Set connection timeout
        properties.put("mail.imaps.timeout", "10000"); // Optional:Read timeout
    }

    /*Connect to mail server and fetch emails
    
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
    
    /* Open a specific folder like "INBOX" and fetch messages.
    Arguments:
    Store store: The connected mail store from which to access folders and messages.

    Returns:
    void
    */
    public static Folder openFolder(Store store, String folderName) {
        try{// Implementation to open a specific folder ("INBOX") and fetch messages
            Folder inbox = store.getFolder(folderName);
            inbox.open(Folder.READ_ONLY);  // READ_ONLY for security requirement
            return inbox;
        } catch (Exception e) {
            e.printStackTrace();
            // throw new Exception("Failed to open folder: " + e.getMessage());
            return null;
        }
    }

    // Search for emails FROM a specific sender
    public static SearchTerm filterMessages(String senderEmail) {
        try{
            // Filter messages based on sender's email address
            SearchTerm filter = new FromStringTerm(senderEmail);
            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static SearchTerm filterMessages(String senderEmail, String subject) {
        try{
            SearchTerm filter = new AndTerm(
                new FromStringTerm("alerts@td.com"),
                new SubjectTerm("transaction")   // subject contains "transaction"
            );
            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Fetch messages from the specified folder
     * Optionally, apply a search filter to retrieve specific emails.
     *
     * @param folder
    */
    public static Message[] fetchMessages(Folder folder) {
        try{
            Message[] message = folder.getMessages();
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Message[] fetchMessages(Folder folder, SearchTerm filter) {
        try{
            Message[] message = folder.search(filter);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getText(Message[] message) {
        // Implementation to extract text content from the fetched messages
        // Email bodies can be plain text, HTML, or multipart (both)
        try {
            Object stuff = message[0].getContent();
            if (stuff instanceof String) {
                return (String) stuff;
            }
            if (stuff instanceof MimeMultipart multipart){
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart part = multipart.getBodyPart(i);
                    if (part.isMimeType("text/plain")) {
                        return (String) part.getContent();
                    }
                }
            } 
            // fallback if no text/plain part is found or content is of an unexpected type
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void checkDouplicateMessages(Message messages) {
        // Check for duplicate messages based on unique identifiers (Message-ID header)
        try{
            String[] messageId = messages.getHeader("Message-ID");
            // Store this ID in your DB — if it's already there, skip parsing
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

    public static void closeStuff(Folder folder, Store store) {
        try{
            folder.close(false);  // false = don't expunge deleted messages
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

