package com.technotrade.pts2.datastructs;

import java.util.ArrayList;

/// <summary>
/// Price board configuration data
/// </summary>
public class PriceBoardsConfiguration {
    private ArrayList<PriceBoardPort> mPriceBoardPorts;
    private ArrayList<PriceBoard> mPriceBoards;

    public PriceBoardsConfiguration() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static PriceBoardsConfiguration create() {
        return new PriceBoardsConfiguration();
    }
    /// <summary>
    /// List of price board ports getter and setter
    /// </summary>
    public ArrayList<PriceBoardPort> getPriceBoardPorts() { return mPriceBoardPorts; }
    public void setPriceBoardPorts(ArrayList<PriceBoardPort> priceBoardPorts) { mPriceBoardPorts = priceBoardPorts; }
    /// <summary>
    /// List of price boards getter and setter
    /// </summary>
    public ArrayList<PriceBoard> getPriceBoards() { return mPriceBoards; }
    public void setPriceBoards(ArrayList<PriceBoard> priceBoards) { mPriceBoards = priceBoards; }
}