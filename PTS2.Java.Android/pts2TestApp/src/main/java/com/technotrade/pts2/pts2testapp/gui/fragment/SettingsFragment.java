package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.helper.DialogHelper;
import com.technotrade.pts2.pts2testapp.helper.ValidationHelper;
import com.technotrade.pts2.util.Logger;

import java.math.BigDecimal;

/// <summary>
/// The fragment that allowing to enter and store settings.
/// </summary>
public class SettingsFragment extends PreferenceFragmentCompat {
    private FragmentManager mSupportFragmentManager = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSupportFragmentManager = requireActivity().getSupportFragmentManager();
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        androidx.preference.EditTextPreference httpPortNumberText = getPreferenceManager()
            .findPreference("http_port_number");
        assert httpPortNumberText != null;
        httpPortNumberText.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setKeyListener(DigitsKeyListener.getInstance("1234567890."));
        });

        androidx.preference.EditTextPreference httpsPortNumberText = getPreferenceManager()
            .findPreference("https_port_number");
        assert httpsPortNumberText != null;
        httpsPortNumberText.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setKeyListener(DigitsKeyListener.getInstance("1234567890."));
        });

        androidx.preference.EditTextPreference predefinedVolumeText = getPreferenceManager()
            .findPreference("predefined_volume");
        assert predefinedVolumeText != null;
        predefinedVolumeText.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setKeyListener(DigitsKeyListener.getInstance("1234567890."));
        });

        androidx.preference.EditTextPreference predefinedAmountText = getPreferenceManager()
            .findPreference("predefined_amount");
        assert predefinedAmountText != null;
        predefinedAmountText.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setKeyListener(DigitsKeyListener.getInstance("1234567890."));
        });
    }

    /// <summary>
    /// The fragment overridden method that will be called at start to load settings
    /// </summary>
    @Override
    public void onStart() {
        super.onStart();

        loadSettings();
    }

    /// <summary>
    /// The fragment overridden method that will be called at stop to save settings
    /// </summary>
    @Override
    public void onStop() {
        super.onStop();

        saveSettings();

        Bundle result = new Bundle();
        result.putBoolean("SETTINGS_CHANGED", true);
        getParentFragmentManager().setFragmentResult("SETTINGS_RESULT", result);
    }

    public void loadSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        String login = preferences.getString("login", "admin");
        EditTextPreference editLoginPreference = findPreference("login");
        assert editLoginPreference != null;
        editLoginPreference.setText(login);
        editLoginPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onLoginChanged((String) newValue);
            return true;
        });

        String password = preferences.getString("password", "admin");
        EditTextPreference editPasswordPreference = findPreference("password");
        assert editPasswordPreference != null;
        editPasswordPreference.setText(password);
        editPasswordPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onPasswordChanged((String) newValue);
            return true;
        });

        String hostDomain = preferences.getString("host_domain", "192.168.1.117");
        EditTextPreference editHostDomainPreference = findPreference("host_domain");
        assert editHostDomainPreference != null;
        editHostDomainPreference.setText(hostDomain);
        editHostDomainPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onHostDomainChanged((String) newValue);
            return true;
        });

        int defaultHttpPortNumber = 80;
        int httpPortNumber;

        String httpPortNumberString = preferences.getString("http_port_number", String.valueOf(defaultHttpPortNumber));
        httpPortNumber = Integer.parseInt(httpPortNumberString);

        if (!ValidationHelper.isValidPortNumber(httpPortNumber)) {
            httpPortNumber = defaultHttpPortNumber;
        }

        EditTextPreference editHttpPortNumberPreference = findPreference("http_port_number");
        assert editHttpPortNumberPreference != null;
        editHttpPortNumberPreference.setText(String.valueOf(httpPortNumber));
        editHttpPortNumberPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (ValidationHelper.isValidPortNumber((String) newValue)) {
                onHttpPortNumberChanged((String) newValue);
                return true;
            } else {
                DialogHelper.showDialogWarning(this, this, getResources().getString(R.string.error), requireActivity().getString(R.string.field_HTTP_port_must_be_a_valid_number_between_0_and_65535));
                return false;
            }
        });

        int defaultHttpsPortNumber = 443;
        int httpsPortNumber;

        String httpsPortNumberString = preferences.getString("https_port_number", String.valueOf(defaultHttpsPortNumber));
        httpsPortNumber = Integer.parseInt(httpsPortNumberString);

        if (!ValidationHelper.isValidPortNumber(httpsPortNumber)) {
            httpsPortNumber = defaultHttpsPortNumber;
        }

        EditTextPreference editHttpsPortNumberPreference = findPreference("https_port_number");
        assert editHttpsPortNumberPreference != null;
        editHttpsPortNumberPreference.setText(String.valueOf(httpsPortNumber));
        editHttpsPortNumberPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (ValidationHelper.isValidPortNumber((String) newValue)) {
                onHttpsPortNumberChanged((String) newValue);
                return true;
            } else {
                DialogHelper.showDialogWarning(this, this, getResources().getString(R.string.error), requireActivity().getString(R.string.field_HTTPS_port_must_be_a_valid_number_between_0_and_65535));
                return false;
            }
        });

        ListPreference protocolPreference = findPreference("protocol_preference");
        assert protocolPreference != null;
        String protocol = preferences.getString("protocol_preference", "https");
        protocolPreference.setSummary(protocol.equals("https") ? "HTTPS" : "HTTP");

        protocolPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String selectedProtocol = (String) newValue;
            preference.setSummary(selectedProtocol.equals("https") ? "HTTPS" : "HTTP");
            onProtocolChanged(selectedProtocol);
            return true;
        });

        boolean digest = preferences.getBoolean("digest_auth_switch", true);
        SwitchPreference switchDigestPreference = findPreference("digest_auth_switch");
        assert switchDigestPreference != null;
        switchDigestPreference.setChecked(digest);
        switchDigestPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onDigestAuthChanged((Boolean) newValue);
            return true;
        });

        String currency = preferences.getString("currency", "$");
        EditTextPreference editCurrencyPreference = findPreference("currency");
        assert editCurrencyPreference != null;
        editCurrencyPreference.setText(currency);
        editCurrencyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onCurrencyChanged((String) newValue);
            return true;
        });

        boolean nozzleMustBeTaken = preferences.getBoolean("nozzle_must_be_taken_switch", true);
        SwitchPreference switchNozzleMustBeTakenPreference = findPreference("nozzle_must_be_taken_switch");
        assert switchNozzleMustBeTakenPreference != null;
        switchNozzleMustBeTakenPreference.setChecked(nozzleMustBeTaken);
        switchNozzleMustBeTakenPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            onNozzleMustBeTakenChanged((Boolean) newValue);
            return true;
        });

        float defaultPredefinedVolume = 100.0f;
        BigDecimal predefinedVolume;

        String predefinedVolumeString = preferences.getString("predefined_volume", String.valueOf(defaultPredefinedVolume));
        predefinedVolume = BigDecimal.valueOf(Float.parseFloat(predefinedVolumeString));

        if (!ValidationHelper.isNumericInRange(predefinedVolume.floatValue(), 0.0f, 99999.0f)) {
            predefinedVolume = BigDecimal.valueOf(defaultPredefinedVolume);
        }

        EditTextPreference editPredefinedVolumePreference = findPreference("predefined_volume");
        assert editPredefinedVolumePreference != null;
        editPredefinedVolumePreference.setText(String.valueOf(predefinedVolume));
        editPredefinedVolumePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (ValidationHelper.isNumericFloatInRange((String) newValue,0.0f, 99999.0f)) {
                onPredefinedVolumeChanged((String) newValue);
                return true;
            } else {
                DialogHelper.showDialogWarning(this, this, getResources().getString(R.string.error), requireActivity().getString(R.string.field_predefined_volume_must_be_a_valid_number_between_0_and_99999));
                return false;
            }
        });

        float defaultPredefinedAmount = 100.0f;
        BigDecimal predefinedAmount;

        String predefinedAmountString = preferences.getString("predefined_amount", String.valueOf(defaultPredefinedAmount));
        predefinedAmount = BigDecimal.valueOf(Float.parseFloat(predefinedAmountString));

        if (!ValidationHelper.isNumericInRange(predefinedAmount.floatValue(), 0.0f, 99999.0f)) {
            predefinedAmount = BigDecimal.valueOf(defaultPredefinedAmount);
        }

        EditTextPreference editPredefinedAmountPreference = findPreference("predefined_amount");
        assert editPredefinedAmountPreference != null;
        editPredefinedAmountPreference.setText(String.valueOf(predefinedAmount));
        editPredefinedAmountPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (ValidationHelper.isNumericFloatInRange((String) newValue,0.0f, 99999.0f)) {
                onPredefinedAmountChanged((String) newValue);
                return true;
            } else {
                DialogHelper.showDialogWarning(this, this, getResources().getString(R.string.error), requireActivity().getString(R.string.field_predefined_amount_must_be_a_valid_number_between_0_and_99999));
                return false;
            }
        });
    }

    public void saveSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor editor = preferences.edit();

        EditTextPreference editLoginPreference = findPreference("login");
        assert editLoginPreference != null;
        String login = editLoginPreference.getText();
        editor.putString("login", login);

        EditTextPreference editPasswordPreference = findPreference("password");
        assert editPasswordPreference != null;
        String password = editPasswordPreference.getText();
        editor.putString("password", password);

        EditTextPreference editHostDomainPreference = findPreference("host_domain");
        assert editHostDomainPreference != null;
        String hostDomain = editHostDomainPreference.getText();
        editor.putString("host_domain", hostDomain);

        EditTextPreference editHttpPortNumberPreference = findPreference("http_port_number");
        assert editHttpPortNumberPreference != null;
        String httpPortNumber = editHttpPortNumberPreference.getText();

        if (ValidationHelper.isValidPortNumber(httpPortNumber)) {
            editor.putString("http_port_number", httpPortNumber);
        } else {
            DialogHelper.showDialogWarning(this, this, getResources().getString(R.string.error), requireActivity().getString(R.string.field_HTTP_port_must_be_a_valid_number_between_0_and_65535));
        }

        EditTextPreference editHttpsPortNumberPreference = findPreference("https_port_number");
        assert editHttpsPortNumberPreference != null;
        String httpsPortNumber = editHttpsPortNumberPreference.getText();

        if (ValidationHelper.isValidPortNumber(httpsPortNumber)) {
            editor.putString("https_port_number", httpsPortNumber);
        } else {
            DialogHelper.showDialogWarning(this, this, getResources().getString(R.string.error), requireActivity().getString(R.string.field_HTTPS_port_must_be_a_valid_number_between_0_and_65535));
        }

        ListPreference protocolPreference = findPreference("protocol_preference");
        assert protocolPreference != null;
        String protocol = protocolPreference.getValue();
        editor.putString("protocol_preference", protocol);

        SwitchPreference switchDigestPreference = findPreference("digest_auth_switch");
        assert switchDigestPreference != null;
        boolean digest = switchDigestPreference.isChecked();
        editor.putBoolean("digest_auth_switch", digest);

        EditTextPreference editCurrencyPreference = findPreference("currency");
        assert editCurrencyPreference != null;
        String currency = editCurrencyPreference.getText();
        editor.putString("currency", currency);

        SwitchPreference switchNozzleMustBeTakenPreference = findPreference("nozzle_must_be_taken_switch");
        assert switchNozzleMustBeTakenPreference != null;
        boolean nozzleMustBeTaken = switchNozzleMustBeTakenPreference.isChecked();
        editor.putBoolean("nozzle_must_be_taken_switch", nozzleMustBeTaken);

        EditTextPreference editPredefinedVolumePreference = findPreference("predefined_volume");
        assert editPredefinedVolumePreference != null;
        String predefinedVolume = editPredefinedVolumePreference.getText();

        if (ValidationHelper.isNumericFloatInRange(predefinedVolume, 0.0f, 99999.0f)) {
            editor.putString("predefined_volume", predefinedVolume);
        } else {
            DialogHelper.showDialogWarning(this, this, getResources().getString(R.string.error), requireActivity().getString(R.string.field_predefined_volume_must_be_a_valid_number_between_0_and_99999));
        }

        EditTextPreference editPredefinedAmountPreference = findPreference("predefined_amount");
        assert editPredefinedAmountPreference != null;
        String predefinedAmount = editPredefinedAmountPreference.getText();

        if (ValidationHelper.isNumericFloatInRange(predefinedAmount, 0.0f, 99999.0f)) {
            editor.putString("predefined_amount", predefinedAmount);
        } else {
            DialogHelper.showDialogWarning(this, this, getResources().getString(R.string.error), requireActivity().getString(R.string.field_predefined_amount_must_be_a_valid_number_between_0_and_99999));
        }

        editor.apply();

        ApplicationFacade.getInstance().loadSettings();
    }

    private void onLoginChanged(String newLogin) {
        Logger.debug("SettingsFragment", "Login changed to: " + newLogin);
        saveSettings();
    }

    private void onPasswordChanged(String newPassword) {
        Logger.debug("SettingsFragment", "Password changed to: " + newPassword);
        saveSettings();
    }

    private void onHostDomainChanged(String newHostDomain) {
        Logger.debug("SettingsFragment", "Host domain changed to: " + newHostDomain);
        saveSettings();
    }

    private void onHttpPortNumberChanged(String newHttpPortNumber) {
        Logger.debug("SettingsFragment", "HTTP port number changed to: " + newHttpPortNumber);
        saveSettings();
    }

    private void onHttpsPortNumberChanged(String newHttpsPortNumber) {
        Logger.debug("SettingsFragment", "HTTPS port number changed to: " + newHttpsPortNumber);
        saveSettings();
    }

    private void onProtocolChanged(String newProtocol) {
        Logger.debug("SettingsFragment", "Protocol changed to: " + newProtocol);
        saveSettings();
    }

    private void onDigestAuthChanged(Boolean newDigestAuth) {
        Logger.debug("SettingsFragment", "Digest auth changed to: " + newDigestAuth);
        saveSettings();
    }

    private void onCurrencyChanged(String newCurrency) {
        Logger.debug("SettingsFragment", "Currency changed to: " + newCurrency);
        saveSettings();
    }

    private void onNozzleMustBeTakenChanged(Boolean newNozzleMustBeTaken) {
        Logger.debug("SettingsFragment", "Nozzle must be taken changed to: " + newNozzleMustBeTaken);
        saveSettings();
    }

    private void onPredefinedVolumeChanged(String predefinedVolume) {
        Logger.debug("SettingsFragment", "predefined volume changed to: " + predefinedVolume);
        saveSettings();
    }

    private void onPredefinedAmountChanged(String predefinedAmount) {
        Logger.debug("SettingsFragment", "predefined amount changed to: " + predefinedAmount);
        saveSettings();
    }
}