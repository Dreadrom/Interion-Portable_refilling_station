package com.technotrade.pts2.network.http;

import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// GetConfigurationIdentifier request.
/// Gets configuration unique identifier, which is used to point if configuration was
/// changed anyhow: given identifier is assigned a new unique value at any change of
/// configuration, thus an application may know if the configuration was changed based
/// on value of this identifier
/// </summary>
public class GetConfigurationIdentifier extends BaseHTTPRequest<String> {
    public static final String REQUEST_NAME = "GetConfigurationIdentifier";
    public static final String RESPONSE_NAME = "ConfigurationIdentifier";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetConfigurationIdentifier() {
        super(REQUEST_NAME, RESPONSE_NAME);
    }
    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws JSONException, TTException {
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

            if (!dataJSONObject.has("Id")) {
                throw new TTException("Error: response doesn't contain Id property", Result.PROTOCOL_ERROR);
            }

            String id = dataJSONObject.getString("Id");

            mResult = id;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}