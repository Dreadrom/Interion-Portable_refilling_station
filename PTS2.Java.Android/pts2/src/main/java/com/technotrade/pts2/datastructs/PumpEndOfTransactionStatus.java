package com.technotrade.pts2.datastructs;

import static com.technotrade.pts2.enumeration.PumpStatus.NONE;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.util.Flags;

import java.util.Date;

/// <summary>
/// Pump end of transaction status data
/// </summary>
public class PumpEndOfTransactionStatus extends PumpStatusBase {
    ///Flags to define which value is set
    private enum Flag {
        Tag,
        FuelGradeId,
        FuelGradeName,
        TotalVolume,
        TotalAmount,
        DateTimeStart,
        DateTime
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
    private double mTotalVolume;
    private double mTotalAmount;
    private Date mDateTimeStart;
    private Date mDateTime;

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
    public void setFuelGradeId(int fuelGradeId) { mFuelGradeId = fuelGradeId; }
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
    public void setFuelGradeName(String fuelGradeName) { mFuelGradeName = fuelGradeName; }
    /// <summary>
    /// Checking that FuelGradeName is set
    /// </summary>
    public boolean isFuelGradeNameSet() { return mFlags.isFlagSet(Flag.FuelGradeName); }
    /// <summary>
    /// TotalVolume getter
    /// </summary>
    public double getTotalVolume() { return mTotalVolume; }
    /// <summary>
    /// TotalVolume setter
    /// </summary>
    public void setTotalVolume(double totalVolume) { mTotalVolume = totalVolume; }
    /// <summary>
    /// Checking that TotalVolume is set
    /// </summary>
    public boolean isTotalVolumeSet() { return mFlags.isFlagSet(Flag.TotalVolume); }
    /// <summary>
    /// TotalAmount getter
    /// </summary>
    public double getTotalAmount() { return mTotalAmount; }
    /// <summary>
    /// TotalAmount setter
    /// </summary>
    public void setTotalAmount(double totalAmount) { mTotalAmount = totalAmount; }
    /// <summary>
    /// Checking that TotalAmount is set
    /// </summary>
    public boolean isTotalAmountSet() { return mFlags.isFlagSet(Flag.TotalAmount); }
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
}