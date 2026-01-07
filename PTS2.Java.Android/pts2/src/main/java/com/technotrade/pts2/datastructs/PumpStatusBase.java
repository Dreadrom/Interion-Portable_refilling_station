package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.enumeration.PumpStatus;

/// <summary>
/// Pump status base class data
/// </summary>
public class PumpStatusBase {
    private int mPump;
    private String mUser;
    protected PumpStatus mStatus;

    /// <summary>
    /// Pump number getter
    /// </summary>
    public int getPump() { return mPump; }
    /// <summary>
    /// Pump number setter
    /// </summary>
    public void setPump(int pump) { mPump = pump; }
    /// <summary>
    /// User getter
    /// </summary>
    public String getUser() { return mUser; }
    /// <summary>
    /// User setter
    /// </summary>
    public void setUser(String user) { mUser = user; }
    /// <summary>
    /// Status getter
    /// </summary>
    public PumpStatus getStatus() { return mStatus; };
    /// <summary>
    /// Status setter
    /// </summary>
    public void setStatus(PumpStatus status) { mStatus = status; };
}