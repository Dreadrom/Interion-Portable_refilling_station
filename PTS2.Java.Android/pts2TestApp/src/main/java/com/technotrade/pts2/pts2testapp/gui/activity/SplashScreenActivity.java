package com.technotrade.pts2.pts2testapp.gui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashScreenActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SplashScreen.installSplashScreen(this);

		super.onCreate(savedInstanceState);

		Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
//		Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}