package com.technotrade.pts2.datastructs;

import static com.technotrade.pts2.enumeration.PumpStatus.FILLING;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.util.Flags;

import java.util.Date;

/// <summary>
/// Pump filling status data
/// </summary>
public class PumpFillingStatus extends PumpStatusBase {
    ///Flags to define which value is set
    private enum Flag {
        Tag,
        FuelGradeId,
        FuelGradeName,
        DateTimeStart
    };

    private int mNozzle;
    private double mVolume;
    private double mTCVolume;
    private double mPrice;
    private double mAmount;
    private int mTransaction;
    private String mTag;
    private int mFuelGradeId;
    private String mFuelGradeName;
    private Date mDateTimeStart;

    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    /// <summary>
    /// Status getter
    /// </summary>
    @Override
    public PumpStatus getStatus() {
        mStatus = FILLING;
        return mStatus;
    };
    /// <summary>
    /// Nozzle getter
    /// </summary>
    public int getNozzle() { return mNozzle; }
    /// <summary>
    /// Nozzle setter
    /// </summary>
    public void setNozzle(int nozzle) { mNozzle = nozzle; }
    /// <summary>
    /// Volume getter
    /// </summary>
    public double getVolume() { return mVolume; }
    /// <summary>
    /// Volume setter
    /// </summary>
    public void setVolume(double volume) { mVolume = volume; }
    /// <summary>
    /// TCVolume getter
    /// </summary>
    public double getTCVolume()  { return mTCVolume; }
    /// <summary>
    /// TCVolume setter
    /// </summary>
    public void setTCVolume(double tCVolume)  { mTCVolume = tCVolume; }
    /// <summary>
    /// Price getter
    /// </summary>
    public double getPrice() { return mPrice; }
    /// <summary>
    /// Price setter
    /// </summary>
    public void setPrice(double price) { mPrice = price; }
    /// <summary>
    /// Amount getter
    /// </summary>
    public double getAmount() { return mAmount; }
    /// <summary>
    /// Amount setter
    /// </summary>
    public void setAmount(double amount) { mAmount = amount; }
    /// <summary>
    /// Transaction getter
    /// </summary>
    public int getTransaction() { return mTransaction; }
    /// <summary>
    /// Transaction setter
    /// </summary>
    public void setTransaction(int transaction) { mTransaction = transaction; }
    /// <summary>
    /// Tag getter
    /// </summary>
    public String getTag() { return mTag; }
    /// <summary>
    /// Tag setter
    /// </summary>
    public void setTag(String tag) { mTag = tag; mFlags.setFlag(Flag.Tag); }
    /// <summary>
    /// Checking that Tag is set
    /// </summary>
    public boolean isTagSet() { return mFlags.isFlagSet(Flag.Tag); }
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
    /// DateTimeStart getter
    /// </summary>
    public Date getDateTimeStart() { return mDateTimeStart; }
    /// <summary>
    /// DateTimeStart setter
    /// </summary>
    public void setDateTimeStart(Date dateTimeStart) { mDateTimeStart = dateTimeStart; mFlags.setFlag(Flag.DateTimeStart); }
    /// <summary>
    /// Checking that DateTimeStart is set
    /// </summary>
    public boolean isDateTimeStartSet() { return mFlags.isFlagSet(Flag.DateTimeStart); }
}