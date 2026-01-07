package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.PumpDisplayData;
import com.technotrade.pts2.datastructs.PumpEndOfTransactionStatus;
import com.technotrade.pts2.datastructs.PumpFillingStatus;
import com.technotrade.pts2.datastructs.PumpIdleStatus;
import com.technotrade.pts2.datastructs.PumpOfflineStatus;
import com.technotrade.pts2.datastructs.PumpPrices;
import com.technotrade.pts2.datastructs.PumpStatusBase;
import com.technotrade.pts2.datastructs.PumpTag;
import com.technotrade.pts2.datastructs.PumpTotals;
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
/// PumpGetStatus request
/// </summary>
public class PumpGetStatus extends BaseHTTPRequest<PumpStatusBase> {

    public static final String REQUEST_NAME = "PumpGetStatus";
    public static final String PUMP_IDLE_STATUS = "PumpIdleStatus";
    public static final String PUMP_FILLING_STATUS = "PumpFillingStatus";
    public static final String PUMP_END_OF_TRANSACTION_STATUS = "PumpEndOfTransactionStatus";
    public static final String PUMP_OFFLINE_STATUS = "PumpOfflineStatus";
    public static final String PUMP_TOTALS = "PumpTotals";
    public static final String PUMP_PRICES = "PumpPrices";
    public static final String PUMP_TAG = "PumpTag";
    public static final String PUMP_DISPLAY_DATA = "PumpDisplayData";
    public static final String[] RESPONSE_NAMES = { PUMP_IDLE_STATUS,
            PUMP_FILLING_STATUS,
            PUMP_END_OF_TRANSACTION_STATUS,
            PUMP_OFFLINE_STATUS,
            PUMP_TOTALS,
            PUMP_PRICES,
            PUMP_TAG,
            PUMP_DISPLAY_DATA };
    private int mPump = 0;

    public PumpGetStatus(int pump) {
        super(REQUEST_NAME, RESPONSE_NAMES);

        mPump = pump;
    }

    public int getPump() {
        return mPump;
    }

    public void setPump(int pump) {
        mPump = pump;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Pump", mPump);

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

            PumpStatusBase pumpStatus = null;

            String receivedResponseName = getReceivedResponseName();

            if (receivedResponseName.equals(PUMP_IDLE_STATUS)) {
                PumpIdleStatus pumpIdleStatus = new PumpIdleStatus();

                if ( dataJSONObject.has("NozzleUp")) {
                    pumpIdleStatus.setNozzleUp(dataJSONObject.getInt("NozzleUp"));
                }

                if ( dataJSONObject.has("LastNozzle")) {
                    pumpIdleStatus.setLastNozzle(dataJSONObject.getInt("LastNozzle"));
                }

                if ( dataJSONObject.has("LastVolume")) {
                    pumpIdleStatus.setLastVolume(dataJSONObject.getDouble("LastVolume"));
                }

                if ( dataJSONObject.has("LastPrice")) {
                    pumpIdleStatus.setLastPrice(dataJSONObject.getDouble("LastPrice"));
                }

                if ( dataJSONObject.has("LastAmount")) {
                    pumpIdleStatus.setLastAmount(dataJSONObject.getDouble("LastAmount"));
                }

                if ( dataJSONObject.has("LastTransaction")) {
                    pumpIdleStatus.setLastTransaction(dataJSONObject.getInt("LastTransaction"));
                }

                if ( dataJSONObject.has("Request")) {
                    pumpIdleStatus.setRequest(dataJSONObject.getString("Request"));
                }

                if ( dataJSONObject.has("Tag")) {
                    pumpIdleStatus.setTag(dataJSONObject.getString("Tag"));
                }

                if ( dataJSONObject.has("Nozzle")) {
                    pumpIdleStatus.setNozzle(dataJSONObject.getInt("Nozzle"));
                }

                if ( dataJSONObject.has("FuelGradeId")) {
                    pumpIdleStatus.setFuelGradeId(dataJSONObject.getInt("FuelGradeId"));
                }

                if ( dataJSONObject.has("FuelGradeName")) {
                    pumpIdleStatus.setFuelGradeName(dataJSONObject.getString("FuelGradeName"));
                }

                if ( dataJSONObject.has("Transaction")) {
                    pumpIdleStatus.setTransaction(dataJSONObject.getInt("Transaction"));
                }

                if ( dataJSONObject.has("LastDateTimeStart")) {
                    String lastDateTimeStartStr = dataJSONObject.getString("LastDateTimeStart");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date lastDateStartTime = formatter.parse(lastDateTimeStartStr);
                    pumpIdleStatus.setLastDateTimeStart(lastDateStartTime);
                }

                if ( dataJSONObject.has("LastDateTime")) {
                    String lastDateTimeStr = dataJSONObject.getString("LastDateTime");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date lastDateTime = formatter.parse(lastDateTimeStr);
                    pumpIdleStatus.setLastDateTime(lastDateTime);
                }

                if ( dataJSONObject.has("LastFuelGradeId")) {
                    pumpIdleStatus.setLastFuelGradeId(dataJSONObject.getInt("LastFuelGradeId"));
                }

                if ( dataJSONObject.has("LastFuelGradeName")) {
                    pumpIdleStatus.setLastFuelGradeName(dataJSONObject.getString("LastFuelGradeName"));
                }

                if ( dataJSONObject.has("LastTotalVolume")) {
                    pumpIdleStatus.setLastTotalVolume(dataJSONObject.getDouble("LastTotalVolume"));
                }

                if ( dataJSONObject.has("LastTotalAmount")) {
                    pumpIdleStatus.setLastTotalAmount(dataJSONObject.getDouble("LastTotalAmount"));
                }

                if ( dataJSONObject.has("LastUser")) {
                    pumpIdleStatus.setLastUser(dataJSONObject.getString("LastUser"));
                }

                //Fields “NozzleUp” and “Nozzle” show the same value, field “Nozzle” was added for
                //homogeneity with other requests/responses, field “NozzleUp” is left for
                //compatibility with previous versions of this protocol
                if (!pumpIdleStatus.isNozzleSet()) {
                    pumpIdleStatus.setNozzle(pumpIdleStatus.getNozzleUp());
                }

                pumpStatus = pumpIdleStatus;
            }
            else if (receivedResponseName.equals(PUMP_FILLING_STATUS)) {
                PumpFillingStatus pumpFillingStatus = new PumpFillingStatus();

                if ( dataJSONObject.has("Nozzle")) {
                    pumpFillingStatus.setNozzle(dataJSONObject.getInt("Nozzle"));
                }

                if ( dataJSONObject.has("Volume")) {
                    pumpFillingStatus.setVolume(dataJSONObject.getDouble("Volume"));
                }

                if ( dataJSONObject.has("TCVolume")) {
                    pumpFillingStatus.setTCVolume(dataJSONObject.getDouble("TCVolume"));
                }

                if ( dataJSONObject.has("Price")) {
                    pumpFillingStatus.setPrice(dataJSONObject.getDouble("Price"));
                }

                if ( dataJSONObject.has("Amount")) {
                    pumpFillingStatus.setAmount(dataJSONObject.getDouble("Amount"));
                }

                if ( dataJSONObject.has("Transaction")) {
                    pumpFillingStatus.setTransaction(dataJSONObject.getInt("Transaction"));
                }

                if ( dataJSONObject.has("Tag")) {
                    pumpFillingStatus.setTag(dataJSONObject.getString("Tag"));
                }

                if ( dataJSONObject.has("FuelGradeId")) {
                    pumpFillingStatus.setFuelGradeId(dataJSONObject.getInt("FuelGradeId"));
                }

                if ( dataJSONObject.has("FuelGradeName")) {
                    pumpFillingStatus.setFuelGradeName(dataJSONObject.getString("FuelGradeName"));
                }

                if ( dataJSONObject.has("DateTimeStart")) {
                    String dateTimeStartStr = dataJSONObject.getString("DateTimeStart");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date dateStartTime = formatter.parse(dateTimeStartStr);
                    pumpFillingStatus.setDateTimeStart(dateStartTime);
                }

                pumpStatus = pumpFillingStatus;
            }
            else if (receivedResponseName.equals(PUMP_END_OF_TRANSACTION_STATUS)) {
                PumpEndOfTransactionStatus pumpEndOfTransactionStatus = new PumpEndOfTransactionStatus();

                if ( dataJSONObject.has("Nozzle")) {
                    pumpEndOfTransactionStatus.setNozzle(dataJSONObject.getInt("Nozzle"));
                }

                if ( dataJSONObject.has("Volume")) {
                    pumpEndOfTransactionStatus.setVolume(dataJSONObject.getDouble("Volume"));
                }

                if ( dataJSONObject.has("TCVolume")) {
                    pumpEndOfTransactionStatus.setTCVolume(dataJSONObject.getDouble("TCVolume"));
                }

                if ( dataJSONObject.has("Price")) {
                    pumpEndOfTransactionStatus.setPrice(dataJSONObject.getDouble("Price"));
                }

                if ( dataJSONObject.has("Amount")) {
                    pumpEndOfTransactionStatus.setAmount(dataJSONObject.getDouble("Amount"));
                }

                if ( dataJSONObject.has("Transaction")) {
                    pumpEndOfTransactionStatus.setTransaction(dataJSONObject.getInt("Transaction"));
                }

                if ( dataJSONObject.has("Tag")) {
                    pumpEndOfTransactionStatus.setTag(dataJSONObject.getString("Tag"));
                }

                if ( dataJSONObject.has("FuelGradeId")) {
                    pumpEndOfTransactionStatus.setFuelGradeId(dataJSONObject.getInt("FuelGradeId"));
                }

                if ( dataJSONObject.has("FuelGradeName")) {
                    pumpEndOfTransactionStatus.setFuelGradeName(dataJSONObject.getString("FuelGradeName"));
                }

                if ( dataJSONObject.has("TotalVolume")) {
                    pumpEndOfTransactionStatus.setTotalVolume(dataJSONObject.getDouble("TotalVolume"));
                }

                if ( dataJSONObject.has("TotalAmount")) {
                    pumpEndOfTransactionStatus.setTotalAmount(dataJSONObject.getDouble("TotalAmount"));
                }

                if ( dataJSONObject.has("DateTimeStart")) {
                    String dateTimeStartStr = dataJSONObject.getString("DateTimeStart");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date dateStartTime = formatter.parse(dateTimeStartStr);
                    pumpEndOfTransactionStatus.setDateTimeStart(dateStartTime);
                }

                if ( dataJSONObject.has("DateTime")) {
                    String dateTimeStr = dataJSONObject.getString("DateTime");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date dateTime = formatter.parse(dateTimeStr);
                    pumpEndOfTransactionStatus.setDateTime(dateTime);
                }

                pumpStatus = pumpEndOfTransactionStatus;
            }
            else if (receivedResponseName.equals(PUMP_OFFLINE_STATUS)) {
                PumpOfflineStatus pumpOfflineStatus = new PumpOfflineStatus();

                if ( dataJSONObject.has("Nozzle")) {
                    pumpOfflineStatus.setNozzle(dataJSONObject.getInt("Nozzle"));
                }

                if ( dataJSONObject.has("NozzleUp")) {
                    pumpOfflineStatus.setNozzleUp(dataJSONObject.getInt("NozzleUp"));
                }

                if ( dataJSONObject.has("FuelGradeId")) {
                    pumpOfflineStatus.setFuelGradeId(dataJSONObject.getInt("FuelGradeId"));
                }

                if ( dataJSONObject.has("FuelGradeName")) {
                    pumpOfflineStatus.setFuelGradeName(dataJSONObject.getString("FuelGradeName"));
                }

                if ( dataJSONObject.has("Volume")) {
                    pumpOfflineStatus.setVolume(dataJSONObject.getDouble("Volume"));
                }

                if ( dataJSONObject.has("TCVolume")) {
                    pumpOfflineStatus.setTCVolume(dataJSONObject.getDouble("TCVolume"));
                }

                if ( dataJSONObject.has("Price")) {
                    pumpOfflineStatus.setPrice(dataJSONObject.getDouble("Price"));
                }

                if ( dataJSONObject.has("Amount")) {
                    pumpOfflineStatus.setAmount(dataJSONObject.getDouble("Amount"));
                }

                if ( dataJSONObject.has("Transaction")) {
                    pumpOfflineStatus.setTransaction(dataJSONObject.getInt("Transaction"));
                }

                if ( dataJSONObject.has("TotalVolume")) {
                    pumpOfflineStatus.setTotalVolume(dataJSONObject.getDouble("TotalVolume"));
                }

                if ( dataJSONObject.has("TotalAmount")) {
                    pumpOfflineStatus.setTotalAmount(dataJSONObject.getDouble("TotalAmount"));
                }

                if ( dataJSONObject.has("DateTimeStart")) {
                    String dateTimeStartStr = dataJSONObject.getString("DateTimeStart");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date dateStartTime = formatter.parse(dateTimeStartStr);
                    pumpOfflineStatus.setDateTimeStart(dateStartTime);
                }

                if ( dataJSONObject.has("Tag")) {
                    pumpOfflineStatus.setTag(dataJSONObject.getString("Tag"));
                }

                if ( dataJSONObject.has("Request")) {
                    pumpOfflineStatus.setRequest(dataJSONObject.getString("Request"));
                }

                if ( dataJSONObject.has("LastDateTimeStart")) {
                    String lastDateTimeStartStr = dataJSONObject.getString("LastDateTimeStart");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date lastDateStartTime = formatter.parse(lastDateTimeStartStr);
                    pumpOfflineStatus.setLastDateTimeStart(lastDateStartTime);
                }

                if ( dataJSONObject.has("LastDateTime")) {
                    String lastDateTimeStr = dataJSONObject.getString("LastDateTime");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date lastDateTime = formatter.parse(lastDateTimeStr);
                    pumpOfflineStatus.setLastDateTime(lastDateTime);
                }

                if ( dataJSONObject.has("LastNozzle")) {
                    pumpOfflineStatus.setLastNozzle(dataJSONObject.getInt("LastNozzle"));
                }

                if ( dataJSONObject.has("LastFuelGradeId")) {
                    pumpOfflineStatus.setLastFuelGradeId(dataJSONObject.getInt("LastFuelGradeId"));
                }

                if ( dataJSONObject.has("LastFuelGradeName")) {
                    pumpOfflineStatus.setLastFuelGradeName(dataJSONObject.getString("LastFuelGradeName"));
                }

                if ( dataJSONObject.has("LastTransaction")) {
                    pumpOfflineStatus.setLastTransaction(dataJSONObject.getInt("LastTransaction"));
                }

                if ( dataJSONObject.has("LastVolume")) {
                    pumpOfflineStatus.setLastVolume(dataJSONObject.getDouble("LastVolume"));
                }

                if ( dataJSONObject.has("LastPrice")) {
                    pumpOfflineStatus.setLastPrice(dataJSONObject.getDouble("LastPrice"));
                }

                if ( dataJSONObject.has("LastAmount")) {
                    pumpOfflineStatus.setLastAmount(dataJSONObject.getDouble("LastAmount"));
                }

                if ( dataJSONObject.has("LastTotalVolume")) {
                    pumpOfflineStatus.setLastTotalVolume(dataJSONObject.getDouble("LastTotalVolume"));
                }

                if ( dataJSONObject.has("LastTotalAmount")) {
                    pumpOfflineStatus.setLastTotalAmount(dataJSONObject.getDouble("LastTotalAmount"));
                }

                if ( dataJSONObject.has("LastUser")) {
                    pumpOfflineStatus.setLastUser(dataJSONObject.getString("LastUser"));
                }

                pumpStatus = pumpOfflineStatus;
            }
            else if (receivedResponseName.equals(PUMP_TOTALS)) {
                PumpTotals pumpTotals = new PumpTotals();

                if ( dataJSONObject.has("Nozzle")) {
                    pumpTotals.setNozzle(dataJSONObject.getInt("Nozzle"));
                }

                if ( dataJSONObject.has("Volume")) {
                    pumpTotals.setVolume(dataJSONObject.getDouble("Volume"));
                }

                if ( dataJSONObject.has("Amount")) {
                    pumpTotals.setAmount(dataJSONObject.getDouble("Amount"));
                }

                if ( dataJSONObject.has("Transaction")) {
                    pumpTotals.setTransaction(dataJSONObject.getInt("Transaction"));
                }

                if ( dataJSONObject.has("FuelGradeId")) {
                    pumpTotals.setFuelGradeId(dataJSONObject.getInt("FuelGradeId"));
                }

                if ( dataJSONObject.has("FuelGradeName")) {
                    pumpTotals.setFuelGradeName(dataJSONObject.getString("FuelGradeName"));
                }

                pumpStatus = pumpTotals;
            }
            else if (receivedResponseName.equals(PUMP_PRICES)) {
                PumpPrices pumpPrices = new PumpPrices();
                ArrayList<Double> prices = new ArrayList<Double>();

                if ( dataJSONObject.has("Prices")) {
                    JSONArray pricesJArray = dataJSONObject.getJSONArray("Prices");

                    for (int i = 0; i < pricesJArray.length(); ++i) {
                        prices.add(pricesJArray.getDouble(i));
                    }
                }

                pumpPrices.setPrices(prices);
                pumpStatus = pumpPrices;
            }
            else if (receivedResponseName.equals(PUMP_TAG)) {
                PumpTag pumpTag = new PumpTag();

                if ( dataJSONObject.has("Nozzle")) {
                    pumpTag.setNozzle(dataJSONObject.getInt("Nozzle"));
                }

                if ( dataJSONObject.has("Tag")) {
                    pumpTag.setTag(dataJSONObject.getString("Tag"));
                }

                if ( dataJSONObject.has("FuelGradeId")) {
                    pumpTag.setFuelGradeId(dataJSONObject.getInt("FuelGradeId"));
                }

                if ( dataJSONObject.has("FuelGradeName")) {
                    pumpTag.setFuelGradeName(dataJSONObject.getString("FuelGradeName"));
                }

                pumpStatus = pumpTag;
            }
            else if (receivedResponseName.equals(PUMP_DISPLAY_DATA)) {
                PumpDisplayData pumpDisplayData = new PumpDisplayData();

                if ( dataJSONObject.has("LastNozzle")) {
                    pumpDisplayData.setLastNozzle(dataJSONObject.getInt("LastNozzle"));
                }

                if ( dataJSONObject.has("LastTransaction")) {
                    pumpDisplayData.setLastTransaction(dataJSONObject.getInt("LastTransaction"));
                }

                if ( dataJSONObject.has("Volume")) {
                    pumpDisplayData.setVolume(dataJSONObject.getDouble("Volume"));
                }

                if ( dataJSONObject.has("Amount")) {
                    pumpDisplayData.setAmount(dataJSONObject.getDouble("Amount"));
                }

                if ( dataJSONObject.has("LastFuelGradeId")) {
                    pumpDisplayData.setLastFuelGradeId(dataJSONObject.getInt("LastFuelGradeId"));
                }

                if ( dataJSONObject.has("LastFuelGradeName")) {
                    pumpDisplayData.setLastFuelGradeName(dataJSONObject.getString("LastFuelGradeName"));
                }

                pumpStatus = pumpDisplayData;
            }
            else {
                throw new TTException("Error: response doesn't contain correct Type property", Result.PROTOCOL_ERROR);
            }

            if (pumpStatus != null) {
                if ( dataJSONObject.has("Pump")) {
                    pumpStatus.setPump(dataJSONObject.getInt("Pump"));
                }

                if ( dataJSONObject.has("User")) {
                    pumpStatus.setUser(dataJSONObject.getString("User"));
                }
            }

            mResult = pumpStatus;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
