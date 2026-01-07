package com.technotrade.pts2.datastructs;

import java.util.ArrayList;

/// <summary>
/// Readers configuration data
/// </summary>
public class ReadersConfiguration {
	private ArrayList<ReaderPort> mReaderPorts;
    private ArrayList<Reader> mReaders;

    public ReadersConfiguration() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ReadersConfiguration create() {
        return new ReadersConfiguration();
    }
    /// <summary>
    /// List of reader ports getter and setter
    /// </summary>
    public ArrayList<ReaderPort> getReaderPorts() { return mReaderPorts; }
    public void setReaderPorts(ArrayList<ReaderPort> readerPorts) { mReaderPorts = readerPorts; }
    /// <summary>
    /// List of readers getter and setter
    /// </summary>
    public ArrayList<Reader> getReaders() { return mReaders; }
    public void setReaders(ArrayList<Reader> readers) { mReaders = readers; }
}