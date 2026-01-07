package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Tank;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// GetTanksConfiguration request.
/// Gets tanks configuration: tanks height and assignment to fuel grades.
/// It is assumed that tanks’ IDs completely correspond to probes’ IDs in meaning tank
/// with ID 1 corresponds probe with ID 1, tank with ID 2 corresponds to probe with ID 2
/// and so on
/// </summary>
public class GetTanksConfiguration extends BaseHTTPRequest<List<Tank>> {
    public static final String REQUEST_NAME = "GetTanksConfiguration";
    public static final String RESPONSE_NAME = "TanksConfiguration";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetTanksConfiguration() {
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

            if (!dataJSONObject.has("Tanks")) {
                throw new TTException("Error: response doesn't contain Tanks property", Result.PROTOCOL_ERROR);
            }

            JSONArray tanksArray = dataJSONObject.getJSONArray("Tanks");

            List<Tank> tanks = new ArrayList<Tank>();

            for (int tanksArrayIterator = 0 ; tanksArrayIterator < tanksArray.length(); tanksArrayIterator++) {
                Tank tank = new Tank();
                JSONObject tankJSONObject = tanksArray.getJSONObject(tanksArrayIterator);

                tank.setId(tanksArrayIterator);

                int id = tankJSONObject.getInt("Id");
                tank.setId(id);

                if (tankJSONObject.has("FuelGradeId"))
                {
                    int fuelGradeId = tankJSONObject.getInt("FuelGradeId");
                    tank.setFuelGradeId(fuelGradeId);
                }

                if (tankJSONObject.has("Height"))
                {
                    int height = tankJSONObject.getInt("Height");
                    tank.setHeight(height);
                }

                if (tankJSONObject.has("CriticalHighProductAlarmHeight"))
                {
                    int criticalHighProductAlarmHeight = tankJSONObject.getInt("CriticalHighProductAlarmHeight");
                    tank.setCriticalHighProductAlarmHeight(criticalHighProductAlarmHeight);
                    tank.setCriticalHighProductAlarmHeightEnabled(true);
                }

                if (tankJSONObject.has("HighProductAlarmHeight"))
                {
                    int highProductAlarmHeight = tankJSONObject.getInt("HighProductAlarmHeight");
                    tank.setHighProductAlarmHeight(highProductAlarmHeight);
                    tank.setHighProductAlarmHeightEnabled(true);
                }

                if (tankJSONObject.has("LowProductAlarmHeight"))
                {
                    int lowProductAlarmHeight = tankJSONObject.getInt("LowProductAlarmHeight");
                    tank.setLowProductAlarmHeight(lowProductAlarmHeight);
                    tank.setLowProductAlarmHeightEnabled(true);
                }

                if (tankJSONObject.has("CriticalLowProductAlarmHeight"))
                {
                    int criticalLowProductAlarmHeight = tankJSONObject.getInt("CriticalLowProductAlarmHeight");
                    tank.setCriticalLowProductAlarmHeight(criticalLowProductAlarmHeight);
                    tank.setCriticalLowProductAlarmHeightEnabled(true);
                }

                if (tankJSONObject.has("HighWaterAlarmHeight"))
                {
                    int highWaterAlarmHeight = tankJSONObject.getInt("HighWaterAlarmHeight");
                    tank.setHighWaterAlarmHeight(highWaterAlarmHeight);
                    tank.setHighWaterAlarmHeightEnabled(true);
                }

                if (tankJSONObject.has("StopPumpsAtCriticalLowProductHeight"))
                {
                    boolean stopPumpsAtCriticalLowProductHeight = tankJSONObject.getBoolean("StopPumpsAtCriticalLowProductHeight");
                    tank.setStopPumpsAtCriticalLowProductHeight(stopPumpsAtCriticalLowProductHeight);
                    tank.setStopPumpsAtCriticalLowProductHeightEnabled(true);
                }

                tanks.add(tank);
            }

            mResult = tanks;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
