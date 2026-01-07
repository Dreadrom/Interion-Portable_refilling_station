package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.util.Flags;

import java.util.Date;

/// <summary>
/// Pump idle status data
/// </summary>
public class PumpIdleStatus extends PumpStatusBase {
    ///Flags to define which value is set
    private enum Flag {
        Tag,
        Nozzle,
        FuelGradeId,
        FuelGradeName,
        Transaction,
        LastDateTimeStart,
        LastDateTime,
        LastFuelGradeId,
        LastFuelGradeName,
        LastTotalVolume,
        LastTotalAmount,
        LastUser
    };

    private int mNozzleUp;
    private int mLastNozzle;
    private double mLastVolume;
    private double mLastPrice;
    private double mLastAmount;
    private int mLastTransaction;
    private String mRequest;
    private String mTag;
    private int mNozzle;
    private int mFuelGradeId;
    private String mFuelGradeName;
    private int mTransaction;
    private Date mLastDateTimeStart;
    private Date mLastDateTime;
    private int mLastFuelGradeId;
    private String mLastFuelGradeName;
    private double mLastTotalVolume;
    private double mLastTotalAmount;
    private String mLastUser;

    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    /// <summary>
    /// Status getter
    /// </summary>
    @Override
    public PumpStatus getStatus() { return getNozzleUp() > 0 ? PumpStatus.NOZZLE : PumpStatus.IDLE; }
    /// <summary>
    /// NozzleUp getter
    /// </summary>
    public int getNozzleUp() { return mNozzleUp; }
    /// <summary>
    /// NozzleUp setter
    /// </summary>
    public void setNozzleUp(int nozzleUp) { mNozzleUp = nozzleUp; }
    /// <summary>
    /// LastNozzle getter
    /// </summary>
    public int getLastNozzle() { return mLastNozzle; }
    /// <summary>
    /// LastNozzle setter
    /// </summary>
    public void setLastNozzle(int lastNozzle) { mLastNozzle = lastNozzle; }
    /// <summary>
    /// LastVolume getter
    /// </summary>
    public double getLastVolume() { return mLastVolume; }
    /// <summary>
    /// LastVolume setter
    /// </summary>
    public void setLastVolume(double lastVolume) { mLastVolume = lastVolume; }
    /// <summary>
    /// LastPrice getter
    /// </summary>
    public double getLastPrice() { return mLastPrice; }
    /// <summary>
    /// LastPrice setter
    /// </summary>
    public void setLastPrice(double lastPrice) { mLastPrice = lastPrice; }
    /// <summary>
    /// LastAmount getter
    /// </summary>
    public double getLastAmount() { return mLastAmount; }
    /// <summary>
    /// LastAmount setter
    /// </summary>
    public void setLastAmount(double lastAmount) { mLastAmount = lastAmount; }
    /// <summary>
    /// LastTransaction getter
    /// </summary>
    public int getLastTransaction() { return mLastTransaction; }
    /// <summary>
    /// LastTransaction setter
    /// </summary>
    public void setLastTransaction(int lastTransaction) { mLastTransaction = lastTransaction; }
    /// <summary>
    /// Request getter
    /// </summary>
    public String getRequest() { return mRequest; }
    /// <summary>
    /// Request setter
    /// </summary>
    public void setRequest(String request) { mRequest = request; }
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
    /// Nozzle getter
    /// </summary>
    public int getNozzle() { return mNozzle; }
    /// <summary>
    /// NozzleUp setter
    /// </summary>
    public void setNozzle(int nozzle) { mNozzle = nozzle; mFlags.setFlag(Flag.Nozzle); }
    /// <summary>
    /// Checking that FuelGradeId is set
    /// </summary>
    public boolean isNozzleSet() { return mFlags.isFlagSet(Flag.Nozzle); }
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
    /// Transaction getter
    /// </summary>
    public int getTransaction() { return mTransaction; }
    /// <summary>
    /// Transaction setter
    /// </summary>
    public void setTransaction(int transaction) { mTransaction = transaction; mFlags.setFlag(Flag.Transaction); }
    /// <summary>
    /// Checking that Transaction is set
    /// </summary>
    public boolean isTransactionSet() { return mFlags.isFlagSet(Flag.Transaction); }
    /// <summary>
    /// LastDateTimeStart getter
    /// </summary>
    public Date getLastDateTimeStart() { return mLastDateTimeStart; }
    /// <summary>
    /// LastDateTimeStart setter
    /// </summary>
    public void setLastDateTimeStart(Date lastDateTimeStart) { mLastDateTimeStart = lastDateTimeStart; mFlags.setFlag(Flag.LastDateTimeStart); }
    /// <summary>
    /// Checking that LastDateTimeStart is set
    /// </summary>
    public boolean isLastDateTimeStartSet() { return mFlags.isFlagSet(Flag.LastDateTimeStart); }
    /// <summary>
    /// LastDateTime getter
    /// </summary>
    public Date getLastDateTime() { return mLastDateTime; }
    /// <summary>
    /// LastDateTimeStart setter
    /// </summary>
    public void setLastDateTime(Date lastDateTime) { mLastDateTime = lastDateTime; mFlags.setFlag(Flag.LastDateTime); }
    /// <summary>
    /// Checking that LastDateTime is set
    /// </summary>
    public boolean isLastDateTimeSet() { return mFlags.isFlagSet(Flag.LastDateTime); }
    /// <summary>
    /// LastFuelGradeId getter
    /// </summary>
    public int getLastFuelGradeId() { return mLastFuelGradeId; }
    /// <summary>
    /// LastFuelGradeId setter
    /// </summary>
    public void setLastFuelGradeId(int lastFuelGradeId) { mLastFuelGradeId = lastFuelGradeId; mFlags.setFlag(Flag.LastFuelGradeId); }
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
    /// <summary>
    /// LastTotalVolume getter
    /// </summary>
    public double getLastTotalVolume() { return mLastTotalVolume; }
    /// <summary>
    /// LastTotalVolume setter
    /// </summary>
    public void setLastTotalVolume(double lastTotalVolume) { mLastTotalVolume = lastTotalVolume; mFlags.setFlag(Flag.LastTotalVolume); }
    /// <summary>
    /// Checking that LastTotalVolume is set
    /// </summary>
    public boolean isLastTotalVolumeSet() { return mFlags.isFlagSet(Flag.LastTotalVolume); }
    /// <summary>
    /// LastTotalAmount getter
    /// </summary>
    public double getLastTotalAmount() { return mLastTotalAmount; }
    /// <summary>
    /// LastTotalAmount setter
    /// </summary>
    public void setLastTotalAmount(double lastTotalAmount) { mLastTotalAmount = lastTotalAmount; mFlags.setFlag(Flag.LastTotalAmount); }
    /// <summary>
    /// Checking that LastTotalAmount is set
    /// </summary>
    public boolean isLastTotalAmountSet() { return mFlags.isFlagSet(Flag.LastTotalAmount); }
    /// <summary>
    /// LastUser getter
    /// </summary>
    public String getLastUser() { return mLastUser; }
    /// <summary>
    /// LastUser setter
    /// </summary>
    public void setLastUser(String lastUser) { mLastUser = lastUser; mFlags.setFlag(Flag.LastUser); }
    /// <summary>
    /// Checking that LastUser is set
    /// </summary>
    public boolean isLastUserSet() { return mFlags.isFlagSet(Flag.LastUser); }
}