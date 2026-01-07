package com.technotrade.pts2.datastructs;

/// <summary>
/// Price board port data
/// </summary>
public class PriceBoardPort {
    private String mId;
    private int mProtocol;
    private int mBaudRate;
	
    public PriceBoardPort() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static PriceBoardPort create() {
        return new PriceBoardPort();
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