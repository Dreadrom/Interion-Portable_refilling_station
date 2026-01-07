package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.InTankDelivery;
import com.technotrade.pts2.datastructs.ReportInTankDelivery;
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
/// ReportGetInTankDeliveries request.
/// Gets list of in-tank deliveries saved
/// </summary>
public class ReportGetInTankDeliveries extends BaseHTTPRequest<List<ReportInTankDelivery>> {
    public static final String REQUEST_NAME = "ReportGetInTankDeliveries";
    public static final String RESPONSE_NAME = "ReportInTankDeliveries";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public int mTank;
    public Date mDateTimeStart;
    public Date mDateTimeEnd;

    public ReportGetInTankDeliveries(int tank, Date dateTimeStart, Date dateTimeEnd) {
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

            ArrayList<ReportInTankDelivery> reportInTankDeliveries = new ArrayList<>();

            for (int i = 0; i < dataJSONArray.length(); ++i)
            {
                JSONObject reportInTankDeliveryJObject = dataJSONArray.getJSONObject(i);
                if (reportInTankDeliveryJObject == null) {
                    break;
                }

                ReportInTankDelivery reportInTankDelivery = new ReportInTankDelivery();

                if (reportInTankDeliveryJObject.has("Tank")) {
                    reportInTankDelivery.setTank(reportInTankDeliveryJObject.getInt("Tank"));
                }

                if (reportInTankDeliveryJObject.has("FuelGradeId")) {
                    reportInTankDelivery.setFuelGradeId(reportInTankDeliveryJObject.getInt("Tank"));
                }

                if (reportInTankDeliveryJObject.has("FuelGradeName")) {
                    reportInTankDelivery.setFuelGradeName(reportInTankDeliveryJObject.getString("FuelGradeName"));
                }

                if (reportInTankDeliveryJObject.has("StartValues")) {
                    JSONObject startValuesjObject = reportInTankDeliveryJObject.getJSONObject("StartValues");

                    InTankDelivery startValues = new InTankDelivery();

                    if (startValuesjObject.has("DateTime")) {
                        String dateTimeStr = startValuesjObject.getString("DateTime");
                        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                        Date dateTime = formatter.parse(dateTimeStr);
                        startValues.setDateTime(dateTime);
                    }

                    if (startValuesjObject.has("ProductHeight")) {
                        startValues.setProductHeight(startValuesjObject.getDouble("ProductHeight"));
                    }

                    if (startValuesjObject.has("WaterHeight")) {
                        startValues.setWaterHeight(startValuesjObject.getDouble("WaterHeight"));
                    }

                    if (startValuesjObject.has("Temperature")) {
                        startValues.setTemperature(startValuesjObject.getDouble("Temperature"));
                    }

                    if (startValuesjObject.has("ProductVolume")) {
                        startValues.setProductVolume(startValuesjObject.getDouble("ProductVolume"));
                    }

                    if (startValuesjObject.has("ProductTCVolume")) {
                        startValues.setProductTCVolume(startValuesjObject.getDouble("ProductTCVolume"));
                    }

                    if (startValuesjObject.has("ProductDensity")) {
                        startValues.setProductDensity(startValuesjObject.getDouble("ProductDensity"));
                    }

                    if (startValuesjObject.has("ProductMass")) {
                        startValues.setProductMass(startValuesjObject.getDouble("ProductMass"));
                    }

                    reportInTankDelivery.setStartValues(startValues);
                }

                if (reportInTankDeliveryJObject.has("EndValues")) {
                    JSONObject endValuesjObject = reportInTankDeliveryJObject.getJSONObject("EndValues");

                    InTankDelivery endValues = new InTankDelivery();

                    if (endValuesjObject.has("DateTime")) {
                        String dateTimeStr = endValuesjObject.getString("DateTime");
                        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                        Date dateTime = formatter.parse(dateTimeStr);
                        endValues.setDateTime(dateTime);
                    }

                    if (endValuesjObject.has("ProductHeight")) {
                        endValues.setProductHeight(endValuesjObject.getDouble("ProductHeight"));
                    }

                    if (endValuesjObject.has("WaterHeight")) {
                        endValues.setWaterHeight(endValuesjObject.getDouble("WaterHeight"));
                    }

                    if (endValuesjObject.has("Temperature")) {
                        endValues.setTemperature(endValuesjObject.getDouble("Temperature"));
                    }

                    if (endValuesjObject.has("ProductVolume")) {
                        endValues.setProductVolume(endValuesjObject.getDouble("ProductVolume"));
                    }

                    if (endValuesjObject.has("ProductTCVolume")) {
                        endValues.setProductTCVolume(endValuesjObject.getDouble("ProductTCVolume"));
                    }

                    if (endValuesjObject.has("ProductDensity")) {
                        endValues.setProductDensity(endValuesjObject.getDouble("ProductDensity"));
                    }

                    if (endValuesjObject.has("ProductMass")) {
                        endValues.setProductMass(endValuesjObject.getDouble("ProductMass"));
                    }

                    reportInTankDelivery.setEndValues(endValues);
                }

                if (reportInTankDeliveryJObject.has("AbsoluteValues")) {
                    JSONObject absoluteValuesjObject = reportInTankDeliveryJObject.getJSONObject("AbsoluteValues");

                    InTankDelivery absoluteValues = new InTankDelivery();

                    if (absoluteValuesjObject.has("ProductHeight")) {
                        absoluteValues.setProductHeight(absoluteValuesjObject.getDouble("ProductHeight"));
                    }

                    if (absoluteValuesjObject.has("WaterHeight")) {
                        absoluteValues.setWaterHeight(absoluteValuesjObject.getDouble("WaterHeight"));
                    }

                    if (absoluteValuesjObject.has("Temperature")) {
                        absoluteValues.setTemperature(absoluteValuesjObject.getDouble("Temperature"));
                    }

                    if (absoluteValuesjObject.has("ProductVolume")) {
                        absoluteValues.setProductVolume(absoluteValuesjObject.getDouble("ProductVolume"));
                    }

                    if (absoluteValuesjObject.has("ProductTCVolume")) {
                        absoluteValues.setProductTCVolume(absoluteValuesjObject.getDouble("ProductTCVolume"));
                    }

                    if (absoluteValuesjObject.has("ProductDensity")) {
                        absoluteValues.setProductDensity(absoluteValuesjObject.getDouble("ProductDensity"));
                    }

                    if (absoluteValuesjObject.has("ProductMass")) {
                        absoluteValues.setProductMass(absoluteValuesjObject.getDouble("ProductMass"));
                    }

                    if (absoluteValuesjObject.has("PumpsDispensedVolume")) {
                        absoluteValues.setPumpsDispensedVolume(absoluteValuesjObject.getDouble("PumpsDispensedVolume"));
                    }

                    reportInTankDelivery.setAbsoluteValues(absoluteValues);
                }

                reportInTankDeliveries.add(reportInTankDelivery);
            }

            mResult = reportInTankDeliveries;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
