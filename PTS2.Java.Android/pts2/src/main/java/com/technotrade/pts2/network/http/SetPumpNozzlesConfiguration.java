package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.PumpNozzles;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// SetPumpNozzlesConfiguration request
/// </summary>
public class SetPumpNozzlesConfiguration extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetPumpNozzlesConfiguration";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public List<PumpNozzles> mPumpNozzlesConfiguration;

    public SetPumpNozzlesConfiguration(List<PumpNozzles> pumpNozzlesConfiguration) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPumpNozzlesConfiguration = pumpNozzlesConfiguration;
    }

    public List<PumpNozzles> getPumpNozzlesConfiguration() {
        return mPumpNozzlesConfiguration;
    }

    public void setPumpNozzlesConfiguration(List<PumpNozzles> pumpNozzlesConfiguration) {
        mPumpNozzlesConfiguration = pumpNozzlesConfiguration;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mPumpNozzlesConfiguration == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONArray pumpNozzlesConfigurationJArray = new JSONArray();

        for (int i = 0; i < mPumpNozzlesConfiguration.size(); ++i) {
            PumpNozzles pumpNozzles = mPumpNozzlesConfiguration.get(i);

            JSONObject pumpNozzlesJObject = new JSONObject();
            pumpNozzlesJObject.put("PumpId", pumpNozzles.getPumpId());

            ArrayList<Integer> fuelGradeIds = pumpNozzles.getFuelGradeIds();

            if (fuelGradeIds != null) {
                JSONArray fuelGradeIdsJArray = new JSONArray();

                for (int fuelGradeIdsIter = 0; fuelGradeIdsIter < fuelGradeIds.size(); ++fuelGradeIdsIter) {
                    fuelGradeIdsJArray.put(fuelGradeIds.get(fuelGradeIdsIter));
                }

                pumpNozzlesJObject.put("FuelGradeIds", fuelGradeIdsJArray);
            }

            if (pumpNozzles.getTankIdsEnabled()) {

                ArrayList<Integer> tankIds = pumpNozzles.getTankIds();

                if (tankIds != null) {
                    JSONArray tankIdsJArray = new JSONArray();

                    for (int tankIdsIter = 0; tankIdsIter < tankIds.size(); ++tankIdsIter) {
                        tankIdsJArray.put(tankIds.get(tankIdsIter));
                    }

                    pumpNozzlesJObject.put("TankIds", tankIdsJArray);
                }
            }

            pumpNozzlesConfigurationJArray.put(pumpNozzlesJObject);
        }

        JSONObject requestData = new JSONObject();
        requestData.put("PumpNozzles", pumpNozzlesConfigurationJArray);

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
