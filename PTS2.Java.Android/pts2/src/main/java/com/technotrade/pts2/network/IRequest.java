package com.technotrade.pts2.network;

import com.technotrade.pts2.datastructs.ErrorData;
import com.technotrade.pts2.exception.TTException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/// <summary>
/// A request Base class.
/// Contains methods for creating and parsing requests
/// </summary>
public interface IRequest<T> {
    T getResult();
    /// <summary>
    /// Creates request JSON.
    /// Called at the end of RequestJSON function of each inherited class (request)
    /// </summary>
    /// <returns>JSON object</returns>
    JSONObject requestJSON() throws JSONException, TTException;
    /// <summary>
    /// Parse JSON object keys that are common for any request.
    /// Called at the begin of ParseJSON function of each inherited class (request)
    /// </summary>
    /// <param name="result">JSON object</param>
    /// <returns>True is response have no errors</returns>
    boolean parseJSON(JSONObject result) throws JSONException, TTException;
    /// <summary>
    /// Answers that response must be confirmation or not
    /// </summary>
    /// <returns>True is response must be confirmation</returns>
    boolean isConfirmationResponse();
    /// <summary>
    /// Returns a request name
    /// </summary>
    /// <returns>Request name string</returns>
    String getRequestName();
    /// <summary>
    /// Returns a received response name
    /// </summary>
    /// <returns>Received response name string</returns>
    String getReceivedResponseName();
    /// <summary>
    /// Setter for a response name
    /// </summary>
    /// <param name="responseNameReceived"></param>
    void setReceivedResponseName(String responseNameReceived);
    /// <summary>
    /// Returns the possible response names for response
    /// </summary>
    /// <returns>List of possible response names</returns>
    List<String> getPossibleResponseNames();
    /// <summary>
    /// Returns a key that could be further used as a key for responses map
    /// </summary>
    /// <returns>Key string</returns>
    String getKey();
    /// <summary>
    /// Id getter. Returns a response Id
    /// </summary>
    /// <returns>Id ingeter value</returns>
    int getId();
    /// <summary>
    /// Id setter. Sets a response Id
    /// </summary>
    /// <param name="id">Id ingeter value</param>
    void setId(int id);
    /// <summary>
    /// Error getter. Answers that error happened or not
    /// </summary>
    /// <returns>True is error, False if not</returns>
    boolean isError();
    /// <summary>
    /// Error setter. Sets that error happened
    /// </summary>
    /// <param name="error">True is error, False if not</param>
    void setError(boolean error);
    /// <summary>
    /// ErrorCode getter
    /// </summary>
    /// <returns>ErrorCode integer value</returns>
    int getErrorCode();
    /// <summary>
    /// ErrorCode setter
    /// </summary>
    /// <param name="errorCode">ErrorCode integer value</param>
    void setErrorCode(int errorCode);
    /// <summary>
    /// ErrorMessage getter
    /// </summary>
    /// <returns>Error message string</returns>
    String getErrorMessage();
    /// <summary>
    /// ErrorMessage setter
    /// </summary>
    /// <param name="errorMessage">Error message string</param>
    void setErrorMessage(String errorMessage);
    /// <summary>
    /// ErrorData getter
    /// </summary>
    /// <returns>Error data instance</returns>
    ErrorData getErrorData();
    /// <summary>
    /// ErrorData setter
    /// </summary>
    /// <param name="errorData">Error data instance</param>
    void setErrorData(ErrorData errorData);
}