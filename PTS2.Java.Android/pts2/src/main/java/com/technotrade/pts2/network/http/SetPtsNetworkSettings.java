package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.PtsNetworkSettings;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;
import com.technotrade.pts2.util.NetworkHelper;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// SetPtsNetworkSettings request
/// </summary>
public class SetPtsNetworkSettings extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetPtsNetworkSettings";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private PtsNetworkSettings mPtsNetworkSettings;

    public SetPtsNetworkSettings(PtsNetworkSettings ptsNetworkSettings) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPtsNetworkSettings = ptsNetworkSettings;
    }

    public PtsNetworkSettings getPtsNetworkSettings() {
        return mPtsNetworkSettings;
    }

    public void setPtsNetworkSettings(PtsNetworkSettings ptsNetworkSettings) {
        mPtsNetworkSettings = ptsNetworkSettings;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mPtsNetworkSettings == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONObject requestData = new JSONObject();

        requestData.put("IpAddress", NetworkHelper.convertIPAddressToJArray(mPtsNetworkSettings.getIPAddress()));
        requestData.put("NetMask", NetworkHelper.convertIPAddressToJArray(mPtsNetworkSettings.getNetMask()));
        requestData.put("Gateway", NetworkHelper.convertIPAddressToJArray(mPtsNetworkSettings.getGateway()));
        requestData.put("HttpPort", mPtsNetworkSettings.getHttpPort());
        requestData.put("HttpsPort", mPtsNetworkSettings.getHttpsPort());
        requestData.put("Dns1", NetworkHelper.convertIPAddressToJArray(mPtsNetworkSettings.getDNS1()));
        requestData.put("Dns2", NetworkHelper.convertIPAddressToJArray(mPtsNetworkSettings.getDNS2()));

        return super.requestJSON(requestData);
    }

    /// <summary>
    /// ParseJSON overridden method. Parsing the response JSON data.
    /// </summary>
    /// <param name="jObject">JSON object</param>
    /// <returns>True is response have no errors</returns>
    @Override
    public boolean parseJSON(JSONObject jsonObject) throws TTException, JSONException {
        mResult = super.parseJSON(jsonObject);
        return mResult;
    }
}
