package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.SystemDecimalDigits;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// GetSystemDecimalDigits request.
/// Gets system decimal digits settings
/// </summary>
public class GetSystemDecimalDigits extends BaseHTTPRequest<SystemDecimalDigits> {
    public static final String REQUEST_NAME = "GetSystemDecimalDigits";
    public static final String RESPONSE_NAME = "SystemDecimalDigits";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetSystemDecimalDigits() {
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

            SystemDecimalDigits systemDecimalDigits = new SystemDecimalDigits();

            if (!dataJSONObject.has("Price")) {
                throw new TTException("Error: response doesn't contain Price property", Result.PROTOCOL_ERROR);
            }

            systemDecimalDigits.setPrice((short)dataJSONObject.getInt("Price"));

            if (!dataJSONObject.has("Amount")) {
                throw new TTException("Error: response doesn't contain Amount property", Result.PROTOCOL_ERROR);
            }

            systemDecimalDigits.setAmount((short)dataJSONObject.getInt("Amount"));

            if (!dataJSONObject.has("Volume")) {
                throw new TTException("Error: response doesn't contain Volume property", Result.PROTOCOL_ERROR);
            }

            systemDecimalDigits.setVolume((short)dataJSONObject.getInt("Volume"));

            if (!dataJSONObject.has("AmountTotal")) {
                throw new TTException("Error: response doesn't contain AmountTotal property", Result.PROTOCOL_ERROR);
            }

            systemDecimalDigits.setAmountTotal((short)dataJSONObject.getInt("AmountTotal"));

            if (!dataJSONObject.has("VolumeTotal")) {
                throw new TTException("Error: response doesn't contain VolumeTotal property", Result.PROTOCOL_ERROR);
            }

            systemDecimalDigits.setVolumeTotal((short)dataJSONObject.getInt("VolumeTotal"));

            mResult = systemDecimalDigits;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
