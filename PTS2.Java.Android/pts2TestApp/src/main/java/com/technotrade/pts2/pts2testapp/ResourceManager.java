package com.technotrade.pts2.pts2testapp;

import android.content.Context;

import java.io.Serializable;
import java.lang.ref.WeakReference;

/// <summary>
/// Resource class that responsible for orders
/// </summary>
public class ResourceManager implements Serializable {
    private WeakReference<Context> mContextRef;

    ResourceManager(Context context) {
        Context appContext = context.getApplicationContext();
        mContextRef = new WeakReference<>(appContext);
    }

    public String getResourceString(int resId) {
        return mContextRef.get().getString(resId);
    }
}