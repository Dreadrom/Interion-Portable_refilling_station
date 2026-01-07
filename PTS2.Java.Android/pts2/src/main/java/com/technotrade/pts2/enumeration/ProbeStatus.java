package com.technotrade.pts2.enumeration;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// Enum of possible probes statuses
/// </summary>
public enum ProbeStatus implements Serializable {
    NONE("None"),
    OK("OK"),
    ERROR( "Error"),
    OFFLINE("Offline");

    private final String mValue;
    private static final Map<String, ProbeStatus> lookup
            = new HashMap<String, ProbeStatus>();

    ProbeStatus(String value) {
        this.mValue = value;
    }

    static {
        for (ProbeStatus w : EnumSet.allOf(ProbeStatus.class))
            lookup.put(w.getValue(), w);
    }

    public String getValue() { return mValue; }

    public static ProbeStatus get(String value) {
        return lookup.get(value);
    }
};