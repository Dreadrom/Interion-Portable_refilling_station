package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Reader;
import com.technotrade.pts2.datastructs.ReaderPort;
import com.technotrade.pts2.datastructs.ReadersConfiguration;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/// <summary>
/// GetReadersConfiguration request.
/// Gets readers configuration
/// </summary>
public class GetReadersConfiguration extends BaseHTTPRequest<ReadersConfiguration> {
    public static final String REQUEST_NAME = "GetReadersConfiguration";
    public static final String RESPONSE_NAME = "ReadersConfiguration";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetReadersConfiguration() {
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

            ReadersConfiguration readersConfiguration = new ReadersConfiguration();

            if (!dataJSONObject.has("Ports")) {
                throw new TTException("Error: response doesn't contain Ports property", Result.PROTOCOL_ERROR);
            }

            ArrayList<ReaderPort> readerPorts = new ArrayList<ReaderPort>();

            JSONArray readerPortsJArray = dataJSONObject.getJSONArray("Ports");
            for (int i = 0; i < readerPortsJArray.length(); i++) {
                JSONObject readerPortJObject = readerPortsJArray.getJSONObject(i);

                ReaderPort readerPort = new ReaderPort();

                if (readerPortJObject.has("Id")) {
                    readerPort.setId(readerPortJObject.getString("Id"));
                }

                if (readerPortJObject.has("Protocol")) {
                    readerPort.setProtocol(readerPortJObject.getInt("Protocol"));
                }

                if (readerPortJObject.has("BaudRate")) {
                    readerPort.setBaudRate(readerPortJObject.getInt("BaudRate"));
                }

                readerPorts.add(readerPort);
            }

            readersConfiguration.setReaderPorts(readerPorts);

            if (!dataJSONObject.has("Readers")) {
                throw new TTException("Error: response doesn't contain Readers property", Result.PROTOCOL_ERROR);
            }

            ArrayList<Reader> readers = new ArrayList<Reader>();

            JSONArray readersJArray = dataJSONObject.getJSONArray("Readers");
            for (int i = 0; i < readersJArray.length(); i++) {
                JSONObject readerJObject = readersJArray.getJSONObject(i);

                Reader reader = new Reader();

                if (readerJObject.has("Id")) {
                    reader.setId(readerJObject.getInt("Id"));
                }

                if (readerJObject.has("Port")) {
                    reader.setPort(readerJObject.getString("Port"));
                }

                if (readerJObject.has("Address")) {
                    reader.setAddress(readerJObject.getInt("Address"));
                }

                if (readerJObject.has("PumpId")) {
                    reader.setPumpId(readerJObject.getInt("PumpId"));
                }

                if (readerJObject.has("AnyPump")) {
                    reader.setAnyPump(readerJObject.getBoolean("AnyPump"));
                }

                readers.add(reader);
            }

            readersConfiguration.setReaders(readers);

            mResult = readersConfiguration;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
