package com.technotrade.pts2.datastructs;

/// <summary>
/// Error data
/// </summary>
public class ErrorData {
    private int mPump;
    private String mUser;
    private String mRequest;

    public ErrorData() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static ErrorData create() {
        return new ErrorData();
    }
    /// <summary>
    /// Id getter and setter
    /// </summary>
    public int getPump() { return mPump; }
    public void setPump(int pump) { mPump = pump; }
    /// <summary>
    /// Port getter and setter
    /// </summary>
    public String getUser() { return mUser; }
    public void setUser(String user) { mUser = user; }
    /// <summary>
    /// Address getter and setter
    /// </summary>
    public String getRequest() { return mRequest; }
    public void setRequest(String request) { mRequest = request; }
}