package com.technotrade.pts2.datastructs;

/// <summary>
/// Reader data
/// </summary>
public class Reader {
	private int mId;
    private String mPort;
    private int mAddress;
    private int mPumpId;
    private boolean mAnyPump;

    public Reader() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static Reader create() {
        return new Reader();
    }
    /// <summary>
    /// Id getter and setter
    /// </summary>
    public int getId() { return mId; }
    public void setId(int id) { mId = id; }
    /// <summary>
    /// Port getter and setter
    /// </summary>
    public String getPort() { return mPort; }
    public void setPort(String port) { mPort = port; }
    /// <summary>
    /// Address getter and setter
    /// </summary>
    public int getAddress() { return mAddress; }
    public void setAddress(int address) { mAddress = address; }
    /// <summary>
    /// PumpId getter and setter
    /// </summary>
    public int getPumpId() { return mPumpId; }
    public void setPumpId(int pumpId) { mPumpId = pumpId; }
    /// <summary>
    /// AnyPump getter and setter
    /// </summary>
    public boolean getAnyPump() { return mAnyPump; }
    public void setAnyPump(boolean anyPump) { mAnyPump = anyPump; }
}