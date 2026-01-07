package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.ReportPumpTransaction;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/// <summary>
/// ReportGetPumpTransactions request
/// </summary>
public class ReportGetPumpTransactions extends BaseHTTPRequest<List<ReportPumpTransaction>> {
    public static final String REQUEST_NAME = "ReportGetPumpTransactions";
    public static final String RESPONSE_NAME = "ReportPumpTransactions";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mPump = 0;
    public Date mDateTimeStart;
    public Date mDateTimeEnd;

    public ReportGetPumpTransactions(int pump, Date dateTimeStart, Date dateTimeEnd) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPump = pump;
        mDateTimeStart = dateTimeStart;
        mDateTimeEnd = dateTimeEnd;
    }

    public int getPump() {
        return mPump;
    }

    public void setPump(int pump) {
        mPump = pump;
    }

    public Date getDateTimeStart() {
        return mDateTimeStart;
    }

    public void setDateTimeStart(Date date) {
        mDateTimeStart = date;
    }

    public Date getDateTimeEnd() {
        return mDateTimeEnd;
    }

    public void setDateTimeEnd(Date date) {
        mDateTimeEnd = date;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Pump",mPump);

        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        String dateTimeStartString = sdf.format(mDateTimeStart);
        requestData.put("DateTimeStart", dateTimeStartString);

        String dateTimeEndString = sdf.format(mDateTimeEnd);
        requestData.put("DateTimeEnd", dateTimeEndString);

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

            JSONArray dataJSONArray = jsonObject.getJSONArray("Data");

            ArrayList<ReportPumpTransaction> reportPumpTransactions = new ArrayList<ReportPumpTransaction>();

            for (int i = 0; i < dataJSONArray.length(); ++i) {
                JSONObject reportPumpTransactionJObject = dataJSONArray.getJSONObject(i);
                if (reportPumpTransactionJObject == null) {
                    break;
                }

                ReportPumpTransaction reportPumpTransaction = new ReportPumpTransaction();

                if (!reportPumpTransactionJObject.has("DateTimeStart")) {
                    throw new TTException("Error: response doesn't contain DateTimeStart property", Result.PROTOCOL_ERROR);
                }

                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                String dateTimeStartStr = reportPumpTransactionJObject.getString("DateTimeStart");
                Date dateTimeStart  = formatter.parse(dateTimeStartStr);
                reportPumpTransaction.setDateTimeStart(dateTimeStart);

                if (!reportPumpTransactionJObject.has("DateTime")) {
                    throw new TTException("Error: response doesn't contain DateTime property", Result.PROTOCOL_ERROR);
                }

                String dateTimeStr = reportPumpTransactionJObject.getString("DateTime");
                Date dateTime  = formatter.parse(dateTimeStr);
                reportPumpTransaction.setDateTime(dateTime);

                if (!reportPumpTransactionJObject.has("Pump")) {
                    throw new TTException("Error: response doesn't contain Pump property", Result.PROTOCOL_ERROR);
                }

                reportPumpTransaction.setPump(reportPumpTransactionJObject.getInt("Pump"));

                if (!reportPumpTransactionJObject.has("Nozzle")) {
                    throw new TTException("Error: response doesn't contain Nozzle property", Result.PROTOCOL_ERROR);
                }

                reportPumpTransaction.setNozzle(reportPumpTransactionJObject.getInt("Nozzle"));

                if (!reportPumpTransactionJObject.has("Transaction")) {
                    throw new TTException("Error: response doesn't contain Transaction property", Result.PROTOCOL_ERROR);
                }

                reportPumpTransaction.setTransaction(reportPumpTransactionJObject.getInt("Transaction"));

                if (!reportPumpTransactionJObject.has("Volume")) {
                    throw new TTException("Error: response doesn't contain Volume property", Result.PROTOCOL_ERROR);
                }

                reportPumpTransaction.setVolume(reportPumpTransactionJObject.getDouble("Volume"));

                if (!reportPumpTransactionJObject.has("TCVolume")) {
                    throw new TTException("Error: response doesn't contain TCVolume property", Result.PROTOCOL_ERROR);
                }

                reportPumpTransaction.setTCVolume(reportPumpTransactionJObject.getDouble("TCVolume"));

                if (!reportPumpTransactionJObject.has("Price")) {
                    throw new TTException("Error: response doesn't contain Price property", Result.PROTOCOL_ERROR);
                }

                reportPumpTransaction.setPrice(reportPumpTransactionJObject.getDouble("Price"));

                if (!reportPumpTransactionJObject.has("Amount")) {
                    throw new TTException("Error: response doesn't contain Amount property", Result.PROTOCOL_ERROR);
                }

                reportPumpTransaction.setAmount(reportPumpTransactionJObject.getDouble("Amount"));

                if (!reportPumpTransactionJObject.has("TotalVolume")) {
                    throw new TTException("Error: response doesn't contain TotalVolume property", Result.PROTOCOL_ERROR);
                }

                reportPumpTransaction.setTotalVolume(reportPumpTransactionJObject.getDouble("TotalVolume"));

                if (!reportPumpTransactionJObject.has("TotalAmount")) {
                    throw new TTException("Error: response doesn't contain TotalAmount property", Result.PROTOCOL_ERROR);
                }

                reportPumpTransaction.setTotalAmount(reportPumpTransactionJObject.getDouble("TotalAmount"));

                if (!reportPumpTransactionJObject.has("UserId")) {
                    throw new TTException("Error: response doesn't contain UserId property", Result.PROTOCOL_ERROR);
                }

                reportPumpTransaction.setUserId(reportPumpTransactionJObject.getInt("UserId"));

                if (reportPumpTransactionJObject.has("Tag")) {
                    reportPumpTransaction.setTag(reportPumpTransactionJObject.getString("Tag"));
                }

                if ( reportPumpTransactionJObject.has("FuelGradeId")) {
                    reportPumpTransaction.setFuelGradeId(reportPumpTransactionJObject.getInt("FuelGradeId"));
                }

                if ( reportPumpTransactionJObject.has("FuelGradeName")) {
                    reportPumpTransaction.setFuelGradeName(reportPumpTransactionJObject.getString("FuelGradeName"));
                }

                reportPumpTransactions.add(reportPumpTransaction);
            }

            mResult = reportPumpTransactions;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
