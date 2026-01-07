package com.technotrade.pts2.datastructs;

import java.util.ArrayList;

/// <summary>
/// Probes configuration data
/// </summary>
public class ProbesConfiguration {
	private ArrayList<ProbePort> mProbePorts;
    private ArrayList<Probe> mProbes;

	public ProbesConfiguration() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ProbesConfiguration create() {
        return new ProbesConfiguration();
    }
    /// <summary>
    /// List of probe ports getter and setter
    /// </summary>
    public ArrayList<ProbePort> getProbePorts() { return mProbePorts; }
    public void setProbePorts(ArrayList<ProbePort> probePorts) { mProbePorts = probePorts; }
    /// <summary>
    /// List of probes getter and setter
    /// </summary>
    public ArrayList<Probe> getProbes() { return mProbes; }
    public void setProbes(ArrayList<Probe> probes) { mProbes = probes; }
}