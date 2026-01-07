package com.technotrade.pts2.enumeration;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// Enum with authorization types for PTS2 connection
/// </summary>
public enum AuthenticationType implements Serializable {
    NONE(""),
    BASIC("Basic"),
    DIGEST("Digest");

    private final String mValue;
    private static final Map<String, AuthenticationType> lookup = new HashMap<>();

    AuthenticationType(String value) {
        this.mValue = value;
    }

    static {
        for (AuthenticationType w : EnumSet.allOf(AuthenticationType.class))
            lookup.put(w.getValue(), w);
    }

    public String getValue() { return mValue; }

    public static AuthenticationType get(String value) {
        return lookup.get(value);
    }
};