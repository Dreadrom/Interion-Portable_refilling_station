package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.FuelGrade;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/// <summary>
/// SetFuelGradesConfiguration request
/// </summary>
public class SetFuelGradesConfiguration extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetFuelGradesConfiguration";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    public List<FuelGrade> mFuelGrades;

    public SetFuelGradesConfiguration(List<FuelGrade> fuelGrades) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mFuelGrades = fuelGrades;
    }

    public List<FuelGrade> getFuelGrades() {
        return mFuelGrades;
    }

    public void setFuelGrades(List<FuelGrade> fuelGrades) {
        mFuelGrades = fuelGrades;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mFuelGrades == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONArray fuelGradesJArray = new JSONArray();

        for (int i = 0; i < mFuelGrades.size(); ++i)
        {
            FuelGrade fuelGrade = mFuelGrades.get(i);

            JSONObject fuelGradeJObject = new JSONObject();
            fuelGradeJObject.put("Id", fuelGrade.getId());
            fuelGradeJObject.put("Name", fuelGrade.getName());
            fuelGradeJObject.put("Price", fuelGrade.getPrice());
            fuelGradeJObject.put("ExpansionCoefficient", fuelGrade.getExpansionCoefficient());

            if (fuelGrade.getUseBlendTankParameters()) {
                fuelGradeJObject.put("BlendTank1Id", fuelGrade.getBlendTank1Id());
                fuelGradeJObject.put("BlendTank1Percentage", fuelGrade.getBlendTank1Percentage());
                fuelGradeJObject.put("BlendTank2Id", fuelGrade.getBlendTank2Id());
            }

            fuelGradesJArray.put(fuelGradeJObject);
        }

        JSONObject requestData = new JSONObject();
        requestData.put("FuelGrades", fuelGradesJArray);

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
