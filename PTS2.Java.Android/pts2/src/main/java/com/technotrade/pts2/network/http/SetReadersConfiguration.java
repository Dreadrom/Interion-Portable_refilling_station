package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Reader;
import com.technotrade.pts2.datastructs.ReaderPort;
import com.technotrade.pts2.datastructs.ReadersConfiguration;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/// <summary>
/// SetReadersConfiguration request
/// </summary>
public class SetReadersConfiguration extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetReadersConfiguration";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    public ReadersConfiguration mReadersConfiguration;

    public SetReadersConfiguration(ReadersConfiguration readersConfiguration) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mReadersConfiguration = readersConfiguration;
    }

    public ReadersConfiguration getReadersConfiguration() {
        return mReadersConfiguration;
    }

    public void setReadersConfiguration(ReadersConfiguration readersConfiguration) {
        mReadersConfiguration = readersConfiguration;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mReadersConfiguration == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONObject requestData = new JSONObject();
        JSONArray portsJArray = new JSONArray();

        List<ReaderPort> readerPorts = mReadersConfiguration.getReaderPorts();

        for (int i = 0; i < readerPorts.size(); ++i) {
            ReaderPort readerPort = readerPorts.get(i);

            JSONObject readerPortJObject = new JSONObject();
            readerPortJObject.put("Id", readerPort.getId());
            readerPortJObject.put("Protocol", readerPort.getProtocol());
            readerPortJObject.put("BaudRate", readerPort.getBaudRate());

            portsJArray.put(readerPortJObject);
        }

        requestData.put("Ports", portsJArray);

        JSONArray readersJArray = new JSONArray();

        List<Reader> readers = mReadersConfiguration.getReaders();

        for (int i = 0; i < readers.size(); ++i) {
            Reader reader = readers.get(i);

            JSONObject readerJObject = new JSONObject();
            readerJObject.put("Id", reader.getId());
            readerJObject.put("Port", reader.getPort());
            readerJObject.put("Address", reader.getAddress());
            readerJObject.put("PumpId", reader.getPumpId());
            readerJObject.put("AnyPump", reader.getAnyPump());

            readersJArray.put(readerJObject);
        }

        requestData.put("Readers", readersJArray);
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
