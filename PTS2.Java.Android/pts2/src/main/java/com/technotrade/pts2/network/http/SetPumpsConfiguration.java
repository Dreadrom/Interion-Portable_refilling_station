package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Port;
import com.technotrade.pts2.datastructs.Pump;
import com.technotrade.pts2.datastructs.PumpsConfiguration;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/// <summary>
/// SetPumpsConfiguration request
/// </summary>
public class SetPumpsConfiguration extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetPumpsConfiguration";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    public PumpsConfiguration mPumpsConfiguration;

    public SetPumpsConfiguration(PumpsConfiguration pumpsConfiguration) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPumpsConfiguration = pumpsConfiguration;
    }

    public PumpsConfiguration getPumpsConfiguration() {
        return mPumpsConfiguration;
    }

    public void setPumpAuthorizeData(PumpsConfiguration pumpsConfiguration) {
        mPumpsConfiguration = pumpsConfiguration;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mPumpsConfiguration == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONObject requestData = new JSONObject();
        JSONArray portsJArray = new JSONArray();

        List<Port> ports = mPumpsConfiguration.getPorts();

        for (int i = 0; i < ports.size(); ++i)
        {
            Port port = ports.get(i);

            JSONObject portJObject = new JSONObject();
            portJObject.put("Id", port.getId());
            portJObject.put("Protocol", port.getProtocol());
            portJObject.put("BaudRate", port.getBaudRate());

            portsJArray.put(portJObject);
        }

        requestData.put("Ports", portsJArray);

        JSONArray pumpsJArray = new JSONArray();

        List<Pump> pumps = mPumpsConfiguration.getPumps();

        for (int i = 0; i < pumps.size(); ++i)
        {
            Pump pump = pumps.get(i);

            JSONObject pumpJObject = new JSONObject();
            pumpJObject.put("Id", pump.getId());
            pumpJObject.put("Port", pump.getPort());
            pumpJObject.put("Address", pump.getAddress());

            pumpsJArray.put(pumpJObject);
        }

        requestData.put("Pumps", pumpsJArray);

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
