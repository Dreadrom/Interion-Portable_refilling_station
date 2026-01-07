package com.technotrade.pts2.datastructs;

/// <summary>
/// Tag information data
/// </summary>
public class TagInformation {
    private String mTag;
    private String mName;
    private boolean mValid;
    private boolean mPresent;

    public TagInformation() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static TagInformation create() {
        return new TagInformation();
    }
    /// <summary>
    /// Tag getter and setter
    /// </summary>
    public String getTag() { return mTag; }
    public void setTag(String tag) { mTag = tag; }
    /// <summary>
    /// Name getter and setter
    /// </summary>
    public String getName() { return mName; }
    public void setName(String name) { mName = name; }
    /// <summary>
    /// Valid getter and setter
    /// </summary>
    public boolean getValid() { return mValid; }
    public void setValid(boolean valid) { mValid = valid; }
    /// <summary>
    /// Present getter and setter
    /// </summary>
    public boolean getPresent() { return mPresent; }
    public void setPresent(boolean present) { mPresent = present; }
}