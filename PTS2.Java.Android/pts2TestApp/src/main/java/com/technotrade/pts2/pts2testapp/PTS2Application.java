package com.technotrade.pts2.pts2testapp;

import android.app.Application;

public class PTS2Application extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
		applicationFacade.init(getApplicationContext());
	}
}