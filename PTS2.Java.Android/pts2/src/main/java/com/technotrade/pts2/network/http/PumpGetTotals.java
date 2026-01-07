package com.technotrade.pts2.network.http;

import com.technotrade.pts2.enumeration.NozzleOrFuelIdSelector;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// PumpGetTotals request
/// </summary>
public class PumpGetTotals extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "PumpGetTotals";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mPump = 0;
    public int mNozzle;
    public int mFuelGradeId;
    public NozzleOrFuelIdSelector mNozzleOrFielIdSelector;

    public PumpGetTotals(int pump, int nozzle, int fuelGradeId, NozzleOrFuelIdSelector nozzleOrFielIdSelector) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPump = pump;
        mNozzle = nozzle;
        mFuelGradeId = fuelGradeId;
        mNozzleOrFielIdSelector = nozzleOrFielIdSelector;
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

    public int getFuelGradeId() {
        return mFuelGradeId;
    }

    public void setFuelGradeId(int fuelGradeId) {
        mFuelGradeId = fuelGradeId;
    }

    public NozzleOrFuelIdSelector getNozzleOrFuelIdSelector() {
        return mNozzleOrFielIdSelector;
    }

    public void setNozzleOrFuelIdSelector(NozzleOrFuelIdSelector nozzleOrFuelIdSelector) {
        mNozzleOrFielIdSelector = nozzleOrFuelIdSelector;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Pump", mPump);

        switch (mNozzleOrFielIdSelector)
        {
            case NOZZLE:
                requestData.put("Nozzle", mNozzle);
                break;
            case FUELGRADEID:
                requestData.put("FuelGradeId", mFuelGradeId);
                break;
            default:
                requestData.put("Nozzle", mNozzle);
                requestData.put("FuelGradeId", mFuelGradeId);
                break;
        }

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
