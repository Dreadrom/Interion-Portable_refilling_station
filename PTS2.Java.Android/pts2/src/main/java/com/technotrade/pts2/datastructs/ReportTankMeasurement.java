package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.enumeration.ProbeStatus;
import com.technotrade.pts2.util.Flags;

import java.util.ArrayList;
import java.util.Date;

/// <summary>
/// Measurements saved by probes at changes of product height
/// </summary>
public class ReportTankMeasurement {
    ///Flags to define which value is set
    private enum Flag {
        DateTime,
        Tank,
        Status,
        ProductHeight,
        WaterHeight,
        Temperature,
        ProductVolume,
        ProductDensity,
        ProductMass,
        WaterVolume,
        ProductUllage,
        ProductTCVolume,
        Alarms,
        TankFillingPercentage,
        FuelGradeId,
        FuelGradeName
    };

    private Date mDateTime;
    private int mTank;
    private ProbeStatus mStatus;
    private ArrayList<String> mAlarms;
    private double mProductHeight;
    private double mWaterHeight;
    private double mTemperature;
    private double mProductVolume;
    private double mProductDensity;
    private double mProductMass;
    private double mWaterVolume;
    private double mProductUllage;
    private double mProductTCVolume;
    private int mTankFillingPercentage;
    private int mFuelGradeId;
    private String mFuelGradeName;

    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    public ReportTankMeasurement() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ReportTankMeasurement create() {
        return new ReportTankMeasurement();
    }
    /// <summary>
    /// DateTime getter and setter
    /// </summary>
    public Date getDateTime() { return mDateTime; }
    public void setDateTime(Date dateTime) { mDateTime = dateTime; mFlags.setFlag(Flag.DateTime); }
    /// <summary>
    /// Checking that DateTime is set
    /// </summary>
    public boolean isDateTimeSet() { return mFlags.isFlagSet(Flag.DateTime); }
    /// <summary>
    /// Tank number getter and setter
    /// </summary>
    public int getTank() { return mTank; }
    public void setTank(int tank) { mTank = tank; mFlags.setFlag(Flag.Tank); }
    /// <summary>
    /// Checking that Tank is set
    /// </summary>
    public boolean isTankSet() { return mFlags.isFlagSet(Flag.Tank); }
    /// <summary>
    /// Status getter and setter
    /// </summary>
    public ProbeStatus getStatus() { return mStatus; }
    public void setStatus(ProbeStatus status) { mStatus = status; mFlags.setFlag(Flag.Status); }
    /// <summary>
    /// Checking that Status is set
    /// </summary>
    public boolean isStatusSet() { return mFlags.isFlagSet(Flag.Status); }
    /// <summary>
    /// Alarms getter and setter
    /// </summary>
    public ArrayList<String> getAlarms() { return mAlarms; }
    public void setAlarms(ArrayList<String> alarms) { mAlarms = alarms; mFlags.setFlag(Flag.Alarms); }
    /// <summary>
    /// Checking that Alarms is set
    /// </summary>
    public boolean isAlarmsSet() { return mFlags.isFlagSet(Flag.Alarms); }
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
    public double getProductVolume() { return mProductVolume; }
    public void setProductVolume(double productVolume) { mProductVolume = productVolume; mFlags.setFlag(Flag.ProductVolume); }
    /// <summary>
    /// Checking that ProductVolume is set
    /// </summary>
    public boolean isProductVolumeSet() { return mFlags.isFlagSet(Flag.ProductVolume); }
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
    /// TankFillingPercentage number getter and setter
    /// </summary>
    public int getTankFillingPercentage() { return mTankFillingPercentage; }
    public void setTankFillingPercentage(int tankFillingPercentage) { mTankFillingPercentage = tankFillingPercentage; mFlags.setFlag(Flag.TankFillingPercentage); }
    /// <summary>
    /// Checking that TankFillingPercentage is set
    /// </summary>
    public boolean isTankFillingPercentageSet() { return mFlags.isFlagSet(Flag.TankFillingPercentage); }
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