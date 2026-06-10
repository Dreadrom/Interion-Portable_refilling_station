# Power Distribution Diagram

This diagram focuses on how the 24V and 12V rails are generated from the mains supply and distributed to the various loads. It also highlights the critical "star ground" bus point.

```mermaid
graph TD
    subgraph "Power Rails"
        AC_Mains["230V AC Mains"]
        PSU_24V["DIN-Rail 24V/5A PSU"]
        PSU_12V["12V/2A PSU"]
        UPS["12V Mini UPS"]
        Bus_24V["24V DC Bus"]
        Bus_12V["12V DC Bus"]
        Bus_GND["GND Bus (Star Point)"]
    end

    subgraph "24V Loads"
        PLC["Siemens LOGO! 8.3"]
        Solenoid["Solenoid Valve"]
        Relay_VCC["Relay Module VCC"]
    end

    subgraph "12V Loads"
        M2["ODROID-M2"]
    end

    AC_Mains --> PSU_24V
    AC_Mains --> PSU_12V

    PSU_24V -- "+V" --> Bus_24V
    PSU_24V -- "-V" --> Bus_GND

    PSU_12V --> UPS --> Bus_12V
    PSU_12V -- "GND" --> Bus_GND
    UPS -- "GND" --> Bus_GND

    Bus_24V --> PLC
    Bus_24V --> Solenoid
    Bus_24V --> Relay_VCC

    Bus_12V --> M2

    Bus_GND -- " " --> PLC
    Bus_GND -- " " --> Solenoid
    Bus_GND -- " " --> Relay_VCC
    Bus_GND -- " " --> M2

    style Bus_24V fill:#f66,stroke:#333,stroke-width:2px
    style Bus_12V fill:#66f,stroke:#333,stroke-width:2px
    style Bus_GND fill:#999,stroke:#333,stroke-width:2px
```
