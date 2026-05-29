BudgetApp — COMP 410 Final Project
University of Athabasca | COMP 410 | Dr. Volodymyr Voytenko
Team Member	Student ID
Boris Bojanov	3608550
Michael Jean	3361470
---
Project Overview
BudgetApp is a JavaFX desktop budgeting prototype developed for the COMP 410 final project. The app helps users track personal spending by storing transactions locally, categorizing expenses, displaying budget progress, and showing spending analytics on a dashboard.
The original project goal was to extract transaction data from bank notification emails. The current submitted prototype implements the core budgeting workflow and includes partial email-import support. The working version focuses on local SQLite storage, manual transaction management, budget tracking, keyword-based categorization, dashboard charts, and parser/email-import components prepared for testing.
---
Current Status
The coding portion is complete as a test-ready prototype. Automated tests have passed successfully, and the app is ready for manual testing, screenshots, and final report documentation.
Final automated test result:
```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
---
Implemented Features
JavaFX Dashboard
Main dashboard screen
Total spending summary
Total transaction count
Budget progress section
Pie chart for category spending
Bar chart for monthly spending trends
Scrollable transaction table
Transaction Management
Add manual transactions
Edit transactions
Delete transactions
Persist transactions in SQLite after restart
Budget Management
Add or update category budgets
Delete category budgets
Persist budgets in SQLite after restart
Show progress bars for budget usage
SQLite Storage
Local `budget.db` database
`Messages` table
`Transactions` table
`Budgets` table
Duplicate message checking using email message IDs
Parser Logic
Extracts transaction amount, date, and merchant from controlled email-body text
Rejects incomplete email content
Saves parsed transactions into SQLite
Categorization
Keyword-based categorization through `CategoryService`
Example mappings:
Starbucks → Food
Uber → Transport
Netflix → Subscription
Amazon → Shopping
Unknown merchants default to `Uncategorized`
Partial Email Import Support
Gmail IMAP connection structure
Read-only inbox access
Sender-based email filtering
Email body extraction
Message-ID extraction for deduplication
`Import Email Transactions` button in the UI
Safe handling when `.env` credentials are missing
Testing
JUnit tests for email filter creation
JUnit tests for parser extraction
JUnit tests for invalid parser input
JUnit tests for manual transaction storage
JUnit tests for budget storage
---
Known Limitations
The following items were planned in the proposal but are not fully production-ready in the current prototype:
Full Gmail integration with real bank emails has not been fully validated with live credentials.
Outlook/Microsoft Graph integration is not implemented as a working import path.
User authentication/login screen is not implemented.
Advanced multi-bank parsing templates are not implemented.
Encryption and production-grade credential storage are not fully implemented.
Subscription detection remains a future improvement.
Mobile deployment through Gluon Mobile is future work.
These limitations should be described in the final report as scope and time limitations, not as completed production features.
---
Tech Stack
Layer	Technology
Language	Java 21
UI Framework	JavaFX
Build Tool	Maven
Database	SQLite
Email Library	Jakarta Mail
Environment Variables	dotenv
Testing	JUnit 5
Version Control	Git / GitHub
---
Project Structure
```text
COMP410FinalProject/
├── src/
│   ├── main/
│   │   ├── java/com/budgetapp/
│   │   │   ├── App.java
│   │   │   ├── Main.java
│   │   │   ├── dashboard/
│   │   │   │   └── DashboardView.java
│   │   │   ├── email/
│   │   │   │   └── email.java
│   │   │   ├── model/
│   │   │   │   └── Transaction.java
│   │   │   ├── parser/
│   │   │   │   └── parser.java
│   │   │   ├── service/
│   │   │   │   ├── CategoryService.java
│   │   │   │   └── EmailImportService.java
│   │   │   └── storage/
│   │   │       └── storage.java
│   │   └── resources/
│   │       └── styles.css
│   └── test/
│       └── java/com/budgetapp/
│           ├── email/
│           │   └── EmailTest.java
│           ├── parser/
│           │   └── ParserTest.java
│           └── storage/
│               └── StorageTest.java
├── pom.xml
└── README.md
```
---
Prerequisites
Install the following before running the project:
Java 21
Maven 3.9+
Git
Check versions:
```bash
java -version
mvn -version
```
If Maven is installed but `mvn` does not work in Git Bash, use the full Maven path:
```bash
"/c/Program Files/apache-maven-3.9.15/bin/mvn.cmd" -version
```
---
How to Run the App
Clone the repository:
```bash
git clone https://github.com/BorisBojanov/COMP410FinalProject.git
cd COMP410FinalProject
```
Make sure you are on the `main` branch:
```bash
git switch main
git pull
```
Run the JavaFX app:
```bash
mvn javafx:run
```
If `mvn` is not recognized in Git Bash, run:
```bash
"/c/Program Files/apache-maven-3.9.15/bin/mvn.cmd" javafx:run
```
When the app opens, the dashboard should display sample transactions such as Starbucks, Uber, and Netflix if the local database is empty.
---
How to Run Automated Tests
Run:
```bash
mvn test
```
If Maven is not available through the normal command, run:
```bash
"/c/Program Files/apache-maven-3.9.15/bin/mvn.cmd" test
```
Expected successful result:
```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
---
Manual Testing Checklist
[ ] App opens without errors.
[ ] Dashboard title appears.
[ ] Sample transactions appear when the database is empty.
[ ] Total Spend displays correctly.
[ ] Total Transactions displays correctly.
[ ] Pie chart displays spending by category.
[ ] Monthly spending chart displays spending trend.
[ ] Budget progress bars display correctly.
[ ] Add Transaction works.
[ ] Edit Transaction works.
[ ] Delete Transaction works.
[ ] Add/Edit Budget works.
[ ] Delete Budget works.
[ ] Data remains after closing and reopening the app.
[ ] Import Email Transactions button shows a safe message if `.env` is missing.
[ ] Automated tests pass with Maven.
---
Development Roadmap
Completed for Prototype
[x] JavaFX dashboard
[x] SQLite transaction storage
[x] Manual transaction add/edit/delete
[x] Budget progress display
[x] Persistent budget storage
[x] Keyword-based categorization
[x] Parser component
[x] Email import service structure
[x] Import button with safe missing-credentials handling
[x] JUnit automated tests
[x] Maven test success
Future Improvements
[ ] Full Gmail live testing with real bank alert emails
[ ] Outlook/Microsoft Graph integration
[ ] User login/authentication
[ ] Encrypted token storage
[ ] Advanced parser templates for multiple banks
[ ] Subscription detection
[ ] More complete security controls
[ ] Mobile version through Gluon Mobile
[ ] More extensive integration testing
---
Process Model
The project follows an incremental and evolutionary process model with prototyping. This approach fits the project because the most uncertain features involve email formats, parser behavior, and dashboard usability. Each increment adds a working feature that can be reviewed, tested, and improved.
---
