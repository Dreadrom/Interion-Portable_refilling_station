package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.ReaderTag;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// ReaderGetTag request.
/// Gets value of tag ID from reader
/// </summary>
public class ReaderGetTag extends BaseHTTPRequest<ReaderTag> {
    public static final String REQUEST_NAME = "ReaderGetTag";
    public static final String RESPONSE_NAME = "ReaderTag";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mReader;

    public ReaderGetTag(int reader) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mReader = reader;
    }

    public int getReader() {
        return mReader;
    }

    public void setReader(int reader) {
        mReader = reader;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Reader", mReader);

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

            ReaderTag readerTag = new ReaderTag();

            if (!dataJSONObject.has("Reader")) {
                throw new TTException("Error: response doesn't contain Reader property", Result.PROTOCOL_ERROR);
            }

            readerTag.setReader(dataJSONObject.getInt("Reader"));

            if (!dataJSONObject.has("Tag")) {
                throw new TTException("Error: response doesn't contain Tag property", Result.PROTOCOL_ERROR);
            }

            readerTag.setTag(dataJSONObject.getString("Tag"));

            if (!dataJSONObject.has("Online")) {
                throw new TTException("Error: response doesn't contain Online property", Result.PROTOCOL_ERROR);
            }

            readerTag.setOnline(dataJSONObject.getBoolean("Online"));

            if (!dataJSONObject.has("Error")) {
                throw new TTException("Error: response doesn't contain Error property", Result.PROTOCOL_ERROR);
            }

            readerTag.setError(dataJSONObject.getBoolean("Error"));

            mResult = readerTag;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
