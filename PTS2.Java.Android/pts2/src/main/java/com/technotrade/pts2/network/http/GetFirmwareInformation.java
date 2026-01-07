package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.FirmwareInformation;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/// <summary>
/// GetFirmwareInformation request.
/// Gets information on current firmware: version, release date and time, list of
/// supported communication protocols
/// </summary>
public class GetFirmwareInformation extends BaseHTTPRequest<FirmwareInformation> {
    public static final String REQUEST_NAME = "GetFirmwareInformation";
    public static final String RESPONSE_NAME = "FirmwareInformation";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetFirmwareInformation() {
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

            FirmwareInformation firmwareInformation = new FirmwareInformation();

            if (!dataJSONObject.has("DateTime")) {
                throw new TTException("Error: response doesn't contain DateTime property", Result.PROTOCOL_ERROR);
            }

            String dateTimeStr = dataJSONObject.getString("DateTime");

            String pattern = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
            Date dateTime = formatter.parse(dateTimeStr);
            firmwareInformation.setDateTime(dateTime);

            if (!dataJSONObject.has("PumpProtocols")) {
                throw new TTException("Error: response doesn't contain PumpProtocols property", Result.PROTOCOL_ERROR);
            }

            ArrayList<Integer> pumpProtocols = new ArrayList<Integer>();
            JSONArray pumpProtocolsJArray = dataJSONObject.getJSONArray("PumpProtocols");

            for (int i = 0; i < pumpProtocolsJArray.length(); ++i) {
                pumpProtocols.add(pumpProtocolsJArray.getInt(i));
            }

            firmwareInformation.setPumpProtocols(pumpProtocols);

            if (!dataJSONObject.has("ProbeProtocols")) {
                throw new TTException("Error: response doesn't contain ProbeProtocols property", Result.PROTOCOL_ERROR);
            }

            ArrayList<Integer> probeProtocols = new ArrayList<Integer>();
            JSONArray probeProtocolsJArray = dataJSONObject.getJSONArray("ProbeProtocols");

            for (int i = 0; i < probeProtocolsJArray.length(); ++i) {
                probeProtocols.add(probeProtocolsJArray.getInt(i));
            }

            firmwareInformation.setProbeProtocols(probeProtocols);

            if (!dataJSONObject.has("PriceBoardProtocols")) {
                throw new TTException("Error: response doesn't contain PriceBoardProtocols property", Result.PROTOCOL_ERROR);
            }

            ArrayList<Integer> priceBoardProtocols = new ArrayList<Integer>();
            JSONArray priceBoardProtocolsJArray = dataJSONObject.getJSONArray("PriceBoardProtocols");

            for (int i = 0; i < priceBoardProtocolsJArray.length(); ++i) {
                priceBoardProtocols.add(priceBoardProtocolsJArray.getInt(i));
            }

            firmwareInformation.setPriceBoardProtocols(priceBoardProtocols);

            if (!dataJSONObject.has("ReaderProtocols")) {
                throw new TTException("Error: response doesn't contain ReaderProtocols property", Result.PROTOCOL_ERROR);
            }

            ArrayList<Integer> readerProtocols = new ArrayList<Integer>();
            JSONArray readerProtocolsJArray = dataJSONObject.getJSONArray("ReaderProtocols");

            for (int i = 0; i < readerProtocolsJArray.length(); ++i) {
                readerProtocols.add(readerProtocolsJArray.getInt(i));
            }

            firmwareInformation.setReaderProtocols(readerProtocols);

            mResult = firmwareInformation;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
