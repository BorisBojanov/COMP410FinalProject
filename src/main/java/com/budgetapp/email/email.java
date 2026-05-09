package com.budgetapp.email;

import jakarta.mail.*;
// import java.io.IOException;

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




*/

public class email {
 
    public static void configureEmail() {

    }
}

