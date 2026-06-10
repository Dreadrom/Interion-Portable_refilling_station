-- COMPLETE DATABASE SCHEMA
-- Add these tables to your existing database

-- ============================================
-- Stations Table
-- ============================================
CREATE TABLE IF NOT EXISTS Stations (
    StationID VARCHAR(36) PRIMARY KEY,
    StationName VARCHAR(255) NOT NULL,
    Address TEXT,
    Latitude NUMERIC(10, 7) NOT NULL,
    Longitude NUMERIC(10, 7) NOT NULL,
    Timezone VARCHAR(50) DEFAULT 'Asia/Kuala_Lumpur',
    Status VARCHAR(20) DEFAULT 'IDLE' CHECK (Status IN ('IDLE', 'DISPENSING', 'ALARM', 'OFFLINE', 'MAINTENANCE')),
    LastHeartbeat TIMESTAMP,
    
    -- PTS Controller connection details
    PTSHost VARCHAR(255),
    PTSPort INTEGER DEFAULT 8080,
    PTSProtocol VARCHAR(10) DEFAULT 'HTTP' CHECK (PTSProtocol IN ('HTTP', 'HTTPS')),
    
    -- Configuration
    MaxDispenseVolume NUMERIC(10, 2) DEFAULT 100.00,
    MaxDispenseAmount NUMERIC(10, 2) DEFAULT 500.00,
    MaintenanceMode BOOLEAN DEFAULT FALSE,
    Enabled BOOLEAN DEFAULT TRUE,
    
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_stations_status ON Stations(Status);
CREATE INDEX IF NOT EXISTS idx_stations_location ON Stations(Latitude, Longitude);

DROP TRIGGER IF EXISTS update_stations_updated_at ON Stations;
CREATE TRIGGER update_stations_updated_at
    BEFORE UPDATE ON Stations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- Tanks Table
-- ============================================
CREATE TABLE IF NOT EXISTS Tanks (
    TankID VARCHAR(36) PRIMARY KEY,
    StationID VARCHAR(36) NOT NULL,
    Product VARCHAR(50) NOT NULL CHECK (Product IN ('RON95', 'RON97', 'DIESEL', 'PREMIUM_DIESEL', 'AdBlue')),
    CapacityLitres NUMERIC(10, 2) NOT NULL,
    LevelLitres NUMERIC(10, 2) DEFAULT 0,
    TemperatureC NUMERIC(5, 2),
    LowLevelAlarm BOOLEAN DEFAULT FALSE,
    HighLevelAlarm BOOLEAN DEFAULT FALSE,
    LastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (StationID) REFERENCES Stations(StationID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_tanks_station ON Tanks(StationID);

-- ============================================
-- Pricing Table
-- ============================================
CREATE TABLE IF NOT EXISTS Pricing (
    PricingID VARCHAR(36) PRIMARY KEY,
    StationID VARCHAR(36) NOT NULL,
    Product VARCHAR(50) NOT NULL CHECK (Product IN ('RON95', 'RON97', 'DIESEL', 'PREMIUM_DIESEL', 'AdBlue')),
    UnitPrice NUMERIC(10, 2) NOT NULL,
    Currency VARCHAR(3) DEFAULT 'MYR',
    EffectiveFrom TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    EffectiveTo TIMESTAMP,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (StationID) REFERENCES Stations(StationID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_pricing_station ON Pricing(StationID);
CREATE INDEX IF NOT EXISTS idx_pricing_product ON Pricing(Product);

-- ============================================
-- Pumps Table
-- ============================================
CREATE TABLE IF NOT EXISTS Pumps (
    PumpID VARCHAR(36) PRIMARY KEY,
    StationID VARCHAR(36) NOT NULL,
    PumpNumber INTEGER NOT NULL,
    Status VARCHAR(20) DEFAULT 'IDLE' CHECK (Status IN ('IDLE', 'IN_USE', 'FAULT', 'OFFLINE')),
    LastUsed TIMESTAMP,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (StationID) REFERENCES Stations(StationID) ON DELETE CASCADE,
    UNIQUE(StationID, PumpNumber)
);

CREATE INDEX IF NOT EXISTS idx_pumps_station ON Pumps(StationID);

-- ============================================
-- Payments Table
-- ============================================
CREATE TABLE IF NOT EXISTS Payments (
    PaymentID VARCHAR(36) PRIMARY KEY,
    UserID VARCHAR(36) NOT NULL,
    Amount NUMERIC(10, 2) NOT NULL,
    Currency VARCHAR(3) DEFAULT 'MYR',
    
    -- Payment gateway details (e.g., Fiuu/MOLPay)
    GatewayTransactionID VARCHAR(255),
    GatewayName VARCHAR(50) DEFAULT 'FIUU',
    
    -- Status
    Status VARCHAR(20) DEFAULT 'PENDING' CHECK (Status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED')),
    
    -- Payment method
    PaymentMethod VARCHAR(50), -- e.g., 'credit', 'fpx', 'tng', etc.
    
    -- Timestamps
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CompletedAt TIMESTAMP,
    
    -- Additional data
    Metadata JSONB,
    
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_payments_user ON Payments(UserID);
CREATE INDEX IF NOT EXISTS idx_payments_status ON Payments(Status);
CREATE INDEX IF NOT EXISTS idx_payments_gateway_tx ON Payments(GatewayTransactionID);

-- ============================================
-- Wallets Table (for user balance)
-- ============================================
CREATE TABLE IF NOT EXISTS Wallets (
    WalletID VARCHAR(36) PRIMARY KEY,
    UserID VARCHAR(36) UNIQUE NOT NULL,
    Balance NUMERIC(10, 2) DEFAULT 0.00 CHECK (Balance >= 0),
    Currency VARCHAR(3) DEFAULT 'MYR',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_wallets_user ON Wallets(UserID);

DROP TRIGGER IF EXISTS update_wallets_updated_at ON Wallets;
CREATE TRIGGER update_wallets_updated_at
    BEFORE UPDATE ON Wallets
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- Wallet Transactions Table (for audit trail)
-- ============================================
CREATE TABLE IF NOT EXISTS WalletTransactions (
    WalletTxID VARCHAR(36) PRIMARY KEY,
    WalletID VARCHAR(36) NOT NULL,
    Type VARCHAR(20) NOT NULL CHECK (Type IN ('TOP_UP', 'DEBIT', 'REFUND', 'ADJUSTMENT')),
    Amount NUMERIC(10, 2) NOT NULL,
    BalanceBefore NUMERIC(10, 2) NOT NULL,
    BalanceAfter NUMERIC(10, 2) NOT NULL,
    Reference VARCHAR(255), -- e.g., PaymentID or TransactionID
    Description TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (WalletID) REFERENCES Wallets(WalletID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_wallettx_wallet ON WalletTransactions(WalletID);
CREATE INDEX IF NOT EXISTS idx_wallettx_reference ON WalletTransactions(Reference);

-- ============================================
-- Transactions Table (Dispense transactions)
-- ============================================
CREATE TABLE IF NOT EXISTS Transactions (
    TransactionID VARCHAR(36) PRIMARY KEY,
    StationID VARCHAR(36) NOT NULL,
    UserID VARCHAR(36) NOT NULL,
    PaymentID VARCHAR(36),
    
    -- Dispense details
    Nozzle INTEGER NOT NULL,
    Product VARCHAR(50) NOT NULL CHECK (Product IN ('RON95', 'RON97', 'DIESEL', 'PREMIUM_DIESEL', 'AdBlue')),
    
    -- Preset (what user requested)
    PresetType VARCHAR(10) CHECK (PresetType IN ('VOLUME', 'AMOUNT')),
    PresetVolumeLitres NUMERIC(10, 3),
    PresetAmount NUMERIC(10, 2),
    
    -- Actual results
    ActualVolumeLitres NUMERIC(10, 3) DEFAULT 0,
    UnitPrice NUMERIC(10, 2) NOT NULL,
    TotalAmount NUMERIC(10, 2) DEFAULT 0,
    
    -- Status
    Status VARCHAR(20) DEFAULT 'PENDING' CHECK (Status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'STOPPED', 'FAILED', 'CANCELLED')),
    StopReason VARCHAR(50),
    
    -- Timestamps
    StartTime TIMESTAMP,
    EndTime TIMESTAMP,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Additional data
    Metadata JSONB,
    
    FOREIGN KEY (StationID) REFERENCES Stations(StationID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (PaymentID) REFERENCES Payments(PaymentID) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_transactions_station ON Transactions(StationID);
CREATE INDEX IF NOT EXISTS idx_transactions_user ON Transactions(UserID);
CREATE INDEX IF NOT EXISTS idx_transactions_payment ON Transactions(PaymentID);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON Transactions(Status);
CREATE INDEX IF NOT EXISTS idx_transactions_created ON Transactions(CreatedAt DESC);

DROP TRIGGER IF EXISTS update_transactions_updated_at ON Transactions;
CREATE TRIGGER update_transactions_updated_at
    BEFORE UPDATE ON Transactions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- Alarms Table
-- ============================================
CREATE TABLE IF NOT EXISTS Alarms (
    AlarmID VARCHAR(36) PRIMARY KEY,
    StationID VARCHAR(36) NOT NULL,
    AlarmType VARCHAR(50) NOT NULL, -- e.g., 'TANK_LOW', 'TANK_HIGH', 'PUMP_FAULT', 'SYSTEM_ERROR'
    Severity VARCHAR(20) DEFAULT 'WARNING' CHECK (Severity IN ('INFO', 'WARNING', 'ERROR', 'CRITICAL')),
    Message TEXT NOT NULL,
    Acknowledged BOOLEAN DEFAULT FALSE,
    AcknowledgedBy VARCHAR(36),
    AcknowledgedAt TIMESTAMP,
    Resolved BOOLEAN DEFAULT FALSE,
    ResolvedAt TIMESTAMP,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (StationID) REFERENCES Stations(StationID) ON DELETE CASCADE,
    FOREIGN KEY (AcknowledgedBy) REFERENCES Users(UserID) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_alarms_station ON Alarms(StationID);
CREATE INDEX IF NOT EXISTS idx_alarms_resolved ON Alarms(Resolved);
CREATE INDEX IF NOT EXISTS idx_alarms_severity ON Alarms(Severity);

-- ============================================
-- Insert Sample Data for Testing
-- ============================================

-- Sample Station (Kuala Lumpur area)
INSERT INTO Stations (StationID, StationName, Address, Latitude, Longitude, Status, PTSHost, PTSPort, Enabled)
VALUES (
    'station-001',
    'AceRev AdBlue Station KL',
    '123 Jalan Sultan Ismail, Kuala Lumpur',
    3.1478,
    101.6953,
    'IDLE',
    'localhost',  -- Change this to your actual PTS controller host
    8080,
    TRUE
) ON CONFLICT (StationID) DO NOTHING;

-- Sample Tank for the station
INSERT INTO Tanks (TankID, StationID, Product, CapacityLitres, LevelLitres, TemperatureC)
VALUES (
    'tank-001',
    'station-001',
    'AdBlue',
    10000.00,
    7500.00,
    25.5
) ON CONFLICT (TankID) DO NOTHING;

-- Sample Pricing
INSERT INTO Pricing (PricingID, StationID, Product, UnitPrice, Currency, EffectiveFrom)
VALUES (
    'price-001',
    'station-001',
    'AdBlue',
    2.50,
    'MYR',
    CURRENT_TIMESTAMP
) ON CONFLICT (PricingID) DO NOTHING;

-- Sample Pump
INSERT INTO Pumps (PumpID, StationID, PumpNumber, Status)
VALUES (
    'pump-001',
    'station-001',
    1,
    'IDLE'
) ON CONFLICT (PumpID) DO NOTHING;

-- ============================================
-- Create Wallet for existing users
-- ============================================
INSERT INTO Wallets (WalletID, UserID, Balance)
SELECT 
    'wallet-' || UserID,
    UserID,
    0.00
FROM Users
WHERE NOT EXISTS (
    SELECT 1 FROM Wallets WHERE Wallets.UserID = Users.UserID
);

-- ============================================
-- Database Views for Common Queries
-- ============================================

-- View: Active Stations with current tank levels
CREATE OR REPLACE VIEW ActiveStations AS
SELECT 
    s.StationID,
    s.StationName,
    s.Address,
    s.Latitude,
    s.Longitude,
    s.Status,
    s.LastHeartbeat,
    json_agg(
        json_build_object(
            'product', t.Product,
            'levelLitres', t.LevelLitres,
            'capacityLitres', t.CapacityLitres,
            'percentFull', ROUND((t.LevelLitres / NULLIF(t.CapacityLitres, 0)) * 100, 2)
        )
    ) AS tanks
FROM Stations s
LEFT JOIN Tanks t ON s.StationID = t.StationID
WHERE s.Enabled = TRUE
GROUP BY s.StationID, s.StationName, s.Address, s.Latitude, s.Longitude, s.Status, s.LastHeartbeat;

-- View: User Transaction History
CREATE OR REPLACE VIEW UserTransactionHistory AS
SELECT 
    t.TransactionID,
    t.UserID,
    t.StationID,
    s.StationName,
    t.Product,
    t.ActualVolumeLitres,
    t.TotalAmount,
    t.Status,
    t.CreatedAt,
    t.EndTime
FROM Transactions t
INNER JOIN Stations s ON t.StationID = s.StationID
ORDER BY t.CreatedAt DESC;

-- ============================================
-- Success Message
-- ============================================
DO $$
BEGIN
    RAISE NOTICE '✅ Database schema updated successfully!';
    RAISE NOTICE '📊 Sample data inserted (1 station, 1 tank, 1 pricing, 1 pump)';
    RAISE NOTICE '🎯 Ready for testing!';
END $$;
