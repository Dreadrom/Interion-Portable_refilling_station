package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.DateTimeSettings;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/// <summary>
/// GetDateTime request.
/// Gets date and time
/// </summary>
public class GetDateTime extends BaseHTTPRequest<DateTimeSettings> {

    public static final String REQUEST_NAME = "GetDateTime";
    public static final String RESPONSE_NAME = "DateTime";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetDateTime() {
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

            DateTimeSettings dateTimeSettings = new DateTimeSettings();

            if (!dataJSONObject.has("DateTime")) {
                throw new TTException("Error: response doesn't contain DateTime property", Result.PROTOCOL_ERROR);
            }

            String dateTimeStr = dataJSONObject.getString("DateTime");

            String pattern = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
            Date dateTime = formatter.parse(dateTimeStr);

            dateTimeSettings.setDate(dateTime);

            if (!dataJSONObject.has("AutoSynchronize")) {
                throw new TTException("Error: response doesn't contain AutoSynchronize property", Result.PROTOCOL_ERROR);
            }

            dateTimeSettings.setAutoSynchronize(dataJSONObject.getBoolean("AutoSynchronize"));

            if (!dataJSONObject.has("UTCOffset")) {
                throw new TTException("Error: response doesn't contain UTCOffset property", Result.PROTOCOL_ERROR);
            }

            dateTimeSettings.setUTCOffset(dataJSONObject.getInt("UTCOffset"));

            mResult = dateTimeSettings;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}