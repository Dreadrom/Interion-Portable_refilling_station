package com.technotrade.pts2.datastructs;

import static com.technotrade.pts2.enumeration.PumpStatus.NONE;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.util.Flags;

/// <summary>
/// Pump tag data
/// </summary>
public class PumpTag extends PumpStatusBase {
    ///Flags to define which value is set
    private enum Flag {
        FuelGradeId,
        FuelGradeName,
    };

    private int mNozzle;
    private String mTag;
    private int mFuelGradeId;
    private String mFuelGradeName;

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
    /// Creates an instance
    /// </summary>
    public static PumpTag create() {
        return new PumpTag();
    }
    /// <summary>
    /// Nozzle number getter and setter
    /// </summary>
    public int getNozzle() { return mNozzle; }
    public void setNozzle(int nozzle) { mNozzle = nozzle; }
    /// <summary>
    /// Tag getter and setter
    /// </summary>
    public String getTag() { return mTag; }
    public void setTag(String tag) { mTag = tag; }
    /// <summary>
    /// FuelGradeId getter
    /// </summary>
    public int getFuelGradeId() { return mFuelGradeId; }
    /// <summary>
    /// FuelGradeId setter
    /// </summary>
    public void setFuelGradeId(int fuelGradeId) { mFuelGradeId = fuelGradeId; mFlags.setFlag(Flag.FuelGradeId); }
    /// <summary>
    /// Checking that FuelGradeId is set
    /// </summary>
    public boolean isFuelGradeIdSet() { return mFlags.isFlagSet(Flag.FuelGradeId); }
    /// <summary>
    /// FuelGradeName getter
    /// </summary>
    public String getFuelGradeName() { return mFuelGradeName; }
    /// <summary>
    /// FuelGradeName setter
    /// </summary>
    public void setFuelGradeName(String fuelGradeName) { mFuelGradeName = fuelGradeName; mFlags.setFlag(Flag.FuelGradeName); }
    /// <summary>
    /// Checking that FuelGradeName is set
    /// </summary>
    public boolean isFuelGradeNameSet() { return mFlags.isFlagSet(Flag.FuelGradeName); }
}