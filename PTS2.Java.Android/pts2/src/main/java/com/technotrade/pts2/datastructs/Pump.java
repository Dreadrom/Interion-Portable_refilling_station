package com.technotrade.pts2.datastructs;

/// <summary>
/// Pump data
/// </summary>
public class Pump {
    private int mId;
    private int mPort;
    private int mAddress;

    public Pump() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static Pump create() {
        return new Pump();
    }
    /// <summary>
    /// Id getter and setter
    /// </summary>
    public int getId() { return mId; }
    public void setId(int id) { mId = id; }
    /// <summary>
    /// Port getter and setter
    /// </summary>
    public int getPort() { return mPort; }
    public void setPort(int port) { mPort = port; }
    /// <summary>
    /// Address getter and setter
    /// </summary>
    public int getAddress() { return mAddress; }
    public void setAddress(int address) { mAddress = address; }
}