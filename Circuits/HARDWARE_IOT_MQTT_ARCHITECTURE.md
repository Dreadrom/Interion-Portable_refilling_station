# Hardware Integration Architecture - IoT & MQTT

**Date**: February 12, 2026  
**Purpose**: Complete architecture showing how mobile app, backend, and hardware (petrol station) connect

---

## 🔍 CURRENT SITUATION ANALYSIS

### ✅ What You Have (Working)
1. **Mobile App** → Direct HTTP connection to PTS hardware (`Device.ts`, `PTSManager.ts`)
2. **Backend** → Direct HTTP connection to PTS hardware (`StationsHandler.ts`)
3. Both use the **jsonPTS protocol** over HTTP/HTTPS

### ❌ What's Missing (AWS IoT Core Integration)
- No MQTT broker setup yet
- No IoT Core topics configured
- Hardware communicates directly via HTTP (not MQTT)

---

## 🏗️ TWO ARCHITECTURE OPTIONS

You have **2 choices** for how hardware connects:

### **Option A: Direct HTTP (CURRENT - SIMPLE)**
✅ Already implemented  
✅ Works immediately  
❌ Not scalable for many stations  
❌ No real-time updates  
❌ Mobile app needs station IP address  

### **Option B: AWS IoT Core + MQTT (CLOUD - SCALABLE)**
✅ Production-ready architecture  
✅ Real-time updates via MQTT  
✅ Scales to 1000+ stations  
❌ Requires IoT Core setup  
❌ Hardware needs MQTT client  

---

## 📊 OPTION A: DIRECT HTTP ARCHITECTURE (CURRENT)

```
┌─────────────────────────────────────────────────────────────┐
│                    CURRENT SETUP                            │
└─────────────────────────────────────────────────────────────┘

┌──────────────┐                              ┌──────────────┐
│  Mobile App  │                              │   Backend    │
│              │                              │   (Lambda)   │
└──────┬───────┘                              └──────┬───────┘
       │                                             │
       │ HTTP/HTTPS                                  │ HTTP/HTTPS
       │ 192.168.1.100:8080                         │ 192.168.1.100:8080
       │                                             │
       ▼                                             ▼
┌──────────────────────────────────────────────────────────────┐
│           PETROL STATION HARDWARE (PTS-2 Controller)         │
│                                                               │
│  Components:                                                  │
│  ┌────────────────┐  ┌────────────────┐  ┌───────────────┐ │
│  │ HTTP Server    │  │ Flow Meter     │  │ Tank Sensor   │ │
│  │ Port 8080/8443 │  │ Volume/Rate    │  │ Level/Temp    │ │
│  └────────────────┘  └────────────────┘  └───────────────┘ │
│                                                               │
│  ┌────────────────┐  ┌────────────────┐  ┌───────────────┐ │
│  │ Solenoid Valve │  │ Pump Motor     │  │ Safety System │ │
│  │ (Lock/Unlock)  │  │ (On/Off)       │  │ (Alarms)      │ │
│  └────────────────┘  └────────────────┘  └───────────────┘ │
└──────────────────────────────────────────────────────────────┘
```

### How It Works (Direct HTTP)

#### 1. Mobile App Connects to Hardware
```typescript
// File: portable-refill-app/app/network/NetworkSettings.ts
const defaultNetworkSettings = {
  host: '192.168.1.100',  // PTS hardware IP address
  protocol: 'HTTP',
  httpPort: 8080,
  httpsPort: 8443,
}

// File: portable-refill-app/app/pts/Device.ts
class Device {
  async send(payload: string): Promise<string> {
    // Sends HTTP POST to hardware directly
    const response = await this.clientManager.sendJson(payload);
    return JSON.stringify(response);
  }
}
```

#### 2. Request Example (Get Tank Status)
```json
// Mobile App sends:
POST http://192.168.1.100:8080/
{
  "Protocol": "jsonPTS",
  "Packets": [
    {
      "Id": 1,
      "Type": "GetTanks"
    }
  ]
}

// Hardware responds:
{
  "Protocol": "jsonPTS",
  "Packets": [
    {
      "Id": 1,
      "Type": "GetTanks",
      "Result": "OK",
      "Data": {
        "Tanks": [
          {
            "Tank": 1,
            "Product": 1,  // RON95
            "Volume": 4500.5,  // Liters
            "Temp": 28.5,  // Celsius
            "Height": 1750,  // mm
            "Water": 0
          }
        ]
      }
    }
  ]
}
```

#### 3. Backend Also Connects Directly
```typescript
// File: backend/src/handlers/StationsHandler.ts
class PTSController {
  private baseUrl: string;

  constructor(host: string, port: number, protocol: 'HTTP' | 'HTTPS') {
    // e.g., http://192.168.1.100:8080
    this.baseUrl = `${protocol}://${host}:${port}`;
  }

  async getTanks() {
    // Sends same jsonPTS request to hardware
    const response = await fetch(this.baseUrl, {
      method: 'POST',
      body: JSON.stringify(payload)
    });
  }
}
```

### 🎯 Pros & Cons of Direct HTTP

| ✅ Pros | ❌ Cons |
|---------|---------|
| Simple setup | Mobile app needs station IP |
| Works immediately | No real-time updates |
| No AWS costs | Not secure over internet |
| Easy debugging | Can't handle 100+ stations |
| Already implemented | Polling required (wasteful) |

---

## 📊 OPTION B: AWS IoT CORE + MQTT (RECOMMENDED)

```
┌─────────────────────────────────────────────────────────────────────┐
│                   PRODUCTION ARCHITECTURE                           │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────┐                                      ┌──────────────┐
│  Mobile App  │                                      │   Backend    │
│              │                                      │   (Lambda)   │
└──────┬───────┘                                      └──────┬───────┘
       │                                                     │
       │ HTTPS                                              │ Event trigger
       │ API Gateway                                        │
       ▼                                                     ▼
┌────────────────────────────────────────────────────────────────────┐
│                       AWS CLOUD                                    │
│                                                                    │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │               API Gateway (REST API)                         │ │
│  │  POST /dispense/start → Lambda                               │ │
│  │  GET  /stations/{id}/status → Lambda                         │ │
│  └───────────────────────┬──────────────────────────────────────┘ │
│                          │                                         │
│                          ▼                                         │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │                    Lambda Functions                          │ │
│  │  - Handles API requests                                      │ │
│  │  - Publishes MQTT commands to IoT Core                      │ │
│  │  - Processes MQTT messages from hardware                    │ │
│  │  - Writes to RDS database                                    │ │
│  └──────────────────┬───────────────────┬───────────────────────┘ │
│                     │                   │                          │
│         Publish to  │                   │  Subscribe from          │
│         topics      │                   │  topics                  │
│                     ▼                   ▼                          │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │                   AWS IoT Core (MQTT Broker)                 │ │
│  │                                                               │ │
│  │  Topics:                                                      │ │
│  │  → stations/{stationId}/commands/unlock    (Lambda → HW)    │ │
│  │  → stations/{stationId}/commands/stop      (Lambda → HW)    │ │
│  │  ← stations/{stationId}/status            (HW → Lambda)     │ │
│  │  ← stations/{stationId}/dispense/progress (HW → Lambda)     │ │
│  │  ← stations/{stationId}/tank              (HW → Lambda)     │ │
│  │  ← stations/{stationId}/alarms            (HW → Lambda)     │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                             ▲                                      │
│                             │ MQTT over TLS                        │
│                             │ (Port 8883)                          │
└─────────────────────────────┼──────────────────────────────────────┘
                              │
                              │ WiFi/4G Internet
                              │
┌─────────────────────────────┴──────────────────────────────────────┐
│           PETROL STATION HARDWARE (PTS-2 Controller)               │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────┐    │
│  │  MQTT Client (needs to be added to hardware)              │    │
│  │  - AWS IoT SDK                                             │    │
│  │  - X.509 Certificate for authentication                    │    │
│  │  - Subscribes to: stations/STN001/commands/*              │    │
│  │  - Publishes to: stations/STN001/status, tank, progress   │    │
│  └───────────────────────────────────────────────────────────┘    │
│                                                                     │
│  ┌────────────────┐  ┌────────────────┐  ┌───────────────┐       │
│  │ Flow Meter     │  │ Tank Sensor    │  │ Solenoid      │       │
│  │ Volume/Rate    │  │ Level/Temp     │  │ Lock/Unlock   │       │
│  └────────────────┘  └────────────────┘  └───────────────┘       │
└──────────────────────────────────────────────────────────────────┘
```

### How It Works (MQTT Architecture)

#### 1. Hardware Publishes Status (Every 5 seconds)
```json
// Hardware publishes to: stations/STN001/status
Topic: stations/STN001/status
Payload: {
  "stationId": "STN001",
  "status": "IDLE",  // or DISPENSING, ALARM, MAINTENANCE
  "timestamp": "2026-02-12T14:30:00Z",
  "controllers": [
    {
      "type": "PTS-2",
      "version": "2.0.1",
      "uptime": 86400  // seconds
    }
  ]
}
```

#### 2. Hardware Publishes Tank Status (Every 30 seconds)
```json
// Hardware publishes to: stations/STN001/tank
Topic: stations/STN001/tank
Payload: {
  "stationId": "STN001",
  "timestamp": "2026-02-12T14:30:15Z",
  "tanks": [
    {
      "tankId": 1,
      "product": "RON95",
      "volume": 4500.5,  // liters
      "capacity": 5000,  // liters
      "percentage": 90,
      "temperature": 28.5,  // Celsius
      "waterLevel": 0,
      "lowLevelAlarm": false,
      "highLevelAlarm": false
    }
  ]
}
```

#### 3. User Starts Fueling (Mobile App → Backend → IoT → Hardware)
```
Step 1: Mobile app sends API request
POST https://api.yourapp.com/dispense/start
Authorization: Bearer {JWT_TOKEN}
Body: {
  "stationId": "STN001",
  "nozzleId": "A1",
  "productType": "RON95",
  "authorizedAmount": 50.00,
  "paymentId": "PAY123"
}

Step 2: Lambda validates payment, publishes MQTT command
Topic: stations/STN001/commands/unlock
Payload: {
  "command": "UNLOCK_PUMP",
  "transactionId": "TXN20260212001",
  "nozzleId": "A1",
  "authorizedAmount": 50.00,
  "authorizedVolume": 24.39,  // liters (50 ÷ 2.05)
  "productType": "RON95",
  "timestamp": "2026-02-12T14:35:00Z"
}

Step 3: Hardware receives command, unlocks pump
Hardware subscribes to: stations/STN001/commands/#
Receives unlock command, opens solenoid valve

Step 4: Hardware publishes acknowledgment
Topic: stations/STN001/status
Payload: {
  "stationId": "STN001",
  "status": "DISPENSING",
  "activeTransaction": "TXN20260212001",
  "nozzleId": "A1",
  "pumpUnlocked": true,
  "timestamp": "2026-02-12T14:35:01Z"
}
```

#### 4. Real-Time Dispensing Progress (Hardware publishes every 500ms)
```json
// Hardware publishes to: stations/STN001/dispense/progress
Topic: stations/STN001/dispense/progress
Payload: {
  "transactionId": "TXN20260212001",
  "stationId": "STN001",
  "nozzleId": "A1",
  "status": "DISPENSING",
  "volumeDispensed": 15.32,  // liters (updates every 0.5s)
  "amountCharged": 31.41,  // RM
  "flowRate": 0.45,  // liters/second
  "startTime": "2026-02-12T14:35:05Z",
  "timestamp": "2026-02-12T14:35:40Z"
}
```

#### 5. Dispensing Complete
```json
// Hardware publishes to: stations/STN001/dispense/complete
Topic: stations/STN001/dispense/complete
Payload: {
  "transactionId": "TXN20260212001",
  "stationId": "STN001",
  "nozzleId": "A1",
  "status": "COMPLETED",
  "finalVolume": 24.35,  // liters
  "finalAmount": 49.92,  // RM (24.35 × 2.05)
  "startTime": "2026-02-12T14:35:05Z",
  "endTime": "2026-02-12T14:36:00Z",
  "duration": 55,  // seconds
  "averageFlowRate": 0.44,  // L/s
  "stopReason": "USER_COMPLETED"  // or EMERGENCY_STOP, AUTHORIZED_LIMIT
}
```

#### 6. Lambda Processes Message, Updates Database
```typescript
// Lambda function triggered by IoT Rule
exports.handler = async (event) => {
  const message = JSON.parse(event.body);
  
  // Update transaction in RDS
  await query(`
    UPDATE transactions 
    SET 
      final_volume = $1,
      final_amount = $2,
      end_time = $3,
      status = 'COMPLETED'
    WHERE transaction_id = $4
  `, [
    message.finalVolume,
    message.finalAmount,
    message.endTime,
    message.transactionId
  ]);
  
  // Update wallet balance
  // Send push notification to mobile app
  // Generate receipt
};
```

---

## 🔧 WHAT HARDWARE NEEDS TO SUPPORT MQTT

### Current Hardware (Direct HTTP)
```
PTS-2 Controller
├── HTTP Server (Port 8080/8443)
├── jsonPTS Protocol Handler
└── Hardware Control (valves, pumps, sensors)
```

### Updated Hardware (With MQTT)
```
PTS-2 Controller
├── HTTP Server (Port 8080/8443) ← Keep for local debugging
├── MQTT Client (AWS IoT SDK) ← ADD THIS
│   ├── Connect to: xxxxx.iot.ap-southeast-1.amazonaws.com:8883
│   ├── Authenticate with X.509 certificate
│   ├── Subscribe to: stations/{stationId}/commands/#
│   └── Publish to: stations/{stationId}/status, tank, progress
├── jsonPTS Protocol Handler
└── Hardware Control (valves, pumps, sensors)
```

### Code Hardware Person Needs to Add
```c
// Example MQTT client integration (pseudocode)
#include <aws_iot_mqtt_client.h>

// Initialize MQTT client
AWS_IoT_Client mqttClient;
aws_iot_mqtt_init(&mqttClient, &initParams);

// Connect to AWS IoT Core
aws_iot_mqtt_connect(&mqttClient, &connectParams);

// Subscribe to commands
aws_iot_mqtt_subscribe(
  &mqttClient,
  "stations/STN001/commands/#",
  QOS0,
  command_callback_handler
);

// Publish status every 5 seconds
void publish_status() {
  char payload[256];
  sprintf(payload, "{\"status\":\"IDLE\",\"timestamp\":\"%s\"}", get_timestamp());
  
  aws_iot_mqtt_publish(
    &mqttClient,
    "stations/STN001/status",
    strlen(payload),
    payload,
    QOS0
  );
}

// Handle commands from cloud
void command_callback_handler(char* topic, char* payload) {
  if (strstr(topic, "unlock")) {
    // Parse JSON, unlock pump
    unlock_solenoid_valve();
    start_flow_meter_monitoring();
  } else if (strstr(topic, "stop")) {
    // Emergency stop
    lock_solenoid_valve();
    stop_pump();
  }
}
```

---

## 🎯 MQTT TOPICS REFERENCE

### Commands (Cloud → Hardware)
| Topic | Direction | Purpose | Frequency |
|-------|-----------|---------|-----------|
| `stations/{id}/commands/unlock` | Lambda → HW | Unlock pump, start dispensing | On-demand |
| `stations/{id}/commands/stop` | Lambda → HW | Emergency stop | On-demand |
| `stations/{id}/commands/pause` | Lambda → HW | Pause dispensing | On-demand |
| `stations/{id}/commands/resume` | Lambda → HW | Resume after pause | On-demand |
| `stations/{id}/config/update` | Lambda → HW | Update pricing, limits | Rare |

### Status (Hardware → Cloud)
| Topic | Direction | Purpose | Frequency |
|-------|-----------|---------|-----------|
| `stations/{id}/status` | HW → Lambda | Device heartbeat, status | Every 5s |
| `stations/{id}/tank` | HW → Lambda | Tank levels, temperature | Every 30s |
| `stations/{id}/dispense/progress` | HW → Lambda | Real-time fueling data | Every 0.5s |
| `stations/{id}/dispense/complete` | HW → Lambda | Transaction summary | On completion |
| `stations/{id}/alarms` | HW → Lambda | Safety alarms | On event |

---

## 🚀 IMPLEMENTATION PLAN

### Phase 1: Keep Direct HTTP (Immediate - 0 work)
✅ Your current setup works  
✅ Good for initial testing  
✅ Good for single station deployment  
⏭️ Use this for MVP/demo  

### Phase 2: Add IoT Core (Production - 2 weeks)

**Week 1: AWS Setup**
- [ ] Create IoT Core thing for each station
- [ ] Generate X.509 certificates
- [ ] Create IoT policies and rules
- [ ] Create Lambda function to process MQTT messages
- [ ] Test with MQTT simulator

**Week 2: Hardware Integration**
- [ ] Hardware team adds AWS IoT SDK
- [ ] Hardware team implements MQTT client
- [ ] Hardware team adds topic pub/sub logic
- [ ] Hardware team tests certificate auth
- [ ] End-to-end testing with real hardware

---

## ✅ CURRENT STATUS CHECK

Based on your code, here's what you have:

### ✅ Mobile App (Ready for HTTP)
- `Device.ts` - Can send jsonPTS commands
- `PTSManager.ts` - High-level PTS interface
- `NetworkSettings.ts` - Connection config
- `PTSConnection.ts` - Connection helper

### ✅ Backend (Ready for HTTP)
- `StationsHandler.ts` - Has `PTSController` class
- Can call all jsonPTS endpoints
- Can get tanks, prices, totalizers, etc.

### ❌ Missing for MQTT
- No AWS IoT Core setup
- No MQTT topics configured
- No IoT rules to trigger Lambda
- Hardware doesn't publish to MQTT (yet)

---

## 💡 RECOMMENDATION FOR YOUR MEETING

**Tell the server/hardware person:**

1. **Current architecture uses direct HTTP** (already working)
2. **Production needs MQTT via AWS IoT Core** (for scalability)
3. **Hardware must add:**
   - AWS IoT SDK
   - MQTT client
   - Certificate authentication
   - Topic publish/subscribe logic
4. **Timeline:** 2 weeks for MQTT integration
5. **For now:** Direct HTTP is fine for testing

---

## 📋 QUESTIONS FOR HARDWARE PERSON

Print and ask these:

1. ☐ Does PTS-2 controller support MQTT client?
2. ☐ What programming language/platform? (C, C++, Python, Node.js?)
3. ☐ Can it connect to AWS IoT Core (port 8883)?
4. ☐ Can it use X.509 certificates for auth?
5. ☐ How often can it publish status? (Every 5 seconds?)
6. ☐ Can it publish real-time progress? (Every 0.5 seconds?)
7. ☐ Does it have sufficient memory for MQTT client library?
8. ☐ Is there documentation for extending the controller?
9. ☐ Timeline to add MQTT support?
10. ☐ For now, HTTP direct connection works?

---

## 🎯 FINAL ANSWER

### Your Architecture is CORRECT but has 2 modes:

**Mode 1: Direct HTTP (CURRENT)**
- Mobile app → Hardware (works now)
- Backend → Hardware (works now)
- ✅ Good for testing/MVP
- ❌ Not scalable for production

**Mode 2: AWS IoT + MQTT (FUTURE)**
- Mobile app → Backend → IoT Core → Hardware
- Hardware → IoT Core → Lambda → Database
- ✅ Production-ready
- ✅ Scales to 1000+ stations
- ⏳ Needs 2 weeks hardware work

**For your meeting:** Tell them you need MQTT support added to hardware for production deployment!

