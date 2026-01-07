package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.enumeration.AlarmType;
import com.technotrade.pts2.enumeration.ProbeStatus;
import com.technotrade.pts2.util.Flags;

import java.util.HashSet;

/// <summary>
/// Probe measurements used for provision of monitoring over probes/tanks. 
/// </summary>
public class ProbeMeasurements {
    ///Flags to define which value is set
    private enum Flag {
        ProductHeight,
        WaterHeight,
        Temperature,
        ProductVolume,
        WaterVolume,
        ProductUllage,
        ProductTCVolume,
        ProductDensity,
        ProductMass,
        TankFillingPercentage,
        LastInTankTankDeliveryStart,
        LastInTankTankDeliveryEnd,
        LastInTankTankDelivery,
        FuelGradeId,
        FuelGradeName
    };

    private int mProbe;
    private ProbeStatus mStatus;
    private HashSet<AlarmType> mAlarms;
    private double mProductHeight;
    private double mWaterHeight;
    private double mTemperature;
    private double mProductVolume;
    private double mWaterVolume;
    private double mProductUllage;
    private double mProductTCVolume;
    private double mProductDensity;
    private double mProductMass;
    private int mTankFillingPercentage;
    private boolean mHighProductAlarm;
    private boolean mLowProductAlarm;
    private InTankDelivery mLastInTankDeliveryStart;
    private InTankDelivery mLastInTankDeliveryEnd;
    private InTankDelivery mLastInTankDelivery;
    private int mFuelGradeId;
    private String mFuelGradeName;

    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    public ProbeMeasurements() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ProbeMeasurements create() {
        return new ProbeMeasurements();
    }
    /// <summary>
    /// Probe getter and setter
    /// </summary>
    public int getProbe() { return mProbe; }
    public void setProbe(int probe) { mProbe = probe; }
    /// <summary>
    /// Status getter and setter
    /// </summary>
    public ProbeStatus getStatus() { return mStatus; }
    public void setStatus(ProbeStatus status) { mStatus = status; }
    /// <summary>
    /// Status value getter
    /// </summary>
    public String getStatusValue() { return mStatus.getValue(); }
    /// <summary>
    /// Alarms getter and setter
    /// </summary>
    public HashSet<AlarmType> getAlarms() { return mAlarms; }
    public void setAlarm(AlarmType a) {mAlarms.add(a); }
    public void setAlarms(HashSet<AlarmType> alarms) { mAlarms = alarms; }
    /// <summary>
    /// Checking that alarm is set
    /// </summary>
    public boolean isAlarmSet(AlarmType a) { return mAlarms.contains(a); }
    /// <summary>
    /// Checking that ProductHeight is set for CriticalHighProduct
    /// </summary>
    public boolean isCriticalHighProductAlarmSet() { return isAlarmSet(AlarmType.CRITICAL_HIGH_PRODUCT); }
    /// <summary>
    /// Checking that ProductHeight is set for HighProduct
    /// </summary>
    public boolean isHighProductAlarmSet() { return isAlarmSet(AlarmType.HIGH_PRODUCT); }
    /// <summary>
    /// Checking that ProductHeight is set for CriticalLowProduct
    /// </summary>
    public boolean isCriticalLowProductAlarmSet() { return isAlarmSet(AlarmType.CRITICAL_LOW_PRODUCT); }
    /// <summary>
    /// Checking that ProductHeight is set for LowProduct
    /// </summary>
    public boolean isLowProductAlarmSet() { return isAlarmSet(AlarmType.LOW_PRODUCT); }
    /// <summary>
    /// Checking that alarm is set for HighWater
    /// </summary>
    public boolean isHighWaterAlarmSet() { return isAlarmSet(AlarmType.HIGH_WATER); }
    /// <summary>
    /// ProductHeight getter and setter
    /// </summary>
    public double getProductHeight() { return mProductHeight; }
    public void setProductHeight(double productHeight) { mProductHeight = productHeight; mFlags.setFlag(Flag.ProductHeight); }
    /// <summary>
    /// Checking that ProductHeight is set
    /// </summary>
    public boolean isProductHeightSet() { return mFlags.isFlagSet(Flag.ProductHeight); }
    /// <summary>
    /// WaterHeight getter and setter
    /// </summary>
    public double getWaterHeight() { return mWaterHeight; }
    public void setWaterHeight(double waterHeight) { mWaterHeight = waterHeight; mFlags.setFlag(Flag.WaterHeight); }
    /// <summary>
    /// Checking that WaterHeight is set
    /// </summary>
    public boolean isWaterHeightSet() { return mFlags.isFlagSet(Flag.WaterHeight); }
    /// <summary>
    /// Temperature getter and setter
    /// </summary>
    public double getTemperature() { return mTemperature; }
    public void setTemperature(double temperature) { mTemperature = temperature; mFlags.setFlag(Flag.Temperature); }
    /// <summary>
    /// Checking that Temperature is set
    /// </summary>
    public boolean isTemperatureSet() { return mFlags.isFlagSet(Flag.Temperature); }
    /// <summary>
    /// ProductVolume getter and setter
    /// </summary>
    public double getProductVolume()  { return mProductVolume; }
    public void setProductVolume(double productVolume) { mProductVolume = productVolume; mFlags.setFlag(Flag.ProductVolume); }
    /// <summary>
    /// Checking that ProductVolume is set
    /// </summary>
    public boolean isProductVolumeSet() { return mFlags.isFlagSet(Flag.ProductVolume); }
    /// <summary>
    /// WaterVolume getter and setter
    /// </summary>
    public double getWaterVolume() { return mWaterVolume; }
    public void setWaterVolume(double waterVolume) { mWaterVolume = waterVolume; mFlags.setFlag(Flag.WaterVolume); }
    /// <summary>
    /// Checking that WaterVolume is set
    /// </summary>
    public boolean isWaterVolumeSet() { return mFlags.isFlagSet(Flag.WaterVolume); }
    /// <summary>
    /// ProductUllage getter and setter
    /// </summary>
    public double getProductUllage() { return mProductUllage; }
    public void setProductUllage(double productUllage) { mProductUllage = productUllage; mFlags.setFlag(Flag.ProductUllage); }
    /// <summary>
    /// Checking that ProductUllage is set
    /// </summary>
    public boolean isProductUllageSet() { return mFlags.isFlagSet(Flag.ProductUllage); }
    /// <summary>
    /// ProductTCVolume getter and setter
    /// </summary>
    public double getProductTCVolume() { return mProductTCVolume; }
    public void setProductTCVolume(double productTCVolume) { mProductTCVolume = productTCVolume; mFlags.setFlag(Flag.ProductTCVolume); }
    /// <summary>
    /// Checking that ProductTCVolume is set
    /// </summary>
    public boolean isProductTCVolumeSet() { return mFlags.isFlagSet(Flag.ProductTCVolume); }
    /// <summary>
    /// ProductDensity getter and setter
    /// </summary>
    public double getProductDensity() { return mProductDensity; }
    public void setProductDensity(double productDensity) { mProductDensity = productDensity; mFlags.setFlag(Flag.ProductDensity); }
    /// <summary>
    /// Checking that ProductDensity is set
    /// </summary>
    public boolean isProductDensitySet() { return mFlags.isFlagSet(Flag.ProductDensity); }
    /// <summary>
    /// ProductMass getter and setter
    /// </summary>
    public double getProductMass() { return mProductMass; }
    public void setProductMass(double productMass) { mProductMass = productMass; mFlags.setFlag(Flag.ProductMass); }
    /// <summary>
    /// Checking that ProductMass is set
    /// </summary>
    public boolean isProductMassSet() { return mFlags.isFlagSet(Flag.ProductMass); }
    /// <summary>
    /// TankFillingPercentage getter and setter
    /// </summary>
    public int getTankFillingPercentage() { return mTankFillingPercentage; }
    public void setTankFillingPercentage(int tankFillingPercentage) { mTankFillingPercentage = tankFillingPercentage; mFlags.setFlag(Flag.TankFillingPercentage); }
    /// <summary>
    /// Checking that TankFillingPercentage is set
    /// </summary>
    public boolean isTankFillingPercentageSet() { return mFlags.isFlagSet(Flag.TankFillingPercentage); }
    /// <summary>
    /// HighProductAlarm getter and setter
    /// </summary>
    public boolean getHighProductAlarm() { return mHighProductAlarm; }
    public void setHighProductAlarm(boolean highProductAlarm) { mHighProductAlarm = highProductAlarm; }
    /// <summary>
    /// LowProductAlarm getter and setter
    /// </summary>
    public boolean getLowProductAlarm() { return mLowProductAlarm; }
    public void setLowProductAlarm(boolean lowProductAlarm) { mLowProductAlarm = lowProductAlarm; }
    /// <summary>
    /// LastInTankDeliveryStart getter and setter
    /// </summary>
    public InTankDelivery getLastInTankDeliveryStart() { return mLastInTankDeliveryStart; }
    public void setLastInTankDeliveryStart(InTankDelivery lastInTankDeliveryStart) { mLastInTankDeliveryStart = lastInTankDeliveryStart; mFlags.setFlag(Flag.LastInTankTankDeliveryStart); }
    /// <summary>
    /// Checking that LastInTankDeliveryStart is set
    /// </summary>
    public boolean isLastInTankDeliveryStartSet() { return mFlags.isFlagSet(Flag.LastInTankTankDeliveryStart); }
    /// <summary>
    /// LastInTankDeliveryEnd getter and setter
    /// </summary>
    public InTankDelivery getLastInTankDeliveryEnd() { return mLastInTankDeliveryEnd; }
    public void setLastInTankDeliveryEnd(InTankDelivery lastInTankDeliveryEnd) { mLastInTankDeliveryEnd = lastInTankDeliveryEnd; mFlags.setFlag(Flag.LastInTankTankDeliveryEnd); }
    /// <summary>
    /// Checking that LastInTankDeliveryEnd is set
    /// </summary>
    public boolean isLastInTankDeliveryEndSet() { return mFlags.isFlagSet(Flag.LastInTankTankDeliveryEnd); }
    /// <summary>
    /// LastInTankDelivery getter and setter
    /// </summary>
    public InTankDelivery getLastInTankDelivery() { return mLastInTankDelivery; }
    public void setLastInTankDelivery(InTankDelivery lastInTankDelivery) { mLastInTankDelivery = lastInTankDelivery; mFlags.setFlag(Flag.LastInTankTankDelivery); }
    /// <summary>
    /// Checking that LastInTankDelivery is set
    /// </summary>
    public boolean isLastInTankDeliverySet() { return mFlags.isFlagSet(Flag.LastInTankTankDelivery); }
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