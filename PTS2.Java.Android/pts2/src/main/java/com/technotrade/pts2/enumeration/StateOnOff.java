package com.technotrade.pts2.enumeration;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// State On/Off with alias support like "true"/"false", etc.
///
/// StateOnOff s1 = StateOnOff.get("On");
/// StateOnOff s2 = StateOnOff.get("true");
/// StateOnOff s3 = StateOnOff.get("FALSE");
///
/// System.out.println(s1); // ON
/// System.out.println(s2); // ON
/// System.out.println(s3); // OFF
///
/// StateOnOff state = StateOnOff.ON;
/// String label = state.getValue(); // returns "On"
/// System.out.println(label);       // prints: On
///
/// </summary>
public enum StateOnOff implements Serializable {
	OFF("Off", false),
	ON("On", true);

	private final String mValue;
	private final boolean mBool;
	private static final Map<String, StateOnOff> lookup = new HashMap<>();

	StateOnOff(String value, boolean boolValue) {
		this.mValue = value;
		this.mBool = boolValue;
	}

	public String getValue() {
		return mValue;
	}

	public boolean asBoolean() {
		return mBool;
	}

	static {
		for (StateOnOff state : EnumSet.allOf(StateOnOff.class)) {
			lookup.put(state.getValue().toLowerCase(), state); // "on", "off"
			lookup.put(String.valueOf(state.mBool), state); // "true", "false"
		}
	}

	public static StateOnOff get(String value) {
		if (value == null) return null;
		return lookup.get(value.toLowerCase());
	}
}