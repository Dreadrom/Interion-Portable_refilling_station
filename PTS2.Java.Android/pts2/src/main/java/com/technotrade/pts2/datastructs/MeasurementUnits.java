package com.technotrade.pts2.datastructs;

/// <summary>
/// Measurement units data
/// </summary>
public class MeasurementUnits {
    private String mVolume;
    private String mTemperature;

    public MeasurementUnits() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static MeasurementUnits create() {
        return new MeasurementUnits();
    }
    /// <summary>
    /// Volume getter and setter
    /// </summary>
    public String getVolume() { return mVolume; }
    public void setVolume(String volume) { mVolume = volume; }
    /// <summary>
    /// Temperature getter and setter
    /// </summary>
    public String getTemperature() { return mTemperature; }
    public void setTemperature(String temperature) { mTemperature = temperature; }
}