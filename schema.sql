-- SentraX fraud detection system — database schema
-- Run this once against a fresh MySQL 8.x server before starting the app.

CREATE DATABASE IF NOT EXISTS sentrax;
USE sentrax;

CREATE TABLE IF NOT EXISTS app_user (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'ANALYST'
);

CREATE TABLE IF NOT EXISTS customer (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name               VARCHAR(100) NOT NULL,
    email                   VARCHAR(100),
    home_location           VARCHAR(100),
    registered_device_id    VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS transaction (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT NOT NULL,
    amount          DECIMAL(12,2) NOT NULL,
    currency        VARCHAR(10) NOT NULL,
    merchant        VARCHAR(100),
    location        VARCHAR(100),
    device_id       VARCHAR(100),
    txn_time        DATETIME NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    risk_score      DOUBLE NOT NULL DEFAULT 0,
    risk_level      VARCHAR(20) NOT NULL DEFAULT 'SAFE',
    CONSTRAINT fk_transaction_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE IF NOT EXISTS alert (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id  BIGINT NOT NULL,
    rule_name       VARCHAR(100) NOT NULL,
    message         VARCHAR(255) NOT NULL,
    created_at      DATETIME NOT NULL,
    resolved        BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_by     VARCHAR(50),
    resolved_at     DATETIME,
    CONSTRAINT fk_alert_transaction FOREIGN KEY (transaction_id) REFERENCES transaction(id)
);

-- ---------------------------------------------------------------------
-- Demo data
-- ---------------------------------------------------------------------

-- Demo login: username "analyst", password "analyst123"
-- (hash below is SHA-256 + Base64, matching PasswordUtil.hash)
INSERT INTO app_user (username, password_hash, full_name, role) VALUES
('analyst', 'ICSXSUEtc6P1eZ9vHc+RDntKo85N4TOx+KY8BEeSpOk=', 'Deepak (Analyst)', 'ANALYST');

INSERT INTO customer (full_name, email, home_location, registered_device_id) VALUES
('Ravi Kumar',   'ravi.kumar@example.com',   'Noida',    'DEV-1001'),
('Ananya Singh', 'ananya.singh@example.com', 'Delhi',    'DEV-1002'),
('Mohit Verma',  'mohit.verma@example.com',  'Lucknow',  'DEV-1003');

-- A few historical transactions so the rule engine has data to compare
-- new submissions against (average spend, last known location/device).
INSERT INTO transaction (customer_id, amount, currency, merchant, location, device_id, txn_time, status, risk_score, risk_level) VALUES
(1, 450.00, 'INR', 'Big Bazaar',      'Noida', 'DEV-1001', NOW() - INTERVAL 5 DAY, 'APPROVED', 5,  'SAFE'),
(1, 520.00, 'INR', 'Amazon',          'Noida', 'DEV-1001', NOW() - INTERVAL 3 DAY, 'APPROVED', 5,  'SAFE'),
(1, 300.00, 'INR', 'Swiggy',          'Noida', 'DEV-1001', NOW() - INTERVAL 1 DAY, 'APPROVED', 2,  'SAFE'),
(2, 1200.00,'INR', 'Myntra',          'Delhi', 'DEV-1002', NOW() - INTERVAL 4 DAY, 'APPROVED', 4,  'SAFE'),
(2, 980.00, 'INR', 'Zomato',          'Delhi', 'DEV-1002', NOW() - INTERVAL 2 DAY, 'APPROVED', 3,  'SAFE'),
(3, 700.00, 'INR', 'Reliance Trends', 'Lucknow','DEV-1003',NOW() - INTERVAL 6 DAY, 'APPROVED', 5,  'SAFE');
