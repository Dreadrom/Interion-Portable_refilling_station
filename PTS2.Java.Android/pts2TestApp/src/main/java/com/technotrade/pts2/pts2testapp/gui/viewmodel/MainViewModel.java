package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showConnectedButton;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showConnectingProgress;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showDisconnectedButton;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showError;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.PTSManager;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.StringProvider;
import com.technotrade.pts2.pts2testapp.entity.DataHolder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class MainViewModel extends BaseViewModel {
    private final StringProvider mStringProvider;
    private final PTSManager mPTSManager;
    static boolean mConnectionButtonBusy;
    private final MutableLiveData<Boolean> mConnectButtonEnabled;
    private final MutableLiveData<Boolean> mSettingsButtonEnabled;

    public MainViewModel(StringProvider stringProvider) {
        mStringProvider = stringProvider;

        mPTSManager = ApplicationFacade.getInstance().getPTSManager();
        mConnectionButtonBusy = false;
        mConnectButtonEnabled = new MutableLiveData<>(true);
        mSettingsButtonEnabled = new MutableLiveData<>(true);
    }

    public void onStart() {
        initOpenedListener();
        initConnectedListener();
    }

    private void initOpenedListener() {
        DataHolder<Boolean> openedDataHolder = ApplicationFacade.getInstance().getPTSManager().getDataStorage().getOpenedDataHolder();
        openedDataHolder.addOnDataChangeListener(opened -> {
            if (opened) {
                if (mPTSManager.isConnected()) {
                    sendViewModelCommandEvent(showConnectedButton.toString(), null);
                } else {
                    sendViewModelCommandEvent(showConnectingProgress.toString(), null);
                }
            }
            else {
                sendViewModelCommandEvent(showDisconnectedButton.toString(), null);
            }
        });
    }

    private void initConnectedListener() {
        DataHolder<Boolean> connectedDataHolder = ApplicationFacade.getInstance().getPTSManager().getDataStorage().getConnectedDataHolder();
        connectedDataHolder.addOnDataChangeListener(connected -> {
            if (connected) {
                if (mPTSManager.isOpened()) {
                    sendViewModelCommandEvent(showConnectedButton.toString(), null);
                }
            }
            else {
                if (mPTSManager.isOpened()) {
                    sendViewModelCommandEvent(showConnectingProgress.toString(), true);
                }
                else {
                    sendViewModelCommandEvent(showDisconnectedButton.toString(), true);
                }
            }
        });
    }

    public LiveData<Boolean> getConnectButtonEnabled() {
        return mConnectButtonEnabled;
    }

    public void setConnectButtonEnabled(boolean enabled) {
        mConnectButtonEnabled.postValue(enabled);
    }

    public LiveData<Boolean> getSettingsButtonEnabled() {
        return mSettingsButtonEnabled;
    }

    public void setSettingsButtonEnabled(boolean enabled) {
        mSettingsButtonEnabled.postValue(enabled);
    }

    public void onConnectClicked() {
        PTSManager ptsManager = ApplicationFacade.getInstance().getPTSManager();

        if (ptsManager.isOpened()) {
            disconnect();
        } else {
            connect();
        }
    }

    public void connect() {
        setConnectButtonEnabled(false);
        setSettingsButtonEnabled(false);
        sendViewModelCommandEvent(showConnectingProgress.toString(), null);

        Supplier<Boolean> supplierOpen = mPTSManager::open;
        CompletableFuture<Boolean> futureOpen = CompletableFuture.supplyAsync(supplierOpen);
        futureOpen.thenAccept(result -> {
            if (!result) {
                disconnect();
                String message = mStringProvider.getResourceString(R.string.failed_to_establish_a_connection);
                sendViewModelCommandEvent(showError.toString(), message);
            }
            else {
                setConnectButtonEnabled(true);
            }
        });
    }

    public void disconnect() {
        setConnectButtonEnabled(false);
        setSettingsButtonEnabled(false);
        sendViewModelCommandEvent(showConnectingProgress.toString(), null);

        Runnable taskClose = mPTSManager::close;
        CompletableFuture<Void> futureStop = CompletableFuture.runAsync(taskClose);
        futureStop.thenRun(() -> {
            sendViewModelCommandEvent(showDisconnectedButton.toString(), null);
            setConnectButtonEnabled(true);
            setSettingsButtonEnabled(true);
        });
    }
}