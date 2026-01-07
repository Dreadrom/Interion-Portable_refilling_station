package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.util.Flags;

import java.util.Date;

/// <summary>
/// Pump transaction information data
/// </summary>
public class ReportPumpTransaction {
    ///Flags to define which value is set
    private enum Flag {
        FuelGradeId,
        FuelGradeName
    };

    private Date mDateTimeStart;
    private Date mDateTime;
    private int mPump;
    private int mNozzle;
    private int mTransaction;
    private double mVolume;
    private double mTCVolume;
    private double mPrice;
    private double mAmount;
    private double mTotalVolume;
    private double mTotalAmount;
    private int mUserId;
    private String mTag;
    private int mFuelGradeId;
    private String mFuelGradeName;

    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    public ReportPumpTransaction() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ReportPumpTransaction create() {
        return new ReportPumpTransaction();
    }
    /// <summary>
    /// DateTimeStart getter and setter
    /// </summary>
    public Date getDateTimeStart() { return mDateTimeStart; }
    public void setDateTimeStart(Date dateTimeStart) { mDateTimeStart = dateTimeStart; }
    /// <summary>
    /// DateTime getter and setter
    /// </summary>
    public Date getDateTime() { return mDateTime; }
    public void setDateTime(Date dateTime) { mDateTime = dateTime; }
    /// <summary>
    /// Pump getter and setter
    /// </summary>
    public int getPump() { return mPump; }
    public void setPump(int pump) { mPump = pump; }
    /// <summary>
    /// Nozzle getter and setter
    /// </summary>
    public int getNozzle() { return mNozzle; }
    public void setNozzle(int nozzle) { mNozzle = nozzle; }
    /// <summary>
    /// Transaction number getter and setter
    /// </summary>
    public int getTransaction() { return mTransaction; }
    public void setTransaction(int transaction) { mTransaction = transaction; }
    /// <summary>
    /// Volume getter and setter
    /// </summary>
    public double getVolume() { return mVolume; }
    public void setVolume(double volume) { mVolume = volume; }
    /// <summary>
    /// TCVolume getter and setter
    /// </summary>
    public double getTCVolume() { return mTCVolume; }
    public void setTCVolume(double tCVolume) { mTCVolume = tCVolume; }
    /// <summary>
    /// Price getter and setter
    /// </summary>
    public double getPrice() { return mPrice; }
    public void setPrice(double price) { mPrice = price; }
    /// <summary>
    /// Amount getter and setter
    /// </summary>
    public double getAmount() { return mAmount; }
    public void setAmount(double amount) { mAmount = amount; }
    /// <summary>
    /// TotalVolume getter and setter
    /// </summary>
    public double getTotalVolume() { return mTotalVolume; }
    public void setTotalVolume(double totalVolume) { mTotalVolume = totalVolume; }
    /// <summary>
    /// TotalAmount getter and setter
    /// </summary>
    public double getTotalAmount() { return mTotalAmount; }
    public void setTotalAmount(double totalAmount) { mTotalAmount = totalAmount; }
    /// <summary>
    /// UserId getter and setter
    /// </summary>
    public int getUserId() { return mUserId; }
    public void setUserId(int userId) { mUserId = userId; }
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