-- Portable Refill Station Database Schema
-- PostgreSQL/RDS Database

-- ============================================
-- Create ENUM types
-- ============================================
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('ADMIN', 'DRIVER');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- ============================================
-- Users Table
-- ============================================
CREATE TABLE IF NOT EXISTS Users (
    UserID VARCHAR(36) PRIMARY KEY,
    UserEmail VARCHAR(255) UNIQUE NOT NULL,
    UserPassword VARCHAR(255) NOT NULL, -- bcrypt hashed
    UserName VARCHAR(255) NOT NULL,
    UserPhone VARCHAR(50),
    UserRole user_role DEFAULT 'DRIVER',
    EmailVerified BOOLEAN DEFAULT FALSE,
    EmailVerificationToken VARCHAR(255),
    EmailVerificationExpires TIMESTAMP,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for email lookups
CREATE INDEX IF NOT EXISTS idx_users_email ON Users(UserEmail);

-- Create trigger for auto-updating UpdatedAt
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.UpdatedAt = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_users_updated_at ON Users;
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON Users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- Refresh Tokens Table (for token management)
-- ============================================
CREATE TABLE IF NOT EXISTS RefreshTokens (
    TokenID VARCHAR(36) PRIMARY KEY,
    UserID VARCHAR(36) NOT NULL,
    Token TEXT NOT NULL,
    ExpiresAt TIMESTAMP NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_refreshtokens_user ON RefreshTokens(UserID);
CREATE INDEX IF NOT EXISTS idx_refreshtokens_expires ON RefreshTokens(ExpiresAt);

-- ============================================
-- Password Reset Tokens Table
-- ============================================
CREATE TABLE IF NOT EXISTS PasswordResetTokens (
    TokenID VARCHAR(36) PRIMARY KEY,
    UserID VARCHAR(36) NOT NULL,
    Token VARCHAR(255) UNIQUE NOT NULL,
    ExpiresAt TIMESTAMP NOT NULL,
    Used BOOLEAN DEFAULT FALSE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_passwordreset_token ON PasswordResetTokens(Token);
CREATE INDEX IF NOT EXISTS idx_passwordreset_user ON PasswordResetTokens(UserID);

-- ============================================
-- Insert Default Admin User (for testing)
-- Password: Admin123! (hashed with bcrypt)
-- ============================================
INSERT INTO Users (UserID, UserEmail, UserPassword, UserName, UserRole)
VALUES (
    'admin-001',
    'admin@portable-refill.com',
    '$2a$10$xQ9vX8YqP5KNJKGHjXYnJ.rVQVHZJYZH8XhYqQYqQYqQYqQYqQYqQ', -- Change this hash
    'System Admin',
    'ADMIN'
) ON CONFLICT (UserID) DO NOTHING;

-- ============================================
-- Phone OTPs Table (for phone-number login)
-- ============================================
CREATE TABLE IF NOT EXISTS PhoneOTPs (
    OTPId VARCHAR(36) PRIMARY KEY,
    Phone VARCHAR(20) NOT NULL,
    OTPCode VARCHAR(6) NOT NULL,
    ExpiresAt TIMESTAMP NOT NULL,
    Used BOOLEAN DEFAULT FALSE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_phoneotps_phone ON PhoneOTPs(Phone);

-- ============================================
-- Bank Accounts Table (for wallet cash-out)
-- ============================================
CREATE TABLE IF NOT EXISTS BankAccounts (
    AccountID VARCHAR(36) PRIMARY KEY,
    UserID VARCHAR(36) NOT NULL,
    AccountHolderName VARCHAR(255) NOT NULL,
    BankName VARCHAR(100) NOT NULL,
    AccountNumber VARCHAR(50) NOT NULL,
    DuitNowPhone VARCHAR(20),
    IsDuitNow BOOLEAN DEFAULT FALSE,
    IsDefault BOOLEAN DEFAULT FALSE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bankaccounts_user ON BankAccounts(UserID);

DROP TRIGGER IF EXISTS update_bankaccounts_updated_at ON BankAccounts;
CREATE TRIGGER update_bankaccounts_updated_at
    BEFORE UPDATE ON BankAccounts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- NOTE: To support phone-only users, run this migration on the live DB:
--   ALTER TABLE Users ALTER COLUMN UserEmail DROP NOT NULL;
--   ALTER TABLE Users ADD COLUMN IF NOT EXISTS WalletBalance NUMERIC(10,2) DEFAULT 0;
-- ============================================
