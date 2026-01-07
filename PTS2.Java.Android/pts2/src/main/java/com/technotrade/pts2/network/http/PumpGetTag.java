package com.technotrade.pts2.network.http;

import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// PumpGetTag request
/// </summary>
public class PumpGetTag extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "PumpGetTag";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mPump = 0;
    public int mNozzle;

    public PumpGetTag(int pump, int nozzle) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPump = pump;
        mNozzle = nozzle;
    }

    public int getPump() {
        return mPump;
    }

    public void setPump(int pump) {
        mPump = pump;
    }

    public int getNozzle() {
        return mNozzle;
    }

    public void setNozzle(int nozzle) {
        mNozzle = nozzle;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Pump", mPump);
        requestData.put("Nozzle", mNozzle);

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
