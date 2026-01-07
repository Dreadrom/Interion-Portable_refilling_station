package com.technotrade.pts2.network;

import static com.technotrade.pts2.enumeration.Result.INIT_ERROR;

import android.content.Context;

import com.technotrade.pts2.RequestCallback;
import com.technotrade.pts2.Settings;
import com.technotrade.pts2.enumeration.Result;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// Client manager base class.
/// Contains methods for requests executing
/// </summary>
public abstract class BaseClientManager {
    protected WeakReference<Context> mContextRef;
    protected Map<String, RequestCallback<?>> mCallbacks;
    protected ArrayList<IRequest<?>> mRequests;
    protected static int mId;
    protected Settings mSettings;
    protected boolean mIsOpened;

    public BaseClientManager(WeakReference<Context> contextRef) {
        mContextRef = contextRef;
        mCallbacks = new HashMap<String, RequestCallback<?>>();
        mRequests = new ArrayList<IRequest<?>>();
        mId = 0;
        mIsOpened = false;
    }
    /// <summary>
    /// Opens the connection
    /// </summary>
    public abstract Result open();
    /// <summary>
    /// Closes the connection
    /// </summary>
    public abstract void close();
    /// <summary>
    /// Settings getter
    /// </summary>
    /// <returns>Settings</returns>
    public synchronized Settings getSettings() {
        return mSettings;
    }
    /// <summary>
    /// Settings setter
    /// </summary>
    /// <param name="settings">Settings instance</param>
    public synchronized void setSettings(Settings settings) {
        mSettings = settings;
    }
    /// <summary>
    /// Appends callback to internal callbacks list
    /// </summary>
    /// <param name="requestName">Name of request</param>
    /// <param name="callback">Callback delegate</param>
    public synchronized void addCallback(String requestName, RequestCallback callback) {
        if (callback != null) {
            mCallbacks.put(requestName, callback);
        }
        else {
            mCallbacks.remove(requestName);
        }
    }
    /// <summary>
    /// Clears callback from internal callbacks list
    /// </summary>
    /// <param name="requestName">Name of request</param>
    public synchronized void removeCallback(String requestName) {
        mCallbacks.remove(requestName);
    }
    /// <summary>
    /// Clears internal callbacks list
    /// </summary>
    public synchronized void clearCallbacks() {
        mCallbacks.clear();
    }
    /// <summary>
    /// Appends a request to internal requests list
    /// </summary>
    /// <param name="request">request object that inherited from IRequest</param>
    /// <returns>Result</returns>
    public synchronized Result addRequest(IRequest<?> request) {
        if (!mIsOpened) { return Result.INIT_ERROR; }

        mRequests.add(request);

        return Result.SUCCESS;
    }
    /// <summary>
    /// Returns request queue. Can be used for example to check requests states after ExecuteRequestsQueue execution
    /// </summary>
    /// <returns>ArrayList<IRequest></returns>
    public synchronized ArrayList<IRequest<?>> getRequestsQueue() {
        return mRequests;
    }
    /// <summary>
    /// Cleans internal requests queue
    /// </summary>
    /// <returns>Result</returns>
    public synchronized Result clearRequestsQueue() {
        if (!mIsOpened) { return INIT_ERROR; }

        mRequests.clear();

        return Result.SUCCESS;
    }
    /// <summary>
    /// Executes the request queue
    /// </summary>
    /// <returns>Result</returns>
    public abstract Result executeRequestsQueue();
}