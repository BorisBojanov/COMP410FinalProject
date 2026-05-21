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
import com.budgetapp.model.User;


// import com.budgetapp.parser.parser;

/* DatabaseManager
Handles saving, retrieving, updating, and deleting application data.

databaseUrl	                                String	Attribute

saveTransaction(transaction: Transaction)	void	Method
getTransactions(userId: int)	            List<Transaction>	Method
updateTransaction(transaction: Transaction)	void	Method
deleteTransaction(transactionId: int)	    void	Method
saveEmailMessage(email: EmailMessage)	    void	Method
*/

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

    /* getInstance
        Singleton pattern implementation to ensure only one instance of storage exists.
     */
    public static storage getInstance() {
        if (instance == null) {
            instance = new storage();
        }
        return instance;
    }

    /** creatDBTables
     * Create tables if they don't exist
     */
    public void creatDBTables(){
        // Create tables if they don't exist
        String createTableUsers = "create table if not exists Users (" +
            "uid integer primary key autoincrement, " +
            "name text not null, " +
            "email text unique not null, " +    // email is the unique login identifier
            "password_hash text not null, " +   // SHA-256 hex string, never store plain-text passwords
            "linked_email_address text" +        // Gmail/Outlook address connected to this account
            ");";
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
        String createTableCategories = "create table if not exists Categories (" +
            "category_Id integer primary key autoincrement, " +
            "name text, " +
            "rule_Keyword text " +
            ");";
        String createTableBudgets = "create table if not exists Budgets (" +
            "budget_Id integer primary key autoincrement, " +
            "monthly_Limit double, " +
            "amount_Spent double " +
            ");";

        try (var statement = this.conn.createStatement()) {
            // createStatement(): Creates a basic Statement object for sending SQL commands.
            statement.execute(createTableUsers);          // Users first (no dependencies)
            statement.execute(createTableMessages);       // Messages before Transactions
            statement.execute(createTableTransactions);   // Transactions depends on Messages
            statement.execute(createTableCategories);     // Categories depends on Transactions
            statement.execute(createTableBudgets);        // Budgets depends on Transactions

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** insertMessages
     * insertMessages: mid, message_id, date_received, subject, sender, body
     * 
     * @param messageId 
     * @param dataReceived
     * @param subject
     * @param sender
     * @param body  
     * @return mid of the newly inserted message, or -1 on failure
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

    /** checkDouplicateMessages
     * Checks if a message with the given messageId already exists in the Messages table.
      * Used to prevent duplicate processing of the same email.
      *
      * @param messageId the unique identifier of the email (Message-ID header)
      * @return true if a duplicate message is found, false otherwise
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

    /** insertTransaction
     * Transactions: tid, amount, date, merchant, category, message_id(FK -> messages.message_id)
     * Insert a new transaction into the Transactions table, linking it to the given messageId (mid).
     * 
     * @param amount
     * @param date
     * @param merchant
     * @param category  
     * @param mid       message_id(FK -> messages.message_id)
     * @return true on success, false on failure
     * */
    public boolean insertTransaction(Double amount, String date, String merchant, String category, int mid){
        // Insert a new transaction into the Transactions table
        String sql = "insert into Transactions (amount, date, merchant, category, message_id) values (?, ?, ?, ?, ?);";

        try (PreparedStatement prepared = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
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



    // Category methods
    public int insertCategory(String name, String ruleKeyword){
        String sql = "insert into Categories (name, rule_Keyword) values (?, ?);";
        
        try (PreparedStatement prepared = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            prepared.setString(1, name);
            prepared.setString(2, ruleKeyword);
            prepared.executeUpdate();
            
            ResultSet rs = prepared.getGeneratedKeys(); // Retrieves any auto-generated keys created by the execution of the SQL statement. In this case, it would be the categoryId of the newly inserted category.
            if (rs.next()) {
                int categoryId = rs.getInt(1); // Standard way to get the first generated column
                System.out.println("Category inserted successfully!");
                return categoryId;
            } else {
                System.out.println("Failed to insert category.");
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** updateCategoryRule
     * Updates the ruleKeyword for a given categoryId in the Categories table.
     *
     * @param categoryId the ID of the category to update
     * @param newRuleKeyword the new rule keyword to set
     * @return the categoryId on success, or -1 on failure
     */
    public int updateCategoryRule(int categoryId, String newRuleKeyword){
    String sql = "update Categories set rule_Keyword = ? where category_Id = ?;";

    try (PreparedStatement prepared = this.conn.prepareStatement(sql)){
        prepared.setString(1, newRuleKeyword);
        prepared.setInt(2, categoryId);
        int row = prepared.executeUpdate(); // Should return 1 if a row was updated, we expect only one row to match the categoryId

        if (row == 1) {
            System.out.println("Updated rule for category_Id=" + categoryId + " to keyword: " + newRuleKeyword);
            return categoryId;
        }
        System.out.println("updateCategoryRule: no category found with category_Id=" + categoryId);
        return -1;

    } catch (Exception e){
        e.printStackTrace();
        return -1;
    }

    } 

    /** updateCategoryName
     * Updates the name for a given categoryId in the Categories table.
     *
     * @param categoryId the ID of the category to update
     * @param newName the new name to set
     * @return true on success, false on failure
    */
    public boolean updateCategoryName(int categoryId, String newName){
        String sql = "update Categories set name = ? where category_Id = ?;";

        try (PreparedStatement prepared = this.conn.prepareStatement(sql)){
            prepared.setString(1, newName);
            prepared.setInt(2, categoryId);
            int row = prepared.executeUpdate(); // Should return 1 if a row was updated, we expect only one row to match the categoryId

            if (row == 1) {
                System.out.println("Updated name for category_Id=" + categoryId + " to name: " + newName);
                return true;
            }
            System.out.println("updateCategoryName: no category found with category_Id=" + categoryId);
            return false;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    // Budget methods
    
    public int insertBudget(Double monthlyLimit, Double amountSpent){
        String sql = "insert into Budgets (monthly_Limit, amount_Spent) values (?, ?);";

        try(PreparedStatement prepared = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            prepared.setDouble(1, monthlyLimit);
            prepared.setDouble(2, amountSpent);
            prepared.executeUpdate();
            
            ResultSet rs = prepared.getGeneratedKeys(); // Retrieves any auto-generated keys created by the execution of the SQL statement. In this case, it would be the budgetId of the newly inserted budget.
            if (rs.next()) {
                int budgetId = rs.getInt(1); // Standard way to get the first generated
                System.out.println("Budget inserted successfully!");
                return budgetId;
            } else {
                System.out.println("Failed to insert budget.");
                return -1;
            }
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateBudget(int budgetId, Double monthlyLimit, Double amountSpent){
        String sql = "update Budgets set monthly_Limit = ?, amount_Spent = ? where budget_Id = ?;";

        try(PreparedStatement prepared = this.conn.prepareStatement(sql)){
            prepared.setDouble(1, monthlyLimit);
            prepared.setDouble(2, amountSpent);
            prepared.setInt(3, budgetId);
            int row = prepared.executeUpdate(); 

            if (row == 1) {
                System.out.println("Updated budget for budget_Id=" + budgetId + " to monthly_Limit: " + monthlyLimit + ", amount_Spent: " + amountSpent);
                return true;
            }
            System.out.println("updateBudget: no budget found with budget_Id=" + budgetId);
            return false;

        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    // User methods --------
    /** insertUser
     * Inserts a new user into the Users table.
     * The password must already be hashed before calling this — never pass
     * a plain-text password here.
     *
     * @param name
     * @param email
     * @param passwordHash the SHA-256 hex string of the user's password
     * @return the generated uid on success, or -1 on failure
     */
    public int insertUser(String name, String email, String passwordHash) {
        String sql = "insert into Users (name, email, password_hash) values (?, ?, ?);";

        try (PreparedStatement prepared = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prepared.setString(1, name);
            prepared.setString(2, email);
            prepared.setString(3, passwordHash);
            prepared.executeUpdate();

            ResultSet rs = prepared.getGeneratedKeys();
            if (rs.next()) {
                int uid = rs.getInt(1);
                System.out.println("User registered: " + email + " (uid=" + uid + ")");
                return uid;
            }
            return -1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** getUserByCredentials
     * Looks up a user by email + already-hashed password.
     * Used by User.login() to validate credentials.
     *
     * @param email the email address to look up
     * @param passwordHash the SHA-256 hex string of
     * @return a populated User object on match, or null if not found
     */
    public User getUserByCredentials(String email, String passwordHash) {
        String sql = "select uid, name, email, password_hash from Users " +
                     "where email = ? and password_hash = ?;";

        try (PreparedStatement prepared = this.conn.prepareStatement(sql)) {
            prepared.setString(1, email);
            prepared.setString(2, passwordHash);
            ResultSet rs = prepared.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getInt("uid"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password_hash")
                );
            }
            return null; // No matching user found

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** linkEmailAccount
     * Stores the Gmail/Outlook address for the user.
     * Called by User.connectEmailAccount().
     * 
     * @param userId the uid of the user to link the email to
     * @param emailAddress the Gmail or Outlook address to connect
     *
     * @return true on success, false on failure
     */
    public boolean linkEmailAccount(int userId, String emailAddress) {
        String sql = "update Users set linked_email_address = ? where uid = ?;";

        try (PreparedStatement prepared = this.conn.prepareStatement(sql)) {
            prepared.setString(1, emailAddress);
            prepared.setInt(2, userId);
            int rows = prepared.executeUpdate();

            if (rows == 1) {
                System.out.println("Linked email account '" + emailAddress +
                                   "' to userId=" + userId);
                return true;
            }
            System.out.println("linkEmailAccount: no user found with uid=" + userId);
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // Transaction / Message methods

    // For testing purposes, print out all transactions in the DB
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

}
