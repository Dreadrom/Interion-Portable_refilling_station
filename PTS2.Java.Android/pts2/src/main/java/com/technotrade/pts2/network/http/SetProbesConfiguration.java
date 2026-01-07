package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Probe;
import com.technotrade.pts2.datastructs.ProbePort;
import com.technotrade.pts2.datastructs.ProbesConfiguration;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/// <summary>
/// SetProbesConfiguration request
/// </summary>
public class SetProbesConfiguration extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetPumpsConfiguration";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    public ProbesConfiguration mProbesConfiguration;

    public SetProbesConfiguration(ProbesConfiguration probesConfiguration) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mProbesConfiguration = probesConfiguration;
    }

    public ProbesConfiguration getProbesConfiguration() {
        return mProbesConfiguration;
    }

    public void setProbesConfiguration(ProbesConfiguration probesConfiguration) {
        mProbesConfiguration = probesConfiguration;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mProbesConfiguration == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONObject requestData = new JSONObject();
        JSONArray portsJArray = new JSONArray();

        List<ProbePort> probePorts = mProbesConfiguration.getProbePorts();

        for (int i = 0; i < probePorts.size(); ++i)
        {
            ProbePort probePort = probePorts.get(i);

            JSONObject probePortJObject = new JSONObject();
            probePortJObject.put("Id", probePort.getId());
            probePortJObject.put("Protocol", probePort.getProtocol());
            probePortJObject.put("BaudRate", probePort.getBaudRate());

            portsJArray.put(probePortJObject);
        }

        requestData.put("Ports", portsJArray);

        JSONArray probesJArray = new JSONArray();

        List<Probe> probes = mProbesConfiguration.getProbes();

        for (int i = 0; i < probes.size(); ++i)
        {
            Probe probe = probes.get(i);

            JSONObject probeJObject = new JSONObject();
            probeJObject.put("Id", probe.getId());
            probeJObject.put("Port", probe.getPort());
            probeJObject.put("Address", probe.getAddress());

            probesJArray.put(probeJObject);
        }

        requestData.put("Probes", probesJArray);

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
