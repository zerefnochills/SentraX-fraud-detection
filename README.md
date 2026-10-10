# SentraX — Real-Time Transaction Fraud Detection System

SentraX is a Java web application that evaluates financial transactions
against a set of weighted fraud-detection rules and gives each one a
risk score (Safe / Suspicious / Fraud). Analysts log in to a dashboard
to submit transactions, watch scores come back in real time, and
resolve the alerts that get raised automatically.

Built for the HCL GUVI hackathon (Java Web track) using core Java,
JDBC and Servlets + JSP — no Spring, no frontend framework, so the
mapping to the marking rubric below stays clear and every line of
logic is easy to walk through in a review.

## Tech stack

| Layer          | Technology                                   |
|----------------|-----------------------------------------------|
| Language       | Java 11                                        |
| Web layer      | Servlets 4.0 + JSP (Jakarta/Java EE, Tomcat 9) |
| Persistence    | JDBC + MySQL 8                                 |
| Build          | Maven                                          |
| Tests          | JUnit 4                                        |

## Architecture

```
Browser (JSP pages)
      │  HTTP
      ▼
Servlets  (LoginServlet, DashboardServlet, TransactionServlet, AlertServlet)
      │
      ▼
FraudDetectionService  ──uses──▶  RiskScoringEngine  ──runs──▶  FraudRule implementations
      │                                                          (AmountThresholdRule, VelocityRule,
      │                                                           LocationMismatchRule, OddHourRule,
      │                                                           NewDeviceRule)
      ▼
DAO layer (TransactionDAO, CustomerDAO, AlertDAO, AppUserDAO)
      │  JDBC (PreparedStatement)
      ▼
MySQL (customer, transaction, alert, app_user)
```

Each submitted transaction is validated, scored against every rule,
saved, and — if any rule fires — has its alerts written on a small
background thread pool so the analyst's page response isn't held up by
alert persistence.

## Project structure

```
sentrax/
├── pom.xml
├── schema.sql                  ← run this first, in MySQL
├── src/main/java/com/synapsecalyx/sentrax/
│   ├── model/                  ← Transaction, Customer, Alert, AppUser, RiskLevel, Role
│   ├── exception/               ← InvalidTransactionException, FraudDetectionException
│   ├── rules/                   ← FraudRule interface + AbstractFraudRule + 5 concrete rules
│   ├── engine/                  ← RiskScoringEngine, FraudDetectionService
│   ├── dao/                     ← GenericDAO<T,ID> + Transaction/Customer/Alert/AppUser DAOs
│   ├── db/                      ← DBConnection (JDBC helper)
│   ├── util/                    ← PasswordUtil (SHA-256 hashing)
│   └── servlet/                 ← Login, Logout, Dashboard, Transaction, Alert servlets
├── src/main/webapp/
│   ├── WEB-INF/web.xml
│   ├── css/style.css
│   └── *.jsp                    ← login, dashboard, newTransaction, transactionResult, alerts, error
└── src/test/java/.../engine/RiskScoringEngineTest.java
```

## How the risk score works

Every rule looks at the new transaction plus the customer's recent
history and returns a 0–100 score. The engine combines all rule scores
using each rule's weight, caps the total at 100, and classifies it:

- **0–34 → Safe**
- **35–69 → Suspicious**
- **70–100 → Fraud** (status is set to `FLAGGED`)

| Rule                 | Weight | What it checks                                              |
|-----------------------|-------|---------------------------------------------------------------|
| Amount Threshold      | 0.30  | Amount vs. this customer's own average recent spend           |
| Transaction Velocity  | 0.25  | More than 2 transactions within a 10-minute window             |
| Location Mismatch     | 0.20  | Transaction location vs. home / last known location            |
| Odd Hour               | 0.10  | Transaction made between 1 AM–4 AM                             |
| New Device             | 0.15  | Device not previously associated with the customer             |

Adding a new rule means writing one new class that implements
`FraudRule` and adding it to the list in `RiskScoringEngine` — nothing
else in the codebase needs to change.

## Setup

**Prerequisites:** JDK 11+, Maven, MySQL 8.x, Apache Tomcat 9.

1. **Create the database**
   ```bash
   mysql -u root -p < schema.sql
   ```
   This creates the `sentrax` database, its four tables, and a demo
   login (`analyst` / `analyst123`) plus three demo customers with a
   little transaction history so new submissions have something to
   compare against.

2. **Configure the connection**
   Edit `src/main/resources/db.properties` with your MySQL username and
   password.

3. **Build the WAR**
   ```bash
   mvn clean package
   ```
   This produces `target/sentrax.war`.

4. **Deploy to Tomcat**
   Copy `target/sentrax.war` into Tomcat's `webapps/` folder (or deploy
   it through the Tomcat Manager app), then start Tomcat and open:
   ```
   http://localhost:8080/sentrax/
   ```

5. **Log in** with `analyst` / `analyst123`, then use **New Transaction**
   to submit one. Try a small amount from the customer's home location
   first (comes back Safe), then a very large amount from an unfamiliar
   location and device (comes back Fraud, and an alert appears).

6. **Run the tests**
   ```bash
   mvn test
   ```

## Marking rubric coverage (Java Web track)

| Criterion                          | Where it's demonstrated |
|------------------------------------|--------------------------|
| Problem Understanding & Solution Design | `README` architecture section; rule-based scoring engine modeling a real fraud workflow |
| Core Java Concepts | Interfaces (`FraudRule`, `GenericDAO<T,ID>`), abstract classes (`AbstractFraudRule`), inheritance (5 rule subclasses), polymorphism (engine calls every rule through one interface), custom checked/unchecked exceptions, generics, `Comparable`, enums with behavior (`RiskLevel.fromScore`), a small thread pool for async alert writes |
| Database Integration (JDBC) | `DBConnection`, `PreparedStatement`-based DAO layer, `schema.sql` with foreign keys |
| Servlets & Web Integration | 5 servlets (`@WebServlet` annotations + `web.xml`), sessions for login, request dispatch to JSP, form handling |

## Known simplifications (worth mentioning if asked)

- Passwords use SHA-256 (no per-user salt) — fine for a student
  project, but a production system should use bcrypt/Argon2.
- `DBConnection` opens a new connection per call rather than pooling
  (e.g. HikariCP) — the first thing to swap out for production load.
- Fraud detection is rule-based rather than a trained ML model, which
  keeps the logic fully auditable in Java and avoids an external ML
  dependency the course environment doesn't provide; the five rules
  mirror the signals a real fraud ML model would use as features
  (amount deviation, velocity, location, time, device).
