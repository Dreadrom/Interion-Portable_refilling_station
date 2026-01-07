package com.technotrade.pts2.pts2testapp.helper;

import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.technotrade.pts2.datastructs.MeasurementUnits;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.R;

import java.math.BigDecimal;

public class BindingAdaptersHelper {

    @BindingAdapter("android:textColorRes")
    public static void setTextColorRes(TextView view, int colorRes) {
        int color = ContextCompat.getColor(view.getContext(), colorRes);
        view.setTextColor(color);
    }

    @BindingAdapter("android:textColor")
    public static void setTextColor(TextView view, int color) {
        view.setTextColor(color);
    }

    @BindingAdapter({"android:volumeWithUnit"})
    public static void setVolumeWithUnit(TextView textView, String volume) {
        //if you want to use locale volume units
        //String volumeWithUnit = String.format(Locale.getDefault(), "%.2f L", volume);

        MeasurementUnits measurementUnits = ApplicationFacade.getInstance().getPTSManager().
            getDataStorage().getMeasurementUnits();
        String valueWithUnitsStringWithPlaceholders = textView.getContext().getResources().getString(R.string._value_with_units);
        String volumeWithUnits = String.format(valueWithUnitsStringWithPlaceholders, volume, measurementUnits.getVolume());
        textView.setText(volumeWithUnits);
    }

    @BindingAdapter({"android:priceWithCurrencySymbol"})
    public static void setPriceWithCurrencySymbol(TextView textView, String price) {
        String currencySymbol = ApplicationFacade.getInstance().getSettings().getCurrency();
        String valueWithUnitsStringWithPlaceholders = textView.getContext().getResources().getString(R.string._value_with_units);
        String volumeWithUnits = String.format(valueWithUnitsStringWithPlaceholders, price, currencySymbol);
        textView.setText(volumeWithUnits);
    }

    @BindingAdapter({"android:priceWithCurrencySymbol"})
    public static void setPriceWithCurrencySymbol(TextView textView, BigDecimal price) {
        String priceStr = String.valueOf(price);
        if(!priceStr.isEmpty()) {
            String currencySymbol = ApplicationFacade.getInstance().getSettings().getCurrency();
            String valueWithUnitsStringWithPlaceholders = textView.getContext().getResources().getString(R.string._value_with_units);
            String volumeWithUnits = String.format(valueWithUnitsStringWithPlaceholders, priceStr, currencySymbol);
            textView.setText(volumeWithUnits);
        }
    }

    @BindingAdapter({"android:priceWithCurrencySymbolInParentheses"})
    public static void setPriceWithCurrencySymbolInParentheses(TextView textView, BigDecimal price) {
        if(price == null) {
            return;
        }

        String priceStr = String.valueOf(price);
        if(!priceStr.isEmpty()) {
            String currencySymbol = ApplicationFacade.getInstance().getSettings().getCurrency();
            String valueWithUnitsStringWithPlaceholders = textView.getContext().getResources().getString(R.string._value_with_units_in_parentheses);
            String volumeWithUnits = String.format(valueWithUnitsStringWithPlaceholders, priceStr, currencySymbol);
            textView.setText(volumeWithUnits);
        }
    }
}