package com.technotrade.pts2.datastructs;

import static com.technotrade.pts2.enumeration.PumpStatus.NONE;

import com.technotrade.pts2.enumeration.PumpStatus;

import java.util.ArrayList;

/// <summary>
/// Pump prices data
/// </summary>
public class PumpPrices extends PumpStatusBase {
    private ArrayList<Double> mPrices;

    /// <summary>
    /// Status getter
    /// </summary>
    @Override
    public PumpStatus getStatus() {
        mStatus = NONE;
        return mStatus;
    };
    /// <summary>
    /// Prices getter
    /// </summary>
    public ArrayList<Double> getPrices() { return mPrices; }
    /// <summary>
    /// Prices setter
    /// </summary>
    public void setPrices(ArrayList<Double> prices) { mPrices = prices; }
}