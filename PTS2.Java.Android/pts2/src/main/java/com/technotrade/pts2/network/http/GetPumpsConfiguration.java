package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Port;
import com.technotrade.pts2.datastructs.Pump;
import com.technotrade.pts2.datastructs.PumpsConfiguration;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/// <summary>
/// GetPumpsConfiguration request.
/// Gets pump ports and pumps configuration
/// </summary>
public class GetPumpsConfiguration extends BaseHTTPRequest<PumpsConfiguration> {
    public static final String REQUEST_NAME = "GetPumpsConfiguration";
    public static final String RESPONSE_NAME = "PumpsConfiguration";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetPumpsConfiguration() {
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

            PumpsConfiguration pumpsConfiguration = new PumpsConfiguration();

            if (!dataJSONObject.has("Ports")) {
                throw new TTException("Error: response doesn't contain Ports property", Result.PROTOCOL_ERROR);
            }

            JSONArray portsArray = dataJSONObject.getJSONArray("Ports");

            ArrayList<Port> ports = new ArrayList<Port>();

            for (int portsArrayIterator = 0 ; portsArrayIterator < portsArray.length(); portsArrayIterator++) {
                Port port = new Port();
                JSONObject portJSONObject = portsArray.getJSONObject(portsArrayIterator);

                if (portJSONObject.has("Id")) {
                    int id = portJSONObject.getInt("Id");
                    port.setId(id);
                }

                if (portJSONObject.has("Protocol")) {
                    int protocol = portJSONObject.getInt("Protocol");
                    port.setProtocol(protocol);
                }

                if (portJSONObject.has("BaudRate")) {
                    int baudRate = portJSONObject.getInt("BaudRate");
                    port.setBaudRate(baudRate);
                }

                ports.add(port);
            }

            pumpsConfiguration.setPorts(ports);

            if (!dataJSONObject.has("Pumps")) {
                throw new TTException("Error: response doesn't contain Pumps property", Result.PROTOCOL_ERROR);
            }

            JSONArray pumpsArray = dataJSONObject.getJSONArray("Pumps");

            ArrayList<Pump> pumps = new ArrayList<Pump>();

            for (int pumpsArrayIterator = 0 ; pumpsArrayIterator < pumpsArray.length(); pumpsArrayIterator++) {
                Pump pump = new Pump();
                JSONObject pumpJSONObject = pumpsArray.getJSONObject(pumpsArrayIterator);

                if (pumpJSONObject.has("Id")) {
                    int id = pumpJSONObject.getInt("Id");
                    pump.setId(id);
                }

                if (pumpJSONObject.has("Port")) {
                    int protocol = pumpJSONObject.getInt("Port");
                    pump.setPort(protocol);
                }

                if (pumpJSONObject.has("Address")) {
                    int baudRate = pumpJSONObject.getInt("Address");
                    pump.setAddress(baudRate);
                }

                pumps.add(pump);
            }

            pumpsConfiguration.setPumps(pumps);

            mResult = pumpsConfiguration;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
