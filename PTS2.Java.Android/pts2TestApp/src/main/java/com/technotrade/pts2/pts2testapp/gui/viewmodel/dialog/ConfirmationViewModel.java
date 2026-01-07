package com.technotrade.pts2.pts2testapp.gui.viewmodel.dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.technotrade.pts2.pts2testapp.entity.Event;

public class ConfirmationViewModel extends ViewModel {
    private final MutableLiveData<String> mDialogMessage;
    private final MutableLiveData<Event<Boolean>> mConfirmation;

    public ConfirmationViewModel() {
        mDialogMessage = new MutableLiveData<>();
        mConfirmation = new MutableLiveData<>();
    }

    public LiveData<String> getDialogMessage() {
        return mDialogMessage;
    }

    public LiveData<Event<Boolean>> getConfirmation() {
        return mConfirmation;
    }

    public void showDialogMessage(String message) {
        mDialogMessage.setValue(message);
    }

    public void onYesClicked() {
        mConfirmation.setValue(new Event<>(true, false));
    }

    public void onNoClicked() {
        mConfirmation.setValue(new Event<>(false, false));
    }

    public void onDialogDismissed() {
        mConfirmation.setValue(new Event<>(null, false));
    }
}