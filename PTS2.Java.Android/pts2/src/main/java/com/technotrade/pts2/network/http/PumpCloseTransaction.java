package com.technotrade.pts2.network.http;

import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// PumpCloseTransaction request.
/// Closes transaction
/// </summary>
public class PumpCloseTransaction extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "PumpCloseTransaction";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    private int mPump = 0;
    private int mTransaction = 0;

    public PumpCloseTransaction(int pump, int transaction) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPump = pump;
        mTransaction = transaction;
    }

    public int getPump() {
        return mPump;
    }

    public void setPump(int pump) {
        mPump = pump;
    }

    public int getTransaction() {
        return mTransaction;
    }

    public void setTransaction(int transaction) {
        mTransaction = transaction;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Pump", mPump);
        requestData.put("Transaction", mTransaction);

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
