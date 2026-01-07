package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.PtsNetworkSettings;
import com.technotrade.pts2.enumeration.AuthenticationType;
import com.technotrade.pts2.enumeration.ProtocolSecurityType;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;
import com.technotrade.pts2.util.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// GetPtsNetworkSettings request.
/// Gets PTS controller network settings: IP-address, network mask and gateway
/// </summary>
public class GetPtsNetworkSettings extends BaseHTTPRequest<PtsNetworkSettings> {
    public static final String REQUEST_NAME = "GetPtsNetworkSettings";
    public static final String RESPONSE_NAME = "PtsNetworkSettings";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetPtsNetworkSettings() {
        super(REQUEST_NAME, RESPONSE_NAME);
    }
    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        return super.requestJSON();
    }
    /// <summary>
    /// ParseJSON overridden method. Parsing the response JSON data.
    /// </summary>
    /// <param name="jObject">JSON object</param>
    /// <returns>True is response have no errors</returns>
    @Override
    public boolean parseJSON(JSONObject jsonObject) throws TTException {
        try {
            super.parseJSON(jsonObject);

            if (isError()) {
                throw new TTException("Error: response error", Result.PROTOCOL_ERROR);
            }

            if (!jsonObject.has("Data")) {
                throw new TTException("Error: response doesn't contain Data property", Result.PROTOCOL_ERROR);
            }

            JSONObject dataJSONObject = jsonObject.getJSONObject("Data");

            if (!dataJSONObject.has("IpAddress")) {
                throw new TTException("Error: response doesn't contain IpAddress property", Result.PROTOCOL_ERROR);
            }

            PtsNetworkSettings ptsNetworkSettings = new PtsNetworkSettings();

            JSONArray ipAddressJArray = dataJSONObject.getJSONArray("IpAddress");
            ptsNetworkSettings.setIPAddress(NetworkHelper.convertJArrayToIPAddress(ipAddressJArray));

            if (!dataJSONObject.has("NetMask")) {
                throw new TTException("Error: response doesn't contain NetMask property", Result.PROTOCOL_ERROR);
            }

            JSONArray netMaskJArray = dataJSONObject.getJSONArray("NetMask");
            ptsNetworkSettings.setNetMask(NetworkHelper.convertJArrayToIPAddress(netMaskJArray));

            if (!dataJSONObject.has("Gateway")) {
                throw new TTException("Error: response doesn't contain Gateway property", Result.PROTOCOL_ERROR);
            }

            JSONArray gatewayJArray = dataJSONObject.getJSONArray("Gateway");
            ptsNetworkSettings.setGateway(NetworkHelper.convertJArrayToIPAddress(gatewayJArray));

            if (!dataJSONObject.has("HttpPort")) {
                throw new TTException("Error: response doesn't contain HttpPort property", Result.PROTOCOL_ERROR);
            }

            ptsNetworkSettings.setHttpPort((short)dataJSONObject.getInt("HttpPort"));

            if (!dataJSONObject.has("HttpsPort")) {
                throw new TTException("Error: response doesn't contain HttpsPort property", Result.PROTOCOL_ERROR);
            }

            ptsNetworkSettings.setHttpsPort((short)dataJSONObject.getInt("HttpsPort"));

            if (!dataJSONObject.has("Dns1")) {
                throw new TTException("Error: response doesn't contain Dns1 property", Result.PROTOCOL_ERROR);
            }

            JSONArray dns1JArray = dataJSONObject.getJSONArray("Dns1");
            ptsNetworkSettings.setDNS1(NetworkHelper.convertJArrayToIPAddress(dns1JArray));

            if (!dataJSONObject.has("Dns2")) {
                throw new TTException("Error: response doesn't contain Dns2 property", Result.PROTOCOL_ERROR);
            }

            JSONArray dns2JArray = dataJSONObject.getJSONArray("Dns2");
            ptsNetworkSettings.setDNS2(NetworkHelper.convertJArrayToIPAddress(dns2JArray));

            if (dataJSONObject.has("UsedProtocolType")) {
                ptsNetworkSettings.setUsedProtocolType(ProtocolSecurityType.valueOf(dataJSONObject.getString("UsedProtocolType")));
            }

            if (dataJSONObject.has("UsedAuthenticationType")) {
                ptsNetworkSettings.setUsedAuthenticationType(AuthenticationType.valueOf(dataJSONObject.getString("UsedAuthenticationType")));
            }

            mResult = ptsNetworkSettings;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}