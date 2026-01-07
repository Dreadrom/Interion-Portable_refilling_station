package com.technotrade.pts2.datastructs;

import java.util.ArrayList;

/// <summary>
/// Pump nozzles data
/// </summary>
public class PumpNozzles {
    private int mPumpId;
    private ArrayList<Integer> mFuelGradeIds;
    private boolean mTankIdsEnabled;
    private ArrayList<Integer> mTankIds;

    public PumpNozzles() {
		mPumpId = 0;
		mTankIdsEnabled = false;
	}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static PumpNozzles create() {
        return new PumpNozzles();
    }
    /// <summary>
    /// PumpId getter and setter
    /// </summary>
    public int getPumpId() { return mPumpId; }
    public void setPumpId(int pumpId) { mPumpId = pumpId; }
    /// <summary>
    /// FuelGradeIds getter and setter
    /// </summary>
    public ArrayList<Integer> getFuelGradeIds() { return mFuelGradeIds; }
    public void setFuelGradeIds(ArrayList<Integer> fuelGradeIds) { mFuelGradeIds = fuelGradeIds; }
    /// <summary>
    /// TankIdsEnabled getter and setter
    /// </summary>
    public boolean getTankIdsEnabled() { return mTankIdsEnabled; }
    public void setTankIdsEnabled(boolean tankIdsEnabled) { mTankIdsEnabled = tankIdsEnabled; }
    /// <summary>
    /// TankIds getter and setter
    /// </summary>
    public ArrayList<Integer> getTankIds() { return mTankIds; }
    public void setTankIds(ArrayList<Integer> tankIds) { mTankIds = tankIds; }
}