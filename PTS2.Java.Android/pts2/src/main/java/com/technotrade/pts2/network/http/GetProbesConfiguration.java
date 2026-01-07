package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Probe;
import com.technotrade.pts2.datastructs.ProbePort;
import com.technotrade.pts2.datastructs.ProbesConfiguration;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/// <summary>
/// GetProbesConfiguration request.
/// Gets probe ports and probes configuration
/// </summary>
public class GetProbesConfiguration extends BaseHTTPRequest<ProbesConfiguration> {
    public static final String REQUEST_NAME = "GetProbesConfiguration";
    public static final String RESPONSE_NAME = "ProbesConfiguration";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetProbesConfiguration() {
        super(REQUEST_NAME, RESPONSE_NAME);
    }
    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        return super.requestJSON();
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

            ProbesConfiguration probesConfiguration = new ProbesConfiguration();

            if (!dataJSONObject.has("Data")) {
                throw new TTException("Error: response doesn't contain Data property", Result.PROTOCOL_ERROR);
            }

            JSONObject datajObject = dataJSONObject.getJSONObject("Data");

            if (!datajObject.has("Ports")) {
                throw new TTException("Error: response doesn't contain Ports property", Result.PROTOCOL_ERROR);
            }

            ArrayList<ProbePort> probePorts = new ArrayList<ProbePort>();

            JSONArray probePortsJArray = datajObject.getJSONArray("Ports");
            for (int i = 0; i < probePortsJArray.length(); ++i) {
                JSONObject probePortJObject = probePortsJArray.getJSONObject(i);

                ProbePort probePort = new ProbePort();

                if (probePortJObject.has("Id")) {
                    probePort.setId(probePortJObject.getString("Id"));
                }

                if (probePortJObject.has("Protocol")) {
                    probePort.setProtocol(probePortJObject.getInt("Protocol"));
                }

                if (probePortJObject.has("BaudRate")) {
                    probePort.setBaudRate(probePortJObject.getInt("BaudRate"));
                }

                probePorts.add(probePort);
            }

            probesConfiguration.setProbePorts(probePorts);

            ArrayList<Probe> probes = new ArrayList<Probe>();

            JSONArray probesJArray = datajObject.getJSONArray("Probes");
            for (int i = 0; i < probesJArray.length(); ++i) {
                JSONObject probeJObject = probesJArray.getJSONObject(i);

                Probe probe = new Probe();

                if (probeJObject.has("Id")) {
                    probe.setId(probeJObject.getInt("Id"));
                }

                if (probeJObject.has("Port")) {
                    probe.setPort(probeJObject.getString("Port"));
                }

                if (probeJObject.has("Address")) {
                    probe.setAddress(probeJObject.getInt("Address"));
                }

                probes.add(probe);
            }

            probesConfiguration.setProbes(probes);

            mResult = probesConfiguration;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
