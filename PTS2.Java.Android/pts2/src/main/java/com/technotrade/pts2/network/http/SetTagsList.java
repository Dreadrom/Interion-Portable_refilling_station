package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.TagInformation;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/// <summary>
/// SetTagsList request
/// </summary>
public class SetTagsList extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetTagsList";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    public List<TagInformation> mTagInformations;

    public SetTagsList(List<TagInformation> tagInformations) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mTagInformations = tagInformations;
    }

    public List<TagInformation> getTagInformations() {
        return mTagInformations;
    }

    public void setTagInformations(List<TagInformation> tagInformations) {
        mTagInformations = tagInformations;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mTagInformations == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONArray tagInformationsJArray = new JSONArray();

        for (int i = 0; i < mTagInformations.size(); ++i) {
            TagInformation mTagInformation = mTagInformations.get(i);

            JSONObject tagInformationJObject = new JSONObject();
            tagInformationJObject.put("Tag", mTagInformation.getTag());
            tagInformationJObject.put("Name", mTagInformation.getName());
            tagInformationJObject.put("Valid", mTagInformation.getValid());

            tagInformationsJArray.put(tagInformationJObject);
        }

        return super.requestJSON(tagInformationsJArray);
    }

    @Override
    public boolean parseJSON(JSONObject jsonObject) throws JSONException, TTException {
        mResult = super.parseJSON(jsonObject);
        return mResult;
    }
}
