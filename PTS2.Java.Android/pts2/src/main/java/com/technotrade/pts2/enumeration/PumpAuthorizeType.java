package com.technotrade.pts2.enumeration;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// Types of pump authorization
/// </summary>
public enum PumpAuthorizeType implements Serializable {
	VOLUME("Volume"),
	AMOUNT("Amount"),
	FULLTANK("FullTank");

    private final String mValue;
    private static final Map<String, PumpAuthorizeType> lookup
            = new HashMap<String, PumpAuthorizeType>();

    PumpAuthorizeType(String value) {
        this.mValue = value;
    }

    static {
        for (PumpAuthorizeType w : EnumSet.allOf(PumpAuthorizeType.class))
            lookup.put(w.getValue(), w);
    }

    public String getValue() { return mValue; }

    public static PumpAuthorizeType get(String value) {
        return lookup.get(value);
    }
};