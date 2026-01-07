package com.technotrade.pts2.datastructs;

/// <summary>
/// Settings for decimals digits
/// </summary>
public class SystemDecimalDigits {
    private short mPrice;
    private short mAmount;
    private short mVolume;
    private short mAmountTotal;
    private short mVolumeTotal;

    public SystemDecimalDigits() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static SystemDecimalDigits create() {
        return new SystemDecimalDigits();
    }
    /// <summary>
    /// Price getter and setter
    /// </summary>
    public short getPrice() { return mPrice; }
    public void setPrice(short price) { mPrice = price; }
    /// <summary>
    /// Amount getter and setter
    /// </summary>
    public short getAmount() { return mAmount; }
    public void setAmount(short amount) { mAmount = amount; }
    /// <summary>
    /// Volume getter and setter
    /// </summary>
    public short getVolume() { return mVolume; }
    public void setVolume(short volume) { mVolume = volume; }
    /// <summary>
    /// AmountTotal getter and setter
    /// </summary>
    public short getAmountTotal() { return mAmountTotal; }
    public void setAmountTotal(short amountTotal) { mAmountTotal = amountTotal; }
    /// <summary>
    /// VolumeTotal getter and setter
    /// </summary>
    public short getVolumeTotal() { return mVolumeTotal; }
    public void setVolumeTotal(short volumeTotal) { mVolumeTotal = volumeTotal; }
}