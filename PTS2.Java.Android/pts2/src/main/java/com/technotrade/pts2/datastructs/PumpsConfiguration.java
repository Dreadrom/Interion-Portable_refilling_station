package com.technotrade.pts2.datastructs;

import java.util.ArrayList;

/// <summary>
/// Pump configuration data
/// </summary>
public class PumpsConfiguration {
    private ArrayList<Port> mPorts;
    private ArrayList<Pump> mPumps;

	public PumpsConfiguration() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static PumpsConfiguration create() {
        return new PumpsConfiguration();
    }
    /// <summary>
    /// List of pump ports getter and setter
    /// </summary>
    public ArrayList<Port> getPorts() { return mPorts; }
    public void setPorts(ArrayList<Port> ports) { mPorts = ports; }
    /// <summary>
    /// List of pumps getter and setter
    /// </summary>
    public ArrayList<Pump> getPumps() { return mPumps; }
    public void setPumps(ArrayList<Pump> pumps) { mPumps = pumps; }
}