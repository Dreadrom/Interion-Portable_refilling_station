package com.technotrade.pts2.pts2testapp.gui.viewmodel.dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.technotrade.pts2.pts2testapp.entity.Event;

public class ExclamationViewModel extends ViewModel {
    private final MutableLiveData<String> mDialogMessage;
    private final MutableLiveData<Event<Boolean>> mAcknowledgement;

    public ExclamationViewModel() {
        mDialogMessage = new MutableLiveData<>();
        mAcknowledgement = new MutableLiveData<>();
    }

    public LiveData<String> getDialogMessage() {
        return mDialogMessage;
    }

    public void showDialogMessage(String message) {
        mDialogMessage.setValue(message);
    }

    public LiveData<Event<Boolean>> getAcknowledgement() {
        return mAcknowledgement;
    }

    public void onOkClicked() {
        mAcknowledgement.setValue(new Event<>(true, false));
    }

    public void onDialogDismissed() {
        mAcknowledgement.setValue(new Event<>(null, false));
    }
}