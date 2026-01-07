package com.technotrade.pts2.network.http;

import static com.technotrade.pts2.enumeration.PumpAuthorizeType.FULLTANK;

import com.technotrade.pts2.datastructs.PumpAuthorizeConfirmation;
import com.technotrade.pts2.datastructs.PumpAuthorizeData;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/// <summary>
/// PumpAuthorize request.
/// Sets preset and nozzle price, allows filling for specified nozzle.
/// </summary>
public class PumpAuthorize extends BaseHTTPRequest<PumpAuthorizeConfirmation> {
    public static final String REQUEST_NAME = "PumpAuthorize";
    public static final String RESPONSE_NAME = "PumpAuthorizeConfirmation";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    public PumpAuthorizeData mPumpAuthorizeData;

    public PumpAuthorizeData getPumpAuthorizeData() {
        return mPumpAuthorizeData;
    }

    public PumpAuthorize(PumpAuthorizeData pumpAuthorizeData) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mPumpAuthorizeData = pumpAuthorizeData;
    }

    public void setPumpAuthorizeData(PumpAuthorizeData pumpAuthorizeData) {
        mPumpAuthorizeData = pumpAuthorizeData;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mPumpAuthorizeData == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONObject requestData = new JSONObject();

        requestData.put("Pump", mPumpAuthorizeData.getPump());

        switch(mPumpAuthorizeData.getNozzleOrFuelIdSelector()) {
            case NOZZLE:
                requestData.put("Nozzle", mPumpAuthorizeData.getNozzle());
                break;
            case NOZZLES:
                JSONArray nozzlesJArray = new JSONArray();
                ArrayList<Integer> nozzles = mPumpAuthorizeData.getNozzles();
                for (int i = 0; i < nozzles.size(); ++i) {
                    nozzlesJArray.put(nozzles.get(i));
                }

                requestData.put("Nozzles", nozzlesJArray);
                break;
            case FUELGRADEID:
                requestData.put("FuelGradeId", mPumpAuthorizeData.getFuelGradeId());
                break;
            case FUELGRADEIDS:
                JSONArray fuelGradeIdsJArray = new JSONArray();
                ArrayList<Integer> fuelGradeIds = mPumpAuthorizeData.getFuelGradeIds();
                for (int i = 0; i < fuelGradeIds.size(); ++i) {
                    fuelGradeIdsJArray.put(fuelGradeIds.get(i));
                }

                requestData.put("FuelGradeIds", fuelGradeIdsJArray);
                break;
            case NONE:
            default:
                break;
        }

        requestData.put("Type", mPumpAuthorizeData.getType().getValue());

        if (mPumpAuthorizeData.getType() != FULLTANK) {
            requestData.put("Dose",mPumpAuthorizeData.getDose());
        }

        if (mPumpAuthorizeData.getPriceEnabled()) {
            requestData.put("Price",mPumpAuthorizeData.getPrice());
        }

        if (mPumpAuthorizeData.getTransactionEnabled()) {
            requestData.put("Transaction",mPumpAuthorizeData.getTransaction());
        }

        requestData.put("AutoCloseTransaction",String.valueOf(mPumpAuthorizeData.getAutoCloseTransaction()).toLowerCase());

        if (mPumpAuthorizeData.getTagEnabled()) {
            requestData.put("Tag",mPumpAuthorizeData.getTag());
        }

        requestData.put("AuthorizeWithoutTag",String.valueOf(mPumpAuthorizeData.getAuthorizeWithoutTag()).toLowerCase());
        requestData.put("NoTagVerification",String.valueOf(mPumpAuthorizeData.getNoTagVerification()).toLowerCase());

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

            PumpAuthorizeConfirmation pumpAuthorizeConfirmation = new PumpAuthorizeConfirmation();

            if (!dataJSONObject.has("Pump")) {
                throw new TTException("Error: response doesn't contain Pump property", Result.PROTOCOL_ERROR);
            }

            int pump = dataJSONObject.getInt("Pump");
            pumpAuthorizeConfirmation.setPump(pump);

            if (!dataJSONObject.has("Transaction")) {
                throw new TTException("Error: response doesn't contain Transaction property", Result.PROTOCOL_ERROR);
            }

            int transaction = dataJSONObject.getInt("Transaction");
            pumpAuthorizeConfirmation.setTransaction(transaction);

            mResult = pumpAuthorizeConfirmation;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
