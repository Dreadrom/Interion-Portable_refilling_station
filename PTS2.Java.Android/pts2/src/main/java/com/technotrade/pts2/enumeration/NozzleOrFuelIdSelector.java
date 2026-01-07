package com.technotrade.pts2.enumeration;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// Enum that specified the nozzle(s) to authorize using PumpAuthorize request
/// </summary>
public enum NozzleOrFuelIdSelector implements Serializable {
	NONE(0),
	NOZZLE(1),
	NOZZLES(2),
	FUELGRADEID(3),
	FUELGRADEIDS(4);
	
    private final int mCode;
    private static final Map<Integer, NozzleOrFuelIdSelector> lookup
            = new HashMap<Integer,NozzleOrFuelIdSelector>();

    NozzleOrFuelIdSelector(int code) {
        this.mCode = code;
    }

    static {
        for (NozzleOrFuelIdSelector w : EnumSet.allOf(NozzleOrFuelIdSelector.class))
            lookup.put(w.getCode(), w);
    }

    public int getCode() { return mCode; }

    public static NozzleOrFuelIdSelector get(int code) {
        return lookup.get(code);
    }
};