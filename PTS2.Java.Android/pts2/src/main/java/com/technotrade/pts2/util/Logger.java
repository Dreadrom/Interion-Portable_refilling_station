package com.technotrade.pts2.util;

import android.util.Log;

import com.technotrade.pts2.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/// <summary>
/// The class for logging
/// </summary>
public class Logger {

    public enum Level {
        error, warn, info, debug, trace
    }

    private static final String DEFAULT_TAG = "pts2";

    private static final Level CURRENT_LEVEL = BuildConfig.DEBUG ? Level.trace : Level.info;

    private static boolean isEnabled(Level l) {
        return CURRENT_LEVEL.compareTo(l) >= 0;
    }

    static {
        Log.i(DEFAULT_TAG, "log level: " + CURRENT_LEVEL.name());
    }
    private final static String mClassname = DEFAULT_TAG;

    public static String getClassname() {
        return mClassname;
    }

    public static boolean isError() {
        return isEnabled(Level.error);
    }

    public static boolean isWarn() {
        return isEnabled(Level.warn);
    }

    public static boolean isInfo() {
        return isEnabled(Level.info);
    }

    public static boolean isDebug() {
        return isEnabled(Level.debug);
    }

    public static boolean isTrace() {
        return isEnabled(Level.trace);
    }

    public static void error(Object... args) {
        if (isError()) Log.e(buildTag(), build(args));
    }

    public static void warn(Object... args) {
        if (isWarn()) Log.w(buildTag(), build(args));
    }

    public static void info(Object... args) {
        if (isInfo()) Log.i(buildTag(), build(args));
    }

    public static void debug(Object... args) {
        if (isDebug()) Log.d(buildTag(), build(args));
    }

    public static void trace(Object... args) {
        if (isTrace()) Log.v(buildTag(), build(args));
    }

    public static void error(String msg, Throwable t) {
        if (isError()) error(buildTag(), msg, stackToString(t));
    }

    public static void warn(String msg, Throwable t) {
        if (isWarn()) warn(buildTag(), msg, stackToString(t));
    }

    public static void info(String msg, Throwable t) {
        if (isInfo()) info(buildTag(), msg, stackToString(t));
    }

    public static void debug(String msg, Throwable t) {
        if (isDebug()) debug(buildTag(), msg, stackToString(t));
    }

    public static void trace(String msg, Throwable t) {
        if (isTrace()) trace(buildTag(), msg, stackToString(t));
    }

    private static String buildTag() {
        String tag;
        if (BuildConfig.DEBUG) {
            StringBuilder b = new StringBuilder(20);
            b.append(getClassname());

            StackTraceElement stackEntry = Thread.currentThread().getStackTrace()[4];
            if (stackEntry != null) {
                b.append('.');
                b.append(stackEntry.getMethodName());
                b.append(':');
                b.append(stackEntry.getLineNumber());
            }
            tag = b.toString();
        } else {
            tag = DEFAULT_TAG;
        }

        return tag;
    }

    private static String build(Object... args) {
        if (args == null) {
            return "null";
        } else {
            StringBuilder b = new StringBuilder(args.length * 10);
            for (Object arg : args) {
                if (arg == null) {
                    b.append("null");
                } else {
                    b.append(arg);
                }
            }
            return b.toString();
        }
    }
    private static String stackToString(Throwable t) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(500);
        baos.toString();
        t.printStackTrace(new PrintStream(baos));
        return baos.toString();
    }
}
