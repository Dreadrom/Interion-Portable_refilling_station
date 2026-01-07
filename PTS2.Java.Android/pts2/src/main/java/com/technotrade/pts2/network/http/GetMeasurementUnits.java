package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.MeasurementUnits;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// GetMeasurementUnits request.
/// Gets measurement units used for graphical user interface
/// </summary>
public class GetMeasurementUnits extends BaseHTTPRequest<MeasurementUnits> {
    public static final String REQUEST_NAME = "GetMeasurementUnits";
    public static final String RESPONSE_NAME = "MeasurementUnits";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetMeasurementUnits() {
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

            MeasurementUnits measurementUnits = new MeasurementUnits();

            if (!dataJSONObject.has("Volume")) {
                throw new TTException("Error: response doesn't contain Volume property", Result.PROTOCOL_ERROR);
            }

            measurementUnits.setVolume(dataJSONObject.getString("Volume"));

            if (dataJSONObject.has("Temperature")) {
                measurementUnits.setTemperature(dataJSONObject.getString("Temperature"));
            }

            mResult = measurementUnits;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
