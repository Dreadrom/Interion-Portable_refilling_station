package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.util.Flags;

/// <summary>
/// Describes a complete in-tank delivery
/// </summary>
public class ReportInTankDelivery {
    ///Flags to define which value is set
    private enum Flag {
        FuelGradeId,
        FuelGradeName
    };

    private int mTank;
    private int mFuelGradeId;
    private String mFuelGradeName;
    private InTankDelivery mStartValues;
    private InTankDelivery mEndValues;
    private InTankDelivery mAbsoluteValues;

    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    public ReportInTankDelivery() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ReportInTankDelivery create() {
        return new ReportInTankDelivery();
    }
    /// <summary>
    /// Tank number getter and setter
    /// </summary>
    public int getTank() { return mTank; }
    public void setTank(int tank) { mTank = tank; }
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
    /// <summary>
    /// StartValues getter and setter
    /// </summary>
    public InTankDelivery getStartValues() { return mStartValues; }
    public void setStartValues(InTankDelivery startValues) { mStartValues = startValues; }
    /// <summary>
    /// EndValues getter and setter
    /// </summary>
    public InTankDelivery getEndValues() { return mEndValues; }
    public void setEndValues(InTankDelivery endValues) { mEndValues = endValues; }
    /// <summary>
    /// AbsoluteValues getter and setter
    /// </summary>
    public InTankDelivery getAbsoluteValues() { return mAbsoluteValues; }
    public void setAbsoluteValues(InTankDelivery absoluteValues) { mAbsoluteValues = absoluteValues; }
}