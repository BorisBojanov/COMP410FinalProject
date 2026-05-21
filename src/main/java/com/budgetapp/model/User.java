package com.budgetapp.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.budgetapp.storage.storage;

/* User
Represents the person using the budgeting app.

userId	                                    int	Attribute
name	                                    String	Attribute
email	                                    String	Attribute
passwordHash	                            String	Attribute
login(email: String, password: String)	    boolean	Method
logout()	                                void	Method
connectEmailAccount(account: EmailAccount)	void	Method
viewDashboard()	                            void	Method
*/

public class User {

    private int userId;
    private String name;
    private String email;
    private String passwordHash;

    /**
     * Used when registering a new user.
     * Accepts a text password and hashes it  — the raw password is not stored.
     */
    public User(String name, String email, String plainPassword) {
        this.name         = name;
        this.email        = email;
        this.passwordHash = hashPassword(plainPassword);
    }

    /**
     * Used when loading an existing user from the database.
     * Accepts a hashed password so it is not double-hashed.
     */
    public User(int userId, String name, String email, String passwordHash) {
        this.userId       = userId;
        this.name         = name;
        this.email        = email;
        this.passwordHash = passwordHash;
    }

    /**
     * Hashes a plain-text password with SHA-256 and returns the hex string.
     * SHA-256 is part of the Java standard library so no extra dependency
     * is needed.
     */
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hex string
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String byteHex = Integer.toHexString(0xff & b);
                if (byteHex.length() == 1) hex.append('0'); // pad single-digit hex
                hex.append(byteHex);
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to be present in any Java SE implementation
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // -----------------------------------------------------------------------
    // Methods (from design class diagram)
    // -----------------------------------------------------------------------

    /**
     * Validates the provided credentials against the database.
     * On success, populates all fields of this User instance.
     * On failure, leaves this instance unchanged and returns false.
     *
     * @param email    email address to look up
     * @param password plain-text password (will be hashed before comparison)
     * @return true if credentials match a stored user, false otherwise
     */
    public boolean login(String email, String password) {
        String hashedInput = hashPassword(password);
        storage db = storage.getInstance();
        User found = db.getUserByCredentials(email, hashedInput);

        if (found != null) {
            this.userId       = found.getUserId();
            this.name         = found.getName();
            this.email        = found.getEmail();
            this.passwordHash = found.getPasswordHash();
            System.out.println("Login successful for: " + this.name);
            return true;
        }

        System.out.println("Login failed: invalid email or password.");
        return false;
    }

    /**
     * Logs the user out by clearing all instance fields.
     * 
     */
    public void logout() {
        System.out.println("User " + this.name + " has logged out.");
        if (this.name == null) {
            System.out.println("Warning: logout called when user was already logged out.");
            return;
        }
        this.userId       = 0;
        this.name         = null;
        this.email        = null;
        this.passwordHash = null;
    }

    /**
     * Links an email address to this user account in the database.
     * The full EmailAccount class will replace the String parameter once
     * that class is implemented.
     *
     * @param emailAddress the Gmail or Outlook address to connect
     */
    public void connectEmailAccount(String emailAddress) {
        storage db = storage.getInstance();
        boolean success = db.linkEmailAccount(this.userId, emailAddress);

        if (success) {
            System.out.println("Email account connected: " + emailAddress);
        } else {
            System.out.println("Failed to connect email account.");
        }
    }

    /**
     * Placeholder for launching the dashboard view.
     * Use with the Dashboard class once that is implemented.
     */
    public void viewDashboard() {
        System.out.println("Opening dashboard for user: " + this.name);
        // TODO: instantiate Dashboard and call displaySummary()
    }

    // -----------------------------------------------------------------------
    // Getters and Setters
    // -----------------------------------------------------------------------

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", name='" + name + "', email='" + email + "'}";
    }
}
