package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import androidx.annotation.NonNull;

public enum ViewModelCommand {
	showConnectingProgress("showConnectingProgress"),
	showConnectedButton("showConnectedButton"),
	showDisconnectedButton("showDisconnectedButton"),
	setSettingsButtonEnabled("setSettingsButtonEnabled"),
	navigateToPump("navigateToPump"),
	showNozzleIsNotPickedUpDialog("showNozzleIsNotPickedUpDialog"),
	chooseNozzleWithList("chooseNozzleWithList"),
	chooseVolumeWithKeyboard("chooseVolumeWithKeyboard"),
	chooseAmountWithKeyboard("chooseAmountWithKeyboard"),
	clearKeyboardStack("clearKeyboardStack"),
	showError("showError"),
	navigateUp("navigateUp"),
	showOrderConfirmation("showOrderConfirmation"),
	showOrdered("showOrdered"),
	setProgressVisible("setProgressVisible"),
	showSynchronizePts2TimeToLocalConfirmationDialog("showSynchronizePts2TimeToLocalConfirmationDialog"),
	setSynchronizePts2TimeToLocalButtonEnabled("setSynchronizePts2TimeToLocalButtonEnabled"),
	updateNozzlesForSelectedPumpItem("updateNozzlesForSelectedPumpItem");

	private final String mText;

	ViewModelCommand(final String text) {
		mText = text;
	}

	@NonNull
	@Override
	public String toString() {
		return mText;
	}
}