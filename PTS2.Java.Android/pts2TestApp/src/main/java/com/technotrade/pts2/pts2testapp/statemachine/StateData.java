package com.technotrade.pts2.pts2testapp.statemachine;

import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;

public class StateData {
	private String mText;
	private BaseViewModel mViewModel;
	private PumpItem mPumpItem;
	private boolean mFullTank;

	public StateData() {
	}

	public String getText() {
		return mText;
	}
	public void setText(String text) {
		mText = text;
	}

	public BaseViewModel getViewModel() {
		return mViewModel;
	}
	public void setViewModel(BaseViewModel viewModel) {
		mViewModel = viewModel;
	}

	public PumpItem getPumpItem() {
		return mPumpItem;
	}
	public void setPumpItem(PumpItem pumpItem) {
		mPumpItem = pumpItem;
	}

	public boolean getFullTank() {
		return mFullTank;
	}
	public void setFullTank(boolean fullTank) {
		mFullTank = fullTank;
	}
}
