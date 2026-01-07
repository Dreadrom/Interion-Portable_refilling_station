package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Parameter;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

/// <summary>
/// GetParameter request.
/// Gets parameter value
/// </summary>
public class  GetParameter extends BaseHTTPRequest<Parameter> {
    public static final String REQUEST_NAME = "GetParameter";
    public static final String RESPONSE_NAME = "Parameter";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private Parameter mParameter;

    public GetParameter(Parameter parameter) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mParameter = parameter;
    }
    /// <summary>
    /// Parameter getter and setter
    /// </summary>
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

            Parameter parameter = new Parameter();

            if (!dataJSONObject.has("Data")) {
                throw new TTException("Error: response doesn't contain Data property", Result.PROTOCOL_ERROR);
            }

            JSONObject datajObject = dataJSONObject.getJSONObject("Data");

            if (!datajObject.has("Device")) {
                throw new TTException("Error: response doesn't contain Device property", Result.PROTOCOL_ERROR);
            }

            parameter.setDevice(datajObject.getString("Device"));

            if (!datajObject.has("Number")) {
                throw new TTException("Error: response doesn't contain Number property", Result.PROTOCOL_ERROR);
            }

            parameter.setNumber(datajObject.getInt("Number"));

            if (!datajObject.has("Address")) {
                throw new TTException("Error: response doesn't contain Address property", Result.PROTOCOL_ERROR);
            }

            parameter.setAddress(datajObject.getInt("Address"));

            if (!datajObject.has("Value")) {
                throw new TTException("Error: response doesn't contain Value property", Result.PROTOCOL_ERROR);
            }

            parameter.setValue(datajObject.getString("Value"));

            mResult = parameter;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
