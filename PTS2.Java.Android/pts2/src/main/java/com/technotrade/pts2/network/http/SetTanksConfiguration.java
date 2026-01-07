package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Tank;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/// <summary>
/// SetTanksConfiguration request
/// </summary>
public class SetTanksConfiguration extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetTanksConfiguration";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    public List<Tank> mTanks;

    public SetTanksConfiguration(List<Tank> tanks) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mTanks = tanks;
    }

    public List<Tank> getTanks() {
        return mTanks;
    }

    public void setTanks(List<Tank> tanks) {
        mTanks = tanks;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mTanks == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONArray tanksJArray = new JSONArray();

        for (int i = 0; i < mTanks.size(); ++i) {
            Tank tank = mTanks.get(i);

            JSONObject tankJObject = new JSONObject();
            tankJObject.put("Id", tank.getId());
            tankJObject.put("FuelGradeId", tank.getFuelGradeId());
            tankJObject.put("Height", tank.getHeight());

            if (tank.getHighProductAlarmHeightEnabled()) {
                tankJObject.put("HighProductAlarmHeight", tank.getHighProductAlarmHeight());
            }

            if (tank.getLowProductAlarmHeightEnabled()) {
                tankJObject.put("LowProductAlarmHeight", tank.getLowProductAlarmHeight());
            }

            if (tank.getCriticalLowProductAlarmHeightEnabled()) {
                tankJObject.put("CriticalLowProductAlarmHeight", tank.getCriticalLowProductAlarmHeight());
            }

            if (tank.getHighWaterAlarmHeightEnabled()) {
                tankJObject.put("HighWaterAlarmHeight", tank.getHighWaterAlarmHeight());
            }

            if (tank.getStopPumpsAtCriticalLowProductHeightEnabled()) {
                tankJObject.put("StopPumpsAtCriticalLowProductHeight", tank.getStopPumpsAtCriticalLowProductHeight());
            }

            tanksJArray.put(tankJObject);
        }

        JSONObject requestData = new JSONObject();
        requestData.put("Tanks", tanksJArray);

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
