package com.technotrade.pts2.datastructs;

import java.util.ArrayList;

/// <summary>
/// Price board data
/// </summary>
public class PriceBoard {
    private int mId;
    private String mPort;
    private int mAddress;
    private ArrayList<Integer> mFuelGradeIds;
	
    public PriceBoard() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static PriceBoard create() {
        return new PriceBoard();
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
    /// FuelGradeIds getter and setter
    /// </summary>
    public ArrayList<Integer> getFuelGradeIds() { return mFuelGradeIds; }
    public void setFuelGradeIds(ArrayList<Integer> fuelGradeIds) { mFuelGradeIds = fuelGradeIds; }
}