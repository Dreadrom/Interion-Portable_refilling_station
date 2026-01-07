package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Permissions;
import com.technotrade.pts2.datastructs.User;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// GetUsersConfiguration request.
/// Gets users configuration: logins and roles
/// </summary>
public class GetUsersConfiguration extends BaseHTTPRequest<List<User>> {
    public static final String REQUEST_NAME = "GetUsersConfiguration";
    public static final String RESPONSE_NAME = "UsersConfiguration";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);

    public GetUsersConfiguration() {
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

            if (!dataJSONObject.has("Users")) {
                throw new TTException("Error: response doesn't contain Users property", Result.PROTOCOL_ERROR);
            }

            JSONArray usersArray = dataJSONObject.getJSONArray("Users");

            List<User> users = new ArrayList<User>();

            for (int usersArrayIterator = 0 ; usersArrayIterator < usersArray.length(); usersArrayIterator++) {
                JSONObject userJSONObject = usersArray.getJSONObject(usersArrayIterator);

                User user = new User();

                if (userJSONObject.has("Id")) {
                    int id = userJSONObject.getInt("Id");
                    user.setId(id);
                }

                if (userJSONObject.has("Login")) {
                    String name = userJSONObject.getString("Login");
                    user.setLogin(name);
                }

                if (userJSONObject.has("Permissions")) {
                    JSONObject permissionsJSONObject = userJSONObject.getJSONObject("Permissions");

                    Permissions permissions = new Permissions();

                    if (permissionsJSONObject.has("Configuration")) {
                        Boolean configurationPermission = permissionsJSONObject.getBoolean("Configuration");
                        permissions.setConfiguration(configurationPermission);
                    }

                    if (permissionsJSONObject.has("Control")) {
                        Boolean controlPermission = permissionsJSONObject.getBoolean("Control");
                        permissions.setControl(controlPermission);
                    }

                    if (permissionsJSONObject.has("Monitoring")) {
                        Boolean monitoringPermission = permissionsJSONObject.getBoolean("Monitoring");
                        permissions.setMonitoring(monitoringPermission);
                    }

                    if (permissionsJSONObject.has("Reports")) {
                        Boolean reportsPermission = permissionsJSONObject.getBoolean("Reports");
                        permissions.setReports(reportsPermission);
                    }
                }

                users.add(user);
            }

            mResult = users;
        } catch(JSONException e) {
            throw new TTException("Error occurred during parsing JSON", Result.JSON_PARSE_ERROR);
        } catch(Exception e) {
            throw new TTException(e.getMessage());
        }
        
        return true;
    }
}