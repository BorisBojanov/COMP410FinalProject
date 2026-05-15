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

## Current Prototype Features (feature/dashboard-ui)

The current JavaFX dashboard prototype includes:

- Dynamic transaction dashboard using temporary fake financial data
- Monthly, yearly, and category-based transaction filtering
- Live merchant search/filter functionality
- Interactive spending analytics dashboard
- Pie chart visualization for spending categories
- Bar chart visualization for monthly spending trends
- Scrollable transaction table
- Dynamic summary cards:
  - Total spending
  - Remaining budget
  - Transaction count
- Centralized JavaFX CSS styling system
- Modular layered architecture:
  - `dashboard/`
  - `model/`
  - `service/`
- Maven + JavaFX build configuration
- Feature-branch Git workflow

---

## Dashboard Filters

Users can currently filter dashboard data by:

- Year
- Month
- Transaction category
- Merchant name search

All dashboard components update dynamically:
- transaction table
- charts
- summary cards
- category breakdowns

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
