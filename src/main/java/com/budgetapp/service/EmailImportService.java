package com.budgetapp.service;

import com.budgetapp.email.email;
import com.budgetapp.parser.parser;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Message;

public class EmailImportService {

    public static class ImportResult {
        private final int importedCount;
        private final int scannedCount;
        private final String message;

        public ImportResult(int importedCount, int scannedCount, String message) {
            this.importedCount = importedCount;
            this.scannedCount = scannedCount;
            this.message = message;
        }

        public int getImportedCount() {
            return importedCount;
        }

        public int getScannedCount() {
            return scannedCount;
        }

        public String getMessage() {
            return message;
        }
    }

    public ImportResult importTransactionsFromEmail() {
        email emailClient = null;

        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String emailReceiver = dotenv.get("GMAIL_ADDRESS");
            String appPassword = dotenv.get("GMAIL_APP_PASSWORD");
            String bankSender = dotenv.get("BANK_SENDER");

            if (isBlank(emailReceiver) || isBlank(appPassword) || isBlank(bankSender)) {
                return new ImportResult(0, 0,
                        "Email import skipped. Add GMAIL_ADDRESS, GMAIL_APP_PASSWORD, and BANK_SENDER to your .env file.");
            }

            emailClient = new email(emailReceiver, appPassword);

            if (emailClient.getStore() == null) {
                return new ImportResult(0, 0, "Could not connect to the Gmail IMAP server. Check your email/app password.");
            }

            emailClient.openInbox();

            if (emailClient.getFolder() == null) {
                return new ImportResult(0, 0, "Could not open the inbox folder.");
            }

            Message[] messages = emailClient.getMessages(bankSender);

            if (messages == null || messages.length == 0) {
                return new ImportResult(0, 0, "No matching transaction emails found from: " + bankSender);
            }

            parser parser = new parser();
            int imported = 0;

            for (Message msg : messages) {
                String messageId = email.getMessageID(msg);
                String body = email.getText(msg);
                String subject = msg.getSubject();
                String sender = msg.getFrom() == null || msg.getFrom().length == 0 ? "unknown" : msg.getFrom()[0].toString();
                String date = msg.getReceivedDate() == null ? "unknown" : msg.getReceivedDate().toString();

                if (parser.processEmail(messageId, date, subject, sender, body)) {
                    imported++;
                }
            }

            return new ImportResult(imported, messages.length,
                    "Scanned " + messages.length + " emails and imported " + imported + " new transactions.");

        } catch (Exception e) {
            e.printStackTrace();
            return new ImportResult(0, 0, "Email import failed: " + e.getMessage());
        } finally {
            if (emailClient != null) {
                emailClient.close();
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
