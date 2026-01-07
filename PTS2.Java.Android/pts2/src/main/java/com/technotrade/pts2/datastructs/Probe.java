package com.technotrade.pts2.datastructs;

/// <summary>
/// Probe data
/// </summary>
public class Probe {
	private int mId;
    private String mPort;
    private int mAddress;
	
    public Probe() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static Probe create() {
        return new Probe();
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
}