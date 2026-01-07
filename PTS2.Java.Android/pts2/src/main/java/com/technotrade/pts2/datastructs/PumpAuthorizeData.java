package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.enumeration.NozzleOrFuelIdSelector;
import com.technotrade.pts2.enumeration.PumpAuthorizeType;

import java.math.BigDecimal;
import java.util.ArrayList;

/// <summary>
/// Pump authorize data.
/// The class data used for PumpAuthorize request
/// </summary>
public class PumpAuthorizeData {
    private int mPump;
    private NozzleOrFuelIdSelector mNozzleOrFuelIdSelector;
    private int mNozzle;
    private ArrayList<Integer> mNozzles;
    private int mFuelGradeId;
    private ArrayList<Integer> m_FuelGradeIds;
    private PumpAuthorizeType mType;
    private BigDecimal mDose;
    private boolean mPriceEnabled;
    private BigDecimal mPrice;
    private boolean mTransactionEnabled;
    private int mTransaction;
    private boolean mAutoCloseTransaction;
    private boolean mTagEnabled;
    private String mTag;
    private boolean mAuthorizeWithoutTag;
    private boolean mNoTagVerification;

    public PumpAuthorizeData() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static PumpAuthorizeData create() {
        return new PumpAuthorizeData();
    }
    /// <summary>
    /// Pump number getter and setter
    /// </summary>
    public int getPump() { return mPump; }
    public void setPump(int pump) { mPump = pump; }
    /// <summary>
    /// NozzleOrFielIdSelector getter and setter
    /// </summary>
    public NozzleOrFuelIdSelector getNozzleOrFuelIdSelector() { return mNozzleOrFuelIdSelector; }
    public void setNozzleOrFuelIdSelector(NozzleOrFuelIdSelector nozzleOrFuelIdSelector) { mNozzleOrFuelIdSelector = nozzleOrFuelIdSelector; }
    /// <summary>
    /// Nozzle number getter and setter
    /// </summary>
    public int getNozzle() { return mNozzle; }
    public void setNozzle(int nozzle) { mNozzle = nozzle; }
    /// <summary>
    /// Nozzles getter and setter
    /// </summary>
    public ArrayList<Integer> getNozzles() { return mNozzles; }
    public void setNozzles(ArrayList<Integer> nozzles) { mNozzles = nozzles; }
    /// <summary>
    /// FuelGradeId getter and setter
    /// </summary>
    public int getFuelGradeId() { return mFuelGradeId; }
    public void setFuelGradeId(int fuelGradeId) { mFuelGradeId = fuelGradeId; }
    /// <summary>
    /// FuelGradeIds getter and setter
    /// </summary>
    public ArrayList<Integer> getFuelGradeIds() { return m_FuelGradeIds; }
    public void setFuelGradeIds(ArrayList<Integer> fuelGradeIds) { m_FuelGradeIds = fuelGradeIds; }
    /// <summary>
    /// Type getter and setter
    /// </summary>
    public PumpAuthorizeType getType() { return mType; }
    public void setType(PumpAuthorizeType type) { mType = type; }
    /// <summary>
    /// Dose getter and setter
    /// </summary>
    public BigDecimal getDose() { return mDose; }
    public void setDose(BigDecimal dose) { mDose = dose; }
    /// <summary>
    /// PriceEnabled getter and setter
    /// </summary>
    public boolean getPriceEnabled() { return mPriceEnabled; }
    public void setPriceEnabled(boolean priceEnabled) { mPriceEnabled = priceEnabled; }
    /// <summary>
    /// Price getter and setter
    /// </summary>
    public BigDecimal getPrice() { return mPrice; }
    public void setPrice(BigDecimal price) { mPrice = price; }
    /// <summary>
    /// TransactionEnabled getter and setter
    /// </summary>
    public boolean getTransactionEnabled() { return mTransactionEnabled; }
    public void setTransactionEnabled(boolean transactionEnabled) { mTransactionEnabled = transactionEnabled; }
    /// <summary>
    /// Transaction getter and setter
    /// </summary>
    public int getTransaction() { return mTransaction; }
    public void setTransaction(int transaction) { mTransaction = transaction; }
    /// <summary>
    /// AutoCloseTransaction getter and setter
    /// </summary>
    public boolean getAutoCloseTransaction() { return mAutoCloseTransaction; }
    public void setAutoCloseTransaction(boolean autoCloseTransaction) { mAutoCloseTransaction = autoCloseTransaction; }
    /// <summary>
    /// TagEnabled getter and setter
    /// </summary>
    public boolean getTagEnabled() { return mTagEnabled; }
    public void setTagEnabled(boolean tagEnabled) { mTagEnabled = tagEnabled; }
    /// <summary>
    /// Tag getter and setter
    /// </summary>
    public String getTag() { return mTag; }
    public void setTag(String tag) { mTag = tag; }
    /// <summary>
    /// AuthorizeWithoutTag getter and setter
    /// </summary>
    public boolean getAuthorizeWithoutTag() { return mAuthorizeWithoutTag; }
    public void setAuthorizeWithoutTag(boolean authorizeWithoutTag) { mAuthorizeWithoutTag = authorizeWithoutTag; }
    /// <summary>
    /// NoTagVerification getter and setter
    /// </summary>
    public boolean getNoTagVerification() { return mNoTagVerification; }
    public void setNoTagVerification(boolean noTagVerification) { mNoTagVerification = noTagVerification; }
}