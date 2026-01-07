package com.technotrade.pts2.datastructs;

/// <summary>
/// Probe port data
/// </summary>
public class ProbePort {
    private String mId;
    private int mProtocol;
    private int mBaudRate;

    public ProbePort() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ProbePort create() {
        return new ProbePort();
    }
    /// <summary>
    /// Id getter and setter
    /// </summary>
    public String getId() { return mId; }
    public void setId(String id) { mId = id; }
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