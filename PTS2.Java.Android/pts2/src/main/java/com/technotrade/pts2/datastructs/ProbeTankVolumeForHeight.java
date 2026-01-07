package com.technotrade.pts2.datastructs;

/// <summary>
/// Tank volume for certain height based on tank’s calibration chart
/// </summary>
public class ProbeTankVolumeForHeight {
    private int mProbe;
    private int mHeight;
    private int mVolume;

    public ProbeTankVolumeForHeight() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ProbeTankVolumeForHeight create() {
        return new ProbeTankVolumeForHeight();
    }
    /// <summary>
    /// Probe getter and setter
    /// </summary>
    public int getProbe() { return mProbe; }
    public void setProbe(int probe) { mProbe = probe; }
    /// <summary>
    /// Height getter and setter
    /// </summary>
    public int getHeight() { return mHeight; }
    public void setHeight(int height) { mHeight = height; }
    /// <summary>
    /// Volume getter and setter
    /// </summary>
    public int getVolume() { return mVolume; }
    public void setVolume(int volume) { mVolume = volume; }
}