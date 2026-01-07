package com.technotrade.pts2.datastructs;

import java.util.ArrayList;
import java.util.Date;

/// <summary>
/// Firmware information
/// </summary>
public class FirmwareInformation {
    private Date mDateTime;
    private ArrayList<Integer> mPumpProtocols;
    private ArrayList<Integer> mProbeProtocols;
    private ArrayList<Integer> mPriceBoardProtocols;
    private ArrayList<Integer> mReaderProtocols;

    public FirmwareInformation() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static FirmwareInformation create() {
        return new FirmwareInformation();
    }
    /// <summary>
    /// DateTime getter and setter
    /// </summary>
    public Date getDateTime() { return mDateTime; }
    public void setDateTime(Date dateTime) { mDateTime = dateTime; }
    /// <summary>
    /// PumpProtocols getter and setter
    /// </summary>
    public ArrayList<Integer> getPumpProtocols() { return mPumpProtocols; }
    public void setPumpProtocols(ArrayList<Integer> pumpProtocols) { mPumpProtocols = pumpProtocols; }
    /// <summary>
    /// ProbeProtocols getter and setter
    /// </summary>
    public ArrayList<Integer> getProbeProtocols() { return mProbeProtocols; }
    public void setProbeProtocols(ArrayList<Integer> probeProtocols) { mProbeProtocols = probeProtocols; }
    /// <summary>
    /// PriceBoardProtocols getter and setter
    /// </summary>
    public ArrayList<Integer> getPriceBoardProtocols() { return mPriceBoardProtocols; }
    public void setPriceBoardProtocols(ArrayList<Integer> priceBoardProtocols) { mPriceBoardProtocols = priceBoardProtocols; }
    /// <summary>
    /// ReaderProtocols getter and setter
    /// </summary>
    public ArrayList<Integer> getReaderProtocols() { return mReaderProtocols; }
    public void setReaderProtocols(ArrayList<Integer> readerProtocols) { mReaderProtocols = readerProtocols; }
}