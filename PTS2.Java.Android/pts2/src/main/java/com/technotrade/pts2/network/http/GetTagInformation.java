package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.TagInformation;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// GetTagInformation request.
/// Returns configured tag
/// </summary>
public class GetTagInformation extends BaseHTTPRequest<TagInformation> {
    public static final String REQUEST_NAME = "GetTagInformation";
    public static final String RESPONSE_NAME = "TagInformation";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private String mTag;

    public GetTagInformation(String pump) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mTag = pump;
    }
    /// <summary>
    /// Tag getter and setter
    /// </summary>
    public String getTag() {
        return mTag;
    }
    public void setTag(String tag) {
        mTag = tag;
    }
    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Tag", mTag);

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

            TagInformation tagInformation = new TagInformation();

            if (!dataJSONObject.has("Tag")) {
                throw new TTException("Error: response doesn't contain Tag property", Result.PROTOCOL_ERROR);
            }

            tagInformation.setTag(dataJSONObject.getString("Tag"));

            if (!dataJSONObject.has("Name")) {
                throw new TTException("Error: response doesn't contain Name property", Result.PROTOCOL_ERROR);
            }

            tagInformation.setName(dataJSONObject.getString("Name"));

            if (!dataJSONObject.has("Valid")) {
                throw new TTException("Error: response doesn't contain Valid property", Result.PROTOCOL_ERROR);
            }

            tagInformation.setValid(dataJSONObject.getBoolean("Valid"));

            if (dataJSONObject.has("Present")) {
                tagInformation.setPresent(dataJSONObject.getBoolean("Present"));
            }

            mResult = tagInformation;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
