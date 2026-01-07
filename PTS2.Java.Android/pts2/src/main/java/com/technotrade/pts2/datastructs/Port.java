package com.technotrade.pts2.datastructs;

/// <summary>
/// Port data
/// </summary>
public class Port {
    private int mId;
    private int mProtocol;
    private int mBaudRate;

    public Port() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static Port create() {
        return new Port();
    }
    /// <summary>
    /// Id getter and setter
    /// </summary>
    public int getId() { return mId; }
    public void setId(int id) { mId = id; }
    /// <summary>
    /// Protocol getter and setter
    /// </summary>
    public int getProtocol() { return mProtocol; }
    public void setProtocol(int protocol) { mProtocol = protocol; }
    /// <summary>
    /// BaudRate getter and setter
    /// </summary>
    public int getBaudRate() { return mBaudRate; }
    public void setBaudRate(int baudRate) { mBaudRate = baudRate; }
}