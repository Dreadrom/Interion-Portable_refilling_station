package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.enumeration.PumpTransactionState;
import com.technotrade.pts2.util.Flags;

import java.util.Date;

/// <summary>
/// Pump transaction information data.
/// </summary>
public class PumpTransactionInformation {
    private enum Flag {
        DateTimeStart,
        DateTime,
        Nozzle,
        FuelGradeId,
        FuelGradeName,
        Volume,
        Price,
        Amount,
        TCVolume,
        TotalVolume,
        TotalAmount,
        Tag,
        UserId
    };

    private int mPump;
    private int mTransaction;
    private PumpTransactionState mState;
    private Date mDateTimeStart;
    private Date mDateTime;
    int mNozzle;
    private int mFuelGradeId;
    private String mFuelGradeName;
    private double mVolume;
    private double mTCVolume;
    private double mPrice;
    private double mAmount;
    private double mTotalVolume;
    private double mTotalAmount;
    private String mTag;
    private int mUserId;

    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    public PumpTransactionInformation() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static PumpTransactionInformation create() {
        return new PumpTransactionInformation();
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
    /// <summary>
    /// State getter and setter
    /// </summary>
    public PumpTransactionState getState() { return mState; }
    public void setState(PumpTransactionState state) { mState = state; }
    /// <summary>
    /// DateTimeStart getter and setter
    /// </summary>
    public Date getDateTimeStart() { return mDateTimeStart; }
    public void setDateTimeStart(Date dateTimeStart) {
        mDateTimeStart = dateTimeStart;
        mFlags.setFlag(Flag.DateTimeStart);
    }
    /// <summary>
    /// Checking that DateTimeStart is set
    /// </summary>
    public boolean isDateTimeStartSet() { return mFlags.isFlagSet(Flag.DateTimeStart); }
    /// <summary>
    /// DateTime getter and setter
    /// </summary>
    public Date getDateTime() { return mDateTime; }
    public void setDateTime(Date dateTime) {
        mDateTime = dateTime;
        mFlags.setFlag(Flag.DateTime);
    }
    /// <summary>
    /// Checking that DateTime is set
    /// </summary>
    public boolean isDateTimeSet() { return mFlags.isFlagSet(Flag.DateTime); }
    /// <summary>
    /// Nozzle getter and setter
    /// </summary>
    public int getNozzle() { return mNozzle; }
    public void setNozzle(int nozzle) {
        mNozzle = nozzle;
        mFlags.setFlag(Flag.Nozzle);
    }
    /// <summary>
    /// Checking that Nozzle is set
    /// </summary>
    public boolean isNozzleSet() { return mFlags.isFlagSet(Flag.Nozzle); }
    /// <summary>
    /// FuelGradeId getter and setter
    /// </summary>
    public int getFuelGradeId() { return mFuelGradeId; }
    public void setFuelGradeId(int fuelGradeId) {
        mFuelGradeId = fuelGradeId;
        mFlags.setFlag(Flag.FuelGradeId);
    }
    /// <summary>
    /// Checking that Nozzle is set
    /// </summary>
    public boolean isFuelGradeIdSet() { return mFlags.isFlagSet(Flag.FuelGradeId); }
    /// <summary>
    /// FuelGradeName getter and setter
    /// </summary>
    public String getFuelGradeName() { return mFuelGradeName; }
    public void setFuelGradeName(String fuelGradeName) {
        mFuelGradeName = fuelGradeName;
        mFlags.setFlag(Flag.FuelGradeName);
    }
    /// <summary>
    /// Checking that FuelGradeName is set
    /// </summary>
    public boolean isFuelGradeNameSet() { return mFlags.isFlagSet(Flag.FuelGradeName); }
    /// <summary>
    /// Volume getter and setter
    /// </summary>
    public double getVolume() { return mVolume; }
    public void setVolume(double volume) {
        mVolume = volume;
        mFlags.setFlag(Flag.Volume);
    }
    /// <summary>
    /// Checking that Volume is set
    /// </summary>
    public boolean isVolumeSet() { return mFlags.isFlagSet(Flag.Volume); }
    /// <summary>
    /// TCVolume getter and setter
    /// </summary>
    public double getTCVolume() { return mTCVolume; }
    public void setTCVolume(double tCVolume) {
        mTCVolume = tCVolume;
        mFlags.setFlag(Flag.TCVolume);
    }
    /// <summary>
    /// Checking that TCVolume is set
    /// </summary>
    boolean isTCVolumeSet() { return mFlags.isFlagSet(Flag.TCVolume); }
    /// <summary>
    /// Price getter and setter
    /// </summary>
    public double getPrice() { return mPrice; }
    public void setPrice(double price) {
        mPrice = price;
        mFlags.setFlag(Flag.Price);
    }
    /// <summary>
    /// Checking that Price is set
    /// </summary>
    public boolean isPriceSet() { return mFlags.isFlagSet(Flag.Price); }
    /// <summary>
    /// Amount getter and setter
    /// </summary>
    public double getAmount() { return mAmount; }
    public void setAmount(double amount) {
        mAmount = amount;
        mFlags.setFlag(Flag.Amount);
    }
    /// <summary>
    /// Checking that Amount is set
    /// </summary>
    public boolean isAmountSet() { return mFlags.isFlagSet(Flag.Amount); }
    /// <summary>
    /// TotalVolume getter and setter
    /// </summary>
    public double getTotalVolume() { return mTotalVolume; }
    public void setTotalVolume(double totalVolume) {
        mTotalVolume = totalVolume;
        mFlags.setFlag(Flag.TotalVolume);
    }
    /// <summary>
    /// Checking that TotalVolume is set
    /// </summary>
    public boolean isTotalVolumeSet() { return mFlags.isFlagSet(Flag.TotalVolume); }
    /// <summary>
    /// TotalAmount getter and setter
    /// </summary>
    public double getTotalAmount() { return mTotalAmount; }
    public void setTotalAmount(double totalAmount) {
        mTotalAmount = totalAmount;
        mFlags.setFlag(Flag.TotalAmount);
    }
    /// <summary>
    /// Checking that TotalAmount is set
    /// </summary>
    public boolean isTotalAmountSet() { return mFlags.isFlagSet(Flag.TotalAmount); }
    /// <summary>
    /// Tag getter and setter
    /// </summary>
    public String getTag() { return mTag; }
    public void setTag(String tag) {
        mTag = tag;
        mFlags.setFlag(Flag.Tag);
    }
    /// <summary>
    /// Checking that Tag is set
    /// </summary>
    public boolean isTagSet() { return mFlags.isFlagSet(Flag.Tag); }
    /// <summary>
    /// UserId getter and setter
    /// </summary>
    public int getUserId() { return mUserId; }
    public boolean isUserIdSet() { return mFlags.isFlagSet(Flag.UserId); }
    public void setUserId(int userId) {
        mUserId = userId;
        mFlags.setFlag(Flag.UserId);
    }
}