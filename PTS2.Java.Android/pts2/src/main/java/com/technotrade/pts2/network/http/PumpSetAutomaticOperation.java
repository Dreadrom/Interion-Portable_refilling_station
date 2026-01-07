package com.technotrade.pts2.network.http;

import com.technotrade.pts2.enumeration.StateOnOff;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// PumpSetAutomaticOperation request
/// </summary>
public class PumpSetAutomaticOperation extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "PumpSetAutomaticOperation";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mPump = 0;
    private boolean mAutomaticPumpAuthorization;

    public PumpSetAutomaticOperation(int pump, boolean automaticPumpAuthorization) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPump = pump;
        mAutomaticPumpAuthorization = automaticPumpAuthorization;
    }

    public int getPump() {
        return mPump;
    }

    public void setPump(int pump) {
        mPump = pump;
    }

    public boolean getAutomaticPumpAuthorization() {
        return mAutomaticPumpAuthorization;
    }

    public void setAutomaticPumpAuthorization(boolean automaticPumpAuthorization) {
        mAutomaticPumpAuthorization = automaticPumpAuthorization;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Pump", mPump);
        requestData.put("State", mAutomaticPumpAuthorization ? StateOnOff.ON.getValue() : StateOnOff.OFF.getValue());

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
