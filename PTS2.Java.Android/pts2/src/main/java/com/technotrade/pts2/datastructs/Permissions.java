package com.technotrade.pts2.datastructs;

/// <summary>
/// User permissions
/// </summary>
public class Permissions {
    private boolean mConfiguration;
    private boolean mControl;
    private boolean mMonitoring;
    private boolean mReports;

    public Permissions() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static Permissions create() {
        return new Permissions();
    }
    /// <summary>
    /// Configuration getter and setter
    /// </summary>
    public boolean getConfiguration() { return mConfiguration; }
    public void setConfiguration(boolean configuration) { mConfiguration = configuration; }
    /// <summary>
    /// Control getter and setter
    /// </summary>
    public boolean getControl() { return mControl; }
    public void setControl(boolean control) { mControl = control; }
    /// <summary>
    /// Monitoring getter and setter
    /// </summary>
    public boolean getMonitoring() { return mMonitoring; }
    public void setMonitoring(boolean monitoring) { mMonitoring = monitoring; }
    /// <summary>
    /// Reports getter and setter
    /// </summary>
    public boolean getReports() { return mReports; }
    public void setReports(boolean reports) { mReports = reports; }
}