package com.budgetapp.email;
/* EmailAccount
Stores connected Gmail or Outlook account information.

accountId	            int	Attribute
emailAddress	        String	Attribute
provider	            String	Attribute
accessToken	            String	Attribute
isConnected	            boolean	Attribute
connect()	            boolean	Method
disconnect()	        void	Method
authorizeAccess()	    boolean	Method
*/

/* EmailService
Connects to the user’s email provider and retrieves email messages.

serviceName	                                        String	Attribute
fetchEmails(account: EmailAccount)	                List<EmailMessage>	Method
filterTransactionEmails(emails: List<EmailMessage>)	List<EmailMessage>	Method
*/

/* EmailMessage
Represents an email retrieved from the user’s inbox.

messageId	            String	Attribute
sender	                String	Attribute
subject	                String	Attribute
body	                String	Attribute
receivedDate	        Date	Attribute
processed	            boolean	Attribute
isTransactionEmail()	boolean	Method
markAsProcessed()	    void	Method
*/

import java.util.Properties;

import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.FromStringTerm;
import jakarta.mail.search.SearchTerm;
import jakarta.mail.search.SubjectTerm;


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
    String protocol = "imaps"; // Default protocol for secure IMAP
    String email = "boris.bojanov@gmail.com";
    String appPassword = "";
    Store store;
    Folder folder;
    Session session;

    // Constructor
    public email(String emailAddress, String appPassword) {
        this.email = emailAddress;
        this.appPassword = appPassword;
        configureGmail(properties); // update global properties with Gmail settings
        session = Session.getInstance(properties);
        store = connectTomailServerStore(session, email, appPassword);
    }

    public Session getSession() {
        return session;
    }
    public Store getStore() {
        return store;
    }
    public Folder getFolder() {
        return folder;
    }


    public void setSession(Session session) {
        this.session = session;
    }
    public void setStore(Store store) {
        this.store = store;
    }
    public void setFolder(Folder folder) {
        this.folder = folder;
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
    public static Store connectTomailServerStore(Session session, String email, String appPassword) {
    // Implementation to connect to the mail server using the provided properties and credentials
        try{
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", email, appPassword);
            return store;
        } catch (Exception e) {
            e.printStackTrace();
            // throw new Exception("Failed to connect to mail server: " + e.getMessage());
            return null; 
        }
    }
    
    public void openInbox() {
        this.folder = openFolder(this.store, "INBOX");
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
                new FromStringTerm(senderEmail), // sender is "someBankAlerts@Cibc_DT_RBC.com"
                new SubjectTerm(subject)   // subject contains "transaction"
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

    // 
    public Message[] getMessages(String senderEmail){
        SearchTerm filter = filterMessages(senderEmail);
        Message[] message = fetchMessages(folder, filter);
        return message;
    }

    public static String getText(Message message) {
        // Implementation to extract text content from the fetched messages
        // Email bodies can be plain text, HTML, or multipart (both)
        try {
            Object stuff = message.getContent();
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
            return "no text/plain part is found";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMessageID(Message messages) {
        // Check for duplicate messages based on unique identifiers (Message-ID header)
        try{
            String[] messageId = messages.getHeader("Message-ID");
            if (messageId != null && messageId.length > 0){
                return messageId[0];
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
        }   
    }

    public void close() {
        closeConnection(this.folder, this.store);
    }
    public static void closeConnection(Folder folder, Store store) {
        try{
            if (folder != null && folder.isOpen()){
                folder.close(false);  // false = don't expunge deleted messages
            }
            if (store != null){
                store.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      
}
