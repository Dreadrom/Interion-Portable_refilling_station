package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Parameter;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// SetParameter request.
/// Sets parameter value
/// </summary>
public class SetParameter extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetParameter";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private Parameter mParameter;

    public SetParameter(Parameter parameter) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mParameter = parameter;
    }

    public Parameter getParameter() {
        return mParameter;
    }

    public void setParameter(Parameter parameter) {
        mParameter = parameter;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mParameter == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONObject requestData = new JSONObject();

        requestData.put("Device", mParameter.getDevice());
        requestData.put("Number", mParameter.getNumber());
        requestData.put("Address", mParameter.getAddress());
        requestData.put("Value", mParameter.getValue());

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
