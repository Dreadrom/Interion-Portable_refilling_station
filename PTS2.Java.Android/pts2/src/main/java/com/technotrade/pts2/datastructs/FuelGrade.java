package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.util.Flags;

import java.math.BigDecimal;

/// <summary>
/// Fuel grade data
/// </summary>
public class FuelGrade {
    ///Flags to define which value is set
    private enum Flag {
        BlendTank1Id,
        BlendTank1Percentage,
        BlendTank2Id
    };

    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    private int mId;
    private String mName;
    private BigDecimal mPrice;
    private BigDecimal mExpansionCoefficient;
    private boolean mUseBlendTankParameters;
    private int mBlendTank1Id;
    private int mBlendTank1Percentage;
    private int mBlendTank2Id;

    public FuelGrade() {
        mUseBlendTankParameters = false;
    }
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static FuelGrade create() {
        return new FuelGrade();
    }
    /// <summary>
    /// Id getter and setter
    /// </summary>
    public int getId() { return mId; }
    public void setId(int id) { mId = id; }
    /// <summary>
    /// Name getter and setter
    /// </summary>
    public String getName() { return mName; }
    public void setName(String name) { mName = name; }
    /// <summary>
    /// Price getter and setter
    /// </summary>
    public BigDecimal getPrice() { return mPrice; }
    public void setPrice(BigDecimal price) { mPrice = price; }
    /// <summary>
    /// ExpansionCoefficient getter and setter
    /// </summary> 
    public BigDecimal getExpansionCoefficient() { return mExpansionCoefficient; }
    public void setExpansionCoefficient(BigDecimal expansionCoefficient) { mExpansionCoefficient = expansionCoefficient; }
    /// <summary>
    /// UseBlendTankParameters getter and setter - determines if BlendTank1Id, BlendTank1Percentage and BlendTank2Id will be used or not
    /// </summary>
    public boolean getUseBlendTankParameters() { return mUseBlendTankParameters; }
    public void setUseBlendTankParameters(boolean useBlendTankParameters) { mUseBlendTankParameters = useBlendTankParameters; }
    /// <summary>
    /// BlendTank1Id getter and setter
    /// </summary>
    public int getBlendTank1Id() { return mBlendTank1Id; }
    public void setBlendTank1Id(int blendTank1Id) { mBlendTank1Id = blendTank1Id; mFlags.setFlag(Flag.BlendTank1Id); }
    /// <summary>
    /// Checking that BlendTank1Id is set
    /// </summary>
    public boolean isBlendTank1IdSet() { return mFlags.isFlagSet(Flag.BlendTank1Id); }
    /// <summary>
    /// BlendTank1Percentage getter and setter
    /// </summary>
    public int getBlendTank1Percentage() { return mBlendTank1Percentage; }
    public void setBlendTank1Percentage(int blendTank1Percentage) { mBlendTank1Percentage = blendTank1Percentage; mFlags.setFlag(Flag.BlendTank1Percentage); }
    /// <summary>
    /// Checking that BlendTank1Percentage is set
    /// </summary>
    public boolean isBlendTank1PercentageSet() { return mFlags.isFlagSet(Flag.BlendTank1Percentage); }
    /// <summary>
    /// BlendTank12d getter and setter
    /// </summary>
    public int getBlendTank2Id() { return mBlendTank2Id; }
    public void setBlendTank2Id(int blendTank2Id) { mBlendTank2Id = blendTank2Id; mFlags.setFlag(Flag.BlendTank2Id); }
    /// <summary>
    /// Checking that BlendTank2Id is set
    /// </summary>
    public boolean isBlendTank2IdSet() { return mFlags.isFlagSet(Flag.BlendTank2Id); }
}