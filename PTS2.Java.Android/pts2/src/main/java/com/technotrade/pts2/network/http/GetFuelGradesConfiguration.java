package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.FuelGrade;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/// <summary>
/// GetFuelGradesConfiguration request.
/// Gets fuel grades configuration: fuel products codes, names and prices
/// </summary>
public class GetFuelGradesConfiguration extends BaseHTTPRequest<List<FuelGrade>> {
    public static final String REQUEST_NAME = "GetFuelGradesConfiguration";
    public static final String RESPONSE_NAME = "FuelGradesConfiguration";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetFuelGradesConfiguration() {
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

            if (!dataJSONObject.has("FuelGrades")) {
                throw new TTException("Error: response doesn't contain FuelGrades property", Result.PROTOCOL_ERROR);
            }

            JSONArray fuelGradesArray = dataJSONObject.getJSONArray("FuelGrades");

            List<FuelGrade> fuelsGrades = new ArrayList<FuelGrade>();

            for (int fuelGradeIterator = 0 ; fuelGradeIterator < fuelGradesArray.length(); ++fuelGradeIterator) {
                FuelGrade fuelGrade = new FuelGrade();
                JSONObject fuelGradeJSONObject = fuelGradesArray.getJSONObject(fuelGradeIterator);

                fuelGrade.setId(fuelGradeIterator);

                if (fuelGradeJSONObject.has("Id")) {
                    int id = fuelGradeJSONObject.getInt("Id");
                    fuelGrade.setId(id);
                }

                if (fuelGradeJSONObject.has("Name")) {
                    String name = fuelGradeJSONObject.getString("Name");
                    fuelGrade.setName(name);
                }

                if (fuelGradeJSONObject.has("Price")) {
                    String price = fuelGradeJSONObject.getString("Price");
                    fuelGrade.setPrice(new BigDecimal(price));
                }

                if (fuelGradeJSONObject.has("ExpansionCoefficient")) {
                    String expansionCoefficient = fuelGradeJSONObject.getString("ExpansionCoefficient");
                    fuelGrade.setExpansionCoefficient(new BigDecimal(expansionCoefficient));
                }

                if (fuelGradeJSONObject.has("BlendTank1Id")) {
                    fuelGrade.setBlendTank1Id(fuelGradeJSONObject.getInt("BlendTank1Id"));
                }

                if (fuelGradeJSONObject.has("BlendTank1Percentage")) {
                    fuelGrade.setBlendTank1Percentage(fuelGradeJSONObject.getInt("BlendTank1Percentage"));
                }

                if (fuelGradeJSONObject.has("BlendTank2Id")) {
                    fuelGrade.setBlendTank2Id(fuelGradeJSONObject.getInt("BlendTank2Id"));
                }

                fuelsGrades.add(fuelGrade);
            }

            mResult = fuelsGrades;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}
