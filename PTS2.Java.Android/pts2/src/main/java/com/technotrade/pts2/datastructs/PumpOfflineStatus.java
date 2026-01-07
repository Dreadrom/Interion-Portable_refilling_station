package com.technotrade.pts2.datastructs;

import static com.technotrade.pts2.enumeration.PumpStatus.OFFLINE;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.util.Flags;

import java.util.Date;

/// <summary>
/// Pump offline status data
/// </summary>
public class PumpOfflineStatus extends PumpStatusBase {
    ///Flags to define which value is set
    private enum Flag {
        NozzleUp,
        Nozzle,
        FuelGradeId,
        FuelGradeName,
        Volume,
        TCVolume,
        Price,
        Amount,
        Transaction,
        TotalVolume,
        TotalAmount,
        DateTimeStart,
        Tag,
        Request,
        LastDateTimeStart,
        LastDateTime,
        LastNozzle,
        LastFuelGradeId,
        LastFuelGradeName,
        LastTransaction,
        LastVolume,
        LastPrice,
        LastAmount,
        LastTotalVolume,
        LastTotalAmount,
        LastUser
    };

    private int mNozzle;
    private int mNozzleUp;
    private int mFuelGradeId;
    private String mFuelGradeName;
    private double mVolume;
    private double mTCVolume;
    private double mPrice;
    private double mAmount;
    private int mTransaction;
    private double mTotalVolume;
    private double mTotalAmount;
    private Date mDateTimeStart;
    private String mTag;
    private String mRequest;
    private Date mLastDateTimeStart;
    private Date mLastDateTime;
    private int mLastNozzle;
    private int mLastFuelGradeId;
    private String mLastFuelGradeName;
    private int mLastTransaction;
    private double mLastVolume;
    private double mLastPrice;
    private double mLastAmount;
    private double mLastTotalVolume;
    private double mLastTotalAmount;
    private String mLastUser;

    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    /// <summary>
    /// Status getter
    /// </summary>
    @Override
    public PumpStatus getStatus() {
        mStatus = OFFLINE;
        return mStatus;
    };

    /// <summary>
    /// Nozzle getter
    /// </summary>
    public int getNozzle() { return mNozzle; }
    /// <summary>
    /// Nozzle setter
    /// </summary>
    public void setNozzle(int nozzle) { mNozzle = nozzle; mFlags.setFlag(Flag.Nozzle); }
    /// <summary>
    /// Checking that Nozzle is set
    /// </summary>
    public boolean isNozzleSet() { return mFlags.isFlagSet(Flag.Nozzle); }
    /// <summary>
    /// NozzleUp getter
    /// </summary>
    public int getNozzleUp() { return mNozzleUp; }
    /// <summary>
    /// NozzleUp setter
    /// </summary>
    public void setNozzleUp(int nozzleUp) { mNozzleUp = nozzleUp; mFlags.setFlag(Flag.NozzleUp); }
    /// <summary>
    /// Checking that NozzleUp is set
    /// </summary>
    public boolean isNozzleUpSet() { return mFlags.isFlagSet(Flag.NozzleUp); }
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
    /// Volume getter
    /// </summary>
    public double getVolume() { return mVolume; }
    /// <summary>
    /// Volume setter
    /// </summary>
    public void setVolume(double volume) { mVolume = volume; mFlags.setFlag(Flag.Volume); }
    /// <summary>
    /// Checking that Volume is set
    /// </summary>
    public boolean isVolumeSet() { return mFlags.isFlagSet(Flag.Volume); }
    /// <summary>
    /// TCVolume getter
    /// </summary>
    public double getTCVolume()  { return mTCVolume; }
    /// <summary>
    /// TCVolume setter
    /// </summary>
    public void setTCVolume(double tCVolume)  { mTCVolume = tCVolume; mFlags.setFlag(Flag.TCVolume); }
    /// <summary>
    /// Checking that TCVolume is set
    /// </summary>
    public boolean isTCVolumeSet() { return mFlags.isFlagSet(Flag.TCVolume); }
    /// <summary>
    /// Price getter
    /// </summary>
    public double getPrice() { return mPrice; }
    /// <summary>
    /// Price setter
    /// </summary>
    public void setPrice(double price) { mPrice = price; mFlags.setFlag(Flag.Price); }
    /// <summary>
    /// Checking that Price is set
    /// </summary>
    public boolean isPriceSet() { return mFlags.isFlagSet(Flag.Amount); }
    /// <summary>
    /// Amount getter
    /// </summary>
    public double getAmount() { return mAmount; }
    /// <summary>
    /// Amount setter
    /// </summary>
    public void setAmount(double amount) { mAmount = amount; mFlags.setFlag(Flag.Amount); }
    /// <summary>
    /// Checking that Amount is set
    /// </summary>
    public boolean isAmountSet() { return mFlags.isFlagSet(Flag.Amount); }
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
    /// TotalVolume getter
    /// </summary>
    public double getTotalVolume() { return mTotalVolume; }
    /// <summary>
    /// TotalVolume setter
    /// </summary>
    public void setTotalVolume(double totalVolume) { mTotalVolume = totalVolume; mFlags.setFlag(Flag.TotalVolume); }
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
    public void setTotalAmount(double totalAmount) { mTotalAmount = totalAmount; mFlags.setFlag(Flag.TotalAmount); }
    /// <summary>
    /// Checking that TotalAmount is set
    /// </summary>
    public boolean isTotalAmountSet() { return mFlags.isFlagSet(Flag.TotalAmount); }
    /// <summary>
    /// DateTimeStart getter and setter
    /// </summary>
    public Date getDateTimeStart() { return mDateTimeStart; }
    public void setDateTimeStart(Date dateTimeStart) { mDateTimeStart = dateTimeStart; mFlags.setFlag(Flag.DateTimeStart); }
    /// <summary>
    /// Checking that DateTimeStart is set
    /// </summary>
    public boolean isDateTimeStartSet() { return mFlags.isFlagSet(Flag.DateTimeStart); }
    /// <summary>
    /// Tag getter
    /// </summary>
    public String getTag() { return mTag; }
    /// <summary>
    /// Tag setter
    /// </summary>
    public void setTag(String tag) { mTag = tag; mFlags.setFlag(Flag.Tag); mFlags.setFlag(Flag.Tag); }
    /// <summary>
    /// Checking that Tag is set
    /// </summary>
    public boolean isTagSet() { return mFlags.isFlagSet(Flag.Tag); }
    /// <summary>
    /// Request getter
    /// </summary>
    public String getRequest() { return mRequest; }
    /// <summary>
    /// Request setter
    /// </summary>
    public void setRequest(String request) { mRequest = request; mFlags.setFlag(Flag.Request); }
    /// <summary>
    /// Checking that Request is set
    /// </summary>
    public boolean isRequestSet() { return mFlags.isFlagSet(Flag.Request); }
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
    /// LastNozzle getter
    /// </summary>
    public int getLastNozzle() { return mLastNozzle; }
    /// <summary>
    /// LastNozzle setter
    /// </summary>
    public void setLastNozzle(int lastNozzle) { mLastNozzle = lastNozzle; mFlags.setFlag(Flag.LastNozzle); }
    /// <summary>
    /// Checking that LastNozzle is set
    /// </summary>
    public boolean isLastNozzleSet() { return mFlags.isFlagSet(Flag.LastNozzle); }
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
    /// LastTransaction getter
    /// </summary>
    public int getLastTransaction() { return mLastTransaction; }
    /// <summary>
    /// LastTransaction setter
    /// </summary>
    public void setLastTransaction(int lastTransaction) { mLastTransaction = lastTransaction; mFlags.setFlag(Flag.LastTransaction); }
    /// <summary>
    /// Checking that LastTransaction is set
    /// </summary>
    public boolean isLastTransactionSet() { return mFlags.isFlagSet(Flag.LastTransaction); }
    /// <summary>
    /// LastVolume getter
    /// </summary>
    public double getLastVolume() { return mLastVolume; }
    /// <summary>
    /// LastVolume setter
    /// </summary>
    public void setLastVolume(double lastVolume) { mLastVolume = lastVolume; mFlags.setFlag(Flag.LastVolume); }
    /// <summary>
    /// Checking that LastVolume is set
    /// </summary>
    public boolean isLastVolumeSet() { return mFlags.isFlagSet(Flag.LastVolume); }
    /// <summary>
    /// LastPrice getter
    /// </summary>
    public double getLastPrice() { return mLastPrice; }
    /// <summary>
    /// LastPrice setter
    /// </summary>
    public void setLastPrice(double lastPrice) { mLastPrice = lastPrice; mFlags.setFlag(Flag.LastPrice); }
    /// <summary>
    /// Checking that LastAmount is set
    /// </summary>
    public boolean isLastPriceSet() { return mFlags.isFlagSet(Flag.LastPrice); }
    /// <summary>
    /// LastAmount getter
    /// </summary>
    public double getLastAmount() { return mLastAmount; }
    /// <summary>
    /// LastAmount setter
    /// </summary>
    public void setLastAmount(double lastAmount) { mLastAmount = lastAmount; mFlags.setFlag(Flag.LastAmount); }
    /// <summary>
    /// Checking that LastAmount is set
    /// </summary>
    public boolean isLastAmountSet() { return mFlags.isFlagSet(Flag.LastAmount); }
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