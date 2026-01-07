package com.technotrade.pts2.network.http;

import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

/// <summary>
/// PumpSetPrices request
/// </summary>
public class PumpSetPrices extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "PumpSetPrices";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mPump = 0;
    public ArrayList<BigDecimal> mPrices;

    public PumpSetPrices(int pump, ArrayList<BigDecimal> prices) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPump = pump;
        mPrices = prices;
    }

    public int getPump() {
        return mPump;
    }

    public void setPump(int pump) {
        mPump = pump;
    }

    public ArrayList<BigDecimal> getPrices() {
        return mPrices;
    }

    public void setPrices(ArrayList<BigDecimal> prices) {
        mPrices = prices;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Pump", mPump);

        JSONArray pricesJArray = new JSONArray();

        for (int i = 0; i < mPrices.size(); ++i) {
            pricesJArray.put(mPrices.get(i));
        }

        requestData.put("Prices", pricesJArray);

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
