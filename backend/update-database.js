/**
 * Database Schema Update Script
 * Runs the complete database schema update using Node.js
 */

const fs = require('fs');
const { Client } = require('pg');
require('dotenv').config();

// Database configuration from .env
const dbConfig = {
  host: process.env.DB_HOST || '127.0.0.1',
  port: parseInt(process.env.DB_PORT || '5432'),
  user: process.env.DB_USER || 'gasapp',
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME || 'gasapp',
};

console.log('========================================');
console.log(' Portable Refill - Database Update');
console.log('========================================');
console.log('');
console.log('Database Configuration:');
console.log(`  Host: ${dbConfig.host}`);
console.log(`  Port: ${dbConfig.port}`);
console.log(`  Database: ${dbConfig.database}`);
console.log(`  User: ${dbConfig.user}`);
console.log('');

async function updateDatabase() {
  const client = new Client(dbConfig);

  try {
    console.log('Connecting to database...');
    await client.connect();
    console.log('✓ Connected successfully!');
    console.log('');

    // Read SQL file
    console.log('Reading schema file...');
    const sqlFilePath = './database/complete-schema.sql';
    
    if (!fs.existsSync(sqlFilePath)) {
      throw new Error(`Schema file not found: ${sqlFilePath}`);
    }

    const sql = fs.readFileSync(sqlFilePath, 'utf8');
    console.log('✓ Schema file loaded');
    console.log('');

    // Execute SQL
    console.log('Executing schema update...');
    console.log('This may take a minute...');
    console.log('');
    
    await client.query(sql);
    
    console.log('✓ Schema updated successfully!');
    console.log('');

    // Verify tables
    console.log('Verifying tables...');
    const tablesResult = await client.query(`
      SELECT table_name 
      FROM information_schema.tables 
      WHERE table_schema = 'public' 
      ORDER BY table_name
    `);

    console.log('✓ Tables in database:');
    tablesResult.rows.forEach(row => {
      console.log(`  - ${row.table_name}`);
    });
    console.log('');

    // Check stations
    console.log('Checking sample data...');
    const stationsResult = await client.query('SELECT COUNT(*) as count FROM Stations');
    const stationCount = stationsResult.rows[0].count;
    
    console.log(`✓ Stations in database: ${stationCount}`);
    
    if (stationCount > 0) {
      const stationsData = await client.query('SELECT StationID, StationName, Status FROM Stations LIMIT 5');
      console.log('');
      console.log('Sample stations:');
      stationsData.rows.forEach(station => {
        console.log(`  - ${station.stationname} (${station.status})`);
      });
    }

    console.log('');
    console.log('========================================');
    console.log('✅ Database Setup Complete!');
    console.log('========================================');
    console.log('');
    console.log('Next steps:');
    console.log('1. Fix API Gateway routing (see BACKEND_FIX_GUIDE.md)');
    console.log('2. Test the backend:');
    console.log('   cd ../portable-refill-app/playstore');
    console.log('   node backend-test.js');
    console.log('3. Test in the mobile app');
    console.log('');

  } catch (error) {
    console.error('❌ Error updating database:');
    console.error(error.message);
    console.error('');
    console.error('Troubleshooting:');
    console.error('- Check database credentials in .env file');
    console.error('- Verify database is running');
    console.error('- Check network/firewall settings');
    console.error('- If DB_HOST is 127.0.0.1, make sure PostgreSQL is running locally');
    process.exit(1);
  } finally {
    await client.end();
  }
}

// Run the update
updateDatabase().catch(err => {
  console.error('Fatal error:', err);
  process.exit(1);
});
