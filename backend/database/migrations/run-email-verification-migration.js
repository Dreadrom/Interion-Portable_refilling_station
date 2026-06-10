/**
 * Migration script to add email verification columns
 */

const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
});

async function migrate() {
  console.log('Running email verification migration...');
  
  try {
    await pool.query(`
      ALTER TABLE Users 
      ADD COLUMN IF NOT EXISTS EmailVerified BOOLEAN DEFAULT FALSE,
      ADD COLUMN IF NOT EXISTS EmailVerificationToken VARCHAR(255),
      ADD COLUMN IF NOT EXISTS EmailVerificationExpires TIMESTAMP
    `);
    
    console.log('✅ Added email verification columns');
    
    await pool.query(`
      CREATE INDEX IF NOT EXISTS idx_users_verification_token ON Users(EmailVerificationToken)
    `);
    
    console.log('✅ Created verification token index');
    
    // Optional: Mark existing users as verified for development
    const result = await pool.query(`
      UPDATE Users SET EmailVerified = TRUE WHERE EmailVerified IS NULL OR EmailVerified = FALSE
    `);
    
    console.log(`✅ Marked ${result.rowCount} existing users as verified`);
    
    console.log('\n🎉 Migration completed successfully!');
  } catch (error) {
    console.error('❌ Migration failed:', error);
    process.exit(1);
  } finally {
    await pool.end();
  }
}

migrate();
