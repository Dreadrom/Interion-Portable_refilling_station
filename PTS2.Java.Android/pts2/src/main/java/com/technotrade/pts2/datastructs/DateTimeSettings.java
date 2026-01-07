package com.technotrade.pts2.datastructs;

import androidx.annotation.NonNull;

import java.util.Date;

/// <summary>
/// Date and time settings
/// </summary>
public class DateTimeSettings implements Cloneable {
    private Date mDateTime;
    private boolean mAutoSynchronize;
    private int mUTCOffset;

    public DateTimeSettings() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static DateTimeSettings create() {
        return new DateTimeSettings();
    }
    /// <summary>
    /// DateTime getter and setter
    /// </summary>
    public Date getDate() {
        return mDateTime;
    }
    public void setDate(Date dateTime) {
        mDateTime = dateTime;
    }
    /// <summary>
    /// AutoSynchronize getter and setter
    /// </summary>
    public boolean getAutoSynchronize() {
        return mAutoSynchronize;
    }
    public void setAutoSynchronize(boolean autoSynchronize) {
        mAutoSynchronize = autoSynchronize;
    }
    /// <summary>
    /// UTCOffset getter and setter
    /// </summary>
    public int getUTCOffset() {
        return mUTCOffset;
    }
    public void setUTCOffset(int utcOffset) {
        mUTCOffset = utcOffset;
    }

    @NonNull
    @Override
    public DateTimeSettings clone() throws CloneNotSupportedException {
        return (DateTimeSettings) super.clone();
    }
}