# ODROID-M2 GPIO Wiring Diagram

This diagram shows the specific connections from the ODROID-M2's 40-pin GPIO header to the low-voltage peripherals like the OLED display, relay module logic side, and E-Stop button.

```mermaid
graph TD
    subgraph "ODROID-M2"
        M2_Board["ODROID-M2"]
    end

    subgraph "Peripherals"
        OLED["SSD1306 OLED"]
        Relay["Opto-Relay Module"]
        EStop["E-Stop Button (NC)"]
    end

    subgraph "Connections"
        Pin1["Pin 1 (3.3V)"]
        Pin3["Pin 3 (I2C1_SDA)"]
        Pin5["Pin 5 (I2C1_SCL)"]
        Pin6["Pin 6 (GND)"]
        Pin11["Pin 11 (GPIO17)"]
        Pin15["Pin 15 (GPIO22)"]
    end

    M2_Board -- "40-Pin Header" --> Pin1
    M2_Board -- " " --> Pin3
    M2_Board -- " " --> Pin5
    M2_Board -- " " --> Pin6
    M2_Board -- " " --> Pin11
    M2_Board -- " " --> Pin15

    Pin1 --> OLED_VCC[OLED VCC]
    Pin6 --> OLED_GND[OLED GND]
    Pin3 --> OLED_SDA[OLED SDA]
    Pin5 --> OLED_SCL[OLED SCL]
    OLED_VCC & OLED_GND & OLED_SDA & OLED_SCL --> OLED

    Pin1 --> Relay_VCC[Relay Opto-VCC]
    Pin6 --> Relay_GND[Relay Opto-GND]
    Pin11 --> Relay_IN1[Relay IN1]
    Relay_VCC & Relay_GND & Relay_IN1 --> Relay

    Pin15 -- "Internal Pull-up" --> EStop_Pin[E-Stop Pin]
    EStop_Pin -- "NC Contact" --> Pin6
    EStop_Pin --> EStop
    
    style M2_Board fill:#f9f,stroke:#333,stroke-width:2px
    style Pin1 fill:#f96,stroke:#333,stroke-width:1px
    style Pin6 fill:#999,stroke:#333,stroke-width:1px
```
