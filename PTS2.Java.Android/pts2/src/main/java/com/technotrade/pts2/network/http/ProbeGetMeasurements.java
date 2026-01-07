package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.InTankDelivery;
import com.technotrade.pts2.datastructs.ProbeMeasurements;
import com.technotrade.pts2.enumeration.AlarmType;
import com.technotrade.pts2.enumeration.ProbeStatus;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

/// <summary>
/// ProbeGetMeasurements request.
/// Gets probes status, list of measured values and measured values
/// </summary>
public class ProbeGetMeasurements extends BaseHTTPRequest<ProbeMeasurements> {
    public static final String REQUEST_NAME = "ProbeGetMeasurements";
    public static final String RESPONSE_NAME = "ProbeMeasurements";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    private int mProbe = 0;

    public ProbeGetMeasurements(int probe) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mProbe = probe;
    }

    public int getProbe() {
        return mProbe;
    }

    public void setProbe(int probe) {
        mProbe = probe;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        JSONObject requestData = new JSONObject();

        requestData.put("Probe", mProbe);

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

            ProbeMeasurements probeMeasurements = new ProbeMeasurements();

            if (!dataJSONObject.has("Probe")) {
                throw new TTException("Error: response doesn't contain Probe property", Result.PROTOCOL_ERROR);
            }

            probeMeasurements.setProbe(dataJSONObject.getInt("Probe"));

            if (!dataJSONObject.has("Status")) {
                throw new TTException("Error: response doesn't contain Status property", Result.PROTOCOL_ERROR);
            }

            probeMeasurements.setStatus(ProbeStatus.valueOf(dataJSONObject.getString("Status")));

            if (dataJSONObject.has("Alarms")) {
                HashSet<AlarmType> alarms = new HashSet<AlarmType>();
                JSONArray alarmsJArray = dataJSONObject.getJSONArray("Alarms");

                for (int i = 0; i < alarmsJArray.length(); ++i) {
                    alarms.add(AlarmType.valueOf(alarmsJArray.getString(i)));
                }

                probeMeasurements.setAlarms(alarms);
            }

            if (dataJSONObject.has("ProductHeight")) {
                probeMeasurements.setProductHeight(dataJSONObject.getDouble("ProductHeight"));
            }

            if (dataJSONObject.has("WaterHeight")) {
                probeMeasurements.setWaterHeight(dataJSONObject.getDouble("WaterHeight"));
            }

            if (dataJSONObject.has("Temperature")) {
                probeMeasurements.setTemperature(dataJSONObject.getDouble("Temperature"));
            }

            if (dataJSONObject.has("ProductVolume")) {
                probeMeasurements.setProductVolume(dataJSONObject.getDouble("ProductVolume"));
            }

            if (dataJSONObject.has("WaterVolume")) {
                probeMeasurements.setWaterVolume(dataJSONObject.getDouble("WaterVolume"));
            }

            if (dataJSONObject.has("ProductUllage")) {
                probeMeasurements.setProductUllage(dataJSONObject.getDouble("ProductUllage"));
            }

            if (dataJSONObject.has("ProductTCVolume")) {
                probeMeasurements.setProductTCVolume(dataJSONObject.getDouble("ProductTCVolume"));
            }

            if (dataJSONObject.has("ProductDensity")) {
                probeMeasurements.setProductDensity(dataJSONObject.getDouble("ProductDensity"));
            }

            if (dataJSONObject.has("ProductMass")) {
                probeMeasurements.setProductMass(dataJSONObject.getDouble("ProductMass"));
            }

            if (dataJSONObject.has("TankFillingPercentage")) {
                probeMeasurements.setTankFillingPercentage(dataJSONObject.getInt("TankFillingPercentage"));
            }

            if (dataJSONObject.has("HighProductAlarm")) {
                probeMeasurements.setHighProductAlarm(dataJSONObject.getBoolean("HighProductAlarm"));
            }

            if (dataJSONObject.has("LowProductAlarm")) {
                probeMeasurements.setLowProductAlarm(dataJSONObject.getBoolean("LowProductAlarm"));
            }

            if (dataJSONObject.has("LastInTankDeliveryStart")) {
                JSONObject lastInTankDeliveryStartjObject = dataJSONObject.getJSONObject("LastInTankDeliveryStart");

                InTankDelivery lastInTankDeliveryStart = new InTankDelivery();

                if (lastInTankDeliveryStartjObject.has("DateTime")) {
                    String dateTimeStr = dataJSONObject.getString("DateTime");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date dateTime = formatter.parse(dateTimeStr);
                    lastInTankDeliveryStart.setDateTime(dateTime);
                }

                if (lastInTankDeliveryStartjObject.has("ProductHeight")) {
                    lastInTankDeliveryStart.setProductHeight(lastInTankDeliveryStartjObject.getDouble("ProductHeight"));
                }

                if (lastInTankDeliveryStartjObject.has("WaterHeight")) {
                    lastInTankDeliveryStart.setWaterHeight(lastInTankDeliveryStartjObject.getDouble("WaterHeight"));
                }

                if (lastInTankDeliveryStartjObject.has("Temperature")) {
                    lastInTankDeliveryStart.setTemperature(lastInTankDeliveryStartjObject.getDouble("Temperature"));
                }

                if (lastInTankDeliveryStartjObject.has("ProductVolume")) {
                    lastInTankDeliveryStart.setProductVolume(lastInTankDeliveryStartjObject.getDouble("ProductVolume"));
                }

                if (lastInTankDeliveryStartjObject.has("ProductTCVolume")) {
                    lastInTankDeliveryStart.setProductTCVolume(lastInTankDeliveryStartjObject.getDouble("ProductTCVolume"));
                }

                if (lastInTankDeliveryStartjObject.has("ProductDensity")) {
                    lastInTankDeliveryStart.setProductDensity(lastInTankDeliveryStartjObject.getDouble("ProductDensity"));
                }

                if (lastInTankDeliveryStartjObject.has("ProductMass")) {
                    lastInTankDeliveryStart.setProductMass(lastInTankDeliveryStartjObject.getDouble("ProductMass"));
                }

                probeMeasurements.setLastInTankDeliveryStart(lastInTankDeliveryStart);
            }

            if (dataJSONObject.has("LastInTankDeliveryEnd")) {
                JSONObject lastInTankDeliveryEndjObject = dataJSONObject.getJSONObject("LastInTankDeliveryEnd");

                InTankDelivery lastInTankDeliveryEnd = new InTankDelivery();

                if (lastInTankDeliveryEndjObject.has("DateTime")) {
                    String dateTimeStr = dataJSONObject.getString("DateTime");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date dateTime = formatter.parse(dateTimeStr);
                    lastInTankDeliveryEnd.setDateTime(dateTime);
                }

                if (lastInTankDeliveryEndjObject.has("ProductHeight")) {
                    lastInTankDeliveryEnd.setProductHeight(lastInTankDeliveryEndjObject.getDouble("ProductHeight"));
                }

                if (lastInTankDeliveryEndjObject.has("WaterHeight")) {
                    lastInTankDeliveryEnd.setWaterHeight(lastInTankDeliveryEndjObject.getDouble("WaterHeight"));
                }

                if (lastInTankDeliveryEndjObject.has("Temperature")) {
                    lastInTankDeliveryEnd.setTemperature(lastInTankDeliveryEndjObject.getDouble("Temperature"));
                }

                if (lastInTankDeliveryEndjObject.has("ProductVolume")) {
                    lastInTankDeliveryEnd.setProductVolume(lastInTankDeliveryEndjObject.getDouble("ProductVolume"));
                }

                if (lastInTankDeliveryEndjObject.has("ProductTCVolume")) {
                    lastInTankDeliveryEnd.setProductTCVolume(lastInTankDeliveryEndjObject.getDouble("ProductTCVolume"));
                }

                if (lastInTankDeliveryEndjObject.has("ProductDensity")) {
                    lastInTankDeliveryEnd.setProductDensity(lastInTankDeliveryEndjObject.getDouble("ProductDensity"));
                }

                if (lastInTankDeliveryEndjObject.has("ProductMass")) {
                    lastInTankDeliveryEnd.setProductMass(lastInTankDeliveryEndjObject.getDouble("ProductMass"));
                }

                probeMeasurements.setLastInTankDeliveryEnd(lastInTankDeliveryEnd);
            }

            if (dataJSONObject.has("LastInTankDelivery")) {
                JSONObject lastInTankDeliveryjObject = dataJSONObject.getJSONObject("LastInTankDelivery");

                InTankDelivery lastInTankDelivery = new InTankDelivery();

                if (lastInTankDeliveryjObject.has("DateTime")) {
                    String dateTimeStr = dataJSONObject.getString("DateTime");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                    Date dateTime = formatter.parse(dateTimeStr);
                    lastInTankDelivery.setDateTime(dateTime);
                }

                if (lastInTankDeliveryjObject.has("ProductHeight")) {
                    lastInTankDelivery.setProductHeight(lastInTankDeliveryjObject.getDouble("ProductHeight"));
                }

                if (lastInTankDeliveryjObject.has("WaterHeight")) {
                    lastInTankDelivery.setWaterHeight(lastInTankDeliveryjObject.getDouble("WaterHeight"));
                }

                if (lastInTankDeliveryjObject.has("Temperature")) {
                    lastInTankDelivery.setTemperature(lastInTankDeliveryjObject.getDouble("Temperature"));
                }

                if (lastInTankDeliveryjObject.has("ProductVolume")) {
                    lastInTankDelivery.setProductVolume(lastInTankDeliveryjObject.getDouble("ProductVolume"));
                }

                if (lastInTankDeliveryjObject.has("ProductTCVolume")) {
                    lastInTankDelivery.setProductTCVolume(lastInTankDeliveryjObject.getDouble("ProductTCVolume"));
                }

                if (lastInTankDeliveryjObject.has("ProductDensity")) {
                    lastInTankDelivery.setProductDensity(lastInTankDeliveryjObject.getDouble("ProductDensity"));
                }

                if (lastInTankDeliveryjObject.has("ProductMass")) {
                    lastInTankDelivery.setProductMass(lastInTankDeliveryjObject.getDouble("ProductMass"));
                }

                if (lastInTankDeliveryjObject.has("PumpsDispensedVolume")) {
                    lastInTankDelivery.setPumpsDispensedVolume(lastInTankDeliveryjObject.getDouble("PumpsDispensedVolume"));
                }

                probeMeasurements.setLastInTankDelivery(lastInTankDelivery);
            }

            if ( dataJSONObject.has("FuelGradeId")) {
                probeMeasurements.setFuelGradeId(dataJSONObject.getInt("FuelGradeId"));
            }

            if ( dataJSONObject.has("FuelGradeName")) {
                probeMeasurements.setFuelGradeName(dataJSONObject.getString("FuelGradeName"));
            }

            mResult = probeMeasurements;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
