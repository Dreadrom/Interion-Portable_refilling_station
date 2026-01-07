package com.technotrade.pts2.datastructs;

/// <summary>
/// Reader port data
/// </summary>
public class ReaderPort {
	private String mId;
    private int mProtocol;
    private int mBaudRate;

    public ReaderPort() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ReaderPort create() {
        return new ReaderPort();
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