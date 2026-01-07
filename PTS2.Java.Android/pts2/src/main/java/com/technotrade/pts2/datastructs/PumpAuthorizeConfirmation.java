package com.technotrade.pts2.datastructs;

/// <summary>
/// Pump authorize authorize confirmation data.
/// The class data used for PumpAuthorize request
/// </summary>
public class PumpAuthorizeConfirmation {
    private int mPump;
    private int mTransaction;

    public PumpAuthorizeConfirmation() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static PumpAuthorizeConfirmation create() {
        return new PumpAuthorizeConfirmation();
    }
    /// <summary>
    /// Pump number getter and setter
    /// </summary>
    public int getPump() { return mPump; }
    public void setPump(int pump) { mPump = pump; }
    /// <summary>
    /// Transaction getter and setter
    /// </summary>
    public int getTransaction() { return mTransaction; }
    public void setTransaction(int transaction) { mTransaction = transaction; }
}