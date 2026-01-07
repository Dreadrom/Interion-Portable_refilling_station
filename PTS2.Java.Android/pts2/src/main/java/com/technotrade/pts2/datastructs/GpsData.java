package com.technotrade.pts2.datastructs;

import java.util.Date;

/// <summary>
/// GPS data
/// </summary>
public class GpsData {
    private String mStatus;
    private Date mDateTime;
    private String mLatitude;
    private String mNorthSouthIndicator;
    private String mLongitude;
    private String mEastWestIndicator;
    private double mSpeedOverGround;
    private double mCourseOverGround;
    private String mMode;

    public GpsData() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static GpsData create() {
        return new GpsData();
    }
    /// <summary>
    /// Status getter and setter
    /// </summary>
    public String getStatus() { return mStatus; }
    public void setStatus(String status) { mStatus = status; }
    /// <summary>
    /// DateTime getter and setter
    /// </summary>
    public Date getDateTime() { return mDateTime; }
    public void setDateTime(Date dateTime) { mDateTime = dateTime; }
    /// <summary>
    /// Latitude getter and setter
    /// </summary>
    public String getLatitude() { return mLatitude; }
    public void setLatitude(String latitude) { mLatitude = latitude; }
    /// <summary>
    /// NorthSouthIndicator getter and setter
    /// </summary>
    public String getNorthSouthIndicator() { return mNorthSouthIndicator; }
    public void setNorthSouthIndicator(String northSouthIndicator) { mNorthSouthIndicator = northSouthIndicator; }
    /// <summary>
    /// Longitude getter and setter
    /// </summary>
    public String getLongitude() { return mLongitude; }
    public void setLongitude(String longitude) { mLongitude = longitude; }
    /// <summary>
    /// EastWestIndicator getter and setter
    /// </summary>
    public String getEastWestIndicator() { return mEastWestIndicator; }
    public void setEastWestIndicator(String eastWestIndicator) { mEastWestIndicator = eastWestIndicator; }
    /// <summary>
    /// SpeedOverGround getter and setter
    /// </summary>
    public double getSpeedOverGround() { return mSpeedOverGround; }
    public void setSpeedOverGround(double speedOverGround) { mSpeedOverGround = speedOverGround; }
    /// <summary>
    /// CourseOverGround getter and setter
    /// </summary>
    public double getCourseOverGround() { return mCourseOverGround; }
    public void setCourseOverGround(double courseOverGround) { mCourseOverGround = courseOverGround; }
    /// <summary>
    /// Mode getter and setter
    /// </summary>
    public String getMode() { return mMode; }
    public void setMode(String mode) { mMode = mode; }
}