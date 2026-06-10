# Complete Wiring Diagram — AceRev Portable AdBlue Refilling Station
> Rev 1.0 — May 12, 2026 | SBC: ODROID-M2 (8 GB) | PLC: Siemens LOGO! 8.3 (MCU fallback: Arduino MEGA 2560 + W5500)

---

## Table of Contents

1. [System-Level Block Diagram](#1-system-level-block-diagram)
2. [Power Distribution](#2-power-distribution)
3. [ODROID-M2 — 40-Pin GPIO Header Pinout](#3-odroid-m2--40-pin-gpio-header-pinout)
4. [ODROID-M2 — 14-Pin Expansion Header Pinout](#4-odroid-m2--14-pin-expansion-header-pinout)
5. [ODROID-M2 GPIO Wiring to Peripherals](#5-odroid-m2-gpio-wiring-to-peripherals)
6. [SSD1306 OLED Display Wiring](#6-ssd1306-oled-display-wiring)
7. [4-Channel Opto-Isolated Relay Module Wiring](#7-4-channel-opto-isolated-relay-module-wiring)
8. [RS485 Bus Wiring — Level Gauge + Flow Meter](#8-rs485-bus-wiring--level-gauge--flow-meter)
9. [Solenoid Valve Wiring (24V DC)](#9-solenoid-valve-wiring-24v-dc)
10. [Siemens LOGO! 8.3 — Terminal Wiring](#10-siemens-logo-83--terminal-wiring)
11. [Siemens LOGO! 8.3 — Modbus TCP Register Map](#11-siemens-logo-83--modbus-tcp-register-map)
12. [Arduino MEGA 2560 + W5500 — Fallback PLC Wiring](#12-arduino-mega-2560--w5500--fallback-plc-wiring)
13. [Arduino MEGA + MAX485 — RS485 Module Wiring](#13-arduino-mega--max485--rs485-module-wiring)
14. [Camera USB Connections](#14-camera-usb-connections)
15. [LAN / Ethernet Connections](#15-lan--ethernet-connections)
16. [DIN-Rail Enclosure Layout](#16-din-rail-enclosure-layout)
17. [Wiring Safety Notes & Checklist](#17-wiring-safety-notes--checklist)

---

## 1. System-Level Block Diagram

```
230V AC MAINS
    │
    ├──[DIN-Rail 24V/5A PSU (E5)]──────────────── 24V DC Bus
    │         ├── Siemens LOGO! 8.3 L+ / M        │
    │         ├── Solenoid Valve (via relay Q1)    │
    │         ├── Relay module coil VCC (C4)       │
    │         └── 24V → 5V step-down (relay logic) │
    │                                              │
    └──[12V/2A PSU (A2)]──[12V Mini UPS (C5)]─── 12V DC Bus
              └── ODROID-M2 DC jack (12V/2A)

ODROID-M2 (Ubuntu 20.04 / hardware_service)
    │
    ├── GbE (RJ45, CAT6) ──────── LAN Switch/Router ──── 4G Modem ──── AWS IoT Core
    │                                    │
    │                             LOGO! 8.3 (Modbus TCP :502)
    │                             [OR Arduino MEGA fallback]
    │
    ├── USB 3.0 ────────────────── Logitech C920 #1 (/dev/video0) — Nozzle tamper
    ├── USB 3.0-C ───────────────── Logitech C920 #2 (/dev/video1) — Licence plate
    ├── USB 2.0 ─────────────────── USB-to-RS485 dongle (CH340/CP2102)
    │                                    │
    │                             RS485 A/B twisted pair (Belden 9841)
    │                                    ├── Digmesa FHS flow meter (addr 0x01)
    │                                    └── VEGA PULS 10 level gauge (addr 0x02)
    │
    ├── GPIO Pin 3  (I2C1 SDA) ─── SSD1306 OLED SDA
    ├── GPIO Pin 5  (I2C1 SCL) ─── SSD1306 OLED SCL
    ├── GPIO Pin 11 (GPIO17)   ─── Opto-relay IN1 → valve command
    ├── GPIO Pin 15 (GPIO22)   ─── E-stop dry contact read
    ├── GPIO Pin 1  (3.3V)     ─── OLED VCC / relay opto-side VCC
    └── GPIO Pin 6  (GND)      ─── OLED GND / relay opto-side GND

SIEMENS LOGO! 8.3 (Primary PLC)
    ├── I1 ── E-stop (NC contact → 24V)
    ├── I2 ── Overpressure alarm (NC contact → 24V)
    ├── I3 ── Low-tank alarm (level gauge dry contact)
    ├── I4 ── Valve position feedback (solenoid aux contact)
    ├── Q1 ── Solenoid valve 24V DC coil (via NC relay contact)
    ├── Q2 ── Permit indicator LED (optional)
    ├── L+  ── 24V DC from PSU
    ├── M   ── 0V / GND (PSU negative)
    └── RJ45 ── LAN switch → ODROID-M2 Modbus TCP master
```

---

## 2. Power Distribution

```
┌──────────────────────────────────────────────────────────────────────┐
│                       POWER RAIL SUMMARY                             │
├────────────┬──────────────┬────────────┬─────────────────────────────┤
│ Rail       │ Source       │ Voltage    │ Loads                       │
├────────────┼──────────────┼────────────┼─────────────────────────────┤
│ 24V DC     │ E5 DIN PSU   │ 24V / 5A   │ LOGO! 8.3, relay coils,    │
│            │              │            │ solenoid valve, DIs (24V)   │
├────────────┼──────────────┼────────────┼─────────────────────────────┤
│ 12V DC     │ A2 + C5 UPS  │ 12V / 2A   │ ODROID-M2 DC jack           │
├────────────┼──────────────┼────────────┼─────────────────────────────┤
│ 5V DC      │ ODROID USB   │ 5V         │ USB hub, cameras, RS485     │
│            │              │ (from hub) │ dongle, OLED                │
├────────────┼──────────────┼────────────┼─────────────────────────────┤
│ 3.3V DC    │ ODROID GPIO  │ 3.3V       │ Opto-relay IN pins, OLED    │
│            │ Pin 1 / 17   │ (50 mA max)│ VCC (SSD1306: 3.3V ok)      │
└────────────┴──────────────┴────────────┴─────────────────────────────┘

Ground star point: All 24V GND, 12V GND, and 5V/3.3V GND tie to
a single DIN-rail ground bus bar to prevent ground loops.
```

### 24V PSU Wiring

```
DIN-Rail 24V/5A PSU (E5)
  L  ────── 230V Live (via MCB / circuit breaker)
  N  ────── 230V Neutral
  PE ────── Earth (enclosure DIN rail)
  +  ────── 24V DC positive bus bar
  -  ────── 0V / GND bus bar (star ground)
```

---

## 3. ODROID-M2 — 40-Pin GPIO Header Pinout

> Physical pin numbering — Pin 1 is at the corner nearest the USB ports.
> All GPIO are 3.3V logic. Do NOT apply 5V or 24V directly to any GPIO pin.

```
        3.3V [1 ]──[2 ]  5V
   I2C1_SDA  [3 ]──[4 ]  5V          ← GPIO2  — OLED SDA ★
   I2C1_SCL  [5 ]──[6 ]  GND         ← GPIO3  — OLED SCL ★
       GPIO4  [7 ]──[8 ]  UART0_TX    ← GPIO14 — debug serial
         GND  [9 ]──[10]  UART0_RX    ← GPIO15 — debug serial
      GPIO17  [11]──[12]  GPIO18      ← GPIO17 — relay IN1 (valve cmd) ★
      GPIO27  [13]──[14]  GND
      GPIO22  [15]──[16]  GPIO23      ← GPIO22 — e-stop read ★
        3.3V  [17]──[18]  GPIO24
      MOSI0   [19]──[20]  GND         ← GPIO10 — SPI0 MOSI (CAN-FD add-on)
      MISO0   [21]──[22]  GPIO25      ← GPIO9  — SPI0 MISO (CAN-FD add-on)
      SCLK0   [23]──[24]  CE0         ← GPIO11 — SPI0 CLK,  GPIO8 — SPI0 CE0
         GND  [25]──[26]  CE1         ← GPIO7  — SPI0 CE1
       SDA0   [27]──[28]  SCL0        ← GPIO0/1 — ID EEPROM (do not use)
      GPIO5   [29]──[30]  GND
      GPIO6   [31]──[32]  GPIO12
      GPIO13  [33]──[34]  GND
      GPIO19  [35]──[36]  GPIO16
      GPIO26  [37]──[38]  GPIO20
         GND  [39]──[40]  GPIO21
```

**★ = Actively wired in this project**

| Pin | GPIO    | Function in Project       | Connected To                        |
|-----|---------|---------------------------|-------------------------------------|
| 1   | 3.3V    | Logic power               | OLED VCC, relay opto-side VCC       |
| 3   | GPIO2   | I2C1 SDA                  | SSD1306 OLED pin SDA                |
| 5   | GPIO3   | I2C1 SCL                  | SSD1306 OLED pin SCL                |
| 6   | GND     | Ground                    | OLED GND, relay opto-side GND       |
| 11  | GPIO17  | Digital Output — relay    | Opto-relay module IN1               |
| 15  | GPIO22  | Digital Input — e-stop    | NC dry contact → GND when tripped   |
| 17  | 3.3V    | Logic power (aux)         | Spare / pull-up resistors           |
| 14/20/25/34/39 | GND | Ground bus           | Ground bus bar                      |

**Unused / Reserved Pins:**
- Pins 19, 21, 23, 24, 26: SPI0 — used by CAN-FD add-on board (A4) if installed
- Pins 8, 10: UART0 — available for debug console / serial debug
- Pins 7, 11–13, 15–16, 22, 29–40: Available GPIOs for future expansion

---

## 4. ODROID-M2 — 14-Pin Expansion Header Pinout

> Located on CON11, adjacent to the 40-pin header. Provides ADC, additional UART, and 5V/GND.

```
       5V  [1 ]──[2 ]  GND
      ADC0  [3 ]──[4 ]  ADC1        ← 1.8V MAX on ADC pins — use voltage divider
    UART2_TX [5 ]──[6 ]  UART2_RX
      GPIO  [7 ]──[8 ]  GPIO
      GPIO  [9 ]──[10]  GPIO
      GPIO  [11]──[12]  GPIO
        GND  [13]──[14]  GND
```

| Pin | Function           | Connected To (this project)                          |
|-----|--------------------|------------------------------------------------------|
| 3   | ADC0 (1.8V max)    | SPARE — if 4-20mA level sensor used, wire via ADS1115 on I2C instead |
| 4   | ADC1 (1.8V max)    | SPARE                                                |
| 5   | UART2 TX           | SPARE (could be used for second RS485 or debug)      |
| 6   | UART2 RX           | SPARE                                                |

> ⚠️ ADC input max is **1.8V**. For a 4-20mA sensor with 250Ω shunt (1–5V output),
> you MUST use an external ADS1115 I2C ADC module instead of the on-board ADC.

---

## 5. ODROID-M2 GPIO Wiring to Peripherals

### GPIO17 (Pin 11) → Opto-Relay IN1 (Valve Command)

```
ODROID Pin 11 (GPIO17, 3.3V logic)
    │
    ├──[270Ω resistor]──── IN1 of opto-relay module
    │                      (most modules have 1kΩ built in; add 270Ω if output is marginal)
    │
ODROID Pin 6 (GND) ──── GND (opto-side) of relay module
ODROID Pin 1 (3.3V) ─── VCC (opto-side) of relay module
```

> Verify your relay module: if the IN pin requires >2V to trigger,
> GPIO17's 3.3V HIGH is borderline. Add a 2N3904 NPN transistor
> (base via 1kΩ from GPIO17, emitter to GND, collector to IN) if the
> opto doesn't switch reliably.

### GPIO22 (Pin 15) → Emergency Stop Input

```
ODROID Pin 15 (GPIO22)
    │
    ├──[10kΩ pull-up to 3.3V (internal)]
    │
    └──── E-stop NC contact ──── GND (Pin 6)

State:  Contact CLOSED (normal) → GPIO22 reads HIGH (3.3V)
        Contact OPEN  (tripped) → GPIO22 reads LOW  (0V)

Python: gpio.read(22) == 0 → e-stop active
```

> The e-stop switch must be **Normally Closed (NC)**. If the wire breaks or
> the button is pressed, the circuit opens → logic goes LOW → safe state (valve closes).

---

## 6. SSD1306 OLED Display Wiring

**Part**: SSD1306 0.96" 128×64 I2C OLED (C6)
**Interface**: I2C, default address `0x3C`

```
SSD1306 OLED           ODROID-M2 (40-pin header)
──────────────         ─────────────────────────
VCC  ──────────────── Pin 1  (3.3V)
GND  ──────────────── Pin 6  (GND)
SDA  ──────────────── Pin 3  (GPIO2 / I2C1_SDA)
SCL  ──────────────── Pin 5  (GPIO3 / I2C1_SCL)
```

**Wire length**: Keep I2C wires ≤30 cm. Use twisted pair for SDA/SCL if longer.
**I2C pull-ups**: 4.7kΩ from SDA to 3.3V and SCL to 3.3V (most SSD1306 breakouts include these on-board — verify before adding external ones).

**Linux verification**:
```bash
sudo i2cdetect -y 1
# Expected: address 0x3C or 0x3D visible in the grid
```

**Python library**: `pip install luma.oled` or `Adafruit-SSD1306`

---

## 7. 4-Channel Opto-Isolated Relay Module Wiring

**Part**: 4-ch opto-isolated relay module, 5V coil, active-LOW or active-HIGH (C4)
**Isolation**: Optical — ODROID 3.3V logic fully isolated from 24V load side

```
┌─────────────────────────────────────────────────────────────────┐
│          OPTO-RELAY MODULE (C4) — 4-Channel                     │
│                                                                 │
│  OPTO SIDE (low-voltage, ODROID side)                           │
│  ┌────────────────────────────────────┐                         │
│  │ VCC ── ODROID Pin 1 (3.3V)        │                         │
│  │ GND ── ODROID Pin 6 (GND)         │                         │
│  │ IN1 ── ODROID GPIO17 (Pin 11)     │  Channel 1 = VALVE CMD  │
│  │ IN2 ── ODROID GPIO27 (Pin 13)     │  Channel 2 = SPARE       │
│  │ IN3 ── ODROID GPIO5  (Pin 29)     │  Channel 3 = SPARE       │
│  │ IN4 ── ODROID GPIO6  (Pin 31)     │  Channel 4 = SPARE       │
│  └────────────────────────────────────┘                         │
│                                                                 │
│  LOAD SIDE (high-voltage, isolated)                             │
│  ┌────────────────────────────────────┐                         │
│  │ CH1 COM ── 24V DC from PSU (+)    │                         │
│  │ CH1 NO  ── Solenoid valve (+)     │  Normally Open contact  │
│  │ CH1 NC  ── (not used)             │                         │
│  │                                   │                         │
│  │ CH2–CH4: spare, not wired         │                         │
│  └────────────────────────────────────┘                         │
└─────────────────────────────────────────────────────────────────┘
```

**Logic polarity**: Most opto-relay boards are active-LOW (IN = LOW → relay ON).
Check your board's jumper/silkscreen. In `plc_interface.py` / `GPIOHardware`, set output LOW to open valve, HIGH to close.

---

## 8. RS485 Bus Wiring — Level Gauge + Flow Meter

**Cable**: Belden 9841 or equivalent 120Ω twisted pair (F2)
**Dongle**: CH340 or CP2102 USB-to-RS485 converter (C3) → ODROID USB 2.0 port

```
ODROID-M2
USB 2.0 port
    │
    └── USB-to-RS485 dongle (C3)
           │         │
         A (+)     B (−)
           │         │
           │ 120Ω termination resistor across A–B at dongle end
           │
      ─────┴──────────── RS485 cable A (twisted pair)
      ─────┬──────────── RS485 cable B (twisted pair)
           │
           ├── [Digmesa FHS Flow Meter — Modbus RTU addr 0x01]
           │     Connect:  RS485 A → sensor terminal A (or +)
           │               RS485 B → sensor terminal B (or −)
           │               (check FHS datasheet for terminal labels)
           │
           └── [VEGA PULS 10 Level Gauge — Modbus RTU addr 0x02]
                 Connect:  RS485 A → sensor terminal A
                           RS485 B → sensor terminal B
                           120Ω termination resistor across A–B at this far end

Maximum cable run:  1200 m (at 9600 baud), 600 m (at 19200 baud)
Recommended baud:   9600 baud, 8N1
Max devices:        31 on one segment (only 2 used here)
```

**Sensor power supply**: Both sensors are loop-powered or externally powered — check each sensor's datasheet:
- VEGA PULS 10: typically 12–30V DC supply, connect to 24V bus
- Digmesa FHS: 5–24V DC supply, connect to 24V bus or 5V depending on model

**Linux device path**: `/dev/ttyUSB0` (or `ttyUSB1` if multiple USB-serial adapters)

**Python access**:
```python
from pymodbus.client import ModbusSerialClient
client = ModbusSerialClient(port='/dev/ttyUSB0', baudrate=9600, parity='N',
                            stopbits=1, bytesize=8, timeout=1)
```

---

## 9. Solenoid Valve Wiring (24V DC)

**Part**: 24V DC 1" BSP Normally-Closed solenoid valve (E4)
**Control**: Via opto-relay CH1 NO contact

```
24V DC PSU (+) ──── Relay CH1 COM
                    Relay CH1 NO ──── Solenoid Valve (+) coil terminal
                                           │
                                      [1N4007 flyback diode]  ← MANDATORY
                                           │ (cathode toward 24V+)
                                      ─────┘
24V DC PSU (−) ──── Solenoid Valve (−) / GND coil terminal

Relay contact:  OPEN  → valve de-energised → flow BLOCKED  (safe state)
                CLOSED → valve energised   → flow ALLOWED
```

> ⚠️ **Flyback diode is mandatory.** When the relay opens, the solenoid coil
> generates a large back-EMF spike. Without the 1N4007 (or similar) diode
> across the coil terminals (cathode to + terminal), this spike will destroy
> the relay contacts and may damage nearby electronics.
>
> Mount the diode **physically at the solenoid coil terminals**, not at the relay.

**Solenoid current estimate**: 24V / coil resistance. Typical 1" solenoid: ~1–2A inrush,
~0.5–1A holding. Relay contacts rated 10A (LOGO! 8.3 Q1) — well within spec.

---

## 10. Siemens LOGO! 8.3 — Terminal Wiring

**Part**: 6ED1052-1MD08-0BA1 — 8DI / 4DO relay, 12/24V RCE, Modbus TCP

### Physical Terminal Layout

```
LOGO! 8.3  — TOP TERMINAL STRIP
┌──────────────────────────────────────────────────────────────────────┐
│  L+   M    I1   I2   I3   I4   I5   I6   I7   I8                   │
│  24V  0V   DI   DI   DI   DI   DI   DI   DI   DI                   │
└──────────────────────────────────────────────────────────────────────┘

LOGO! 8.3  — BOTTOM TERMINAL STRIP
┌──────────────────────────────────────────────────────────────────────┐
│  Q1(NO) Q1(COM)  Q2(NO) Q2(COM)  Q3(NO) Q3(COM)  Q4(NO) Q4(COM)   │
│  Relay out 1     Relay out 2     Relay out 3     Relay out 4        │
└──────────────────────────────────────────────────────────────────────┘

LOGO! 8.3  — FRONT (RJ45 Ethernet port)
  RJ45 ──── CAT6 ──── LAN Switch ──── ODROID-M2
```

### Terminal-by-Terminal Wiring

| Terminal | Signal          | Wiring                                                           |
|----------|-----------------|------------------------------------------------------------------|
| L+       | 24V power in    | 24V DC PSU positive (+) bus bar                                 |
| M        | 0V / GND        | 24V DC PSU negative (−) / ground bus bar                        |
| I1       | E-stop input    | E-stop button NC contact → 24V (L+) when closed, open on trip  |
| I2       | Overpressure    | Pressure switch NC contact → 24V (L+) when OK, open on alarm   |
| I3       | Low-tank alarm  | Level gauge alarm relay dry contact → 24V (L+) when OK          |
| I4       | Valve feedback  | Solenoid auxiliary/position contact → 24V when valve open       |
| I5–I8    | Spare           | Not wired (available for future sensors)                        |
| Q1 COM   | Relay 1 common  | 24V DC PSU positive (+)                                         |
| Q1 NO    | Valve command   | Solenoid valve (+) coil terminal [+ flyback diode to 24V+]      |
| Q2 COM   | Relay 2 common  | 24V DC PSU positive (+)                                         |
| Q2 NO    | Permit LED      | Indicator lamp (+) terminal (optional)                          |
| Q3–Q4    | Spare relays    | Not wired                                                        |

> All LOGO! 8.3 DI inputs are **sourcing** (active HIGH at 24V).
> Wire each sensor's dry contact between the DI terminal and L+ (24V).
> When contact closes, current flows into the DI terminal at 24V → logic HIGH.
> When contact opens (alarm/trip), DI reads LOW → PLC takes safety action.

### LOGO! 8.3 Ethernet Configuration

| Parameter         | Value                    |
|-------------------|--------------------------|
| IP Address        | `192.168.1.50` (static)  |
| Subnet Mask       | `255.255.255.0`          |
| Gateway           | `192.168.1.1`            |
| Modbus TCP Port   | `502`                    |
| ODROID `.env`     | `MODBUS_HOST=192.168.1.50` `MODBUS_PORT=502` |

Set via LOGO! Soft Comfort → Network Settings → Modbus.

---

## 11. Siemens LOGO! 8.3 — Modbus TCP Register Map

> ODROID-M2 is the Modbus **master**. LOGO! 8.3 is the Modbus **slave** (server).
> All addresses are 0-based (pymodbus convention).

| Address         | Modbus Type      | Direction         | LOGO! I/O  | Description                              |
|-----------------|------------------|-------------------|------------|------------------------------------------|
| Coil 0 (0x0000) | Coil             | ODROID → LOGO!    | → Q1       | Valve open command (1 = open, 0 = close) |
| Coil 1 (0x0001) | Coil             | ODROID → LOGO!    | → Q2       | Permit active (1 = authorised session)   |
| Input 0 (0x0000)| Discrete Input   | LOGO! → ODROID    | I1 →       | E-stop status (1 = tripped)              |
| Input 1 (0x0001)| Discrete Input   | LOGO! → ODROID    | I2 →       | Overpressure alarm (1 = alarm)           |
| Input 2 (0x0002)| Discrete Input   | LOGO! → ODROID    | I3 →       | Low-tank alarm (1 = alarm)               |
| Input 3 (0x0003)| Discrete Input   | LOGO! → ODROID    | I4 →       | Valve position feedback (1 = open)       |
| HR 0–1 (0x0000–0x0001) | Input Reg | LOGO! → ODROID  | VB →       | Flow pulses 32-bit (hi word / lo word)   |
| HR 2 (0x0002)   | Input Register   | LOGO! → ODROID    | VB →       | Tank level % × 10 (0–1000 = 0–100.0%)   |
| HR 3 (0x0003)   | Input Register   | LOGO! → ODROID    | VB →       | Comms watchdog counter (increments /sec) |

**pymodbus call examples** (`plc_interface.py` — `ModbusHardware`):
```python
# Open valve (write Coil 0 = True)
client.write_coil(0, True, slave=1)

# Read e-stop (read Discrete Input 0)
result = client.read_discrete_inputs(0, 1, slave=1)
estop = result.bits[0]   # True = tripped

# Read flow pulses (Input Registers 0 + 1, 32-bit)
r = client.read_input_registers(0, 2, slave=1)
pulses = (r.registers[0] << 16) | r.registers[1]
```

**PLC Safety Interlock (LOGO! ladder logic)**:
```
Q1 (valve) = Coil0_ValveCmd
           AND NOT I1 (e-stop)
           AND NOT I2 (overpressure)
           AND NOT I3 (low tank)
           AND Coil1_PermitActive
           AND NOT T1 (watchdog timer > 30s)

T1 = Retentive timer reset each time ODROID polls HR3
     If no poll for 30s → T1 times out → Q1 forced OFF
```

---

## 12. Arduino MEGA 2560 + W5500 — Fallback PLC Wiring

> Used in place of LOGO! 8.3 during MVP / while PLC is on order.
> Exposes **identical Modbus TCP register map** (see Section 11).
> `HARDWARE_MODE=MODBUS` in ODROID `.env` — no code change needed.

### W5500 Ethernet Shield on MEGA

The W5500 shield stacks directly on top of the Arduino MEGA 2560.

```
Arduino MEGA 2560
    │
    └─[W5500 Ethernet Shield stacked on top]
           │
           ├── SPI MOSI  ── MEGA Pin 51
           ├── SPI MISO  ── MEGA Pin 50
           ├── SPI SCK   ── MEGA Pin 52
           ├── SPI CS    ── MEGA Pin 10  (W5500 chip select)
           ├── RESET     ── MEGA Pin 9   (W5500 hardware reset)
           └── RJ45 ──── CAT6 ──── LAN Switch ──── ODROID-M2
```

### MEGA Digital I/O Pin Assignments

```
MEGA PIN ASSIGNMENTS
═══════════════════

SPI (W5500 Shield)
  Pin 50  MISO  ─┐
  Pin 51  MOSI  ─┤── W5500 shield (stacked)
  Pin 52  SCK   ─┤
  Pin 10  CS    ─┘

UART1 (MAX485 RS485 Module)
  Pin 18  TX1   ──── MAX485 DI  (data in to RS485)
  Pin 19  RX1   ──── MAX485 RO  (data out from RS485)
  Pin 20  GPIO  ──── MAX485 DE + RE (tied) — direction control
                     HIGH = transmit, LOW = receive

Digital Outputs (relay control)
  Pin 22  DO    ──── Relay module IN1 → solenoid valve (valve command)
  Pin 23  DO    ──── Relay module IN2 → permit indicator (optional)

Digital Inputs (alarm/interlock)
  Pin 24  DI    ──── E-stop NC contact → GND    (INPUT_PULLUP; LOW = tripped)
  Pin 25  DI    ──── Overpressure alarm contact  (INPUT_PULLUP; LOW = alarm)
  Pin 26  DI    ──── Low-tank alarm contact       (INPUT_PULLUP; LOW = alarm)

Interrupt Input (flow pulse)
  Pin 2   INT0  ──── Digmesa FHKU pulse output   (attachInterrupt, RISING)
                     (only for pulse-output flow meter variant)

Analog Input (level gauge 4-20mA fallback)
  Pin A0  ADC   ──── 250Ω shunt → 4-20mA sensor (1V–5V on A0; analogRead)
                     (use ADS1115 I2C ADC for better accuracy)
```

### MEGA Physical Wiring Table

| MEGA Pin | Direction | External Connection                               | Notes                             |
|----------|-----------|---------------------------------------------------|-----------------------------------|
| 2        | DI (IRQ)  | Flow meter pulse output                           | 5V logic, RISING edge             |
| 9        | DO        | W5500 RESET                                       | Shield wired automatically        |
| 10       | DO        | W5500 CS                                          | Shield wired automatically        |
| 18 (TX1) | DO        | MAX485 DI                                         | RS485 transmit data               |
| 19 (RX1) | DI        | MAX485 RO                                         | RS485 receive data                |
| 20       | DO        | MAX485 DE + RE (tied together)                    | HIGH = TX, LOW = RX               |
| 22       | DO        | 2-ch relay module IN1 → solenoid valve            | Active-LOW typical                |
| 23       | DO        | 2-ch relay module IN2 → permit LED                | Active-LOW typical                |
| 24       | DI        | E-stop NC button → GND                            | INPUT_PULLUP; LOW = tripped        |
| 25       | DI        | Overpressure switch → GND                         | INPUT_PULLUP; LOW = alarm          |
| 26       | DI        | Low-tank alarm → GND                              | INPUT_PULLUP; LOW = alarm          |
| 50       | DI        | W5500 MISO                                        | Shield wired automatically        |
| 51       | DO        | W5500 MOSI                                        | Shield wired automatically        |
| 52       | DO        | W5500 SCK                                         | Shield wired automatically        |
| A0       | AI        | 250Ω shunt from 4-20mA sensor                     | 0–5V range (0–1023 raw)           |
| 5V       | PWR       | Relay module VCC (opto-side)                      |                                   |
| GND      | PWR       | Relay module GND, MAX485 GND, all sensor GNDs     |                                   |

### MEGA Network Configuration

| Parameter       | Value                     |
|-----------------|---------------------------|
| IP Address      | `192.168.1.51` (static)   |
| Subnet Mask     | `255.255.255.0`           |
| Modbus TCP Port | `502`                     |
| ODROID `.env`   | `MODBUS_HOST=192.168.1.51` |

---

## 13. Arduino MEGA + MAX485 — RS485 Module Wiring

**Part**: MAX485 TTL-to-RS485 Module (D3)

```
MAX485 Module        Arduino MEGA 2560       RS485 Bus
─────────────        ─────────────────       ─────────
VCC  ──────────────── 5V                     
GND  ──────────────── GND                    
DI   ──────────────── Pin 18 (TX1)           
RO   ──────────────── Pin 19 (RX1)           
DE   ──────┐
RE   ──────┴───────── Pin 20 (direction)     
A    ────────────────────────────────────── A (+)  → sensors
B    ────────────────────────────────────── B (−)  → sensors
```

> DE and RE on the MAX485 module are tied together to a single MEGA pin (20).
> Set HIGH before transmitting UART data; set LOW to receive.

**Bus termination at MEGA/MAX485 end**: Solder a 120Ω resistor between A and B terminals of the MAX485 module (or the screw terminal of the RS485 breakout).

---

## 14. Camera USB Connections

**Parts**: 2× Logitech C920 HD Pro (C1)

```
ODROID-M2 USB 3.0 port-A  ──── USB-A to USB-A cable ──── Logitech C920 #1
                                                           /dev/video0
                                                           Use: Nozzle tamper

ODROID-M2 USB 3.0 port-C  ──── USB-A to USB-C cable ──── Logitech C920 #2
(or via USB hub CH1)                                       /dev/video1
                                                           Use: Licence plate
```

**Mounting positions**:
| Camera | Mount Location          | Height    | FOV        |
|--------|-------------------------|-----------|------------|
| #1     | Adjacent to nozzle      | 0.3–0.5 m | Close, downward angle onto nozzle |
| #2     | Station front face      | 1.0–1.2 m | Aimed at vehicle front plate      |

**USB port allocation on ODROID-M2**:
| USB Port       | Type         | Allocated to            |
|----------------|--------------|-------------------------|
| USB-A (rear 1) | USB 3.0      | Camera #1 (nozzle)      |
| USB-C (rear)   | USB 3.0      | Camera #2 (plate)       |
| USB-A (rear 2) | USB 2.0      | USB-to-RS485 dongle     |

> With Ethernet used for internet uplink, all 3 USB ports are free for cameras + RS485.
> If WiFi dongle is needed, use a small powered USB hub (C2) on the USB-A 2.0 port.

---

## 15. LAN / Ethernet Connections

```
4G Modem / Site Router (192.168.1.1)
         │
    [Network Switch — unmanaged 5-port GbE]
         │
         ├──[CAT6]──── ODROID-M2          192.168.1.10  (static, set in /etc/netplan)
         ├──[CAT6]──── Siemens LOGO! 8.3  192.168.1.50  (static, set in LOGO! Soft Comfort)
         └──[CAT6]──── Arduino MEGA/W5500 192.168.1.51  (static, set in Arduino sketch)
                       (fallback — disconnect when LOGO! arrives)
```

**ODROID-M2 static IP (Ubuntu netplan)**:
```yaml
# /etc/netplan/01-eth.yaml
network:
  version: 2
  ethernets:
    eth0:
      dhcp4: false
      addresses: [192.168.1.10/24]
      gateway4: 192.168.1.1
      nameservers:
        addresses: [8.8.8.8, 8.8.4.4]
```

---

## 16. DIN-Rail Enclosure Layout

```
┌──────────────────────────────────────────────────────────────────────┐
│                    DIN-RAIL ENCLOSURE (IP54)                         │
│  LEFT SIDE                          RIGHT SIDE                       │
│                                                                      │
│  [MCB 10A] [MCB 6A]                                                  │
│   AC mains  24V PSU                                                  │
│                                                                      │
│  ─────────────────────────── DIN RAIL 1 ────────────────────────    │
│  [24V/5A DIN PSU (E5)]   [LOGO! 8.3 PLC (E1)]                     │
│                                                                      │
│  ─────────────────────────── DIN RAIL 2 ────────────────────────    │
│  [ODROID-M2 + case]    [12V Mini UPS (C5)]                          │
│  [mounted with M3 standoffs on aluminium plate]                     │
│                                                                      │
│  ─────────────────────────── DIN RAIL 3 ────────────────────────    │
│  [Arduino MEGA + W5500]  [4-ch relay module (C4)]                  │
│  [2-ch relay module (D4)]  [MAX485 module (D3)]                     │
│                                                                      │
│  ─────────────────────────── TERMINAL RAIL ─────────────────────    │
│  [Ground bus bar]  [24V + bus bar]  [0V − bus bar]                  │
│                                                                      │
│  CABLE ENTRY (bottom glands)                                         │
│  [RS485 cable]  [USB-to-panel glands]  [24V to solenoid]           │
│  [CAT6 to switch]  [OLED ribbon]  [Camera USB exits at top]         │
└──────────────────────────────────────────────────────────────────────┘
```

**Enclosure size recommendation**: 300 × 400 × 150 mm (W×H×D) with 3× 35mm DIN rails.

---

## 17. Wiring Safety Notes & Checklist

### Critical Safety Items

- [ ] **Flyback diode on solenoid** — 1N4007 across coil terminals, cathode to positive. Without this, relay contacts will arc and fail prematurely.
- [ ] **E-stop wired NC** — circuit must open (not close) when button is pressed. Loss of wire = safe (valve closes).
- [ ] **All safety DIs on LOGO! wired to 24V sourcing** — sensor dry contacts connect DI terminal to L+ (24V). Open contact = logic LOW = alarm condition.
- [ ] **Star ground** — all PSU negatives (24V−, 12V−), relay GND, and ODROID GPIO GND must connect to a single ground bus bar. Do not daisy-chain grounds between boards.
- [ ] **Opto-isolation verified** — opto-relay module must fully isolate ODROID (3.3V) from 24V load side. Never connect load-side ground to ODROID GND.
- [ ] **RS485 termination resistors** at BOTH cable ends — 120Ω across A/B at USB-RS485 dongle end AND at the farthest sensor.
- [ ] **MCB (miniature circuit breaker)** — install a 6A MCB on the primary 24V PSU input and a 10A MCB on mains supply before all DIN-rail equipment.
- [ ] **Cable ratings** — 24V solenoid circuit must use ≥0.75 mm² cable. RS485 twisted pair minimum. GPIO wires 26 AWG acceptable.

### GPIO Voltage Rules

| Connection                   | Allowed voltage     | Action if violated          |
|------------------------------|---------------------|-----------------------------|
| ODROID-M2 GPIO input/output  | 0–3.3V only         | Board damaged immediately   |
| ODROID 3.3V supply (Pin 1)   | Max 50 mA total     | Use separate 3.3V reg if more needed |
| Arduino MEGA GPIO            | 0–5V                | 5V tolerant, but not 24V    |
| LOGO! 8.3 DI terminals       | 12–24V DC           | Do not apply ≥30V           |

### Pre-Power Checklist

- [ ] Multimeter continuity check: 24V+ bus bar NOT shorted to GND bar
- [ ] Multimeter continuity check: ODROID 3.3V (GPIO header Pin 1) NOT connected to 5V or 24V
- [ ] Opto-relay load side wires labelled and double-checked before connecting 24V
- [ ] RS485 A/B polarity consistent throughout cable run (A matches A, B matches B at all connectors)
- [ ] W5500 shield seated fully on MEGA headers (all 54 pins engaged)
- [ ] ODROID-M2 powered via 12V DC jack only — do not power via USB-C simultaneously
- [ ] Solenoid valve 1N4007 diode installed and correct polarity (cathode stripe toward 24V+)
- [ ] All enclosure cable entry glands tightened (IP54 rating depends on it)
