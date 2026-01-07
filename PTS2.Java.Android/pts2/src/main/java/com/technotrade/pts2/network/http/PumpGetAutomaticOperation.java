package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.PumpAutomaticOperation;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// PumpGetAutomaticOperation request
/// </summary>
public class PumpGetAutomaticOperation extends BaseHTTPRequest<PumpAutomaticOperation> {
    public static final String REQUEST_NAME = "PumpGetAutomaticOperation";
    public static final String RESPONSE_NAME = "PumpAutomaticOperation";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mPump = 0;

    public PumpGetAutomaticOperation(int pump) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPump = pump;
    }

    public int getPump() {
        return mPump;
    }

    public void setPump(int pump) {
        mPump = pump;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Pump", mPump);

        return super.requestJSON(requestData);
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

            PumpAutomaticOperation pumpAutomaticOperation = new PumpAutomaticOperation();

            if (!dataJSONObject.has("Pump")) {
                throw new TTException("Error: response doesn't contain Pump property", Result.PROTOCOL_ERROR);
            }

            pumpAutomaticOperation.setPump(dataJSONObject.getInt("Pump"));

            if (!dataJSONObject.has("State")) {
                throw new TTException("Error: response doesn't contain State property", Result.PROTOCOL_ERROR);
            }

            pumpAutomaticOperation.setState(dataJSONObject.getString("State"));

            mResult = pumpAutomaticOperation;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }

        return true;
    }
}
