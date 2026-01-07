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

/// <summary>
/// GetPriceBoardsConfiguration request.
/// Gets price boards configuration
/// </summary>
public class GetPriceBoardsConfiguration extends BaseHTTPRequest<PriceBoardsConfiguration> {
    public static final String REQUEST_NAME = "GetPriceBoardsConfiguration";
    public static final String RESPONSE_NAME = "PriceBoardsConfiguration";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetPriceBoardsConfiguration() {
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

            PriceBoardsConfiguration priceBoardsConfiguration = new PriceBoardsConfiguration();

            if (!dataJSONObject.has("Ports")) {
                throw new TTException("Error: response doesn't contain Ports property", Result.PROTOCOL_ERROR);
            }

            ArrayList<PriceBoardPort> priceBoardPorts = new ArrayList<PriceBoardPort>();

            JSONArray priceBoardPortsJArray = dataJSONObject.getJSONArray("Ports");
            for (int i = 0; i < priceBoardPortsJArray.length(); ++i) {
                JSONObject priceBoardPortJObject = priceBoardPortsJArray.getJSONObject(i);

                PriceBoardPort priceBoardPort = new PriceBoardPort();

                if (priceBoardPortJObject.has("Id")) {
                    priceBoardPort.setId(priceBoardPortJObject.getString("Id"));
                }

                if (priceBoardPortJObject.has("Protocol")) {
                    priceBoardPort.setProtocol(priceBoardPortJObject.getInt("Protocol"));
                }

                if (priceBoardPortJObject.has("BaudRate")) {
                    priceBoardPort.setBaudRate(priceBoardPortJObject.getInt("BaudRate"));
                }

                priceBoardPorts.add(priceBoardPort);
            }

            priceBoardsConfiguration.setPriceBoardPorts(priceBoardPorts);

            if (!dataJSONObject.has("PriceBoards")) {
                throw new TTException("Error: response doesn't contain PriceBoards property", Result.PROTOCOL_ERROR);
            }

            ArrayList<PriceBoard> priceBoards = new ArrayList<PriceBoard>();

            JSONArray priceBoardsJArray = dataJSONObject.getJSONArray("PriceBoards");
            for (int i = 0; i < priceBoardsJArray.length(); ++i) {
                JSONObject priceBoardJObject = priceBoardsJArray.getJSONObject(i);

                PriceBoard priceBoard = new PriceBoard();

                if (priceBoardJObject.has("Id")) {
                    priceBoard.setId(priceBoardJObject.getInt("Id"));
                }

                if (priceBoardJObject.has("Port")) {
                    priceBoard.setPort(priceBoardJObject.getString("Port"));
                }

                if (priceBoardJObject.has("Address")) {
                    priceBoard.setAddress(priceBoardJObject.getInt("Address"));
                }

                if (priceBoardJObject.has("FuelGradeIds")) {
                    ArrayList<Integer> fuelGradeIds = new ArrayList<Integer>();
                    JSONArray fuelGradeIdsJArray = priceBoardJObject.getJSONArray("FuelGradeIds");

                    for (int fuelGradeIdsIter = 0; fuelGradeIdsIter < fuelGradeIdsJArray.length(); ++fuelGradeIdsIter) {
                        fuelGradeIds.add(fuelGradeIdsJArray.getInt(fuelGradeIdsIter));
                    }

                    priceBoard.setFuelGradeIds(fuelGradeIds);
                }

                priceBoards.add(priceBoard);
            }

            priceBoardsConfiguration.setPriceBoards(priceBoards);

            mResult = priceBoardsConfiguration;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
