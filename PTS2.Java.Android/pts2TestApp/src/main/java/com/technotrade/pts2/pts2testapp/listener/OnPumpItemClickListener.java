package com.technotrade.pts2.pts2testapp.listener;

import com.technotrade.pts2.pts2testapp.entity.PumpItem;

public abstract class OnPumpItemClickListener {
	private boolean mFireOnce;

	public OnPumpItemClickListener() {
		mFireOnce = false;
	}

	public boolean getFireOnce() {
		return mFireOnce;
	}

	public void setFireOnce(boolean fireOnce) {
		this.mFireOnce = fireOnce;
	}

	public abstract void onPumpItemClick(PumpItem item);


}