package com.technotrade.pts2.pts2testapp;

import android.content.Context;

import com.technotrade.pts2.pts2testapp.helper.KeyboardHelper;
import com.technotrade.pts2.pts2testapp.statemachine.StateMachine;

/// <summary>
/// Application faсade singleton
/// </summary>
public class ApplicationFacade {
    private static ApplicationFacade mInstance;
    private static boolean bInited;
    private PTSManager mPTSManager;
    private KeyboardHelper mKeyboardHelper;
    private StateMachine mStateMachine;
    private OrderManager mOrderManager;
    private ResourceManager mResourceManager;
    private Settings mSettings;

    private ApplicationFacade() {
        bInited = false;
    }

    public static ApplicationFacade getInstance() {
        if (mInstance == null) {
            synchronized (ApplicationFacade.class) {
                if (mInstance == null) {
                    mInstance = new ApplicationFacade();
                }
            }
        }

        return mInstance;
    }

    public void init(Context context) {
        if (bInited) {
            return;
        }

        Context appContext = context.getApplicationContext();
        mResourceManager = new ResourceManager(appContext);
        mPTSManager = new PTSManager(appContext);
        mKeyboardHelper = new KeyboardHelper();
        mStateMachine = new StateMachine();
        mOrderManager = new OrderManager();
        mSettings = new Settings(appContext);

        loadSettings();

        bInited = true;
    }

    public PTSManager getPTSManager() {
        return mPTSManager;
    }

    public KeyboardHelper getKeyboardHelper() {
        return mKeyboardHelper;
    }

    public StateMachine getStateMachine() {
        return mStateMachine;
    }

    public OrderManager getOrderManager() {
        return mOrderManager;
    }

    public ResourceManager getResourceManager() {
        return mResourceManager;
    }

    public Settings getSettings() {
        return mSettings;
    }

    public void loadSettings() {
        mSettings.migratePreferencesToStrings();
        mSettings.loadAppSettings();
        mPTSManager.loadPTSSettings();
    }
}