-- Run in a NEW, empty revature_bank_web database. No sample users or balances included.
BEGIN;
CREATE TABLE bank_users (
 user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 username VARCHAR(50) NOT NULL,
 password_hash TEXT NOT NULL,
 first_name VARCHAR(100) NOT NULL,
 last_name VARCHAR(100) NOT NULL,
 CONSTRAINT bank_users_username_key UNIQUE (username)
);
CREATE TABLE bank_accounts (
 account_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 user_id BIGINT NOT NULL REFERENCES bank_users(user_id),
 account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('Checking', 'Savings')),
 balance NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
 CONSTRAINT bank_accounts_user_type_key UNIQUE (user_id, account_type)
);
COMMIT;
