package com.technotrade.pts2.datastructs;

/// <summary>
/// Pump automatic operation data.
/// The class data used for PumpAuthorize request
/// </summary>
public class PumpAutomaticOperation {
    private int mPump;
    private String mState;

    public PumpAutomaticOperation() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static PumpAutomaticOperation create() {
        return new PumpAutomaticOperation();
    }
    /// <summary>
    /// Pump number getter and setter
    /// </summary>
    public int getPump() { return mPump; }
    public void setPump(int pump) { mPump = pump; }
    /// <summary>
    /// State getter and setter
    /// </summary>
    public String getState() { return mState; }
    public void setState(String state) { mState = state; }
}