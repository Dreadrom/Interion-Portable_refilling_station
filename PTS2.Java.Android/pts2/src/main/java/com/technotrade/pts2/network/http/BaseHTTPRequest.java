package com.technotrade.pts2.network.http;

import com.technotrade.pts2.datastructs.ErrorData;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;
import com.technotrade.pts2.network.IRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/// <summary>
/// HTTP request base class
/// </summary>
public abstract class BaseHTTPRequest<T> implements IRequest<T> {
    protected T mResult;
    protected String mRequestName;
    protected String mReceivedResponseName;
    protected List<String> mPossibleResponseNames;
    protected int mId;
    protected boolean mIsError;
    protected int mErrorCode;
    protected String mErrorMessage;
    protected ErrorData mErrorData;

    /// <summary>
    /// BaseRequest constructor
    /// </summary>
    /// <param name="requestName">Request name</param>
    /// <param name="responseNames">Response names that can be received from PTS2 device</param>
    public BaseHTTPRequest(String requestName, String[] responseNames) {
        init(requestName);

        if (responseNames != null) {
            mPossibleResponseNames = Arrays.asList(responseNames);
        }
    }
    /// <summary>
    /// BaseRequest constructor
    /// </summary>
    /// <param name="requestName">Request name</param>
    /// <param name="responseName">Response name name</param>
    public BaseHTTPRequest(String requestName, String responseName) {
        init(requestName);

        if (responseName != null && !responseName.isEmpty()) {
            mPossibleResponseNames = Collections.singletonList(responseName);
        }
    }
    /// <summary>
    /// Clears request
    /// </summary>
    private void init(String requestName) {
        mRequestName = requestName;
        mReceivedResponseName = null;
        mPossibleResponseNames = null;
        mResult = null;
        mId = 0;
        mIsError = false;
        mErrorCode = Result.SUCCESS.getCode();
        mErrorMessage = Result.SUCCESS.getDescription();
        mErrorData = null;
    }
    @Override
    public T getResult() {
        return mResult;
    }
    /// <summary>
    /// Answers that response must be confirmation or not
    /// </summary>
    /// <returns>True is response must be confirmation</returns>
    @Override
    public boolean isConfirmationResponse() {
        return (getPossibleResponseNames() == null || getPossibleResponseNames().size() == 0);
    }
    /// <summary>
    /// Returns a request name
    /// </summary>
    /// <returns>Request name string</returns>
    @Override
    public String getRequestName() {
        return mRequestName;
    }
    /// <summary>
    /// Returns a received response name
    /// </summary>
    /// <returns>Received response name string</returns>
    @Override
    public String getReceivedResponseName() {
        return mReceivedResponseName;
    }
    /// <summary>
    /// Setter for a response name
    /// </summary>
    /// <param name="responseNameReceived"></param>
    @Override
    public void setReceivedResponseName(String responseNameReceived) {
        mReceivedResponseName = responseNameReceived;
    }
    /// <summary>
    /// Returns the possible response names for response
    /// </summary>
    /// <returns>List of possible response names</returns>
    @Override
    public List<String> getPossibleResponseNames() {
        return mPossibleResponseNames;
    }
    /// <summary>
    /// Returns a static key that could be further used as a key for responses map
    /// </summary>
    /// <param name="requestName">Request name</param>
    /// <param name="responseName">Response name</param>
    /// <returns>Key string</returns>
    public static String GetKeyStatic(String requestName, String responseName) {
        return (responseName == null || responseName.length() == 0) ? requestName : responseName;
    }
    /// <summary>
    /// Returns a key that could be further used as a key for responses map
    /// </summary>
    /// <returns>Key string</returns>
    @Override
    public String getKey() {
        if (!(mReceivedResponseName == null || mReceivedResponseName.isEmpty())
                && mRequestName.equals(mReceivedResponseName)
                && !isConfirmationResponse()) {
            return mPossibleResponseNames.size() > 0 ? mPossibleResponseNames.get(0) : "";
        }

        if (!(mReceivedResponseName == null || mReceivedResponseName.isEmpty())) {
            return mReceivedResponseName;
        }

        String responseName = "";
        List<String> possibleResponseNames = getPossibleResponseNames();
        if (possibleResponseNames != null && possibleResponseNames.size() > 0) {
            responseName = possibleResponseNames.get(0);
        }

        return GetKeyStatic(getRequestName(), responseName);
    }
    /// <summary>
    /// Id getter. Returns a response Id
    /// </summary>
    /// <returns>Id ingeter value</returns>
    @Override
    public int getId() {
        return mId;
    }
    /// <summary>
    /// Id setter. Sets a response Id
    /// </summary>
    /// <param name="id">Id ingeter value</param>
    @Override
    public void setId(int id) {
        mId = id;
    }
    /// <summary>
    /// Error getter. Answers that error happened or not
    /// </summary>
    /// <returns>True is error, False if not</returns>
    @Override
    public boolean isError() {
        return mIsError;
    }
    /// <summary>
    /// Error setter. Sets that error happened
    /// </summary>
    /// <param name="error">True is error, False if not</param>
    @Override
    public void setError(boolean error) {
        mIsError = error;
    }
    /// <summary>
    /// ErrorCode getter
    /// </summary>
    /// <returns>ErrorCode integer value</returns>
    @Override
    public int getErrorCode() {
        return mErrorCode;
    }
    /// <summary>
    /// ErrorCode setter
    /// </summary>
    /// <param name="errorCode">ErrorCode integer value</param>
    @Override
    public void setErrorCode(int errorCode) {
        mErrorCode = errorCode;
    }
    /// <summary>
    /// ErrorMessage getter
    /// </summary>
    /// <returns>Error message string</returns>
    @Override
    public String getErrorMessage() {
        return mErrorMessage;
    }
    /// <summary>
    /// ErrorMessage setter
    /// </summary>
    /// <param name="errorMessage">Error message string</param>
    @Override
    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }
    /// <summary>
    /// ErrorData getter
    /// </summary>
    /// <returns>Error data instance</returns>
    @Override
    public ErrorData getErrorData() {
        return mErrorData;
    }
    /// <summary>
    /// ErrorData setter
    /// </summary>
    /// <param name="errorData">Error data instance</param>
    @Override
    public void setErrorData(ErrorData errorData) {
        mErrorData = errorData;
    }
    /// <summary>
    /// Creates request JSON.
    /// Called at the end of RequestJSON function of each inherited class (request)
    /// </summary>
    /// <param name="data">Data JSON object</param>
    /// <returns>JSON object</returns>
    public JSONObject requestJSON(JSONObject data) throws JSONException {
        JSONObject jObject = new JSONObject();
        jObject.put("Id", mId);
        jObject.put("Type", mRequestName);

        if (data != null) {
            jObject.put("Data", data);
        }

        return jObject;
    }
    /// <summary>
    /// Creates request JSON.
    /// Called at the end of RequestJSON function of each inherited class (request)
    /// </summary>
    /// <param name="data">Data JSON array</param>
    /// <returns>JSON object</returns>
    public JSONObject requestJSON(JSONArray data) throws JSONException {
        JSONObject jObject = new JSONObject();
        jObject.put("Id", mId);
        jObject.put("Type", mRequestName);

        if (data != null) {
            jObject.put("Data", data);
        }

        return jObject;
    }
    /// <summary>
    /// Creates request JSON.
    /// Called at the end of RequestJSON function of each inherited class (request)
    /// </summary>
    /// <returns>JSON object</returns>
    /// <summary>
    /// requestJSON overridden method. Preparing the JSON data for request.
    /// </summary>
    /// <returns>JSON object</returns>
    @Override
    public JSONObject requestJSON() throws TTException, JSONException {
        return requestJSON((JSONObject) null);
    }
    /// <summary>
    /// Parse JSON object keys that are common for any request.
    /// Called at the begin of ParseJSON function of each inherited class (request)
    /// </summary>
    /// <param name="result">JSON object</param>
    /// <returns>True is response have no errors</returns>
    @SuppressWarnings("unchecked")
    public boolean parseJSON(JSONObject result) throws TTException, JSONException {
        if (!result.has("Id")) {
            throw new TTException("Error: response doesn't contain Id property", Result.PROTOCOL_ERROR);
        }

        int id = result.getInt("Id");

        if (getId() != id) {
            throw new TTException("Error: wrong packet sequence in response", Result.PROTOCOL_ERROR);
        }

        if (result.has("Error")) {
            String error = result.getString("Error");
            if (!error.equals("false")) {
                mIsError = true;

                if (result.has("Code")) {
                    mErrorCode = result.getInt("Code");
                }

                if (result.has("Message")) {
                    mErrorMessage = result.getString("Message");
                }

                if (result.has("Data")) {
                    mErrorData = new ErrorData();

                    JSONObject errorDataJObject = result.getJSONObject("Data");

                    if (errorDataJObject.has("Pump")) {
                        int pump = errorDataJObject.getInt("Pump");
                        mErrorData.setPump(pump);
                    }

                    if (errorDataJObject.has("User")) {
                        String user = errorDataJObject.getString("User");
                        mErrorData.setUser(user);
                    }

                    if (errorDataJObject.has("Request")) {
                        String request = errorDataJObject.getString("Request");
                        mErrorData.setRequest(request);
                    }
                }
            }
        }

        //For "Confirmation" request result value is bool
        //Confirmation requests are requests that returning no data but just a confitmation that request was called.
        //Request classes of "Confirmation" type specially have _possibleResponseNames member variable filled to null to check that its exactly "Confirmation" requests
        //The "Type" field of Confirmation response is equal to request name or not returning at all in previous firmwares.
        if (!isConfirmationResponse()) {
            if (result.has("Type")) {
                String responseName = result.getString("Type");

                if ((getPossibleResponseNames() != null && getPossibleResponseNames().contains(responseName))
                        || mRequestName.equals(responseName)) {
                    setReceivedResponseName(responseName);
                } else {
                    throw new TTException("Error: returned Type value does not fit the request", Result.PROTOCOL_ERROR);
                }
            }
        }

        return !mIsError;
    }
}