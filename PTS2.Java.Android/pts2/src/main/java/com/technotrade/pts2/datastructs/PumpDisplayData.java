package com.technotrade.pts2.datastructs;

import static com.technotrade.pts2.enumeration.PumpStatus.NONE;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.util.Flags;

/// <summary>
/// Pump display data
/// </summary>
public class PumpDisplayData extends PumpStatusBase {
    ///Flags to define which value is set
    private enum Flag {
        LastFuelGradeId,
        LastFuelGradeName,
    };

    private int mLastNozzle;
    private int mLastTransaction;
    private double mVolume;
    private double mAmount;
    private int mLastFuelGradeId;
    private String mLastFuelGradeName;

    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    /// <summary>
    /// Status getter
    /// </summary>
    @Override
    public PumpStatus getStatus() {
        mStatus = NONE;
        return mStatus;
    };
    /// <summary>
    /// LastNozzle getter
    /// </summary>
    public int getLastNozzle() { return mLastNozzle; }
    /// <summary>
    /// LastNozzle setter
    /// </summary>
    public void setLastNozzle(int lastNozzle) { mLastNozzle = lastNozzle; }
    /// <summary>
    /// LastTransaction getter
    /// </summary>
    public int getLastTransaction() { return mLastTransaction; }
    /// <summary>
    /// LastTransaction setter
    /// </summary>
    public void setLastTransaction(int lastTransaction) { mLastTransaction = lastTransaction; }
    /// <summary>
    /// Volume getter
    /// </summary>
    public double getVolume() { return mVolume; }
    /// <summary>
    /// Volume setter
    /// </summary>
    public void setVolume(double volume) { mVolume = volume; }
    /// <summary>
    /// Amount getter
    /// </summary>
    public double getAmount() { return mAmount; }
    /// <summary>
    /// Amount setter
    /// </summary>
    public void setAmount(double amount) { mAmount = amount; }
    /// <summary>
    /// LastFuelGradeId getter
    /// </summary>
    public int getLastFuelGradeId() { return mLastFuelGradeId; }
    /// <summary>
    /// LastFuelGradeId setter
    /// </summary>
    public void setLastFuelGradeId(int lastfuelGradeId) { mLastFuelGradeId = lastfuelGradeId; mFlags.setFlag(Flag.LastFuelGradeId); }
    /// <summary>
    /// Checking that LastFuelGradeId is set
    /// </summary>
    public boolean isLastFuelGradeIdSet() { return mFlags.isFlagSet(Flag.LastFuelGradeId); }
    /// <summary>
    /// LastFuelGradeName getter
    /// </summary>
    public String getLastFuelGradeName() { return mLastFuelGradeName; }
    /// <summary>
    /// LastFuelGradeName setter
    /// </summary>
    public void setLastFuelGradeName(String lastFuelGradeName) { mLastFuelGradeName = lastFuelGradeName; mFlags.setFlag(Flag.LastFuelGradeName); }
    /// <summary>
    /// Checking that LastFuelGradeName is set
    /// </summary>
    public boolean isLastFuelGradeNameSet() { return mFlags.isFlagSet(Flag.LastFuelGradeName); }
}