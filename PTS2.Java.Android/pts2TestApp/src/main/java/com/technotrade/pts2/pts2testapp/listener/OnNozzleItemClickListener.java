package com.technotrade.pts2.pts2testapp.listener;

import com.technotrade.pts2.pts2testapp.entity.NozzleItem;

public abstract class OnNozzleItemClickListener {
	private boolean mFireOnce;

	public OnNozzleItemClickListener() {
		mFireOnce = false;
	}

	public boolean getFireOnce() {
		return mFireOnce;
	}

	public void setFireOnce(boolean fireOnce) {
		this.mFireOnce = fireOnce;
	}

	public abstract void onNozzleItemClick(NozzleItem item);


}