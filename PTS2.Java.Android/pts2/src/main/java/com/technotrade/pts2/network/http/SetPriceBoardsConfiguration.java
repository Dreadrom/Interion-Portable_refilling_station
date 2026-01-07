package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.PriceBoard;
import com.technotrade.pts2.datastructs.PriceBoardPort;
import com.technotrade.pts2.datastructs.PriceBoardsConfiguration;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// SetPriceBoardsConfiguration request
/// </summary>
public class SetPriceBoardsConfiguration extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetPriceBoardsConfiguration";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    public PriceBoardsConfiguration mPriceBoardsConfiguration;

    public SetPriceBoardsConfiguration(PriceBoardsConfiguration priceBoardsConfiguration) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPriceBoardsConfiguration = priceBoardsConfiguration;
    }

    public PriceBoardsConfiguration getPriceBoardsConfiguration() {
        return mPriceBoardsConfiguration;
    }

    public void setPriceBoardsConfiguration(PriceBoardsConfiguration priceBoardsConfiguration) {
        mPriceBoardsConfiguration = priceBoardsConfiguration;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mPriceBoardsConfiguration == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONObject requestData = new JSONObject();
        JSONArray portsJArray = new JSONArray();

        List<PriceBoardPort> priceBoardPorts = mPriceBoardsConfiguration.getPriceBoardPorts();

        for (int i = 0; i < priceBoardPorts.size(); ++i) {
            PriceBoardPort priceBoardPort = priceBoardPorts.get(i);

            JSONObject priceBoardPortJObject = new JSONObject();
            priceBoardPortJObject.put("Id", priceBoardPort.getId());
            priceBoardPortJObject.put("Protocol", priceBoardPort.getProtocol());
            priceBoardPortJObject.put("BaudRate", priceBoardPort.getBaudRate());

            portsJArray.put(priceBoardPortJObject);
        }

        requestData.put("Ports", portsJArray);

        JSONArray priceBoardsJArray = new JSONArray();

        List<PriceBoard> priceBoards = mPriceBoardsConfiguration.getPriceBoards();

        for (int i = 0; i < priceBoards.size(); ++i) {
            PriceBoard priceBoard = priceBoards.get(i);

            JSONObject priceBoardJObject = new JSONObject();
            priceBoardJObject.put("Id", priceBoard.getId());
            priceBoardJObject.put("Port", priceBoard.getPort());
            priceBoardJObject.put("Address", priceBoard.getAddress());

            ArrayList<Integer> fuelGradeIds = priceBoard.getFuelGradeIds();

            if (fuelGradeIds != null) {
                JSONArray fuelGradeIdsJArray = new JSONArray();

                for (int fuelGradeIdsIter = 0; fuelGradeIdsIter < fuelGradeIds.size(); ++fuelGradeIdsIter) {
                    fuelGradeIdsJArray.put(fuelGradeIds.get(fuelGradeIdsIter));
                }

                priceBoardJObject.put("FuelGradeIds", fuelGradeIdsJArray);
            }

            priceBoardsJArray.put(priceBoardJObject);
        }

        requestData.put("PriceBoards", priceBoardsJArray);

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
