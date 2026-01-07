package com.technotrade.pts2.enumeration;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// Pump status
/// </summary>
public enum PumpStatus implements Serializable {
    NONE(0),
	OFFLINE(1),
	IDLE(2),
	FILLING(3),
	NOZZLE(4);
	
    private final int mCode;
    private static final Map<Integer, PumpStatus> lookup
            = new HashMap<Integer,PumpStatus>();

    PumpStatus(int code) {
        this.mCode = code;
    }

    static {
        for (PumpStatus w : EnumSet.allOf(PumpStatus.class))
            lookup.put(w.getCode(), w);
    }

    public int getCode() { return mCode; }

    public static PumpStatus get(int code) {
        return lookup.get(code);
    }

    /// <summary>
    /// Description getter
    /// </summary>
    public static String getDescription(PumpStatus pumpStatus) {
        switch (pumpStatus) {
            case OFFLINE: return "OFFLINE";
            case IDLE: return "IDLE";
            case FILLING: return "FILLING";
            case NOZZLE: return "NOZZLE";
            case NONE:
            default: return "";
        }
    }
};