package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.Permissions;
import com.technotrade.pts2.datastructs.User;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/// <summary>
/// SetUsersConfiguration request
/// </summary>
public class SetUsersConfiguration extends BaseHTTPRequest<Boolean> {
    public static final String REQUEST_NAME = "SetUsersConfiguration";
    public static final String RESPONSE_NAME = "";
    public static final String KEY = GetKeyStatic(REQUEST_NAME, RESPONSE_NAME);
    public List<User> mUsers;

    public SetUsersConfiguration(List<User> users) {
        super(REQUEST_NAME, RESPONSE_NAME);

        mUsers = users;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public void setUsers(List<User> users) {
        mUsers = users;
    }

    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {

        if (mUsers == null) {
            throw new TTException("Error: No incoming data", Result.PROTOCOL_ERROR);
        }

        JSONArray usersJArray = new JSONArray();

        for (int i = 0; i < mUsers.size(); ++i)
        {
            User user = mUsers.get(i);

            JSONObject userJObject = new JSONObject();
            userJObject.put("Id", user.getId());
            userJObject.put("Login", user.getLogin());
            userJObject.put("Password", user.getPassword());

            Permissions permissions = user.getPermissions();

            JSONObject permissionsJObject = new JSONObject();
            permissionsJObject.put("Configuration", permissions.getConfiguration());
            permissionsJObject.put("Control", permissions.getControl());
            permissionsJObject.put("Monitoring", permissions.getMonitoring());
            permissionsJObject.put("Reports", permissions.getReports());

            userJObject.put("Permissions", permissionsJObject);

            usersJArray.put(userJObject);
        }

        JSONObject requestData = new JSONObject();
        requestData.put("Users", usersJArray);

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
