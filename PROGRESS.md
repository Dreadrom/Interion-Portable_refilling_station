# AceRev Refill Kiosk — Project Progress Tracker
**Last updated**: April 1, 2026

Legend: ✅ Done · 🔶 Partial/Stub · ⬜ Not started · 🔒 Blocked (pending hardware decision)

---

## 1. USER APP (`portable-refill-app/`) — Expo / React Native / TypeScript

### Auth & Accounts
| Screen / Feature | Status | Notes |
|---|---|---|
| Login (email) | ✅ | Offline fallback to local session when backend unreachable |
| Login (phone OTP) | ✅ | Demo: any 6 digits accepted; real SNS wired in backend |
| Create account | ✅ | Offline fallback to local session |
| Forgot / reset password | ✅ | Email-based reset flow |
| Guest / Quick Dispense mode | ✅ | Phone-OTP verified, no account needed |
| Dev test account (`devLogin`) | ✅ | MYR 500.00, bypasses backend |
| Profile edit | ✅ | Saves locally when API unreachable |
| Change password | ✅ | Offline fallback acknowledgement |

### Core Dispensing Flow (end-to-end)
| Screen | Status | Notes |
|---|---|---|
| QR Scanner / Nearby Stations | ✅ | Falls back to T001–T004 demo stations when API down |
| Station Info | ✅ | Tank level, alarm state, pricing, 20 L low-tank cap |
| Pre-Authorization | ✅ | Exact estimate deducted; no 10% buffer; 10-min reservation text |
| Pump Unlocked | ✅ | 10-min countdown timer; routes to pump-auth |
| Pump Auth | ✅ | Any 4 digits accepted (demo); real challenge via WebSocket when live |
| Live Dispensing — simulation | ✅ | Simulated counter used when stationId starts with `demo-` |
| Live Dispensing — real WebSocket | ✅ | Connects to API GW WebSocket; receives IoT telemetry push |
| Live Dispensing — fallback | ✅ | Falls back to simulation if WebSocket not configured / fails |
| Refueling Complete | ✅ | "Order Fulfilled", live wallet balance, no refund display |

### Wallet & Transactions
| Feature | Status | Notes |
|---|---|---|
| Wallet balance (live, Zustand) | ✅ | Updates on every screen that reads balance |
| Top-Up Wallet (Touch 'n Go only) | ✅ | Simulated — real Fiuu/TnG API not integrated |
| Cash Out (bank / DuitNow) | ✅ | Simulated — real payout API not integrated |
| Transaction History | ✅ | Persistent via AsyncStorage; no clear button; "Order Fulfilled" |
| Recent Activity on Home | ✅ | Last 3 transactions, refreshes on focus |

### Account Management
| Feature | Status | Notes |
|---|---|---|
| Bank Accounts | ✅ | Add/remove, DuitNow support, local AsyncStorage |
| Settings (PTS network) | ✅ | Host, port, protocol for PTS connection |

### Pending App Items
| Feature | Status | Blocked by |
|---|---|---|
| Real Fiuu Top-Up payment | ⬜ | Fiuu merchant account + API keys |
| Real cash-out payout API | ⬜ | Fiuu merchant account + bank API |
| Push notifications | ⬜ | AWS SNS + Expo push token wiring |
| Map view for stations | ⬜ | Requires GPS + map component |
| Receipt PDF / email export | ⬜ | — |
| WebSocket reconnect logic | 🔶 | Currently reconnects once; should backoff |
| BLE station auto-detection | ⬜ | ODROID BLE beacon not yet implemented |

### TypeScript
- **0 errors** as of April 1, 2026

---

## 2. HARDWARE SERVICE (`hardware_service/`) — Python / Ubuntu / ODROID-M2

### Core Service
| File | Status | Notes |
|---|---|---|
| `config.py` | ✅ | Typed config from `.env` |
| `mqtt_client.py` | ✅ | AWS IoT Core mTLS connection, pub/sub, reconnect callbacks |
| `pump_controller.py` | ✅ | Full state machine: IDLE → AUTHORIZING → WAITING\_AUTH → DISPENSING → IDLE |
| `main.py` | ✅ | Entry point, wires all modules, systemd-ready |
| `local_api.py` | ✅ | Flask diagnostics HTTP API on port 8080 |
| `display.py` | 🔶 | Stub — real SSD1306 OLED driver not yet implemented |
| `plc_interface.py — STUB mode` | ✅ | Simulated 5 L/min dispensing for end-to-end testing |
| `plc_interface.py — GPIO mode` | 🔒 | Blocked — confirm ODROID-M2; implement with python3-gpiod (pinout identical to M1S) |
| `plc_interface.py — MODBUS mode` | 🔒 | Blocked — confirm LOGO! 8.3; provide Modbus register map |
| `plc_interface.py — SNAP7 mode` | 🔒 | Blocked — confirm S7-1200 model; provide DB layout |
| `requirements.txt` | ✅ | Core deps listed; hardware-specific deps commented |
| `.env.example` | ✅ | All config keys documented |

### What Can Run Today (STUB mode)
- ✅ `HARDWARE_MODE=STUB python main.py` — connects to IoT Core, runs full dispense flow end-to-end
- ✅ Auth code generation (logged to console + display stub)
- ✅ Simulated flow telemetry published to IoT Core every 500 ms
- ✅ Valve open/close commands sent (simulated — no physical relay)
- ✅ Diagnostics API at `http://localhost:8080`

### Pending Hardware Service Items
| Item | Status | Blocked by |
|---|---|---|
| SSD1306 OLED driver in `display.py` | 🔒 | Confirm display type (SSD1306 recommended) |
| GPIO mode (`python3-gpiod`) | 🔒 | Confirm ODROID-M2 (M1S also viable); confirm solenoid wiring |
| MODBUS mode (LOGO! 8.3) | 🔒 | Confirm PLC; provide Modbus register map |
| Unit price from backend (not hardcoded) | 🔶 | Currently `UNIT_PRICE = 10.00` in `pump_controller.py` |
| BLE beacon advertising | ⬜ | Decision pending |
| Watchdog timer setup | ⬜ | Systemd watchdog config when deploying to production |
| OTA update mechanism | ⬜ | AWS SSM or Ansible playbook |

---

## 3. BACKEND (`backend/`) — Node.js / TypeScript / AWS Lambda

### Handlers
| File | Status | Notes |
|---|---|---|
| `AuthHandler.ts` | ✅ | Register, login (email), OTP send/verify, profile update, change password |
| `AuthHandler.ts — AWS SNS OTP` | ✅ | Real SMS via AWS SNS; `demo_otp` returned in non-production only |
| `StationsHandler.ts` | ✅ | Station list, detail, tank status, alarm reporting |
| `StationCommandHandler.ts` | ✅ | REST → MQTT command publisher (AUTHORIZE / STOP / PING) |
| `WebSocketHandler.ts` | ✅ | API GW WebSocket connection registry in DynamoDB (TTL 2 h) |
| `IoTBridgeHandler.ts` | ✅ | IoT Rule → Lambda → fan-out push to app via WebSocket |

### Infrastructure (AWS — configuration only, not code)
| Item | Status | Notes |
|---|---|---|
| API Gateway REST API | ⬜ | Needs creating and wiring to Lambda handlers |
| API Gateway WebSocket API | ⬜ | Routes: `$connect`, `$disconnect`, `subscribe` → `WebSocketHandler` |
| DynamoDB table `AceRevWsConnections` | ⬜ | PK: transactionId, SK: connectionId, TTL on `ttl` field |
| IoT Core — station Things + certs | ⬜ | One Thing per station, mTLS certs provisioned |
| IoT Core — Rules (telemetry + auth) | ⬜ | Triggers `IoTBridgeHandler` on `acerev/stations/+/telemetry/#` + `auth/challenge` |
| PostgreSQL (RDS) | ⬜ | Schema exists (`database/schema.sql`); DB not yet provisioned |
| Lambda environment variables | ⬜ | `IOT_ENDPOINT`, `WS_API_ENDPOINT`, `WS_CONNECTIONS_TABLE`, `NODE_ENV` |
| App `.env` — `WS_API_URL` | ⬜ | Needs real API GW WebSocket URL once deployed |

### TypeScript
- **0 errors** as of April 1, 2026

---

## 4. HARDWARE DESIGN (`hardware/`) — Physical Components

### Decisions Made
| # | Decision | Choice | Status |
|---|---|---|---|
| Microcontroller | **ODROID-M2 (8 GB)** — updated from M1S | ⛳ User confirming |
| PLC | Siemens LOGO! 8.3 `6ED1052-1MD08-0BA1` | ✅ Confirmed concept — pending register map |
| Internet uplink | WiFi (built-in) + 4G USB dongle backup | ✅ |
| Product | AdBlue | ✅ |
| Pricing | MYR 10.00 / L | ✅ |

### Decisions Pending
| # | Decision | Impact |
|---|---|---|
| 1 | **Confirm SBC: M2 vs M1S** | M2 recommended (3 USB ports, 6 TOPS NPU, 3× CPU); M1S if budget-constrained |
| 2 | **LOGO! 8.3 vs S7-1200** | Determines MODBUS vs SNAP7 mode; provide register map |
| 3 | **MCU fallback (launch safety net)** | Arduino MEGA + W5500 (~$40) or Controllino MEGA (~$130); same Modbus register map |
| 4 | **Level gauge** | VEGA PULS 10 ultrasonic RS485 Modbus RTU recommended; add polling to `sensor_reader.py` |
| 5 | **Flow meter** | Digmesa FHS (±0.5%, RS485) recommended; set Modbus address in `.env` |
| 6 | **Display type** | SSD1306 OLED recommended; implement `display.py` once confirmed |
| 7 | **Cameras** | 3× Logitech C920/BRIO USB 3.0; add `camera_service.py` with RKNN NPU inference |
| 8 | **Solenoid valve spec** | DN15 or DN20, 24VDC NC, SS316 body |
| 9 | **Enclosure IP rating** | IP55 minimum recommended for outdoor/wet environment |

### Bill of Materials (Provisional)
| Component | Part | Est. Cost (USD) |
|---|---|---|
| SBC | **ODROID-M2 8 GB** (recommended) | ~$155 |
| MCU fallback | Arduino MEGA + W5500 + RS485 shield | ~$40 |
| PLC (production) | Siemens LOGO! 8.3 `6ED1052-1MD08-0BA1` | ~$160 |
| Solenoid valve | 24VDC NC, DN20, SS316 | ~$30–60 |
| Flow meter | Digmesa FHS ±0.5%, RS485 Modbus RTU | ~$60–80 |
| Level gauge | VEGA PULS 10 ultrasonic, RS485 | ~$150–200 |
| Cameras | 3× Logitech C920 USB 3.0 | ~$75 (3×$25) |
| USB hub | Powered USB 3.0 7-port | ~$25 |
| OLED display | SSD1306 128×64 I2C | ~$5 |
| NVMe SSD | WD Blue SN580 500 GB | ~$50 |
| UPS kit | Hardkernel UPS Kit for M2 | ~$10 |
| 4G dongle | Huawei E3372 (backup uplink) | ~$40 |
| Enclosure | IP55 DIN-rail box ≥300×300 mm | ~$50–80 |
| **Total est.** | | **~$850–1,000 per station** |

---

## 5. OVERALL SYSTEM READINESS

| Layer | Demo Ready | Production Ready |
|---|---|---|
| User App | ✅ Full demo flow | 🔶 Needs Fiuu, push notif, real WS URL |
| Hardware Service (STUB) | ✅ End-to-end testable | 🔒 Needs hardware decisions + MODBUS impl |
| Backend Lambdas | ✅ Code complete | ⬜ Needs AWS infra provisioning |
| Hardware (physical) | ⬜ Parts unconfirmed | ⬜ Pending component decisions |
| Cloud Infrastructure | ⬜ Not provisioned | ⬜ API GW, DynamoDB, IoT Core, RDS needed |

### Next Actions (in priority order)
1. **Hardware**: Confirm M2 vs M1S SBC → unblocks GPIO implementation (pinout identical so just a config label change)
2. **Hardware**: Decide MCU fallback (Arduino MEGA ~$40 vs Controllino MEGA ~$130) → write Arduino sketch before launch
3. **Hardware**: Confirm level gauge (VEGA PULS 10 RS485) + flow meter (Digmesa FHS RS485) → implement `sensor_reader.py`
4. **Hardware**: Confirm cameras (3× USB 3.0) → implement `camera_service.py` with RKNN NPU on M2
5. **Hardware**: Confirm LOGO! 8.3 register map → implement MODBUS mode in `plc_interface.py`
6. **Backend**: Provision AWS infrastructure (API GW, IoT Core, DynamoDB, RDS)
7. **Hardware Service**: Implement display.py SSD1306 driver
8. **App**: Set `WS_API_URL` in `.env` once API GW WebSocket is provisioned
9. **App / Backend**: Integrate Fiuu payment for real top-up
