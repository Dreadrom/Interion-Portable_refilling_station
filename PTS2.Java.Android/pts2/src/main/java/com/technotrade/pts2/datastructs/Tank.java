package com.technotrade.pts2.datastructs;

/// <summary>
/// Tank data
/// </summary>
public class Tank {
    private int mId;
    private int mFuelGradeId;
    private int mHeight;
    private boolean mCriticalHighProductAlarmHeightEnabled;
    private int mCriticalHighProductAlarmHeight;
    private boolean mHighProductAlarmHeightEnabled;
    private int mHighProductAlarmHeight;
    private boolean mLowProductAlarmHeightEnabled;
    private int mLowProductAlarmHeight;
    private boolean mCriticalLowProductAlarmHeightEnabled;
    private int mCriticalLowProductAlarmHeight;
    private boolean mHighWaterAlarmHeightEnabled;
    private int mHighWaterAlarmHeight;
    private boolean m_StopPumpsAtCriticalLowProductHeightEnabled;
    private boolean mStopPumpsAtCriticalLowProductHeight;

    public Tank() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static Tank create() {
        return new Tank();
    }

    /// <summary>
    /// Id getter and setter
    /// </summary>
    public int getId() { return mId; }
    public void setId(int id) { mId = id; }
    /// <summary>
    /// FuelGradeId getter and setter
    /// </summary>
    public int getFuelGradeId() { return mFuelGradeId; }
    public void setFuelGradeId(int fuelGradeId) { mFuelGradeId = fuelGradeId; }
    /// <summary>
    /// Height getter and setter
    /// </summary>
    public int getHeight() { return mHeight; }
    public void setHeight(int height) { mHeight = height; }
    /// <summary>
    /// CriticalHighProductAlarmHeightEnabled getter and setter
    /// </summary>
    public boolean getCriticalHighProductAlarmHeightEnabled() { return mCriticalHighProductAlarmHeightEnabled; }
    public void setCriticalHighProductAlarmHeightEnabled(boolean criticalHighProductAlarmHeightEnabled) { mCriticalHighProductAlarmHeightEnabled = criticalHighProductAlarmHeightEnabled; }
    /// <summary>
    /// CriticalHighProductAlarmHeight getter and setter
    /// </summary>
    public int getCriticalHighProductAlarmHeight() { return mCriticalHighProductAlarmHeight; }
    public void setCriticalHighProductAlarmHeight(int criticalHighProductAlarmHeight) { mCriticalHighProductAlarmHeight = criticalHighProductAlarmHeight; }
    /// <summary>
    /// HighProductAlarmHeightEnabled getter and setter
    /// </summary>
    public boolean getHighProductAlarmHeightEnabled() { return mHighProductAlarmHeightEnabled; }
    public void setHighProductAlarmHeightEnabled(boolean highProductAlarmHeightEnabled) { mHighProductAlarmHeightEnabled = highProductAlarmHeightEnabled; }
    /// <summary>
    /// HighProductAlarmHeight getter and setter
    /// </summary>
    public int getHighProductAlarmHeight() { return mHighProductAlarmHeight; }
    public void setHighProductAlarmHeight(int highProductAlarmHeight) { mHighProductAlarmHeight = highProductAlarmHeight; }
    /// <summary>
    /// LowProductAlarmHeightEnabled getter and setter
    /// </summary>
    public boolean getLowProductAlarmHeightEnabled() { return mLowProductAlarmHeightEnabled; }
    public void setLowProductAlarmHeightEnabled(boolean lowProductAlarmHeightEnabled) { mLowProductAlarmHeightEnabled = lowProductAlarmHeightEnabled; }
    /// <summary>
    /// LowProductAlarmHeight getter and setter
    /// </summary>
    public int getLowProductAlarmHeight() { return mLowProductAlarmHeight; }
    public void setLowProductAlarmHeight(int lowProductAlarmHeight) { mLowProductAlarmHeight = lowProductAlarmHeight; }
    /// <summary>
    /// CriticalLowProductAlarmHeightEnabled getter and setter
    /// </summary>
    public boolean getCriticalLowProductAlarmHeightEnabled() { return mCriticalLowProductAlarmHeightEnabled; }
    public void setCriticalLowProductAlarmHeightEnabled(boolean criticalLowProductAlarmHeightEnabled) { mCriticalLowProductAlarmHeightEnabled = criticalLowProductAlarmHeightEnabled; }
    /// <summary>
    /// CriticalLowProductAlarmHeight getter and setter
    /// </summary>
    public int getCriticalLowProductAlarmHeight() { return mCriticalLowProductAlarmHeight; }
    public void setCriticalLowProductAlarmHeight(int criticalLowProductAlarmHeight) { mCriticalLowProductAlarmHeight = criticalLowProductAlarmHeight; }
    /// <summary>
    /// HighWaterAlarmHeightEnabled getter and setter
    /// </summary>
    public boolean getHighWaterAlarmHeightEnabled() { return mHighWaterAlarmHeightEnabled; }
    public void setHighWaterAlarmHeightEnabled(boolean highWaterAlarmHeightEnabled) { mHighWaterAlarmHeightEnabled = highWaterAlarmHeightEnabled; }
    /// <summary>
    /// HighWaterAlarmHeight getter and setter
    /// </summary>
    public int getHighWaterAlarmHeight() { return mHighWaterAlarmHeight; }
    public void setHighWaterAlarmHeight(int highWaterAlarmHeight) { mHighWaterAlarmHeight = highWaterAlarmHeight; }
    /// <summary>
    /// StopPumpsAtCriticalLowProductHeightEnabled getter and setter
    /// </summary>
    public boolean getStopPumpsAtCriticalLowProductHeightEnabled() { return m_StopPumpsAtCriticalLowProductHeightEnabled; }
    public void setStopPumpsAtCriticalLowProductHeightEnabled(boolean stopPumpsAtCriticalLowProductHeightEnabled) { m_StopPumpsAtCriticalLowProductHeightEnabled = stopPumpsAtCriticalLowProductHeightEnabled; }
    /// <summary>
    /// StopPumpsAtCriticalLowProductHeight getter and setter
    /// </summary>
    public boolean getStopPumpsAtCriticalLowProductHeight() { return mStopPumpsAtCriticalLowProductHeight; }
    public void setStopPumpsAtCriticalLowProductHeight(boolean stopPumpsAtCriticalLowProductHeight) { mStopPumpsAtCriticalLowProductHeight = stopPumpsAtCriticalLowProductHeight; }
}