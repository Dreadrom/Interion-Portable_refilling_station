# AceRev Hardware Documentation

Architecture guide, component selection notes, and wiring references for the AceRev portable AdBlue refilling station.

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLOUD (AWS)                              │
│  API Gateway ── Lambda ── PostgreSQL (RDS)                      │
│        │                                                        │
│  AWS IoT Core (MQTT broker, mTLS)                               │
│        │                                                        │
│  WebSocket API Gateway ── Lambda  (push updates to app)         │
└─────────────┬───────────────────────────────────────────────────┘
              │  MQTT over TLS 8883
              │
┌─────────────▼───────────────────────────────────────────────────┐
│        STATION HARDWARE (ODROID-M2, Ubuntu 22.04)               │
│                                                                 │
│  hardware_service/main.py (Python)                              │
│    ├── mqtt_client.py     ── AWS IoT Core                       │
│    ├── pump_controller.py ── dispensing state machine           │
│    ├── plc_interface.py   ── hardware abstraction layer         │
│    ├── display.py         ── auth code display                  │
│    └── local_api.py       ── Flask debug HTTP :8080             │
│                                                                 │
│   Ethernet ──────────────────────────────── Router / 4G modem  │
│   BLE  ──────────────────────────────────── Driver's phone      │
│   Ethernet (LAN) ─── Siemens PLC / MCU ─── Solenoid valve/pump │
│   I2C  ──────────────────────────────────── OLED display        │
│   USB 3.0 ×3 ──────── Camera 1/2/3                             │
│   RS485 (USB dongle) ─ Level gauge + flow meter (Modbus RTU)   │
└─────────────────────────────────────────────────────────────────┘
```

---

## Single Board Computer (SBC) Selection

### Requirements Driving This Decision

This station has three constraints that go beyond a basic IoT gateway:

1. **3 IP cameras** — nozzle tamper, licence-plate, area overview. Each camera streams ≥1080p30 h.264 to local storage + AWS S3.
2. **PLC/sensor integration** — Modbus TCP to LOGO! 8.3 PLC, RS485 Modbus RTU to level gauge and flow meter, I2C to OLED display.
3. **Edge AI ambition** — tamper detection, vehicle/person detection, licence-plate OCR — benefits greatly from an on-board NPU.

---

### ODROID Comparison: M1S · M2 · N2+

| | **ODROID-M1S** (8 GB) | **ODROID-M2** (8 GB) ★ | **ODROID-N2+** (4 GB) |
|---|---|---|---|
| **SoC** | Rockchip RK3566 | Rockchip RK3588S2 | Amlogic S922X |
| **CPU** | 4×A55 @ 1.8 GHz | 4×A76 @ 2.3 GHz + 4×A55 @ 1.8 GHz | 4×A73 @ 2.4 GHz + 2×A53 |
| **CPU performance** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ (≈3× M1S) | ⭐⭐⭐⭐ |
| **RAM** | 4 / 8 GB LPDDR4 | 8 / 16 GB LPDDR5 (64-bit bus) | 4 GB DDR4 |
| **NPU (edge AI)** | 0.8 TOPS | **6 TOPS** ← 7.5× faster | None |
| **eMMC storage** | 64 GB soldered ✅ | 64 GB soldered ✅ | Removable module ⚠️ |
| **M.2 NVMe slot** | PCIe 2.1 ×1 | PCIe 2.1 ×1 | ✗ |
| **USB host ports** | 1× USB 2.0 + 1× USB 3.0 = **2 ports** | 1× USB 2.0 + 1× USB 3.0 + 1× USB 3.0-C = **3 ports** | **4× USB 3.0 + 1× USB 2.0 OTG** |
| **Cameras (USB)** | 2 native → need hub for 3 ⚠️ | 3 native (marginal) → prefer hub ✅ | 4 native, no hub needed ✅ |
| **GPIO headers** | 40-pin + 14-pin (54 total) | 40-pin + 14-pin (54 total) | 40-pin only |
| **I2C / SPI / UART** | 2× I2C, 1× SPI, 2× UART (via GPIO) | 2× I2C, 1× SPI, 2× UART (via GPIO) | 2× I2C, 1× SPI, 1× UART |
| **ADC** | ✅ On 14-pin header | ✅ On 14-pin header | 2× ADC (1.8 V max) |
| **CAN-FD** | Add-on board ✅ | Add-on board ✅ | ✗ |
| **Built-in WiFi/BT** | ✅ WiFi 5 + BT 5.0 | ✗ (USB dongle +S$13) | ✗ (USB dongle) |
| **Gigabit Ethernet** | ✅ | ✅ | ✅ |
| **MIPI DSI display** | 4-lane, 30-pin | 4-lane, 30-pin | ✗ |
| **OS** | Ubuntu 22.04 LTS ✅ | Ubuntu 20.04 (22.04/24.04 coming) | Ubuntu 20.04 |
| **Linux kernel** | 6.1.x | 5.10.x | 4.9.x |
| **Power idle** | ≈1 W | ≈1 W | ≈2.2 W |
| **Power peak** | ≈3.5 W | ≈7.5 W | ≈6 W |
| **Power input** | 5 V USB-C | 12 V DC jack | 12 V DC jack |
| **Form factor** | 90×65 mm | 90×90 mm | 90×90 mm |
| **Supply guarantee** | Until 2036 ✅ | Not stated | Older design (2020) |
| **Approx. cost (USD)** | ~$85 inc. WiFi | ~$155 + $10 WiFi dongle | ~$97 + ~$30 eMMC module |
| **Library support** | ✅ RK3566, pymodbus, gpiod | ✅ RK3588S2, same lib stack | ⚠️ Amlogic, smaller community |

---

### Scoring Against Project Requirements

| Requirement | M1S | M2 ★ | N2+ |
|---|---|---|---|
| 3 IP cameras | ⚠️ Need USB hub | ✅ 3 native ports | ✅ 4 ports natively |
| Modbus TCP (PLC) | ✅ Ethernet | ✅ Ethernet | ✅ Ethernet |
| Modbus RTU / RS485 (sensors) | ✅ USB dongle | ✅ USB dongle | ✅ USB dongle |
| Level gauge (4-20 mA or RS485) | ✅ I2C ADC / RS485 | ✅ I2C ADC / RS485 | ✅ ADC / RS485 |
| Flow meter (pulse GPIO / RS485) | ✅ GPIO IRQ | ✅ GPIO IRQ | ✅ GPIO IRQ |
| I2C OLED display | ✅ | ✅ | ✅ |
| Edge AI (camera analytics) | ⚠️ 0.8 TOPS only | ✅ 6 TOPS | ✗ No NPU |
| Python pymodbus/snap7/gpiod | ✅ | ✅ | ⚠️ |
| Built-in WiFi (no dongle) | ✅ | ✗ | ✗ |
| eMMC soldered (field reliability) | ✅ | ✅ | ✗ removable |
| Cost/value | ✅ Best | ⚠️ Most expensive | ⚠️ Cheapest but older |

---

### ★ Recommendation: ODROID-M2 (8 GB)

**Why M2 over M1S:**
- 3 USB ports natively support 3 cameras — no hub required in basic deployment
- 6 TOPS NPU = can run YOLOv5/v8 edge inference on camera feeds (tamper detection, trespasser alert, licence-plate OCR) — this is a meaningful product differentiator
- CPU is 3× faster — handles 3 RTSP streams + Python service + MQTT without sweating
- LPDDR5 64-bit bus = nearly 2× memory bandwidth for concurrent camera decode
- Same 40+14 GPIO pinout as M1S → zero code change needed in `plc_interface.py` / `display.py`
- Hardkernel confirmed M2 has same add-on board ecosystem (relay board, CAN-FD board)

**Trade-offs to manage:**
- No built-in WiFi → use on-site Ethernet (preferred for stability anyway) OR buy S$13 WiFi USB dongle, consuming one USB port (leaving 2 for cameras, requiring a USB hub)
- Ubuntu 24.04 LTS not available at time of writing (20.04 works; 22.04 coming soon); `hardware_service` Python code works identically on 20.04
- Costs ~$70 more than M1S — justified by NPU and camera headroom

**Recommendation for deployment:**
- Use on-site Ethernet (LAN cable to router) for MQTT — avoids WiFi dongle USB port conflict
- Add a powered USB 3.0 hub (S$15–20) for clean 3-camera wiring regardless
- Add NVMe SSD if on-device camera recording is needed (64 GB eMMC is tight for video)

**If budget is the constraint → ODROID-M1S (8 GB) + powered USB 3.0 hub.**
All code works identically; only lose the NPU camera AI and raw CPU headroom.

**ODROID-N2+ is not recommended** for this project:
- No NPU
- Removable eMMC module (field failure risk)
- Older Amlogic SoC with smaller upstream Linux support
- No extra 14-pin GPIO header (less I/O budget)

---

### Recommended Add-ons for M2

| Component | Model | Purpose | Est. Cost |
|---|---|---|
---|
| Powered USB 3.0 hub | Anker 7-port USB 3.0 hub | Cameras + peripherals | ~$25 |
| WiFi dongle (optional) | Hardkernel WiFi Module 5BK | If no site Ethernet | S$13 |
| UPS | Hardkernel UPS Kit for M1S/M2 | Clean shutdown; M2 uses same kit | S$14 |
| NVMe SSD | WD Blue SN580 500 GB | Local video buffer before S3 upload | ~$50 |
| MIPI LCD (HMI option) | Hardkernel Vu8S 8" | On-station status display | S$57 |
| CAN-FD add-on | Hardkernel CAN-FD board | Future expansion | S$16 |
| RTC battery | CR2032 | Keep time without power | ~$1 |

---

## PLC / Valve Control

A PLC is confirmed in the design for **hardware interlocking and tamper resistance**. This is the right call — a PLC acts as an independent safety layer that the software cannot override, preventing:
- Software glitches opening the valve unexpectedly
- A hacked/frozen ODROID from dispensing uncontrolled
- Over-volume / over-pressure conditions

### Recommended PLC: Siemens LOGO! 8.3 (Primary) or S7-1200 (Scale-up)

#### Siemens LOGO! 8.3 — `6ED1052-1MD08-0BA1`
- **Best for single-station MVP** (~$150–200)
- 8 DI / 4 DO (relay), expandable to 24 I/O
- Built-in **Modbus TCP server** — ODROID talks to it over Ethernet
- Built-in display and keypad for manual override
- 0–55 °C operating temp, DIN rail mount
- Program in LOGO! Soft Comfort (free ladder/FBD IDE)
- **ODROID interface**: `HARDWARE_MODE=MODBUS` in `.env`

#### Siemens S7-1200 — `6ES7214-1AG40-0XB0` (Scale-up)
- **For multi-station or stricter SIL requirements** (~$500–700)
- Full IEC 61131-3 programming (Ladder, FBD, SCL)
- Requires Modbus TCP CP module or use S7Comm (python-snap7)
- PROFIsafe optional for functional safety
- **ODROID interface**: `HARDWARE_MODE=MODBUS` or `HARDWARE_MODE=SNAP7`

### PLC Safety Interlock Logic

The PLC independently enforces these conditions — regardless of what software says:

```
VALVE_OPEN = SoftwareCommand
           AND NOT EmergencyStop
           AND NOT OverpressureAlarm
           AND NOT LowTankAlarm
           AND NOT ValveFaultDetected
           AND PermitActive          ← only asserted by PLC after auth handshake
           AND MaxTimeNotExceeded    ← PLC watchdog: auto-close after N minutes
```

The ODROID sets `SoftwareCommand = ON` and `PermitActive = ON` via Modbus write.
The PLC reads the flow sensor independently to cross-check volumes.
If ODROID crashes or loses comms for >30 s, PLC closes the valve automatically (watchdog).

### ODROID ↔ PLC Communication

```
ODROID-M1S (Ethernet) ──── Siemens LOGO! 8.3
                           ├── Coil 0: valve command (W)
                           ├── Coil 1: permit active (W)
                           ├── Input 0: e-stop status (R)
                           ├── Input 1: overpressure alarm (R)
                           └── Register 0–1: flow pulses (R, 32-bit)
```

**Decision needed**: Confirm LOGO! 8.3 vs S7-1200 and share the DB/register layout once PLC program is drafted — then implement `MODBUS` mode in `plc_interface.py`.

---

## MCU as PLC Fallback (Launch Safety Net)

If the LOGO! 8.3 PLC procurement or programming cannot complete before launch, a low-cost MCU can replicate the PLC's role in software, giving you a safe alternative to launch on time. The MCU exposes the **same Modbus TCP interface** as the LOGO! 8.3, so the ODROID-M2 `HARDWARE_MODE=MODBUS` code path works **without change**.

### What the MCU Fallback Must Do

| Function | Signal type | MCU pin type |
|---|---|---|
| Valve open command | Digital output → relay → solenoid coil | DO (relay or MOSFET) |
| Permit active (from ODROID) | Modbus coil write → MCU logic | Modbus register |
| E-stop input | Digital input (NC contact) | DI |
| Overpressure alarm | Digital input | DI |
| Low-tank alarm | Digital input | DI |
| Flow pulse counting | Digital input, interrupt-driven | DI (IRQ) |
| Level gauge alarm | Digital input (dry contact) or 4-20mA ADC | DI / ADC |
| Watchdog comms timer | Software timer — close valve if ODROID silent >30 s | Timer |
| Modbus TCP server | Ethernet | Ethernet shield / built-in |

Total I/O: ≈5 DI + 2 DO + 1 ADC + Ethernet + UART (for debug). Well within any Arduino-class MCU.

---

### Option A — Arduino MEGA 2560 + W5500 Ethernet Shield (★ Recommended for Fallback)

```
[Arduino MEGA 2560]
  ├── W5500 Ethernet Shield ──── Modbus TCP server (port 502)
  │     └── LAN cable ─────────── ODROID-M2 Modbus master
  ├── RS485 module (UART1) ────── Optional: Modbus RTU to sensors
  ├── DO pin 22 → relay → solenoid valve
  ├── DO pin 23 → relay → permit indicator LED
  ├── DI pin 24 → E-stop (NC, pulled HIGH)
  ├── DI pin 25 → overpressure alarm
  ├── DI pin 26 → low-tank alarm
  ├── DI pin 2  → flow pulse (INT0, interrupt)
  └── ADC A0  → 4-20mA shunt → ADS1115 → level gauge current
```

**Libraries (Arduino):**
- `ArduinoModbus` + `ArduinoRS485` — Modbus TCP/RTU server
- `Ethernet` (W5100/W5500) — TCP stack
- `InterruptBasedPulseCounter` or `attachInterrupt()` — flow pulse

**Why this is the best fallback:**
- 54 DI/O + 16 ADC pins → massive headroom
- `ArduinoModbus` library exposes registers in exact same address layout as LOGO! 8.3
- W5500 Ethernet is ultra-stable (hardware TCP/IP stack, no OS jitter)
- Engineers already know Arduino → fast to implement
- Cost: ~$20 clone Mega + $15 W5500 shield + $5 RS485 module = **~$40 total**
- Acts as a drop-in replacement — when LOGO! 8.3 arrives, swap without touching ODROID code

**Limitations vs real PLC:**
- No IEC 61131-3 programming environment
- No hardware watchdog on DO (need to implement in `loop()`)
- 5 V logic → need level shifters if sensors are 24 VDC-sourced (use optocouplers)
- Not SIL-rated — sufficient for MVP, not for certified safety applications

---

### Option B — Controllino MEGA (Industrial-Grade Arduino)

For a more robust fallback with no level-shifter or optocoupler work:

- Arduino MEGA core + 36 relay DO + 12 analog input (24 V compatible) + RS485 + RTC
- DIN rail mount, 24 VDC I/O compatible out of the box
- Same `ArduinoModbus` library works identically
- Cost: ~$120–150
- Best choice if the station is deployed before LOGO! 8.3 arrives and industrial I/O is needed

---

### Option C — ESP32 + LAN8720 Ethernet (Cheap, MQTT-native)

Only recommend if you want the MCU to also talk directly to AWS IoT Core (dual MQTT sources):

- ESP32 with LAN8720 breakout → Ethernet + MQTT direct to AWS IoT Core
- `esp-modbus` library supports Modbus TCP slave
- -40 °C to +85 °C industrial range
- 34 GPIO but 3.3 V only → level shifters needed for 24 V I/O
- Cost: ~$10 + $5 LAN8720 module
- Limitation: GPIO count tight, WiFi-only without Ethernet add-on

---

### MCU Fallback — Modbus Register Map (Matches Future LOGO! 8.3 Layout)

| Address | Type | Direction | Description |
|---|---|---|---|
| Coil 0 (0x0000) | Coil | ODROID → MCU | Valve open command |
| Coil 1 (0x0001) | Coil | ODROID → MCU | Permit active |
| Input 0 (0x0000) | Discrete Input | MCU → ODROID | E-stop status (1 = tripped) |
| Input 1 (0x0001) | Discrete Input | MCU → ODROID | Overpressure alarm |
| Input 2 (0x0002) | Discrete Input | MCU → ODROID | Low-tank alarm |
| Input 3 (0x0003) | Discrete Input | MCU → ODROID | Valve feedback (valve physically open) |
| Register 0–1 (0x0000–0x0001) | Input Register | MCU → ODROID | Flow pulses (32-bit, hi/lo word) |
| Register 2 (0x0002) | Input Register | MCU → ODROID | Level gauge % (0–1000 = 0–100.0%) |
| Register 3 (0x0003) | Input Register | MCU → ODROID | Comms watchdog counter (increments each second) |

**ODROID watchdog rule**: if `Register 3` does not increment for 30 s → MQTT alarm + UI error. MCU independently closes valve if no Modbus poll for 30 s.

---

## Sensor Integration

### Level Gauge (Tank Level Monitoring)

AdBlue tank level monitoring is critical for two reasons: (1) prevent dispensing from an empty tank; (2) trigger low-tank alerts to the operations dashboard.

| Sensor type | Interface | Python library | Notes |
|---|---|---|---|
| **Ultrasonic non-contact** (recommended) | RS485 Modbus RTU | `pymodbus` | Best for AdBlue — no chemical contact; ±1–3 mm accuracy |
| Pressure/hydrostatic | 4-20 mA → ADS1115 | `adafruit-circuitpython-ads1x15` | Cheaper, ±0.5 % FS; needs I2C ADC |
| Float / reed switch | Dry contact DI | `python3-gpiod` | Alarm only (high/low), no continuous reading |
| IO-Link | USB IO-Link master | Vendor SDK | Future expansion |

**Recommended**: **VEGA PULS 10** (non-contact ultrasonic, AdBlue compatible, RS485 Modbus RTU, IP68).
Alternative: **Gems Sensors BM26** level transmitter with 4-20 mA output.

**Wiring to ODROID-M2:**
```
RS485 sensor ──── USB-to-RS485 dongle ───── ODROID-M2 USB port
                                            └── pyserial → pymodbus RTU master
                                                poll interval: 5 s
                                                register: holding register 0x0000 = level (0–10000 mm)
```

If using 4-20 mA:
```
Sensor 4-20mA ── 250Ω shunt resistor ── ADS1115 I2C ADC ── ODROID-M2 I2C (GPIO 3/5)
                  (1–5 V on ADS1115 input)      └── Python reads 16-bit raw → convert to mm
```

---

### Flow Meter

Flow accuracy is critical for billing — every litre counts.

| Flow meter | Interface | Accuracy | Python library | Notes |
|---|---|---|---|---|
| **Digmesa FHS** (recommended) | RS485 Modbus RTU | ±0.5 % | `pymodbus` | Industrial, AdBlue certified, share RS485 bus with level gauge |
| Digmesa FHKU (Keyence-style) | Pulse output | ±1 % | `python3-gpiod` GPIO IRQ | Simpler wiring |
| YF-S201 | Pulse output | ±3–5 % | `python3-gpiod` GPIO IRQ | Too inaccurate for metered billing |
| Coriolis | HART/4-20mA | ±0.1 % | HART modem | Overkill, very expensive |

**Recommended**: **Digmesa FHS** on shared RS485 bus.
Fallback for MVP: **FHKU pulse output** on ODROID-M2 GPIO interrupt (pin from 40-pin header).

**RS485 bus topology (shared):**
```
ODROID-M2 USB-to-RS485 ─────┬──── Digmesa FHS (Modbus addr 0x01)
                             └──── VEGA PULS 10 (Modbus addr 0x02)
                             (max 31 devices on one RS485 segment, 1200 m cable)
```

---

### Camera Integration (3 cameras)

| Camera | Use | Interface | Resolution | Notes |
|---|---|---|---|---|
| Nozzle tamper cam | Detect nozzle inserted/removed, drip detection | USB 3.0 | 1080p30 | Mount at knee height pointing at nozzle |
| Area overview cam | General CCTV, vehicle plate at distance | USB 3.0 | 1080p30 | Wide angle, mount 2–3 m high |
| Licence plate cam | Vehicle plate at entry | USB 3.0 | 1080p30 or 4K | Narrow FOV, mount at bumper height |

**Recommended USB cameras**: Logitech BRIO 4K or C920 HD (verified working on M2 — Hardkernel uses BRIO in NPU demo).

**Python stack:**
```python
import cv2  # opencv-python
cap = cv2.VideoCapture('/dev/video0')  # camera 0
# Use RKNN NPU for YOLOv8 inference (6 TOPS on M2)
# rknn_lite for Python: pip install rknn-lite2
```

**AI inference on M2 NPU (6 TOPS):**
- Run YOLOv8n at ~40 fps on a single 1080p stream
- Run 3 streams sequentially at ~13 fps each, or round-robin
- Convert model to RKNN format: `rknn-toolkit2` (Rockchip SDK)
- Models: person detection, licence plate detection, nozzle tamper detection

**Storage**: Record H.264 to NVMe SSD, upload clips to S3 on dispense events.

---

## Display (Pump Auth Code)

The 4-digit auth code must be visible to the driver at the pump.

| Option | Part | Interface | Cost |
|---|---|---|---|
| **Recommended** | OLED 128×64 SSD1306 | I2C (SDA/SCL) | ~$5 |
| Alternative | 16×2 LCD with PCF8574 backpack | I2C | ~$5 |
| Bright outdoor | MAX7219 7-segment 4-digit module | SPI | ~$3 |

Wire to RPi I2C: GPIO 2 (SDA), GPIO 3 (SCL), 3.3V, GND.
Implement in `display.py` `show_auth_code()` once display type is confirmed.

---

## BLE Station Proximity Beacon (Optional)

RPi BLE can advertise an iBeacon/Eddystone so the app can auto-detect nearby stations without GPS.

- Library: `bluepy` (Linux) or `bleak` (cross-platform)
- Advertise: Station ID + status (IDLE/BUSY) in beacon payload
- App: scan for beacons → pre-select station → smoother UX
- **Decision needed**: Confirm if BLE auto-detection is a required feature.

---

## Connectivity

| Interface | Technology | Purpose |
|---|---|---|
| Internet uplink | WiFi or 4G/LTE dongle (e.g. Huawei E3372) | AWS IoT Core MQTT |
| BLE | RPi BT 5.0 | App proximity detection, pump code |
| Local Ethernet | 100 Mbps | PLC communication (if PLC used) |

---

## Power Supply

- RPi 5: USB-C PD 5A (27W). Use a DIN-rail 24V→5V DC-DC converter for industrial supply.
- Solenoid valve: 24VDC, typically 5–20W. Use the same industrial 24V supply.
- PLC (if used): 24VDC, draw per Siemens datasheet.

---

## Security Checklist

- [ ] AWS IoT Thing created per station with unique mTLS certificate
- [ ] IoT certificate stored in certs/ directory, not committed to git
- [ ] RPi SSH key-based auth only (password auth disabled)
- [ ] UFW firewall: allow only port 22 (SSH), 8883 (MQTT), 8080 (local API from LAN only)
- [ ] OTA updates via AWS Systems Manager or Ansible playbook
- [ ] Watchdog timer enabled in Ubuntu (`systemd-watchdog`)

---

## Pending Hardware Decisions

| # | Decision | Recommendation | Status | Impact on code |
|---|---|---|---|---|
| 1 | **SBC model** | **ODROID-M2 (8 GB)** ← updated recommendation | ⬜ Confirm | Change `hardware_service` env from M1S → M2; GPIO pinout identical |
| 2 | **PLC (production)** | LOGO! 8.3 `6ED1052-1MD08-0BA1` | ⬜ Pending | Implement `MODBUS` mode in `plc_interface.py` once register map is known |
| 3 | **MCU fallback (launch)** | Arduino MEGA + W5500 + RS485 (`~$40`) or Controllino MEGA (`~$130`) | ⬜ Decide | Implement Arduino sketch with Modbus TCP server matching register map above |
| 4 | **Level gauge** | VEGA PULS 10 ultrasonic, RS485 Modbus RTU | ⬜ Confirm | Add polling in `plc_interface.py` or new `sensor_reader.py` |
| 5 | **Flow meter** | Digmesa FHS (±0.5 %, RS485) or FHKU (pulse) | ⬜ Confirm | Set `FLOW_PULSES_PER_LITRE` or Modbus address in `.env` |
| 6 | **Cameras** | 3× Logitech C920 or BRIO, USB 3.0 | ⬜ Confirm | Add `camera_service.py` module + RKNN NPU integration |
| 7 | **Display type** | SSD1306 OLED 128×64 I2C | ⬜ Confirm | Implement `display.py` `show_auth_code()` |
| 8 | **Internet uplink** | Site Ethernet (preferred) + 4G USB dongle backup | ⬜ Confirm | `hardware_service` systemd network config |
| 9 | **BLE beacon** | Built-in BT 5.0 on M2 (if WiFi dongle not consuming BT) | ⬜ Optional | Implement `ble_beacon.py` if app auto-detect required |
| 10 | **Local video storage** | WD Blue SN580 500 GB NVMe | ⬜ Confirm | Mount at `/data`; systemd tmpfiles for retention policy |
