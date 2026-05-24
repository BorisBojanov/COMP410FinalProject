package com.budgetapp.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.budgetapp.model.Transaction;

/**
 * Data that needs to be stored in SQLite
 * Fetch emails
 * Parse email content
 * Needs to not re-process the same email
 * Needs to show the user theri transaction data
 * 
 * 
 * Tables:
 *  Transactions: tid, amount, date, merchant, category, message_id(FK -> messages.message_id)
 *  Messages: mid, message_id, date_received, subject, sender, body
 * 
 * JDBC (Java Database Connectivity)
 * https://www.geeksforgeeks.org/java/introduction-to-jdbc/
 * Process: 
 * 1. Open a Connection -- points to local .db file
 * 2. Create a Statement or PreparedStatement -- SQL query
 * 3. Run the query -- For all queries we save the result to a ResultSet 
 * 4. Close it all
 * 
 * Singleton pattern for storage class to ensure we only have one instance of storage connection
*/
public class storage {
    
    private Connection conn;
    private static storage instance;
    private String connectionUrl = "jdbc:sqlite:budget.db"; // Local file url
    
    private storage(){

        // storage db = storage.getInstance();
        // db.connect("jdbc:sqlite:budget.db");
        connect(connectionUrl);
        creatDBTables();
    }

    private void connect(String url){
        // Local file url = "jdbc:sqlite:sample.db"; 
        
        try {
            this.conn = DriverManager.getConnection(url); 
            
            if (this.conn != null) {
                System.out.println("Connected to SQLite!");

            } else {
                System.out.println("Failed to connect to SQLLite.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static storage getInstance() {
        if (instance == null) {
            instance = new storage();
        }
        return instance;
    }

    public void creatDBTables(){
        // Create tables if they don't exist
        String createTableMessages = "create table if not exists Messages (" +
            "mid integer primary key autoincrement, " +
            "message_id text unique, " + // Unique identifier for email
            "date_received text, " +
            "subject text, " +
            "sender text, " +
            "body text" +
            ");";
        String createTableTransactions = "create table if not exists Transactions (" +
            "tid integer primary key autoincrement, " +
            "amount real, " +
            "date text, " +
            "merchant text, " +
            "category text, " +
            "message_id integer, " + // Foreign key to Messages
            "foreign key (message_id) references Messages(mid)" +
            ");";

        try (var statement = this.conn.createStatement()) {
            // Execute, create table statements 
            // createStatement(): Creates a basic Statement object for sending SQL commands.
            statement.execute(createTableMessages);   // Create Messages before Transactions
            statement.execute(createTableTransactions);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Messages: mid, message_id, date_received, subject, sender, body
     * return the generated mid as an int (or -1 on failure)
     * */
    public int insertMessage(String messageId, String dataReceived, String subject, String sender, String body){
        // Insert a new message into the Messages table
        // PreparedStatement uses ? placeholders -- preventing SQL injection and improper formatting
        
        String sql = "insert into Messages (message_id, date_received, subject, sender, body) values (?, ?, ?, ?, ?);";
        // try making the statement with the placehilders
        try (PreparedStatement prepared = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            //Binds ? to a positional setString(index, value) call — index starts at 1.
            prepared.setString(1, messageId);
            prepared.setString(2, dataReceived);
            prepared.setString(3, subject);
            prepared.setString(4, sender);
            prepared.setString(5, body);

            
            // executeUpdate(): Sends the SQL to the database
            prepared.executeUpdate();        

            ResultSet rs = prepared.getGeneratedKeys(); // Retrieves any auto-generated keys created by the execution of the SQL statement. In this case, it would be the mid of the newly inserted message.
            if (rs.next()) {
                int mid = rs.getInt(1); // Standard way to get the first generated column
                return mid;
            } else {
                return -1; // Return -1 if insertion failed or no ID generated
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Transactions: tid, amount, date, merchant, category, message_id(FK -> messages.message_id)
     * 
     * */
    public boolean insertTransaction(Double amount, String date, String merchant, String category, int mid){
        // Insert a new transaction into the Transactions table
        String sql = "insert into Transactions (amount, date, merchant, category, message_id) values (?, ?, ?, ?, ?);";

        try (PreparedStatement prepared = this.conn.prepareStatement(sql)){
            prepared.setDouble(1, amount);
            prepared.setString(2, date);
            prepared.setString(3, merchant);
            prepared.setString(4, category);
            prepared.setInt(5, mid);

            int transactionCode = prepared.executeUpdate();
            
            if (transactionCode == 1) {
                System.out.println("Transaction inserted successfully!");
                return true;
            } else {
                System.out.println("Failed to insert transaction.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 
    */
    public boolean checkDouplicateMessages(String messageId) {
        // Check for duplicate messages based on unique identifiers (Message-ID header)
        String sql = "SELECT mid FROM Messages WHERE message_id = ?;";
        
         try(var statement = this.conn.prepareStatement(sql);){
            
            statement.setString(1, messageId);
            ResultSet rs =statement.executeQuery();
            if (rs.next()) {
                // If we get a result, it means the message_id already exists in the DB
                System.out.println("Duplicate message found with message_id: " + messageId);
                return true;
            } else {
                System.out.println("No duplicate message found for message_id: " + messageId);
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }   
    }

    // For testing purposes, we can print out all transactions in the DB
    // return a List of transaction objects instead.
    public List<Transaction> getTransactions(){

        String sql = "SELECT * FROM Transactions;";
        try(var statement = this.conn.prepareStatement(sql);){
            ResultSet rs = statement.executeQuery();
            
            List<Transaction> transactions = new ArrayList<>();

            // while (rs.next()) {
            //     int tid = rs.getInt("tid");
            //     double amount = rs.getDouble("amount");
            //     String date = rs.getString("date");
            //     String merchant = rs.getString("merchant");
            //     String category = rs.getString("category");
            //     int messageId = rs.getInt("message_id");

            //     sb.append(String.format("Transaction ID 'tid': %d, Amount 'amount': %.2f, Date 'date': %s, Merchant 'merchant': %s, Category 'category': %s, Message ID 'message_id'(FK): %d\n",
            //         tid, amount, date, merchant, category, messageId));
            // }
            while (rs.next()){
                int tid = rs.getInt("tid");
                double amount = rs.getDouble("amount");
                String date = rs.getString("date");
                String merchant = rs.getString("merchant");
                String category = rs.getString("category");
                int messageId = rs.getInt("message_id");
                transactions.add(new Transaction(tid, amount, date, merchant, category, messageId));
            }

        return transactions;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Transaction>();
        }
    }

	public void seedSampleData() {
    if (!getTransactions().isEmpty()) {
        return;
    }

    int mid1 = insertMessage(
            "sample-message-1",
            "2026-05-01",
            "Transaction Alert",
            "bank@example.com",
            "Starbucks purchase"
    );
    insertTransaction(6.25, "2026-05-01", "Starbucks", "Food", mid1);

    int mid2 = insertMessage(
            "sample-message-2",
            "2026-05-02",
            "Transaction Alert",
            "bank@example.com",
            "Uber purchase"
    );
    insertTransaction(18.40, "2026-05-02", "Uber", "Transport", mid2);

    int mid3 = insertMessage(
            "sample-message-3",
            "2026-05-03",
            "Transaction Alert",
            "bank@example.com",
            "Netflix purchase"
    );
    insertTransaction(16.99, "2026-05-03", "Netflix", "Subscription", mid3);
}


public boolean deleteTransaction(int tid) {
    String sql = "DELETE FROM Transactions WHERE tid = ?;";

    try (PreparedStatement prepared = this.conn.prepareStatement(sql)) {
        prepared.setInt(1, tid);
        return prepared.executeUpdate() == 1;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean updateTransaction(int tid, double amount, String date, String merchant, String category) {
    String sql = "UPDATE Transactions SET amount = ?, date = ?, merchant = ?, category = ? WHERE tid = ?;";

    try (PreparedStatement prepared = this.conn.prepareStatement(sql)) {
        prepared.setDouble(1, amount);
        prepared.setString(2, date);
        prepared.setString(3, merchant);
        prepared.setString(4, category);
        prepared.setInt(5, tid);

        return prepared.executeUpdate() == 1;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean addManualTransaction(double amount, String date, String merchant, String category) {
    int mid = insertMessage(
            "manual-" + System.currentTimeMillis(),
            date,
            "Manual Transaction",
            "user",
            merchant + " " + amount
    );

    if (mid == -1) {
        return false;
    }

    return insertTransaction(amount, date, merchant, category, mid);
}

}
