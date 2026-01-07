package com.technotrade.pts2.datastructs;

/// <summary>
/// Parameter data
/// </summary>
public class Parameter {
    private String mDevice;
    private int mNumber;
    private int mAddress;
    private String mValue;

    public Parameter() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static Parameter create() {
        return new Parameter();
    }
    /// <summary>
    /// Device getter ans setter
    /// </summary>
    public String getDevice() { return mDevice; }
    public void setDevice(String device) { mDevice = device; }
    /// <summary>
    /// Number getter ans setter
    /// </summary>
    public int getNumber() { return mNumber; }
    public void setNumber(int number) { mNumber = number; }
    /// <summary>
    /// Address getter ans setter
    /// </summary>
    public int getAddress() { return mAddress; }
    public void setAddress(int address) { mAddress = address; }
    /// <summary>
    /// Value getter ans setter
    /// </summary>
    public String getValue() { return mValue; }
    public void setValue(String value) { mValue = value; }
}