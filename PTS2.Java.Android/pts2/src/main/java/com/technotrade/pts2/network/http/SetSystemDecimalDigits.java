package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.SystemDecimalDigits;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// SetSystemDecimalDigits request
/// </summary>
public class SetSystemDecimalDigits extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetSystemDecimalDigits";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private SystemDecimalDigits mSystemDecimalDigits;

    public SetSystemDecimalDigits(SystemDecimalDigits systemDecimalDigits) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mSystemDecimalDigits = systemDecimalDigits;
    }

    public SystemDecimalDigits getSystemDecimalDigits() {
        return mSystemDecimalDigits;
    }

    public void setSystemDecimalDigits(SystemDecimalDigits systemDecimalDigits) {
        mSystemDecimalDigits = systemDecimalDigits;
    }
    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mSystemDecimalDigits == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONObject requestData = new JSONObject();

        requestData.put("Price", mSystemDecimalDigits.getPrice());
        requestData.put("Amount", mSystemDecimalDigits.getAmount());
        requestData.put("Volume", mSystemDecimalDigits.getVolume());
        requestData.put("AmountTotal", mSystemDecimalDigits.getAmountTotal());
        requestData.put("VolumeTotal", mSystemDecimalDigits.getVolumeTotal());

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
