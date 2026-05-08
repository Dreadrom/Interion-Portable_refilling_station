# AceRev Hardware Gateway Service

Python service that runs on the station's Ubuntu microcontroller (e.g. Raspberry Pi 5). It bridges the cloud backend (AWS IoT Core) and the physical dispensing hardware (solenoid valve, flow meter, display).

## Quick Start

```bash
# 1. Install Python deps
pip install -r requirements.txt

# 2. Configure environment
cp .env.example .env
nano .env          # fill in STATION_ID, AWS_IOT_ENDPOINT, cert paths

# 3. Place AWS IoT certs in ./certs/
#    (download from AWS IoT Core → Things → Certificates)
mkdir certs
# Copy: device-certificate.pem.crt, private.pem.key, AmazonRootCA1.pem

# 4. Run in stub mode (no hardware required)
HARDWARE_MODE=STUB python main.py
```

## File Structure

| File | Purpose |
|---|---|
| `main.py` | Entry point — wires everything together |
| `config.py` | Loads `.env` into a typed config object |
| `mqtt_client.py` | AWS IoT Core MQTT connection + pub/sub |
| `pump_controller.py` | Dispensing state machine (IDLE → AUTH → DISPENSING → IDLE) |
| `plc_interface.py` | Hardware abstraction (stub / GPIO / S7 / Modbus) |
| `display.py` | Pump auth code display abstraction |
| `local_api.py` | Flask diagnostics API (localhost:8080) |

## Hardware Modes

Set `HARDWARE_MODE` in `.env`:

| Mode | Description | Status |
|---|---|---|
| `STUB` | Fully simulated, no hardware needed | ✅ Working |
| `GPIO` | ODROID-M1S GPIO via python3-gpiod | ⚠️ Stub — implement once ODROID-M1S confirmed |
| `SNAP7` | Siemens S7-1200/S7-1500 via python-snap7 | ⚠️ Stub — implement once PLC program/DB ready |
| `MODBUS` | Siemens LOGO! 8.3 (recommended) via pymodbus | ⚠️ Stub — implement once Modbus register map ready |

## MQTT Topic Schema

All topics follow the pattern: `acerev/stations/{STATION_ID}/{subtopic}`

| Topic | Direction | Payload |
|---|---|---|
| `commands` | Cloud → RPi | `{ action, transactionId, maxVolume, maxAmount }` |
| `auth/challenge` | RPi → Cloud | `{ transactionId, code, ready }` |
| `auth/response` | Cloud → RPi | `{ transactionId, accepted }` |
| `telemetry/flow` | RPi → Cloud | `{ volumeLitres, rateLPM, elapsedSeconds }` |
| `telemetry/status` | RPi → Cloud | `{ status, transactionId? }` |
| `telemetry/complete` | RPi → Cloud | `{ transactionId, volumeLitres, amountCharged, stopReason }` |
| `heartbeat` | RPi → Cloud | `{ stationId }` |

## Diagnostics API

Local HTTP server on port 8080 (no internet required):

```
GET  http://localhost:8080/health              # liveness
GET  http://localhost:8080/status              # current state + volume
POST http://localhost:8080/simulate/authorize  # trigger test dispense (non-prod)
POST http://localhost:8080/simulate/stop       # force stop (non-prod)
```

## Recommended Bill of Materials

| Component | Part | Qty | Est. Cost |
|---|---|---|---|
| SBC | ODROID-M1S (8 GB) | 1 | ~$70 |
| PLC | Siemens LOGO! 8.3 (`6ED1052-1MD08-0BA1`) | 1 | ~$160 |
| Solenoid valve | 24VDC NC, DN20, SS316 body | 1 | ~$30–60 |
| Flow sensor | Digmesa FHS (±0.5%) | 1 | ~$60–80 |
| OLED display | SSD1306 128×64 I2C | 1 | ~$5 |
| UPS HAT | Waveshare 18650 UPS HAT | 1 | ~$20 |
| Enclosure | IP55 DIN-rail box (≥200×200 mm) | 1 | ~$30–60 |
| 4G dongle | Huawei E3372 (backup uplink) | 1 | ~$40 |

---

## Recommended Bill of Materials

| Component | Part | Est. Cost |
|---|---|---|
| SBC | ODROID-M1S (8 GB, eMMC) | ~$70 |
| PLC | Siemens LOGO! 8.3 (`6ED1052-1MD08-0BA1`) | ~$160 |
| Solenoid valve | 24VDC NC, DN20, SS316 body | ~$30–60 |
| Flow sensor | Digmesa FHS (±0.5%) | ~$60–80 |
| OLED display | SSD1306 128×64 I2C | ~$5 |
| UPS HAT | Waveshare 18650 UPS HAT | ~$20 |
| Enclosure | IP55 DIN-rail box (≥200×200 mm) | ~$30–60 |
| 4G dongle | Huawei E3372 (backup uplink) | ~$40 |

---

## Running as a systemd Service (Ubuntu)

```ini
# /etc/systemd/system/acerev-gateway.service
[Unit]
Description=AceRev Hardware Gateway
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/hardware_service
ExecStart=/usr/bin/python3 main.py
Restart=on-failure
RestartSec=10
EnvironmentFile=/home/ubuntu/hardware_service/.env

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl enable acerev-gateway
sudo systemctl start  acerev-gateway
sudo journalctl -u acerev-gateway -f   # follow logs
```
