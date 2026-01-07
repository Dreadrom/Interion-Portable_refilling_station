package com.technotrade.pts2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;

import java.net.InetAddress;

/// <summary>
/// Network helper class
/// </summary>
public class NetworkHelper {
    /// <summary>
    /// Checks the connection
    /// </summary>
    /// <param name="context">Android activity context</param>
    /// <returns>boolean</returns>
    public static boolean isConnectedToInternet (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork ();
        if (activeNetwork != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities (activeNetwork);
            if (networkCapabilities != null) {
                return networkCapabilities.hasCapability (NetworkCapabilities.NET_CAPABILITY_INTERNET) && networkCapabilities.hasCapability (NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
        }
        return false;
    }
    /// <summary>
    /// Converts JSONArray that contains IP address to InetAddress instance
    /// </summary>
    /// <param name="ipAddressJArray">JArray that contains IP address</param>
    /// <returns>InetAddress instance</returns>
    public static InetAddress convertJArrayToIPAddress(JSONArray ipAddressJArray) throws TTException {
        try {
            String iPAddressStr = "";

            for (int i = 0; i < ipAddressJArray.length(); ++i)
            {
                iPAddressStr += ipAddressJArray.getString(i);
                if (i != ipAddressJArray.length() - 1)
                {
                    iPAddressStr += ".";
                }
            }

            InetAddress iPAddress = InetAddress.getByName(iPAddressStr);
            return iPAddress;
        }
        catch(Exception exception) {
            throw new TTException("Error: can't parse IpAddress, " + exception.getMessage(), Result.PROTOCOL_ERROR);
        }
    }
    /// <summary>
    /// Converts InetAddress instance to JArray that contains IP address
    /// </summary>
    /// <param name="iPAddress">InetAddress</param>
    /// <returns>JSONArray that contains IP address</returns>
    public static JSONArray convertIPAddressToJArray(InetAddress iPAddress) {
        JSONArray iPAdressJArray = new JSONArray();
        byte[] iPAddressBytes = iPAddress.getAddress();
        for (int i = 0; i < iPAddressBytes.length; ++i) {
            iPAdressJArray.put(iPAddressBytes[i]);
        }

        return iPAdressJArray;
    }
}
