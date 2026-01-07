package com.technotrade.pts2.enumeration;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// Type of protocol
/// </summary>
public enum ProtocolSecurityType implements Serializable {
	NONE(""),
	HTTP("HTTP"),
	HTTPS("HTTPS");

	private final String mValue;
	private static final Map<String, ProtocolSecurityType> lookup = new HashMap<>();

	ProtocolSecurityType(String value) {
		this.mValue = value;
	}

	static {
		for (ProtocolSecurityType w : EnumSet.allOf(ProtocolSecurityType.class))
			lookup.put(w.getValue(), w);
	}

	public String getValue() { return mValue; }

	public static ProtocolSecurityType get(String value) {
		return lookup.get(value);
	}
};