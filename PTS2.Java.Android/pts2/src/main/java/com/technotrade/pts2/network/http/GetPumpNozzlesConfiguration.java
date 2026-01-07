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
/// GetPumpNozzlesConfiguration request.
/// Gets pump nozzles configuration: assignment of pump nozzles to fuel grades
/// </summary>
public class GetPumpNozzlesConfiguration extends BaseHTTPRequest<List<PumpNozzles>> {
    public static final String REQUEST_NAME = "GetPumpNozzlesConfiguration";
    public static final String RESPONSE_NAME = "PumpNozzlesConfiguration";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetPumpNozzlesConfiguration() {
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

            if (!dataJSONObject.has("PumpNozzles")) {
                throw new TTException("Error: response doesn't contain PumpNozzles property", Result.PROTOCOL_ERROR);
            }

            ArrayList<PumpNozzles> pumpNozzlesConfiguration = new ArrayList<PumpNozzles>();

            JSONArray pumpNozzlesJArray = dataJSONObject.getJSONArray("PumpNozzles");

            for (int i = 0; i < pumpNozzlesJArray.length(); ++i) {
                JSONObject pumpNozzlesObject = pumpNozzlesJArray.getJSONObject(i);
                PumpNozzles pumpNozzles = new PumpNozzles();

                if (pumpNozzlesObject.has("PumpId")) {
                    pumpNozzles.setPumpId(pumpNozzlesObject.getInt("PumpId"));
                }

                if (pumpNozzlesObject.has("FuelGradeIds")) {
                    JSONArray fuelGradeIdsJArray = pumpNozzlesObject.getJSONArray("FuelGradeIds");
                    ArrayList<Integer> fuelGradeIds = new ArrayList<Integer>();

                    for (int fuelGradeIdsIter = 0; fuelGradeIdsIter < fuelGradeIdsJArray.length(); ++fuelGradeIdsIter) {
                        fuelGradeIds.add(fuelGradeIdsJArray.getInt(fuelGradeIdsIter));
                    }
                    pumpNozzles.setFuelGradeIds(fuelGradeIds);
                }

                if (pumpNozzlesObject.has("TankIds")) {
                    JSONArray tankIdsJArray = pumpNozzlesObject.getJSONArray("TankIds");
                    ArrayList<Integer> tankIds = new ArrayList<Integer>();

                    for (int tankIdsIter = 0; tankIdsIter < tankIdsJArray.length(); ++tankIdsIter) {
                        tankIds.add(tankIdsJArray.getInt(tankIdsIter));
                    }
                    pumpNozzles.setTankIds(tankIds);
                    pumpNozzles.setTankIdsEnabled(true);
                }

                pumpNozzlesConfiguration.add(pumpNozzles);
            }

            mResult = pumpNozzlesConfiguration;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
