package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.TagInformation;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// GetTagsList request.
/// Gets tags from controller
/// </summary>
public class GetTagsList extends BaseHTTPRequest<List<TagInformation>> {
    public static final String REQUEST_NAME = "GetTagsList";
    public static final String RESPONSE_NAME = "TagsList";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mStartNumber;
    private int mTotalNumber;
    private boolean mUseStartNumberAndTotalNumber = false;

    public GetTagsList() {
        super(REQUEST_NAME, RESPONSE_NAME);
    }
    public GetTagsList(int startNumber, int totalNumber) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mStartNumber = startNumber;
        mTotalNumber = totalNumber;
        mUseStartNumberAndTotalNumber = true;
    }
    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws JSONException, TTException {
        if (mUseStartNumberAndTotalNumber) {
            JSONObject requestData = new JSONObject();
            requestData.put("StartNumber", mStartNumber);
            requestData.put("TotalNumber", mTotalNumber);

            return super.requestJSON(requestData);
        }

        return super.requestJSON();
    }
    /// <summary>
    /// ParseJSON overridden method. Parsing the response JSON data.
    /// </summary>
    /// <param name="jObject">JSON object</param>
    /// <returns>True is response have no errors</returns>
    @Override
    public boolean parseJSON(JSONObject jsonObject) throws TTException, JSONException {
        super.parseJSON(jsonObject);

        if (isError()) {
            throw new TTException("Error: response error", Result.PROTOCOL_ERROR);
        }

        if (!jsonObject.has("Data")) {
            throw new TTException("Error: response doesn't contain Data property", Result.PROTOCOL_ERROR);
        }

        JSONArray tagInformationsJArray = jsonObject.getJSONArray("Data");

        ArrayList<TagInformation> tagInformations = new ArrayList<>();

        for (int i = 0; i < tagInformationsJArray.length(); ++i) {
            JSONObject tagJObject = tagInformationsJArray.getJSONObject(i);

            TagInformation tagInformation = new TagInformation();

            if (tagJObject.has("Tag")) {
                tagInformation.setTag(tagJObject.getString("Tag"));
            }

            if (tagJObject.has("Name")) {
                tagInformation.setName(tagJObject.getString("Name"));
            }

            if (tagJObject.has("Valid")) {
                tagInformation.setValid(tagJObject.getBoolean("Valid"));
            }

            tagInformations.add(tagInformation);
        }

        mResult = tagInformations;
        return true;
    }
}
