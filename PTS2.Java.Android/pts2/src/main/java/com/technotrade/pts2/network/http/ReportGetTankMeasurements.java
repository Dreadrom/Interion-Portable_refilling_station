package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.ReportTankMeasurement;
import com.technotrade.pts2.enumeration.ProbeStatus;
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
/// ReportGetTankMeasurements request
/// </summary>
public class ReportGetTankMeasurements extends BaseHTTPRequest<List<ReportTankMeasurement>> {
    public static final String REQUEST_NAME = "ReportGetTankMeasurements";
    public static final String RESPONSE_NAME = "ReportTankMeasurements";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public int mTank;
    public Date mDateTimeStart;
    public Date mDateTimeEnd;

    public ReportGetTankMeasurements(int tank, Date dateTimeStart, Date dateTimeEnd) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mTank = tank;
        mDateTimeStart = dateTimeStart;
        mDateTimeEnd = dateTimeEnd;
    }

    public int getTank() {
        return mTank;
    }

    public void setTank(int tank) {
        mTank = tank;
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

        requestData.put("Tank", mTank);

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

            ArrayList<ReportTankMeasurement> reportTankMeasurements = new ArrayList<ReportTankMeasurement>();

            for (int i = 0; i < dataJSONArray.length(); ++i)
            {
                JSONObject reportTankMeasurementJObject = dataJSONArray.getJSONObject(i);
                if (reportTankMeasurementJObject == null) {
                    break;
                }

                ReportTankMeasurement reportTankMeasurement = new ReportTankMeasurement();

                if (reportTankMeasurementJObject.has("DateTime")) {
                    String dateTimeStr = reportTankMeasurementJObject.getString("DateTime");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date dateTime = formatter.parse(dateTimeStr);
                    reportTankMeasurement.setDateTime(dateTime);
                }

                if (reportTankMeasurementJObject.has("Tank")) {
                    reportTankMeasurement.setTank(reportTankMeasurementJObject.getInt("Tank"));
                }

                if (reportTankMeasurementJObject.has("Status")) {
                    String status = reportTankMeasurementJObject.getString("Status");
                    reportTankMeasurement.setStatus(ProbeStatus.valueOf(status));
                }

                if (reportTankMeasurementJObject.has("Alarms")) {
                    JSONArray alarmsJArray = reportTankMeasurementJObject.getJSONArray("Alarms");
                    ArrayList<String> alarms = new ArrayList<String>();

                    for (int alarmsIterator = 0; alarmsIterator < alarmsJArray.length(); ++alarmsIterator) {
                        alarms.add(alarmsJArray.getString(alarmsIterator));
                    }

                    reportTankMeasurement.setAlarms(alarms);
                }

                if (reportTankMeasurementJObject.has("ProductHeight")) {
                    reportTankMeasurement.setProductHeight(reportTankMeasurementJObject.getDouble("ProductHeight"));
                }

                if (reportTankMeasurementJObject.has("WaterHeight")) {
                    reportTankMeasurement.setWaterHeight(reportTankMeasurementJObject.getDouble("WaterHeight"));
                }

                if (reportTankMeasurementJObject.has("Temperature")) {
                    reportTankMeasurement.setTemperature(reportTankMeasurementJObject.getDouble("Temperature"));
                }

                if (reportTankMeasurementJObject.has("ProductVolume")) {
                    reportTankMeasurement.setProductVolume(reportTankMeasurementJObject.getDouble("ProductVolume"));
                }

                if (reportTankMeasurementJObject.has("ProductDensity")) {
                    reportTankMeasurement.setProductDensity(reportTankMeasurementJObject.getDouble("ProductDensity"));
                }

                if (reportTankMeasurementJObject.has("ProductMass")) {
                    reportTankMeasurement.setProductMass(reportTankMeasurementJObject.getDouble("ProductMass"));
                }

                if (reportTankMeasurementJObject.has("WaterVolume")) {
                    reportTankMeasurement.setWaterVolume(reportTankMeasurementJObject.getDouble("WaterVolume"));
                }

                if (reportTankMeasurementJObject.has("ProductUllage")) {
                    reportTankMeasurement.setProductUllage(reportTankMeasurementJObject.getDouble("ProductUllage"));
                }

                if (reportTankMeasurementJObject.has("ProductTCVolume")) {
                    reportTankMeasurement.setProductTCVolume(reportTankMeasurementJObject.getDouble("ProductTCVolume"));
                }

                if (reportTankMeasurementJObject.has("TankFillingPercentage")) {
                    reportTankMeasurement.setTankFillingPercentage(reportTankMeasurementJObject.getInt("TankFillingPercentage"));
                }

                if (reportTankMeasurementJObject.has("FuelGradeId")) {
                    reportTankMeasurement.setFuelGradeId(reportTankMeasurementJObject.getInt("FuelGradeId"));
                }

                if (reportTankMeasurementJObject.has("FuelGradeName")) {
                    reportTankMeasurement.setFuelGradeName(reportTankMeasurementJObject.getString("FuelGradeName"));
                }

                reportTankMeasurements.add(reportTankMeasurement);
            }

            mResult = reportTankMeasurements;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
