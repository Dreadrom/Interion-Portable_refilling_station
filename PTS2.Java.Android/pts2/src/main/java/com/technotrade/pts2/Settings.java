package com.technotrade.pts2;

import com.technotrade.pts2.enumeration.AuthenticationType;
import com.technotrade.pts2.enumeration.ProtocolSecurityType;

/// <summary>
/// A PTS device settings class
/// </summary>
public class Settings {

    protected AuthenticationType mAuthenticationType = AuthenticationType.DIGEST;
    protected ProtocolSecurityType mProtocolSecurityType = ProtocolSecurityType.HTTPS;
    protected int mTimeoutSeconds = 10;
    protected String mHost = "192.168.1.117";
    protected short mHttpPort = 80;
    protected short mHttpsPort = 443;
    protected String mLogin = "admin";
    protected String mPassword = "admin";
    protected String mCurrency = "$";

	public Settings() {
    }
    /// <summary>
    /// Authentication type getter and setter
    /// </summary>
    public AuthenticationType getAuthenticationType() {
        return mAuthenticationType;
    }
    public void setAuthenticationType(AuthenticationType authenticationType) {
        mAuthenticationType = authenticationType;
    }
    /// <summary>
    /// Protocol security type getter and setter
    /// </summary>
    public ProtocolSecurityType getProtocolSecurityType() {
        return mProtocolSecurityType;
    }
    public void setProtocolSecurityType(ProtocolSecurityType protocolSecurityType) {
        mProtocolSecurityType = protocolSecurityType;
    }
    /// <summary>
    /// Timeout getter and setter
    /// </summary>
    public int getTimeoutSeconds() {
        return mTimeoutSeconds;
    }
    public void setTimeoutSeconds(int timeoutSeconds) {
        mTimeoutSeconds = timeoutSeconds;
    }
    /// <summary>
    /// Host getter and setter
    /// </summary>
    public String getHost() {
        return mHost;
    }
    public void setHost(String host) {
        mHost = host;
    }
    /// <summary>
    /// HTTP port getter and setter
    /// </summary>
    public short getHttpPort() {
        return mHttpPort;
    }
    public void setHttpPort(short httpPort) {
        mHttpPort = httpPort;
    }
    /// <summary>
    /// HTTPS port getter and setter
    /// </summary>
    public short getHttpsPort() {
        return mHttpsPort;
    }
    public void setHttpsPort(short httpsPort) {
        mHttpsPort = httpsPort;
    }
    /// <summary>
    /// Login getter and setter
    /// </summary>
    public String getLogin() {
        return mLogin;
    }
    public void setLogin(String login) {
        mLogin = login;
    }
    /// <summary>
    /// Password getter and setter
    /// </summary>
    public String getPassword() {
        return mPassword;
    }
    public void setPassword(String password) {
        mPassword = password;
    }
}