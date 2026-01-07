package com.technotrade.pts2.datastructs;

import com.technotrade.pts2.enumeration.AuthenticationType;
import com.technotrade.pts2.enumeration.ProtocolSecurityType;

import java.net.InetAddress;

/// <summary>
/// PTS2 network settings data
/// </summary>
public class PtsNetworkSettings {
    private InetAddress mIPAddress;
    private InetAddress mNetMask;
    private InetAddress mGateway;
    private short mHTTPPort;
    private short mHTTPSPort;
    private InetAddress mDNS1;
    private InetAddress mDNS2;
    private ProtocolSecurityType mUsedProtocolType;
    private AuthenticationType mUsedAuthenticationType;

    public PtsNetworkSettings() {}
    /// <summary>
    /// Creates an instance
    /// </summary>
    public static PtsNetworkSettings create() {
        return new PtsNetworkSettings();
    }
    /// <summary>
    /// IPAddress getter and setter
    /// </summary>
    public InetAddress getIPAddress() { return mIPAddress; }
    public void setIPAddress(InetAddress ipAddress) { mIPAddress = ipAddress; }
    /// <summary>
    /// NetMask getter and setter
    /// </summary>
    public InetAddress getNetMask() { return mNetMask; }
    public void setNetMask(InetAddress netMask) { mNetMask = netMask; }
    /// <summary>
    /// Gateway getter and setter
    /// </summary>
    public InetAddress getGateway() { return mGateway; }
    public void setGateway(InetAddress gateway) { mGateway = gateway; }
    /// <summary>
    /// HttpPort getter and setter
    /// </summary>
    public short getHttpPort() { return mHTTPPort; }
    public void setHttpPort(short httpPort) { mHTTPPort = httpPort; }
    /// <summary>
    /// HttpsPort getter and setter
    /// </summary>
    public short getHttpsPort() { return mHTTPSPort; }
    public void setHttpsPort(short httpsPort) { mHTTPSPort = httpsPort; }
    /// <summary>
    /// DNS1 getter and setter
    /// </summary>
    public InetAddress getDNS1() { return mDNS1; }
    public void setDNS1(InetAddress dns1) { mDNS1 = dns1; }
    /// <summary>
    /// DNS2 getter and setter
    /// </summary>
    public InetAddress getDNS2() { return mDNS2; }
    public void setDNS2(InetAddress dns2) { mDNS2 = dns2; }
    /// <summary>
    /// UsedProtocolType getter and setter
    /// </summary>
    public ProtocolSecurityType getUsedProtocolType() { return mUsedProtocolType; }
    public void setUsedProtocolType(ProtocolSecurityType usedProtocolType) { mUsedProtocolType = usedProtocolType; }
    /// <summary>
    /// UsedAuthenticationType getter and setter
    /// </summary>
    public AuthenticationType getUsedAuthenticationType() { return mUsedAuthenticationType; }
    public void setUsedAuthenticationType(AuthenticationType usedAuthenticationType) { mUsedAuthenticationType = usedAuthenticationType; }
}