package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.util.Flags;

import java.util.Date;

/// <summary>
/// Class describing last in-tank delivery
/// </summary>
public class InTankDelivery {
    private enum Flag {
        DateTime,
        ProductHeight,
        WaterHeight,
        Temperature,
        ProductVolume,
        ProductTCVolume,
        ProductDensity,
        ProductMass,
        PumpsDispensedVolume
    };

    private Date mDateTime;
    private double mProductHeight;
    private double mWaterHeight;
    private double mTemperature;
    private double mProductVolume;
    private double mProductTCVolume;
    private double mProductDensity;
    private double mProductMass;
    private double mPumpsDispensedVolume;
    //Option values flag
    private final Flags<Flag> mFlags = new Flags<>();

    public InTankDelivery() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static InTankDelivery create() {
        return new InTankDelivery();
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
    /// PumpsDispensedVolume getter and setter
    /// </summary>
    public double getPumpsDispensedVolume() { return mPumpsDispensedVolume; }
    public void setPumpsDispensedVolume(double pumpsDispensedVolume) { mPumpsDispensedVolume = pumpsDispensedVolume; mFlags.setFlag(Flag.PumpsDispensedVolume); }
    /// <summary>
    /// Checking that PumpsDispensedVolume is set
    /// </summary>
    public boolean isPumpsDispensedVolumeSet() { return mFlags.isFlagSet(Flag.PumpsDispensedVolume); }
}