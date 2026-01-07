package com.technotrade.pts2.pts2testapp.enumeration;

import androidx.annotation.NonNull;

public enum KeyboardType {
    ORDER_IN_FORMATION("ORDER_IN_FORMATION"),
    VOLUME("VOLUME"),
    AMOUNT("AMOUNT"),
    ORDER_CONFIRMATION("ORDER_CONFIRMATION"),
    ORDERED("ORDERED");

    private final String value;

    KeyboardType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static KeyboardType fromString(String text) {
        for (KeyboardType type : KeyboardType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No KeyboardType with value " + text);
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}