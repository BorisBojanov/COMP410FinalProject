# BudgetApp — COMP 410 Final Project

**University of Athabasca | COMP 410 | Dr. Volodymyr Voytenko**

| Team Member | Student ID |
|---|---|
| Boris Bojanov | 3608550 |
| Michael Jean | 3361470 |

---

## Project Overview

A budgeting application that automatically tracks personal expenses by extracting transaction data from bank notification emails (Gmail/Outlook). Instead of relying on costly bank-side integrations or third-party financial APIs, the system leverages the existing email ecosystem to provide a low-cost alternative to traditional budgeting platforms.

Users connect their email account (read-only), and the app extracts transaction details (date, merchant, amount, currency) from bank alert emails, categorizes them automatically, and displays spending insights on a central dashboard.

---

## Key Features

- **Email Integration** — Connect Gmail or Outlook with read-only access; only bank alert emails are read
- **Transaction Parsing** — Extracts date, merchant, amount, and currency via regex pattern matching
- **Automatic Categorization** — Rule-based keyword mapping (e.g., "Uber" → Transport), amount-range fallback, and subscription detection
- **Deduplication** — Tracks processed email message IDs to prevent duplicate imports
- **Budget Configuration** — Set monthly spending limits per category
- **Dashboard & Analytics** — Spending totals, budget vs. actual, pie/bar charts, filterable transaction list
- **Manual Editing** — Add, edit, or delete transactions manually
- **Security** — User authentication, encrypted token storage, read-only email permissions

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI Framework | JavaFX (desktop prototype), Gluon Mobile (future mobile) |
| Email APIs | Gmail API / Microsoft Graph API (free tier) |
| Database | SQLite (local, lightweight) |
| Version Control | Git / GitHub |
| Build Tool | Maven |

---

## Current Prototype Features (feature/budget-progress-ui)

The current JavaFX dashboard prototype includes:

- Dynamic transaction dashboard connected to local SQLite-backed transaction storage
- Year and month filtering, with the current month selected by default
- **Current Month** button to quickly reset the dashboard to the current month
- Top filter row showing total spending and total transaction count for the selected period
- Pie chart visualization for spending categories
- Bar chart visualization for monthly spending trends
- Scrollable transaction table
- Manual transaction controls to add, edit, and delete transactions
- Budget progress bars by category
- Budget controls to add/edit category budgets and delete category budgets
- Centralized JavaFX CSS styling system
- Modular layered architecture:
  - `dashboard/`
  - `model/`
  - `storage/`
  - `service/`
- Maven + JavaFX build configuration
- Feature-branch Git workflow

---

## Dashboard Filters

Users can currently filter dashboard data by:

- Year
- Month

The dashboard also includes a **Current Month** button. The dashboard defaults to the current month when opened, and all dashboard components update dynamically when the selected year or month changes:

- transaction table
- pie chart
- monthly spending chart
- budget progress bars
- total spending value
- total transaction count

---

## Project Structure

```
COMP410FinalProject/
├── src/
│   ├── main/java/com/budgetapp/
│   │   ├── email/          # Email retrieval & filtering
│   │   ├── parser/         # Transaction parsing engine
│   │   ├── model/          # Data models (Transaction, Category, Budget)
│   │   ├── storage/        # Database access layer
│   │   ├── categorizer/    # Auto-categorization logic
│   │   ├── dashboard/      # UI controllers & views
│   │   └── auth/           # Authentication & token management
│   └── test/java/com/budgetapp/
├── docs/                   # Design documents, diagrams
├── resources/              # Config files, category keyword mappings
└── README.md
```

---

## Branching Strategy

```
main          → stable, production-ready code
development   → integration branch (PRs merged here first)
feature/*     → isolated feature development branches
```

All feature work is done on `feature/<feature-name>` branches and merged into `development` via pull request. `development` is merged into `main` only after testing.

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- A Gmail or Outlook account with bank transaction alerts enabled

### Setup
```bash
git clone https://github.com/BorisBojanov/COMP410FinalProject.git
cd COMP410FinalProject
mvn install
```

> Full setup instructions (email API credentials, database config) will be added as the project progresses.

### Testing the JavaFX Dashboard UI

To test the current dashboard UI, switch to the feature branch that contains the budget progress dashboard work:

```bash
git switch feature/budget-progress-ui
```

Make sure the branch is up to date:

```bash
git pull origin feature/budget-progress-ui
```

Run the JavaFX application with Maven:

```bash
mvn javafx:run
```

When the app opens, verify the following UI features:

- The dashboard opens without errors.
- The top filter row shows **Year**, **Month**, **Current Month**, **Total Spend**, and **Total Transactions**.
- The dashboard defaults to the current month.
- Changing the year or month updates the transaction table, pie chart, monthly spending chart, budget progress bars, total spend, and total transactions.
- The **Current Month** button returns the dashboard to the current month.
- Transactions can be added, edited, and deleted from the transaction table controls.
- Category budgets can be added or edited using **Add/Edit Budget**.
- Category budgets can be removed using **Delete Budget**.
- Budget progress bars update after budget or transaction changes.

If the app does not start, first confirm that Java and Maven are installed:

```bash
java -version
mvn -version
```

Then confirm you are on the correct branch:

```bash
git branch
```

The active branch should show:

```bash
* feature/budget-progress-ui
```

---

## Development Roadmap

**Increment 1 — Core Backend**
- [ ] Email retrieval (Gmail API integration)
- [ ] Transaction extraction (regex parser)
- [ ] Local database storage + deduplication

**Increment 2 — Categorization & Budget**
- [ ] Keyword-based auto-categorization
- [ ] Budget configuration per category
- [ ] Email account connection UI

**Increment 3 — Dashboard & Analytics**
- [ ] Spending dashboard with pie/bar charts
- [ ] Budget vs. actual tracking
- [ ] Manual transaction add/edit/delete

**Increment 4 — Polish & Mobile**
- [ ] Subscription detection
- [ ] Cross-platform (iOS/Android via Gluon Mobile)
- [ ] User authentication

---

## Process Model

Incremental + Evolutionary development with prototyping, following Pressman's generic process framework. Requirements are expected to evolve — particularly around email format variability and parsing logic — so each increment delivers a working, testable version of the system.
