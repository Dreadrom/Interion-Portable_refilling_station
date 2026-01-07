package com.technotrade.pts2.datastructs;

/// <summary>
/// User data
/// </summary>
public class User {
    private int mId;
    private String mLogin;
    private String mPassword;
    private Permissions mPermissions;

    public User() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static User create() {
        return new User();
    }
    /// <summary>
    /// Id getter and setter
    /// </summary>
    public int getId() { return mId; }
    public void setId(int id) { mId = id; }
    /// <summary>
    /// Login getter and setter
    /// </summary>
    public String getLogin() { return mLogin; }
    public void setLogin(String login) { mLogin = login; }
    /// <summary>
    /// Password getter and setter
    /// </summary>
    public String getPassword() { return mPassword; }
    public void setPassword(String password) { mPassword = password; }
    /// <summary>
    /// Permissions getter and setter
    /// </summary>
    public Permissions getPermissions() { return mPermissions; }
    public void setPermissions(Permissions permissions) { mPermissions = permissions; }
}