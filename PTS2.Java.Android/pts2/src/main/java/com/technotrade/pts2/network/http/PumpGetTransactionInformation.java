package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.PumpTransactionInformation;
import com.technotrade.pts2.enumeration.PumpTransactionState;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/// <summary>
/// PumpGetTransactionInformation request
/// </summary>
public class PumpGetTransactionInformation extends BaseHTTPRequest<PumpTransactionInformation> {
    public static final String REQUEST_NAME = "PumpGetTransactionInformation";
    public static final String RESPONSE_NAME = "PumpTransactionInformation";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mPump = 0;
    private int mTransaction = 0;

    public PumpGetTransactionInformation(int pump, int transaction) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPump = pump;
        mTransaction = transaction;
    }

    public int getPump() {
        return mPump;
    }

    public void setPump(int pump) {
        mPump = pump;
    }

    public int getTransaction() {
        return mTransaction;
    }

    public void setTransaction(int transaction) {
        mTransaction = transaction;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Pump", mPump);
        requestData.put("Transaction", mTransaction);

        return super.requestJSON(requestData);
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

            PumpTransactionInformation pumpTransactionInformation = new PumpTransactionInformation();

            if (!dataJSONObject.has("Pump")) {
                throw new TTException("Error: response doesn't contain Pump property", Result.PROTOCOL_ERROR);
            }

            pumpTransactionInformation.setPump(dataJSONObject.getInt("Pump"));

            if (!dataJSONObject.has("Transaction")) {
                throw new TTException("Error: response doesn't contain Transaction property", Result.PROTOCOL_ERROR);
            }

            pumpTransactionInformation.setTransaction(dataJSONObject.getInt("Transaction"));

            if (!dataJSONObject.has("State")) {
                throw new TTException("Error: response doesn't contain State property", Result.PROTOCOL_ERROR);
            }

            pumpTransactionInformation.setState(PumpTransactionState.valueOf(dataJSONObject.getString("State")));

            if (dataJSONObject.has("DateTimeStart")) {
                String dateTimeStr = dataJSONObject.getString("DateTimeStart");

                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                Date dateTime = formatter.parse(dateTimeStr);

                pumpTransactionInformation.setDateTimeStart(dateTime);
            }

            if (dataJSONObject.has("DateTime")) {
                String dateTimeStr = dataJSONObject.getString("DateTime");

                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                Date dateTime = formatter.parse(dateTimeStr);

                pumpTransactionInformation.setDateTime(dateTime);
            }

            if (dataJSONObject.has("Nozzle")) {
                pumpTransactionInformation.setNozzle(dataJSONObject.getInt("Nozzle"));
            }

            if (dataJSONObject.has("FuelGradeId")) {
                pumpTransactionInformation.setFuelGradeId(dataJSONObject.getInt("FuelGradeId"));
            }

            if (dataJSONObject.has("FuelGradeName")) {
                pumpTransactionInformation.setFuelGradeName(dataJSONObject.getString("FuelGradeName"));
            }

            if (dataJSONObject.has("Volume")) {
                pumpTransactionInformation.setVolume(dataJSONObject.getDouble("Volume"));
            }

            if (dataJSONObject.has("TCVolume")) {
                pumpTransactionInformation.setTCVolume(dataJSONObject.getDouble("TCVolume"));
            }

            if (dataJSONObject.has("Price")) {
                pumpTransactionInformation.setPrice(dataJSONObject.getDouble("Price"));
            }

            if (dataJSONObject.has("Amount")) {
                pumpTransactionInformation.setAmount(dataJSONObject.getDouble("Amount"));
            }

            if (dataJSONObject.has("TotalVolume")) {
                pumpTransactionInformation.setTotalVolume(dataJSONObject.getDouble("TotalVolume"));
            }

            if (dataJSONObject.has("TotalAmount")) {
                pumpTransactionInformation.setTotalAmount(dataJSONObject.getDouble("TotalAmount"));
            }

            if (dataJSONObject.has("Tag")) {
                pumpTransactionInformation.setTag(dataJSONObject.getString("Tag"));
            }

            if (dataJSONObject.has("UserId")) {
                pumpTransactionInformation.setUserId(dataJSONObject.getInt("UserId"));
            }

            mResult = pumpTransactionInformation;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }

        return true;
    }
}
