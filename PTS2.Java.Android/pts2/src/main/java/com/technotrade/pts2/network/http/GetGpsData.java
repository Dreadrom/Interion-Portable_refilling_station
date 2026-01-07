package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.GpsData;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/// <summary>
/// GetGpsData request.
/// Gets GPS data from GPS module.
/// It is possible to receive GPS data from PTS-2 controller when it is equipped with
/// additional GPS module and permission for its application is allowed in parameters of
/// PTS-2 controller
/// </summary>
public class GetGpsData extends BaseHTTPRequest<GpsData> {
    public static final String REQUEST_NAME = "GetGpsData";
    public static final String RESPONSE_NAME = "GpsData";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetGpsData() {
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

            GpsData gpsData = new GpsData();

            if (!dataJSONObject.has("Status")) {
                throw new TTException("Error: response doesn't contain Status property", Result.PROTOCOL_ERROR);
            }

            gpsData.setStatus(dataJSONObject.getString("Status"));

            if (dataJSONObject.has("DateTime")) {
                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                Date dateTime = formatter.parse(dataJSONObject.getString("DateTime"));
                gpsData.setDateTime(dateTime);
            }

            if (dataJSONObject.has("Latitude")) {
                gpsData.setLatitude(dataJSONObject.getString("Latitude"));
            }

            if (dataJSONObject.has("NorthSouthIndicator")) {
                gpsData.setNorthSouthIndicator(dataJSONObject.getString("NorthSouthIndicator"));
            }

            if (dataJSONObject.has("Longitude")) {
                gpsData.setLongitude(dataJSONObject.getString("Longitude"));
            }

            if (dataJSONObject.has("EastWestIndicator")) {
                gpsData.setEastWestIndicator(dataJSONObject.getString("EastWestIndicator"));
            }

            if (dataJSONObject.has("SpeedOverGround")) {
                gpsData.setSpeedOverGround(dataJSONObject.getDouble("SpeedOverGround"));
            }

            if (dataJSONObject.has("CourseOverGround")) {
                gpsData.setCourseOverGround(dataJSONObject.getDouble("CourseOverGround"));
            }

            if (dataJSONObject.has("Mode")) {
                gpsData.setMode(dataJSONObject.getString("Mode"));
            }

            mResult = gpsData;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
