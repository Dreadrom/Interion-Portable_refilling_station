# Wiring Safety & Interlock Diagram

This conceptual diagram illustrates the layers of control and safety. It shows how both the ODROID-M2 (software) and the Siemens PLC (hardware) interact with the E-Stop and solenoid valve, highlighting the PLC's role as a hardware interlock and the critical placement of the flyback diode.

```mermaid
graph TD
    subgraph "Control & Safety"
        ODROID["ODROID-M2"]
        PLC["Siemens LOGO! 8.3"]
        Relay["4-Ch Opto-Relay"]
        Solenoid["24V Solenoid Valve"]
        EStop["E-Stop Button (NC)"]
    end

    subgraph "Power"
        PSU_24V["24V DC PSU"]
    end

    ODROID -- "GPIO17 (Valve Cmd)" --> Relay
    ODROID -- "GPIO22 (E-Stop Read)" --> EStop

    PSU_24V -- "24V" --> PLC
    PSU_24V -- "24V" --> Relay_Load[Relay Load Side]
    Relay_Load -- "Switched 24V" --> Solenoid

    PLC -- "Modbus TCP" --> ODROID
    PLC -- "DI I1 (Hardwired)" --> EStop
    PLC -- "DO Q1 (Hardwired)" --> Solenoid

    Solenoid -- "Back-EMF" --> Diode["Flyback Diode (1N4007)"]
    style Diode fill:red,stroke:red

    %% Safety Checks
    ODROID -- "Software Check" .-> EStop
    PLC -- "Hardware Interlock" .-> EStop
    PLC -- "Hardware Interlock" .-> Solenoid

    linkStyle 0 stroke-width:2px,stroke:green,fill:none;
    linkStyle 1 stroke-width:1px,stroke:blue,fill:none;
    linkStyle 2 stroke-width:2px,stroke:red,fill:none;
    linkStyle 3 stroke-width:2px,stroke:red,fill:none;
    linkStyle 4 stroke-width:2px,stroke:green,fill:none;
    linkStyle 5 stroke-width:2px,stroke:orange,fill:none;
    linkStyle 6 stroke-width:2px,stroke:red,fill:none;
    linkStyle 7 stroke-width:2px,stroke:red,fill:none;
    linkStyle 8 stroke-width:1px,stroke:gray,stroke-dasharray: 5 5;
    linkStyle 9 stroke-width:1px,stroke:gray,stroke-dasharray: 5 5;
    linkStyle 10 stroke-width:1px,stroke:gray,stroke-dasharray: 5 5;
```
