package com.technotrade.pts2.pts2testapp.helper;

import android.util.Log;

public class LogHelper {
	private static String mApplicationTag = "PTSApp";

    public static void logDebug(String sLog) {
        Log.i(mApplicationTag, sLog);
    }

    public static void logInfo(String sLog) {
        Log.i(mApplicationTag, sLog);
    }

    public static void logError(String sLog) {
        Log.e(mApplicationTag, sLog);
    }
}