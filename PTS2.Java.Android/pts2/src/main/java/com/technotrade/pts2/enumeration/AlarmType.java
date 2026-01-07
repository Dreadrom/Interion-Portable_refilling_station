package com.technotrade.pts2.enumeration;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// Enum of possible probe alarm types
/// </summary>
public enum AlarmType implements Serializable {
    NONE("None"),
    CRITICAL_HIGH_PRODUCT("CriticalHighProduct"), //CriticalHighProduct: if product level is higher than maximum critical
    HIGH_PRODUCT("HighProduct"), //HighProduct: if product level is higher than maximum allowed
    LOW_PRODUCT("LowProduct"), //LowProduct: if product level is lower than minimum allowed
    CRITICAL_LOW_PRODUCT("CriticalLowProduct"), //CriticalLowProduct: if product level is lower than minimum critical
    HIGH_WATER("HighWater”"); //HighWater: in case if water level is higher than maximum allowed};

    private final String mValue;
    private static final Map<String, AlarmType> lookup
            = new HashMap<String, AlarmType>();

    AlarmType(String value) {
        this.mValue = value;
    }

    static {
        for (AlarmType w : EnumSet.allOf(AlarmType.class))
            lookup.put(w.getValue(), w);
    }

    public String getValue() { return mValue; }

    public static AlarmType get(String value) {
        return lookup.get(value);
    }
};