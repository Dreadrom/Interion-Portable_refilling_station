package com.technotrade.pts2.pts2testapp;

import androidx.annotation.StringRes;

public interface StringProvider {
	String getResourceString(@StringRes int resId);
}