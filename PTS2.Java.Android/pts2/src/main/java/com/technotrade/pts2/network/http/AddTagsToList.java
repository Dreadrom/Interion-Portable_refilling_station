package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.TagInformation;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/// <summary>
/// AddTagsToList request.
/// Sets parameter value
/// </summary>
public class AddTagsToList extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "AddTagsToList";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private List<TagInformation> mTagInformations;

    public AddTagsToList(List<TagInformation> tagInformations) {
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

        for (int i = 0; i < mTagInformations.size(); ++i)
        {
            TagInformation tagInformation = mTagInformations.get(i);

            JSONObject tagInformationJObject = new JSONObject();
            tagInformationJObject.put("Tag", tagInformation.getTag());
            tagInformationJObject.put("Name", tagInformation.getName());
            tagInformationJObject.put("Valid", tagInformation.getValid());

            tagInformationsJArray.put(tagInformationJObject);
        }

        return super.requestJSON(tagInformationsJArray);
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
