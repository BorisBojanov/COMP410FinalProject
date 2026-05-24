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

public class storage {

    private Connection conn;
    private static storage instance;
    private String connectionUrl = "jdbc:sqlite:budget.db";

    private storage() {
        connect(connectionUrl);
        creatDBTables();
    }

    private void connect(String url) {
        try {
            this.conn = DriverManager.getConnection(url);

            if (this.conn != null) {
                System.out.println("Connected to SQLite!");
            } else {
                System.out.println("Failed to connect to SQLite.");
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

    public void creatDBTables() {
        String createTableMessages = "create table if not exists Messages (" +
                "mid integer primary key autoincrement, " +
                "message_id text unique, " +
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
                "message_id integer, " +
                "foreign key (message_id) references Messages(mid)" +
                ");";

        try (var statement = this.conn.createStatement()) {
            statement.execute(createTableMessages);
            statement.execute(createTableTransactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int insertMessage(String messageId, String dataReceived, String subject, String sender, String body) {
        String sql = "insert into Messages (message_id, date_received, subject, sender, body) values (?, ?, ?, ?, ?);";

        try (PreparedStatement prepared = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prepared.setString(1, messageId);
            prepared.setString(2, dataReceived);
            prepared.setString(3, subject);
            prepared.setString(4, sender);
            prepared.setString(5, body);

            prepared.executeUpdate();

            ResultSet rs = prepared.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return -1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean insertTransaction(Double amount, String date, String merchant, String category, int mid) {
        String sql = "insert into Transactions (amount, date, merchant, category, message_id) values (?, ?, ?, ?, ?);";

        try (PreparedStatement prepared = this.conn.prepareStatement(sql)) {
            prepared.setDouble(1, amount);
            prepared.setString(2, date);
            prepared.setString(3, merchant);
            prepared.setString(4, category);
            prepared.setInt(5, mid);

            return prepared.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkDouplicateMessages(String messageId) {
        String sql = "SELECT mid FROM Messages WHERE message_id = ?;";

        try (PreparedStatement statement = this.conn.prepareStatement(sql)) {
            statement.setString(1, messageId);
            ResultSet rs = statement.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transaction> getTransactions() {
        String sql = "SELECT * FROM Transactions;";

        try (PreparedStatement statement = this.conn.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            List<Transaction> transactions = new ArrayList<>();

            while (rs.next()) {
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
            return new ArrayList<>();
        }
    }

    public void seedSampleData() {
        if (!getTransactions().isEmpty()) {
            return;
        }

        int mid1 = insertMessage("sample-message-1", "2026-05-01", "Transaction Alert", "bank@example.com", "Starbucks purchase");
        insertTransaction(6.25, "2026-05-01", "Starbucks", "Food", mid1);

        int mid2 = insertMessage("sample-message-2", "2026-05-02", "Transaction Alert", "bank@example.com", "Uber purchase");
        insertTransaction(18.40, "2026-05-02", "Uber", "Transport", mid2);

        int mid3 = insertMessage("sample-message-3", "2026-05-03", "Transaction Alert", "bank@example.com", "Netflix purchase");
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
