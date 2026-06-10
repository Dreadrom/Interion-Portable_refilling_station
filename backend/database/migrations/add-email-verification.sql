-- Migration: Add email verification to Users table
-- Run this on existing databases

ALTER TABLE Users 
ADD COLUMN IF NOT EXISTS EmailVerified BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS EmailVerificationToken VARCHAR(255),
ADD COLUMN IF NOT EXISTS EmailVerificationExpires TIMESTAMP;

-- Create index for verification token lookups
CREATE INDEX IF NOT EXISTS idx_users_verification_token ON Users(EmailVerificationToken);

-- For development: Mark existing users as verified (optional)
-- UPDATE Users SET EmailVerified = TRUE WHERE EmailVerified IS NULL OR EmailVerified = FALSE;
