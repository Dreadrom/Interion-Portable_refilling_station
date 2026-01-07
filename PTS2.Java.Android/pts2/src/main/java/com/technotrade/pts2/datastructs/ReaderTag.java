package com.technotrade.pts2.datastructs;

/// <summary>
/// Reader tag data
/// </summary>
public class ReaderTag {
    private int mReader;
    private String mTag;
    private boolean mOnline;
    private boolean mError;

    public ReaderTag() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ReaderTag create() {
        return new ReaderTag();
    }
    /// <summary>
    /// Reader getter and setter
    /// </summary>
    public int getReader() { return mReader; }
    public void setReader(int reader) { mReader = reader; }
    /// <summary>
    /// Tag getter and setter
    /// </summary>
    public String getTag() { return mTag; }
    public void setTag(String tag) { mTag = tag; }
    /// <summary>
    /// Online getter and setter
    /// </summary>
    public boolean getOnline() { return mOnline; }
    public void setOnline(boolean online) { mOnline = online; }
    /// <summary>
    /// Error getter and setter
    /// </summary>
    public boolean getError() { return mError; }
    public void setError(boolean error) { mError = error; }
}