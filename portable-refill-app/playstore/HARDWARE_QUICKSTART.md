# 🚀 Quick Start - Hardware Service Testing

**Goal:** Test the hardware gateway service in STUB mode (no physical hardware needed)

**Time:** 15-20 minutes  
**Cost:** $0 (uses AWS IoT Core free tier)

---

## ⚡ OPTION A: Test Without AWS IoT (Quickest)

If you want to test the hardware logic WITHOUT setting up AWS IoT Core:

### Step 1: Modify MQTT Client for Testing

Create a mock MQTT client for local testing:

```powershell
cd c:\Users\songj\Interion\Interion-Portable_refilling_station\hardware_service
```

Create file: `mqtt_mock.py`

```python
"""Mock MQTT client for testing without AWS IoT Core"""
import logging
logger = logging.getLogger(__name__)

class MQTTClient:
    def __init__(self):
        self._handlers = {}
        
    def connect(self):
        logger.info("MOCK MQTT: Connected (simulated)")
        
    def disconnect(self):
        logger.info("MOCK MQTT: Disconnected (simulated)")
        
    def publish(self, topic, payload):
        logger.info(f"MOCK MQTT: Publishing to {topic}: {payload}")
        
    def publish_status(self, status):
        logger.info(f"MOCK MQTT: Status = {status}")
        
    def subscribe(self, topic, handler):
        logger.info(f"MOCK MQTT: Subscribed to {topic}")
        self._handlers[topic] = handler
```

### Step 2: Modify main.py Temporarily

Line 13, change:
```python
from mqtt_client import MQTTClient
```
To:
```python
from mqtt_mock import MQTTClient  # Use mock for testing
```

### Step 3: Run Service

```powershell
cd c:\Users\songj\Interion\Interion-Portable_refilling_station\hardware_service
python main.py
```

**Expected output:**
```
=== AceRev Hardware Gateway starting ===
Station : Demo Station - Testing (demo-station-001)
HW mode : STUB
MOCK MQTT: Connected (simulated)
MOCK MQTT: Status = IDLE
Diagnostics API running on http://0.0.0.0:8080
Hardware gateway running. Press Ctrl+C to stop.
```

### Step 4: Test Diagnostics API

Open another PowerShell window:

```powershell
# Health check
Invoke-RestMethod -Uri http://localhost:8080/health

# Current status
Invoke-RestMethod -Uri http://localhost:8080/status

# Simulate a dispense
Invoke-RestMethod -Uri http://localhost:8080/simulate/authorize -Method POST

# Check status again
Invoke-RestMethod -Uri http://localhost:8080/status
```

---

## ⚡ OPTION B: Full AWS IoT Core Setup (Complete Testing)

If you want the complete experience with real MQTT:

### Step 1: AWS IoT Core Thing Setup (10 minutes)

#### 1.1 Go to AWS IoT Console
https://console.aws.amazon.com/iot

Select region: **Asia Pacific (Singapore)** (ap-southeast-1)

#### 1.2 Create Thing

1. Click: **Manage** → **Things** → **Create things**
2. Select: **Create single thing**
3. Thing name: `acerev-demo-station-001`
4. Click: **Next**

#### 1.3 Generate Certificates

1. Choose: **Auto-generate a new certificate**
2. Click: **Next**
3. **Create new policy** (first time only):
   - Policy name: `AceRevStationPolicy`
   - Click **Advanced mode**
   - Paste this JSON:

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

4. Select the policy
5. Click: **Create thing**

#### 1.4 Download Certificates

AWS shows 3 download links:

1. **Device certificate** → Save as `device-certificate.pem.crt`
2. **Private key** → Save as `private.pem.key`
3. **Root CA** → Download **Amazon Root CA 1** → Save as `AmazonRootCA1.pem`

**Save all to:**
```
c:\Users\songj\Interion\Interion-Portable_refilling_station\hardware_service\certs\
```

#### 1.5 Get IoT Endpoint

1. In AWS IoT Console: **Settings** (left sidebar)
2. Copy the **Endpoint** (looks like: `a1b2c3d4e5f6g7-ats.iot.ap-southeast-1.amazonaws.com`)

### Step 2: Configure .env

Edit: `hardware_service\.env`

Change this line:
```bash
AWS_IOT_ENDPOINT=placeholder-ats.iot.ap-southeast-1.amazonaws.com
```

To your actual endpoint:
```bash
AWS_IOT_ENDPOINT=a1b2c3d4e5f6g7-ats.iot.ap-southeast-1.amazonaws.com
```

### Step 3: Run Service

```powershell
cd c:\Users\songj\Interion\Interion-Portable_refilling_station\hardware_service
python main.py
```

**Expected output:**
```
=== AceRev Hardware Gateway starting ===
Station : Demo Station - Testing (demo-station-001)
HW mode : STUB
MQTT connected to a1b2c3d4e5f6g7-ats.iot.ap-southeast-1.amazonaws.com
Diagnostics API running on http://0.0.0.0:8080
Hardware gateway running. Press Ctrl+C to stop.
```

### Step 4: Monitor AWS IoT Core

In AWS Console:

1. Go to: **Test** → **MQTT test client**
2. Subscribe to: `acerev/stations/demo-station-001/#`
3. You should see heartbeat messages every 30 seconds!

### Step 5: Test Dispense Flow

In another terminal:

```powershell
# Simulate authorization
Invoke-RestMethod -Uri http://localhost:8080/simulate/authorize -Method POST
```

**Watch AWS IoT MQTT client for messages:**
- `acerev/stations/demo-station-001/auth/challenge`
- `acerev/stations/demo-station-001/telemetry/status`
- `acerev/stations/demo-station-001/telemetry/flow`
- `acerev/stations/demo-station-001/telemetry/complete`

---

## 🎯 WHAT THIS PROVES

When running successfully, you've validated:

✅ **Hardware Gateway Service** - Python service works  
✅ **MQTT Connection** - Can connect to AWS IoT Core  
✅ **State Machine** - IDLE → AUTH → DISPENSING → COMPLETE  
✅ **Simulated Hardware** - STUB mode flow meter works  
✅ **Telemetry Publishing** - Real-time data sent to cloud  
✅ **Local API** - Diagnostics endpoint responds  

---

## 🔧 TROUBLESHOOTING

### Error: `ModuleNotFoundError: No module named 'awsiotsdk'`

```powershell
cd c:\Users\songj\Interion\Interion-Portable_refilling_station\hardware_service
pip install -r requirements.txt
```

### Error: `Connection refused` or `mqtt_connection_builder` fails

**Issue:** No AWS IoT certificates

**Solution:** Use **Option A** (mock MQTT) OR complete AWS IoT Core setup (Option B, Step 1)

### Error: `Certificate verification failed`

**Issue:** Wrong certificates or wrong region

**Solution:**
1. Verify certificates in `certs/` folder
2. Check AWS region matches endpoint (ap-southeast-1)
3. Re-download certificates if needed

### Port 8080 already in use

```powershell
# Find process using port 8080
netstat -ano | findstr :8080

# Kill process (replace PID)
taskkill /PID <pid> /F
```

---

## 📊 NEXT STEPS AFTER TESTING

### ✅ If Testing Successful

1. **Plan hardware procurement** (~$400 per station)
2. **Choose pilot station location**
3. **Order ODROID-M1S** (~$70)
4. **Order sensors/valves** (~$200)
5. **Schedule field installation** (2-4 weeks)

### ⚠️ If Testing Has Issues

1. **Check Python version** (need 3.11+, you have 3.14 ✅)
2. **Verify dependencies** (run `pip list`)
3. **Review logs** (look for error messages)
4. **Try mock MQTT** (Option A) if AWS IoT Core issues

---

## 📁 FILE STRUCTURE

```
hardware_service/
├── .env                    ← Your configuration ✅
├── .env.example           ← Template
├── main.py                ← Entry point
├── config.py              ← Loads .env
├── mqtt_client.py         ← AWS IoT Core
├── mqtt_mock.py           ← Testing only (Option A)
├── pump_controller.py     ← State machine
├── plc_interface.py       ← Hardware abstraction
├── display.py             ← OLED display
├── local_api.py           ← Flask diagnostics
├── requirements.txt       ← Dependencies ✅
└── certs/                 ← AWS IoT certificates
    ├── device-certificate.pem.crt
    ├── private.pem.key
    └── AmazonRootCA1.pem
```

---

## 🎉 SUCCESS CRITERIA

You're ready to move to physical hardware when:

- [x] Service starts without errors
- [x] MQTT connects (or mock works)
- [x] Diagnostics API responds
- [x] Simulated dispense completes
- [x] Telemetry publishes to AWS (or mock logs)
- [x] State transitions work correctly

---

**Choose your path:**
- 🚀 **Option A** = Fastest (no AWS setup, ~5 minutes)
- 🏆 **Option B** = Complete (full AWS IoT, ~20 minutes)

**Recommendation:** Start with Option A to test logic, then do Option B for production readiness.

---

*Want to proceed? Let me know which option you'd like to try!*
