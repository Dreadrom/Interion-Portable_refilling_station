package com.technotrade.pts2.util;

import java.io.Serializable;
import java.util.HashSet;

/// <summary>
/// Flags class
/// </summary>
public class Flags<T> implements Serializable {
    ///Option values flag
    private HashSet<T> mFlags = new HashSet<>();
    /// <summary>
    /// Option values flag setter
    /// </summary>
    public void setFlag(T f) { mFlags.add(f); }
    /// <summary>
    /// Option values reset a flag
    /// </summary>
    public void resetFlag(T f) { mFlags.remove(f); }
    /// <summary>
    /// Option values flag checker
    /// </summary>
    public boolean isFlagSet(T f) { return mFlags.contains(f); }
}
