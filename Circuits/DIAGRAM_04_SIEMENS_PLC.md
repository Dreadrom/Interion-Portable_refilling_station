# Siemens LOGO! 8.3 PLC Wiring Diagram

This diagram details the connections to the Siemens LOGO! PLC, including its 24V power, the safety-critical digital inputs (like the E-Stop), and the high-power relay output controlling the solenoid valve.

```mermaid
graph TD
    subgraph "Siemens LOGO! 8.3 PLC"
        PLC["LOGO! 8.3"]
    end

    subgraph "Power Input"
        L_plus["L+ (24V)"]
        M["M (0V/GND)"]
    end

    subgraph "Digital Inputs (24V Sourcing)"
        I1["I1"]
        I2["I2"]
        I3["I3"]
    end

    subgraph "Digital Outputs (Relay)"
        Q1_COM["Q1 COM"]
        Q1_NO["Q1 NO"]
    end
    
    subgraph "External Components"
        PSU_24V["24V DC PSU (+)"]
        PSU_GND["24V DC PSU (-)"]
        EStop["E-Stop (NC)"]
        Alarm["Alarm Contact (NC)"]
        Solenoid["Solenoid Valve"]
    end

    PSU_24V --> L_plus
    PSU_GND --> M

    PSU_24V --> EStop -- "Contact" --> I1
    PSU_24V --> Alarm -- "Contact" --> I2
    PSU_24V --> I3

    PSU_24V --> Q1_COM
    Q1_NO --> Solenoid
    Solenoid -- "(-)" --> PSU_GND
    
    PLC -- "Power" --> L_plus & M
    PLC -- "Inputs" --> I1 & I2 & I3
    PLC -- "Outputs" --> Q1_COM & Q1_NO

    style PLC fill:#9cf,stroke:#333,stroke-width:2px
    style PSU_24V fill:#f66,stroke:#333,stroke-width:1px
    style PSU_GND fill:#999,stroke:#333,stroke-width:1px
```
