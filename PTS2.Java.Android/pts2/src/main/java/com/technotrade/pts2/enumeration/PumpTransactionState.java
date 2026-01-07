package com.technotrade.pts2.enumeration;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// Pump transaction state
/// </summary>
public enum PumpTransactionState implements Serializable {
    NONE(""),
    WAITING_NOZZLE_UP("WaitingNozzleUpForAuthorization"),
    AUTHORIZED("Authorized"),
    FILLING("Filling"),
    END_OF_TRANSACTION("EndOfTransaction"),
    FINISHED("Finished"),
    NOT_FOUND("Not found");
	
    private final String mValue;
    private static final Map<String, PumpTransactionState> lookup
            = new HashMap<String, PumpTransactionState>();

    PumpTransactionState(String value) {
        this.mValue = value;
    }

    static {
        for (PumpTransactionState w : EnumSet.allOf(PumpTransactionState.class))
            lookup.put(w.getValue(), w);
    }

    public String getValue() { return mValue; }

    public static PumpTransactionState get(String value) {
        return lookup.get(value);
    }
};