package com.technotrade.pts2.pts2testapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.technotrade.pts2.util.Logger;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;

/// <summary>
/// Settings class
/// </summary>
public class Settings implements Serializable {
    private final WeakReference<Context> mContextRef;
	private String mCurrency;
    private boolean mNozzleMustBeTaken;
    private BigDecimal mPredefinedVolume;
    private BigDecimal mPredefinedAmount;

    public Settings(Context context) {
        Context appContext = context.getApplicationContext();
        mContextRef = new WeakReference<>(appContext);
    }

    /// <summary>
    /// Currency symbol getter and setter
    /// </summary>
    public String getCurrency() {
        return mCurrency;
    }
    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    /// <summary>
    /// NozzleMustBeTaken symbol getter and setter
    /// </summary>
    public boolean getNozzleMustBeTaken() {
        return mNozzleMustBeTaken;
    }
    public void setNozzleMustBeTaken(boolean nozzleMustBeTaken) {
        mNozzleMustBeTaken = nozzleMustBeTaken;
    }

    /// <summary>
    /// PredefinedVolume symbol getter and setter
    /// </summary>
    public BigDecimal getPredefinedVolume() {
        return mPredefinedVolume;
    }

    public int getPredefinedVolumeInt() {
        return mPredefinedVolume.intValue();
    }

    public void setPredefinedVolume(BigDecimal predefinedVolume) {
        mPredefinedVolume = predefinedVolume;
    }

    /// <summary>
    /// PredefinedAmount symbol getter and setter
    /// </summary>
    public BigDecimal getPredefinedAmount() {
        return mPredefinedAmount;
    }
    public void setPredefinedAmount(BigDecimal predefinedAmount) {
        mPredefinedAmount = predefinedAmount;
    }

    /// <summary>
    /// Migrates listed settings properties to use String type instead int or float
    /// </summary>
    public synchronized void migratePreferencesToStrings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContextRef.get());
        SharedPreferences.Editor editor = preferences.edit();
        boolean needsApply = false;

        // Array of keys that should be strings for EditTextPreference
        String[] stringKeys = {
            "http_port_number",
            "https_port_number",
            "predefined_volume",
            "predefined_amount"
        };

        String[] defaultValues = {"80", "443", "100.0", "100.0"};

        for (int i = 0; i < stringKeys.length; i++) {
            String key = stringKeys[i];
            String defaultValue = defaultValues[i];

            if (preferences.contains(key)) {
                Object value = preferences.getAll().get(key);

                if (value instanceof Integer) {
                    // Convert Integer to String
                    int intValue = (Integer) value;
                    editor.remove(key).putString(key, String.valueOf(intValue));
                    needsApply = true;
                } else if (value instanceof Float) {
                    // Convert Float to String
                    float floatValue = (Float) value;
                    editor.remove(key).putString(key, String.valueOf(floatValue));
                    needsApply = true;
                } else if (value instanceof Long) {
                    // Convert Long to String (just in case)
                    long longValue = (Long) value;
                    editor.remove(key).putString(key, String.valueOf(longValue));
                    needsApply = true;
                } else if (value instanceof Double) {
                    // Convert Double to String (just in case)
                    double doubleValue = (Double) value;
                    editor.remove(key).putString(key, String.valueOf(doubleValue));
                    needsApply = true;
                }
                // If it's already a String, do nothing
            } else {
                // If key doesn't exist, set default string value
                editor.putString(key, defaultValue);
                needsApply = true;
            }
        }

        if (needsApply) {
            editor.commit(); // Must be commit(), not apply()!
        }
    }

    /// <summary>
    /// Loading PTS2 settings using SharedPreferences
    /// </summary>
    public synchronized void loadAppSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContextRef.get());

        String currency = preferences.getString("currency", "$");
        setCurrency(currency);

        boolean nozzleMustBeTaken = preferences.getBoolean("nozzle_must_be_taken_switch", true);
        setNozzleMustBeTaken(nozzleMustBeTaken);

        float defaultPredefinedVolume = 100.0f;
        BigDecimal predefinedVolume;

        String predefinedVolumeString = preferences.getString("predefined_volume", String.valueOf(defaultPredefinedVolume));
        predefinedVolume = BigDecimal.valueOf(Float.parseFloat(predefinedVolumeString));

        setPredefinedVolume(predefinedVolume);

        float defaultPredefinedAmount = 100.0f;
        BigDecimal predefinedAmount;

        String predefinedAmountString = preferences.getString("predefined_amount", String.valueOf(defaultPredefinedAmount));
        predefinedAmount = BigDecimal.valueOf(Float.parseFloat(predefinedAmountString));

        setPredefinedAmount(predefinedAmount);

        Logger.info("App settings loaded");
    }
}