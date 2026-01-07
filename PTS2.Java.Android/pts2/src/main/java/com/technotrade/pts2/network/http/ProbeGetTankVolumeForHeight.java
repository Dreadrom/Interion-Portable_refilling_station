package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.ProbeTankVolumeForHeight;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// ProbeGetTankVolumeForHeight request.
/// Gets tank volume for certain height based on tank’s calibration chart.
/// Tank should have a valid calibration chart configured
/// </summary>
public class ProbeGetTankVolumeForHeight extends BaseHTTPRequest<ProbeTankVolumeForHeight> {
    public static final String REQUEST_NAME = "ProbeGetTankVolumeForHeight";
    public static final String RESPONSE_NAME = "ProbeTankVolumeForHeight";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mProbe = 0;
    private int mHeight = 0;

    public ProbeGetTankVolumeForHeight(int probe, int height) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mProbe = probe;
        mHeight = height;
    }

    public int getProbe() {
        return mProbe;
    }

    public void setProbe(int probe) {
        mProbe = probe;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Probe", mProbe);
        requestData.put("Height", mHeight);

        return super.requestJSON(requestData);
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

            ProbeTankVolumeForHeight probeTankVolumeForHeight = new ProbeTankVolumeForHeight();

            if (!dataJSONObject.has("Probe")) {
                throw new TTException("Error: response doesn't contain Probe property", Result.PROTOCOL_ERROR);
            }

            probeTankVolumeForHeight.setProbe(dataJSONObject.getInt("Probe"));

            if (!dataJSONObject.has("Height")) {
                throw new TTException("Error: response doesn't contain Height property", Result.PROTOCOL_ERROR);
            }

            probeTankVolumeForHeight.setHeight(dataJSONObject.getInt("Height"));

            if (!dataJSONObject.has("Volume")) {
                throw new TTException("Error: response doesn't contain Volume property", Result.PROTOCOL_ERROR);
            }

            probeTankVolumeForHeight.setVolume(dataJSONObject.getInt("Volume"));

            mResult = probeTankVolumeForHeight;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
