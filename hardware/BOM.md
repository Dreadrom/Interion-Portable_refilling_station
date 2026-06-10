# Bill of Materials — Portable Refilling Station
> Rev 1.0 — April 10, 2026 | SBC: ODROID-M2 (8 GB) | Estimated per-station cost: S$560–650 + RM780–1,050

---

## ⚠️ Compatibility Notes

| Issue | Detail |
|---|---|
| 4-Ch Relay board for M1S | **M1S only** — GPIO 40-pin layout differs on M2. Use standalone relay modules (see Section C). |
| UPS Kit for M1S | **5V output only** — M2 requires 12V DC input. Use a 12V mini UPS instead (see Section C). |
| RTC Backup Battery (Hardkernel) | CR2032 with Molex 51021-0200 connector. M2 has an on-board CR2032 holder — buy a bare **CR2032 coin cell** locally instead of the cable version. |

---

## A — Single-Board Computer (from [hardkernel.com](https://www.hardkernel.com))
> Hardkernel ships to Malaysia. Prices in SGD.

| # | Item | Qty | Unit Price | Link | Notes |
|---|---|---|---|---|---|
| A1 | ODROID-M2 with 8 GB RAM | 1 | S$260 | [hardkernel.com](https://www.hardkernel.com/shop/odroid-m2-with-8gbyte-ram/) | RK3588S2, 6 TOPS NPU, 64 GB eMMC, case & fan included |
| A2 | 12V/2A Power Supply — UK/MY plug | 1 | S$8 | [hardkernel.com](https://www.hardkernel.com/shop/12v-2a-power-supply-uk-plug/) | Use this plug type for Malaysia |
| A3 | WiFi Module 5BK (USB dongle) | 1 | S$13 | [hardkernel.com](https://www.hardkernel.com/shop/wifi-module-5bk/) | RTL8821CU, dual-band AC + BT 4.2 |
| A4 | CAN-FD Add-on Board | 1 | S$16 | [hardkernel.com](https://www.hardkernel.com/shop/can-fd-add-on-board/) | ✅ M2 compatible — GPIO SPI bus |
| A5 | M2 Cube Case | 1 | S$41 | [hardkernel.com](https://www.hardkernel.com/shop/m2-cube-case/) | Optional if not mounting inside panel |
| A6 | Vu8S 8" MIPI LCD (touchscreen) | 1 | S$57 | [hardkernel.com](https://www.hardkernel.com/shop/vu8s-8inch-mipi-lcd-for-m1s/) | ⚠️ Optional — HMI display |

**Section A Subtotal: S$338–395**

---

## B — Storage (from [shop.sandisk.com](https://shop.sandisk.com))

| # | Item | Qty | Unit Price | Link | Notes |
|---|---|---|---|---|---|
| B1 | WD Blue SN580 500 GB NVMe M.2 2280 | 1 | ~RM130 | [shop.sandisk.com](https://shop.sandisk.com/en-my/products/ssd/internal-ssd/wd-blue-sn580-nvme-ssd#WDS500G3B0E) | PCIe 2.1 ×1 slot (M2 board has no PCIe 3.0) — SN580 comfortably fits |

**Section B Subtotal: ~RM130**

---

## C — USB Peripherals & Connectivity (Lazada Malaysia / AliExpress)

| # | Item | Qty | Unit Price (est.) | Purchase Link | Notes |
|---|---|---|---|---|---|
| C1 | Logitech C920 HD Pro USB Webcam | 2 | ~RM299 each | [Lazada search](https://www.lazada.com.my/catalog/?q=logitech+c920+webcam) | 1080p/30fps; use C920s for privacy shutter |
| C2 | Powered USB 3.0 Hub — 7 port, 5V/3A PSU | 1 | ~RM45 | [Lazada search](https://www.lazada.com.my/catalog/?q=powered+usb+3.0+hub+7+port) | Must be powered (self-powered) to drive 2 USB cameras |
| C3 | USB-to-RS485 Converter (CH340 or CP2102) | 1 | ~RM12 | [AliExpress search](https://www.aliexpress.com/wholesale?SearchText=usb+to+rs485+converter+ch340) | For level gauge + flow meter on shared Modbus RTU bus |
| C4 | 4-Channel Opto-isolated Relay Module (5V coil) | 1 | ~RM12 | [AliExpress search](https://www.aliexpress.com/wholesale?SearchText=4+channel+opto+isolated+relay+module+5v) | Replaces M1S relay board. GPIO 3.3V triggers opto-isolator |
| C5 | 12V Mini UPS Module (for router / 12V SBC) | 1 | ~RM65 | [Lazada search](https://www.lazada.com.my/catalog/?q=mini+ups+12v+router+uninterruptible) | Replaces M1S UPS kit. Must output 12V DC at ≥2A |
| C6 | SSD1306 OLED 0.96" 128×64 I2C | 1 | ~RM6 | [AliExpress search](https://www.aliexpress.com/wholesale?SearchText=ssd1306+oled+128x64+i2c+0.96) | Status display on GPIO I2C bus |
| C7 | CR2032 Coin Cell Battery (bare) | 1 | ~RM3 | Any 7-Eleven / Guardian / Ace Hardware | For M2 on-board RTC holder — do NOT buy the Hardkernel cable version |

**Section C Subtotal: ~RM741** (including 2× cameras)  
**Section C Subtotal (excluding cameras): ~RM143**

---

## D — MCU PLC Fallback (AliExpress / Lazada)

> Arduino MEGA 2560 + W5500 acts as drop-in Siemens LOGO! 8.3 replacement via Modbus TCP until the PLC arrives.

| # | Item | Qty | Unit Price (est.) | Purchase Link | Notes |
|---|---|---|---|---|---|
| D1 | Arduino MEGA 2560 Rev3 (clone) | 1 | ~RM28 | [Lazada search](https://www.lazada.com.my/catalog/?q=arduino+mega+2560) | CH340G USB-serial; verified with python-modbus |
| D2 | W5500 Ethernet Shield | 1 | ~RM30 | [AliExpress search](https://www.aliexpress.com/wholesale?SearchText=w5500+ethernet+shield+arduino) | SPI-based, exposes Modbus TCP server |
| D3 | MAX485 TTL-to-RS485 Module | 1 | ~RM5 | [AliExpress search](https://www.aliexpress.com/wholesale?SearchText=max485+rs485+module+ttl) | UART1 on MEGA → RS485 sensors |
| D4 | 2-Channel Opto-isolated Relay Module (5V coil) | 1 | ~RM8 | [AliExpress search](https://www.aliexpress.com/wholesale?SearchText=2+channel+opto+relay+module+5v) | Solenoid valve + pump pilot control during MCU fallback |

**Section D Subtotal: ~RM71**

---

## E — Industrial Components (Authorized Distributors)

> These are B2B / distributor items. Contact sales directly for pricing and lead times.

| # | Item | Qty | Est. Price | Distributor / Link | Notes |
|---|---|---|---|---|---|
| E1 | Siemens LOGO! 8.3 — 6ED1052-1MD08-0BA1 | 1 | ~RM1,800 | [RS Components MY](https://my.rs-online.com/web/c/plcs-hmi/programmable-logic-controllers-plcs/?searchTerm=siemens+logo+8.3) | 12/24V RCE, 8 DI + 4 DO relay, Modbus TCP |
| E2 | VEGA PULS 10 Radar Level Gauge (RS485) | 1 | ~RM3,500–5,000 | [vega.com](https://www.vega.com/en/products/product-catalog/level/radar/vegapuls-10.html) | Non-contact, 80 GHz; Modbus RTU address selectable |
| E3 | Digmesa FHS Flow Meter (RS485) | 1 | ~RM450–700 | [digmesa.com](https://www.digmesa.com/en/products/flow-sensors/) | Food-safe; shared RS485 bus with level gauge |
| E4 | Solenoid Valve 24V DC, 1" BSP (NC) | 1 | ~RM120–200 | [RS Components MY](https://my.rs-online.com/web/c/valves-actuators/solenoid-valves/?searchTerm=solenoid+valve+24v+1+inch) | Normally-closed; 2/2 way; stainless food grade |
| E5 | DIN-rail 24V DC Power Supply, 5A | 1 | ~RM180–280 | [RS Components MY](https://my.rs-online.com/web/c/power-supplies-transformers/din-rail-power-supplies/?searchTerm=24v+5a+din+rail+power+supply) | Powers LOGO!, relay coils, solenoid valve |

**Section E Subtotal: ~RM6,050–7,180** *(majority is VEGA level gauge)*

---

## F — Cables & Miscellaneous

| # | Item | Qty | Est. Price | Where to Buy | Notes |
|---|---|---|---|---|---|
| F1 | CAT6 Ethernet Cable 2m | 1 | ~RM8 | Any hardware store | M2 to network switch |
| F2 | Twisted-pair RS485 cable (Belden 9841 or equiv.) | 3m | ~RM15 | [RS Components MY](https://my.rs-online.com/web/c/cables-wires/data-industrial-cables/rs-485-cables/) | Level gauge + flow meter to USB-RS485 |
| F3 | USB-A to USB-A cable 1m (×2) | 2 | ~RM6 each | Lazada | USB hub to cameras |
| F4 | Jumper wires (male-female, 20cm, 40-pack) | 1 | ~RM5 | [AliExpress search](https://www.aliexpress.com/wholesale?SearchText=jumper+wire+male+female+40pcs+20cm) | GPIO wiring to relay modules |
| F5 | M3 PCB standoffs & screws assorted kit | 1 | ~RM10 | [AliExpress search](https://www.aliexpress.com/wholesale?SearchText=m3+pcb+standoff+nylon+assorted+kit) | Mounting MCU fallback board in enclosure |
| F6 | DIN-rail enclosure box (suitable for M2 + MCU) | 1 | ~RM80–150 | [Lazada search](https://www.lazada.com.my/catalog/?q=din+rail+electrical+enclosure+box) | IP54 rated preferred for kiosk installation |

**Section F Subtotal: ~RM130–200**

---

## Cost Summary (Per Station)

| Section | Items | Estimated Cost |
|---|---|---|
| A — ODROID-M2 + Hardkernel Add-ons | Board, PSU, WiFi, CAN-FD, Case | S$338 (~RM1,149) |
| B — Storage | NVMe SSD | ~RM130 |
| C — Peripherals | 2× cameras, USB hub, RS485, relay, UPS, OLED | ~RM741 |
| C (excl. cameras, ~RM598) | USB hub, RS485, relay, UPS, OLED | ~RM143 |
| D — MCU PLC Fallback | Arduino MEGA, W5500, RS485, relay | ~RM71 |
| E — Industrial (excl. VEGA) | LOGO! 8.3, flow meter, solenoid, 24V PSU | ~RM2,550–3,180 |
| E — VEGA PULS 10 Level Gauge | Radar level sensor | ~RM3,500–5,000 |
| F — Cables & Enclosure | Misc | ~RM130–200 |
| **TOTAL (with VEGA + cameras)** | | **~RM9,271–11,471** |
| **TOTAL (cameras only, no VEGA)** | *(source alternative level sensor)* | **~RM3,871–5,471** |

---

## Priority Buy Order

```
Phase 1 — Board bring-up (order now)
  1. A1  ODROID-M2 8GB
  2. A2  12V/2A PSU UK/MY plug
  3. A3  WiFi Module 5BK
  4. B1  WD SN580 500GB NVMe SSD
  5. F7  CR2032 coin cell (local)

Phase 2 — I/O + connectivity
  6. A4  CAN-FD add-on board
  7. C2  Powered USB 3.0 hub 7-port
  8. C3  USB-to-RS485 converter
  9. C5  12V mini UPS

Phase 3 — Cameras + display
  10. C1  2× Logitech C920 USB webcam
  11. A6  Vu8S 8" LCD (if using MIPI display)
  12. C6  SSD1306 OLED

Phase 4 — MCU fallback (while waiting for LOGO! PLC)
  13. D1  Arduino MEGA 2560
  14. D2  W5500 Ethernet Shield
  15. D3  MAX485 RS485 module
  16. D4  2-channel relay module
  17. C4  4-channel relay module

Phase 5 — Industrial (long lead-time, order in parallel with Phase 2)
  18. E1  Siemens LOGO! 8.3
  19. E2  VEGA PULS 10 level gauge
  20. E3  Digmesa FHS flow meter
  21. E4  Solenoid valve 24V DC
  22. E5  DIN-rail 24V PSU
  23. F6  DIN-rail enclosure
```

---

## Hardkernel Order Checklist

Order these all together on one Hardkernel shipment to minimise shipping cost:

- [ ] ODROID-M2 with 8 GB RAM × 1
- [ ] 12V/2A Power Supply UK plug × 1
- [ ] WiFi Module 5BK × 1
- [ ] CAN-FD Add-on Board × 1
- [ ] M2 Cube Case × 1 *(optional)*
- [ ] Vu8S 8" MIPI LCD × 1 *(optional)*

---

*RS Components Malaysia: https://my.rs-online.com*  
*Element14 Malaysia: https://my.element14.com*  
*VEGA Malaysia office: https://www.vega.com/en/service-hotline-and-offices/malaysia*
