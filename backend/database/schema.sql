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
