package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.DateTimeSettings;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

/// <summary>
/// SetDateTime request.
/// Sets date and time
/// </summary>
public class SetDateTime extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetDateTime";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    private DateTimeSettings mDateTimeSettings;

    public SetDateTime(DateTimeSettings dateTimeSettings) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mDateTimeSettings = dateTimeSettings;
    }

    public DateTimeSettings getDateTimeSettings() {
        return mDateTimeSettings;
    }

    public void setDateTimeSettings(DateTimeSettings dateTimeSettings) {
        mDateTimeSettings = dateTimeSettings;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mDateTimeSettings == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONObject requestData = new JSONObject();

        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        String dateTimeString = sdf.format(mDateTimeSettings.getDate());

        requestData.put("DateTime", dateTimeString);
        requestData.put("AutoSynchronize", mDateTimeSettings.getAutoSynchronize());
        requestData.put("UTCOffset", mDateTimeSettings.getUTCOffset());

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
