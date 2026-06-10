# 🖥️ Hardware OS Deployment Guide

**Date:** May 25, 2026  
**Platform:** ODROID-M1S / M2 with Ubuntu 20.04  
**Service:** BlueDiesel Hardware Gateway (Python)  
**Status:** ✅ Code Ready, ⚠️ Awaiting Physical Deployment

---

## 📋 OVERVIEW

The **BlueDiesel Hardware Gateway Service** runs on a single-board computer (ODROID) at each refilling station. It:

- Connects to **AWS IoT Core** via MQTT (secure, bidirectional)
- Controls **physical hardware** (solenoid valve, flow meter, pump)
- Publishes **real-time telemetry** (volume, rate, status)
- Receives **dispense commands** from mobile app (via backend)
- Manages **local display** (shows authorization codes)
- Provides **local diagnostics API** (http://localhost:8080)

---

## 🎯 DEPLOYMENT STATUS

### ✅ What's Ready

| Component | Status | Location |
|---|---|---|
| **Gateway Service Code** | ✅ Complete | `hardware_service/` folder |
| **Configuration Template** | ✅ Ready | `.env.example` |
| **STUB Mode** | ✅ Working | Simulated hardware for testing |
| **Python Dependencies** | ✅ Installed | `requirements.txt` |
| **MQTT Client** | ✅ Ready | AWS IoT Core integration |
| **State Machine** | ✅ Ready | IDLE → AUTH → DISPENSE → COMPLETE |
| **Display Driver** | ✅ Ready | SSD1306 OLED support |
| **Diagnostics API** | ✅ Ready | Flask on port 8080 |

### ⚠️ What's Needed

| Component | Status | Action Required |
|---|---|---|
| **ODROID Hardware** | ❌ Not purchased | Buy ODROID-M1S (8GB) - ~$70 |
| **Ubuntu Image** | ⚠️ Need to flash | Download Ubuntu 20.04 for ODROID |
| **AWS IoT Thing** | ⚠️ Need setup | Create Thing in AWS IoT Core |
| **IoT Certificates** | ❌ Not generated | Download from AWS IoT Core |
| **Physical Sensors** | ❌ Not installed | Flow meter, solenoid valve |
| **PLC Controller** | ❌ Not deployed | Siemens LOGO! 8.3 or GPIO |
| **Field Installation** | ❌ Not done | Mount at station, wire hardware |

---

## 🛠️ DEPLOYMENT OPTIONS

You have **3 deployment strategies**:

### **Option 1: Full Production (Recommended)**
Deploy to physical ODROID at a real refilling station

**Requirements:**
- ODROID-M1S microcontroller (~$70)
- Ubuntu 20.04 LTS installed
- Physical hardware connected
- AWS IoT Core configured
- Internet connectivity (4G/WiFi)

**Timeline:** 2-4 weeks  
**Cost:** ~$400 per station  
**Status:** ⚠️ Ready when hardware arrives

---

### **Option 2: Simulated Testing (Current)**
Run on your PC/laptop in STUB mode (no hardware)

**Requirements:**
- ✅ Python 3.11+ (you have 3.14)
- ✅ Dependencies installed
- ⚠️ AWS IoT Core (for MQTT) OR skip MQTT testing

**Timeline:** Immediate (today!)  
**Cost:** $0  
**Status:** ✅ Can start now!

**Limitation:** MQTT requires AWS IoT certificates (or mock)

---

### **Option 3: Hybrid Testing**
Run on Raspberry Pi/ODROID without physical sensors

**Requirements:**
- ODROID-M1S or Raspberry Pi 4/5
- Ubuntu 20.04 installed
- AWS IoT Core Thing created
- No physical hardware needed (STUB mode)

**Timeline:** 1 week (hardware shipping)  
**Cost:** ~$70-100  
**Status:** Good middle ground for testing

---

## 📦 HARDWARE BILL OF MATERIALS

### Per Station (Full Production)

| Item | Part Number / Spec | Qty | Est. Cost | Priority |
|---|---|---|---|---|
| **Microcontroller** | ODROID-M1S (8GB RAM) | 1 | $70 | 🔴 Critical |
| **MicroSD Card** | 32GB Class 10 (for OS) | 1 | $10 | 🔴 Critical |
| **PLC** | Siemens LOGO! 8.3 (6ED1052-1MD08-0BA1) | 1 | $160 | 🟡 Optional* |
| **Solenoid Valve** | 24VDC NC, DN20, SS316 body | 1 | $40 | 🔴 Critical |
| **Flow Sensor** | Digmesa FHS series (±0.5%) | 1 | $70 | 🔴 Critical |
| **Display** | SSD1306 OLED 128×64 I2C | 1 | $5 | 🟢 Optional |
| **Power Supply** | 24VDC 2A DIN rail PSU | 1 | $25 | 🔴 Critical |
| **Relay Module** | 4-channel 24VDC trigger | 1 | $15 | 🟡 If no PLC |
| **Enclosure** | IP65 waterproof, DIN rail | 1 | $30 | 🟡 Recommended |
| **Wiring** | 18 AWG, terminal blocks | - | $20 | 🔴 Critical |
| **Network** | 4G modem OR Ethernet cable | 1 | $50 | 🔴 Critical |

**Total:** ~$495 per station (with PLC)  
**Total:** ~$345 per station (GPIO only, no PLC)*

\* *PLC provides industrial reliability but is optional for pilot testing*

---

## 🚀 SETUP GUIDE - OPTION 2 (Simulated Testing)

### Step 1: Verify Prerequisites ✅

```powershell
# Check Python (you have 3.14 ✅)
python --version

# Navigate to hardware service
cd c:\Users\songj\Interion\Interion-Portable_refilling_station\hardware_service

# Check .env file exists ✅
Get-Content .env
```

### Step 2: AWS IoT Core Setup (MQTT)

#### 2A. Create IoT Thing

1. Go to AWS Console: https://console.aws.amazon.com/iot
2. Navigate to: **Manage → Things**
3. Click: **Create things**
4. Select: **Create single thing**
5. Thing name: `acerev-demo-station-001`
6. Click: **Next**

#### 2B. Generate Certificates

1. Choose: **Auto-generate a new certificate**
2. Click: **Next**
3. Create a policy (or attach existing):
   - Name: `AceRevStationPolicy`
   - Policy document:
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Action": "iot:*",
         "Resource": "*"
       }
     ]
   }
   ```
4. Click: **Create thing**

#### 2C. Download Certificates

AWS will provide 3 files:
- `xxx-certificate.pem.crt` (device certificate)
- `xxx-private.pem.key` (private key)
- `AmazonRootCA1.pem` (root CA)

**Save to:** `hardware_service/certs/` folder

```powershell
# Create certs directory
mkdir certs

# Copy downloaded files to certs/ and rename:
#   device-certificate.pem.crt
#   private.pem.key
#   AmazonRootCA1.pem
```

#### 2D. Get IoT Endpoint

1. In AWS IoT Console: **Settings**
2. Copy **Endpoint** (looks like: `xxxxx-ats.iot.ap-southeast-1.amazonaws.com`)
3. Update `.env` file:

```bash
AWS_IOT_ENDPOINT=your-endpoint-here-ats.iot.ap-southeast-1.amazonaws.com
```

### Step 3: Run Hardware Service

```powershell
cd c:\Users\songj\Interion\Interion-Portable_refilling_station\hardware_service

# Run in STUB mode
python main.py
```

**Expected output:**
```
=== AceRev Hardware Gateway starting ===
Station : Demo Station - Testing (demo-station-001)
HW mode : STUB
MQTT connected to xxxxx-ats.iot.ap-southeast-1.amazonaws.com
Diagnostics API running on http://0.0.0.0:8080
Hardware gateway running. Press Ctrl+C to stop.
```

### Step 4: Test Diagnostics API

Open another terminal:

```powershell
# Check health
Invoke-RestMethod -Uri http://localhost:8080/health

# Check status
Invoke-RestMethod -Uri http://localhost:8080/status

# Simulate dispense (non-production only)
Invoke-RestMethod -Uri http://localhost:8080/simulate/authorize -Method POST

# Check status again (should show DISPENSING)
Invoke-RestMethod -Uri http://localhost:8080/status
```

---

## 🚀 SETUP GUIDE - OPTION 1 (Production Deployment)

### Prerequisites

- ✅ ODROID-M1S hardware received
- ✅ Ubuntu 20.04 LTS flashed to microSD
- ✅ Physical sensors installed and wired
- ✅ 4G modem or Ethernet connected
- ✅ AWS IoT Core Thing created (see Option 2, Step 2)

### Step 1: Prepare ODROID

```bash
# SSH into ODROID (default password: odroid)
ssh odroid@<odroid-ip-address>

# Update system
sudo apt update && sudo apt upgrade -y

# Install Python 3.11+
sudo apt install python3 python3-pip python3-venv git -y

# Check version
python3 --version
```

### Step 2: Clone & Configure Service

```bash
# Create app directory
sudo mkdir -p /opt/acerev
sudo chown odroid:odroid /opt/acerev
cd /opt/acerev

# Copy hardware_service files
# (Use scp, git, or USB)
scp -r hardware_service/ odroid@<ip>:/opt/acerev/

cd /opt/acerev/hardware_service

# Create virtual environment
python3 -m venv venv
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

### Step 3: Configure Environment

```bash
# Copy and edit .env
cp .env.example .env
nano .env
```

**Critical settings:**
```bash
STATION_ID=station-<location>-001
STATION_NAME=T001 - <Location Name>
AWS_IOT_ENDPOINT=<your-endpoint>-ats.iot.ap-southeast-1.amazonaws.com

# For production with PLC:
HARDWARE_MODE=MODBUS
MODBUS_HOST=192.168.1.50

# For testing without PLC:
HARDWARE_MODE=GPIO
GPIO_VALVE_PIN=17
GPIO_FLOW_PIN=27
```

### Step 4: Install Certificates

```bash
# Create certs directory
mkdir -p /opt/acerev/hardware_service/certs

# Copy certificates from your PC
scp device-certificate.pem.crt odroid@<ip>:/opt/acerev/hardware_service/certs/
scp private.pem.key odroid@<ip>:/opt/acerev/hardware_service/certs/
scp AmazonRootCA1.pem odroid@<ip>:/opt/acerev/hardware_service/certs/

# Set permissions
chmod 600 /opt/acerev/hardware_service/certs/*.key
```

### Step 5: Test Run

```bash
cd /opt/acerev/hardware_service
source venv/bin/activate

# Test in foreground
python main.py
```

**Look for:**
- ✅ "Hardware gateway starting"
- ✅ "MQTT connected"
- ✅ "Diagnostics API running"
- ✅ No error messages

Press `Ctrl+C` to stop.

### Step 6: Create Systemd Service (Auto-start)

```bash
sudo nano /etc/systemd/system/acerev-gateway.service
```

**Paste:**
```ini
[Unit]
Description=AceRev Hardware Gateway Service
After=network.target

[Service]
Type=simple
User=odroid
WorkingDirectory=/opt/acerev/hardware_service
Environment="PATH=/opt/acerev/hardware_service/venv/bin"
ExecStart=/opt/acerev/hardware_service/venv/bin/python main.py
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**Enable and start:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable acerev-gateway
sudo systemctl start acerev-gateway

# Check status
sudo systemctl status acerev-gateway

# View logs
sudo journalctl -u acerev-gateway -f
```

---

## 🧪 TESTING CHECKLIST

### Local Testing (STUB Mode on PC)

- [ ] Python dependencies installed
- [ ] .env file configured
- [ ] AWS IoT Thing created
- [ ] Certificates downloaded
- [ ] Service starts without errors
- [ ] MQTT connection established
- [ ] Diagnostics API responds
- [ ] Simulated dispense works
- [ ] Status transitions: IDLE → AUTH → DISPENSING → COMPLETE

### Field Testing (Production Hardware)

- [ ] ODROID powered on
- [ ] Ubuntu booted successfully
- [ ] Network connectivity (4G/Ethernet)
- [ ] Service auto-starts on boot
- [ ] MQTT connection stable
- [ ] Solenoid valve opens/closes on command
- [ ] Flow sensor reads correctly
- [ ] Display shows auth codes
- [ ] End-to-end dispense cycle works
- [ ] Mobile app can trigger dispense
- [ ] Telemetry published to cloud
- [ ] Emergency stop button tested

---

## 📊 MQTT TOPIC REFERENCE

All topics follow pattern: `acerev/stations/{STATION_ID}/{subtopic}`

| Topic | Direction | Payload Example |
|---|---|---|
| `commands` | Cloud → Station | `{"action": "start_dispense", "transactionId": "tx-123", "maxVolume": 50}` |
| `auth/challenge` | Station → Cloud | `{"transactionId": "tx-123", "code": "1234", "ready": true}` |
| `auth/response` | Cloud → Station | `{"transactionId": "tx-123", "accepted": true}` |
| `telemetry/flow` | Station → Cloud | `{"volumeLitres": 25.5, "rateLPM": 15.0, "elapsedSeconds": 102}` |
| `telemetry/status` | Station → Cloud | `{"status": "DISPENSING", "transactionId": "tx-123"}` |
| `telemetry/complete` | Station → Cloud | `{"transactionId": "tx-123", "volumeLitres": 50.0, "stopReason": "complete"}` |
| `heartbeat` | Station → Cloud | `{"stationId": "demo-001", "timestamp": 1732483200}` |

---

## 🔧 TROUBLESHOOTING

### Service Won't Start

**Error:** `ModuleNotFoundError: No module named 'awsiotsdk'`
```bash
cd /opt/acerev/hardware_service
source venv/bin/activate
pip install -r requirements.txt
```

**Error:** `MQTT connect failed: No such file or directory`
- Check certificates exist in `certs/` folder
- Verify paths in `.env` match actual filenames

**Error:** `Hardware init failed: NotImplementedError`
- Change `HARDWARE_MODE=STUB` in `.env` for testing
- Or install PLC library: `pip install pymodbus`

### MQTT Connection Issues

**Error:** `Connection refused` or `Connection timeout`
- Check AWS_IOT_ENDPOINT in `.env`
- Verify internet connectivity
- Check firewall allows port 8883

**Error:** `Certificate verification failed`
- Ensure certificates downloaded from correct AWS region
- Verify certificate permissions: `chmod 600 *.key`

### Hardware Issues

**Flow sensor not reading:**
- Check GPIO pin wiring
- Verify `GPIO_FLOW_PIN` in `.env`
- Test with `sudo gpioinfo` command

**Solenoid valve not opening:**
- Check 24VDC power supply
- Test relay manually
- Verify `GPIO_VALVE_PIN` or Modbus address

---

## 📦 NEXT STEPS

### Immediate (This Week)
1. ✅ Complete AWS IoT Core setup
2. ✅ Test STUB mode on your PC
3. ✅ Verify MQTT connectivity
4. ⚠️ Order ODROID hardware (~$70)

### Short Term (Next 2 Weeks)
1. ⚠️ Receive and configure ODROID
2. ⚠️ Flash Ubuntu 20.04
3. ⚠️ Deploy service to ODROID
4. ⚠️ Test without physical sensors (STUB mode)

### Medium Term (Next 4 Weeks)
1. ⚠️ Purchase physical sensors/valves (~$200)
2. ⚠️ Wire hardware components
3. ⚠️ Calibrate flow sensor
4. ⚠️ Complete end-to-end testing
5. ⚠️ Install at pilot station

---

## 💡 RECOMMENDATIONS

### **For Immediate Testing (TODAY)**
- ✅ Set up AWS IoT Core Thing
- ✅ Run hardware service in STUB mode on Windows
- ✅ Test MQTT connectivity
- ✅ Verify state machine logic

**Why:** Validates software without waiting for hardware ($0 cost)

### **For Production Readiness (NEXT MONTH)**
- ⚠️ Order 1× ODROID-M1S for pilot station
- ⚠️ Purchase sensors/valves for 1 station
- ⚠️ Identify pilot installation location
- ⚠️ Plan field installation timeline

**Why:** Gets real-world testing started while keeping costs low (~$400)

### **For Scaling (FUTURE)**
- Wait for pilot station success
- Order hardware for 3-5 additional stations
- Standardize installation procedures
- Train field technicians

**Why:** Proven model before scaling reduces risk

---

## 📞 SUPPORT

**Documentation:**
- Hardware service README: `hardware_service/README.md`
- Circuit diagrams: `Circuits/` folder
- AWS IoT Core: https://docs.aws.amazon.com/iot/

**Testing:**
- Run service: `python main.py`
- Check diagnostics: `http://localhost:8080/health`
- View logs: `sudo journalctl -u acerev-gateway -f` (Linux)

---

**Status:** ✅ Software ready, ⚠️ Awaiting hardware deployment  
**Estimated Time to First Pilot:** 2-4 weeks (after hardware arrives)  
**Estimated Cost:** ~$400 per station

---

*Last Updated: May 25, 2026*
