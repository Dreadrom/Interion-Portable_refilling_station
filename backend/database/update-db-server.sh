#!/bin/bash
# Database update script to run on server

export PGPASSWORD='GasApp2026!'

echo "Updating database schema..."
psql -h 127.0.0.1 -U gasapp -d gasapp -f /tmp/complete-schema.sql

echo ""
echo "Verifying tables..."
psql -h 127.0.0.1 -U gasapp -d gasapp -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name;"

echo ""
echo "Checking stations..."
psql -h 127.0.0.1 -U gasapp -d gasapp -c "SELECT COUNT(*) as station_count FROM Stations;"

echo ""
echo "Sample station data..."
psql -h 127.0.0.1 -U gasapp -d gasapp -c "SELECT StationID, StationName, Status FROM Stations LIMIT 3;"

echo ""
echo "Database update complete!"
