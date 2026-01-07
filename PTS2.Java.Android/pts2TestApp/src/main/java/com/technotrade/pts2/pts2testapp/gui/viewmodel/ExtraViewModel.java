package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.setProgressVisible;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.setSynchronizePts2TimeToLocalButtonEnabled;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showSynchronizePts2TimeToLocalConfirmationDialog;

import android.view.View;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.technotrade.pts2.datastructs.DateTimeSettings;
import com.technotrade.pts2.datastructs.FirmwareInformation;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.DataStorage;
import com.technotrade.pts2.pts2testapp.PTSManager;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.ResourceManager;
import com.technotrade.pts2.pts2testapp.entity.DataHolder;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.helper.TimeHelper;
import com.technotrade.pts2.pts2testapp.listener.DataChangeListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExtraViewModel extends BaseViewModel {
    private final PTSManager mPTSManager;
    private final DataStorage mDataStorage;
    private final ResourceManager mResourceManager;
    private final MutableLiveData<String> mLocalDateTimeFormatted;
    private final MutableLiveData<String> mPts2DateTimeFormatted;
    private final MutableLiveData<String> mFirmwareInformationDateTimeFormatted;
    private final MutableLiveData<String> mFirmwareInformationProtocols;
    private final Lock mGetDateTimeLock;
    private final Condition mGotDateTimeSettingsCondition;
    private final Lock mGetFirmwareInformationLock;
    private DataChangeListener<DateTimeSettings> mDateTimeSettingsListener;
    private DataChangeListener<FirmwareInformation> mFirmwareInformationListener;
    private DateTimeSettings mDateTimeSettings;
    private FirmwareInformation mFirmwareInformation;
    private boolean mDateTimeTaken;

    public ExtraViewModel() {
        ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
        mPTSManager = applicationFacade.getPTSManager();
        mDataStorage = mPTSManager.getDataStorage();
        mResourceManager = applicationFacade.getResourceManager();
        mLocalDateTimeFormatted = new MutableLiveData<>();
        mPts2DateTimeFormatted = new MutableLiveData<>();
        mFirmwareInformationDateTimeFormatted = new MutableLiveData<>();
        mFirmwareInformationProtocols = new MutableLiveData<>();
        mGetDateTimeLock = new ReentrantLock();
        mGetFirmwareInformationLock = new ReentrantLock();
        mGotDateTimeSettingsCondition = mGetDateTimeLock.newCondition();
        mDateTimeSettingsListener = null;
        mFirmwareInformationListener = null;
        mDateTimeSettings = null;
        mFirmwareInformation = null;
        mDateTimeTaken = false;
    }

    public void onStart() {
        if (mDateTimeSettingsListener == null) {
            mDateTimeSettingsListener = data -> {

                mGetDateTimeLock.lock();
                try {
                    mDateTimeSettings = data;
                    mDateTimeTaken = true;
                    mGotDateTimeSettingsCondition.signal();
                } finally {
                    mGetDateTimeLock.unlock();
                }

                String pts2DateTimeFormatted = getFormattedDateTime(TimeHelper.convertToLocalDateTimeViaInstant(data.getDate()));
                mPts2DateTimeFormatted.postValue(pts2DateTimeFormatted);
            };

            DataHolder<DateTimeSettings> dateTimeSettingsDataHolder = mDataStorage.getDateTimeDataHolder();
            dateTimeSettingsDataHolder.addOnDataChangeListener(mDateTimeSettingsListener);
        }

        if (mFirmwareInformationListener == null) {
            mFirmwareInformationListener = data -> {

                mGetFirmwareInformationLock.lock();
                try {
                    mFirmwareInformation = data;
                } finally {
                    mGetFirmwareInformationLock.unlock();
                }

                String firmwareDateTimeFormatted = getFormattedDateTime(TimeHelper.convertToLocalDateTimeViaInstant(data.getDateTime()));
                mFirmwareInformationDateTimeFormatted.postValue(firmwareDateTimeFormatted);

                ArrayList<Integer> protocols = data.getPumpProtocols();
                StringBuilder protocolsWithDelimeter = new StringBuilder();
                final String delimeter = ",";

                for(int i = 0; i < protocols.size(); ++i) {
                    protocolsWithDelimeter.append(protocols.get(i));

                    if(i < protocols.size() - 1) {
                        protocolsWithDelimeter.append(delimeter);
                    }
                }

                mFirmwareInformationProtocols.postValue(protocolsWithDelimeter.toString());
            };

            DataHolder<FirmwareInformation> firmwareInformationDataHolder = mDataStorage.getFirmwareInformationDataHolder();
            firmwareInformationDataHolder.addOnDataChangeListener(mFirmwareInformationListener);
        }
    }

    public void onStop() {
        if (mDateTimeSettingsListener != null) {
            DataHolder<DateTimeSettings> dateTimeSettingsDataHolder = mDataStorage.getDateTimeDataHolder();
            dateTimeSettingsDataHolder.removeOnDataChangeListener(mDateTimeSettingsListener);
            mDateTimeSettingsListener = null;
        }
    }

    public void onGetLocalAndPTS2TimeButtonClicked(View view) {
        sendViewModelCommandEvent(setProgressVisible.toString(), true);

        updateLocalAndPTS2Fields(
            () -> sendViewModelCommandEvent(setProgressVisible.toString(), false),
            message -> {
                sendViewModelCommandEvents(Arrays.asList(
                    new EventCommand<>(ViewModelCommand.setProgressVisible.name(), null),
                    new EventCommand<>(ViewModelCommand.showError.name(), message)
                ));
            }
        );
    }

    public void onGetFirmwareInformationButtonClicked(View view) {
        sendViewModelCommandEvent(setProgressVisible.toString(), true);

        updateFirmwareInformationFields(
            () -> sendViewModelCommandEvent(setProgressVisible.toString(), false),
            message -> {
                sendViewModelCommandEvents(Arrays.asList(
                    new EventCommand<>(ViewModelCommand.setProgressVisible.name(), null),
                    new EventCommand<>(ViewModelCommand.showError.name(), message)
                ));
            }
        );
    }

    public void onSynchronizePts2TimeToLocalButtonClicked(View view) {
        sendViewModelCommandEvent(showSynchronizePts2TimeToLocalConfirmationDialog.toString(), null);
    }

    public LiveData<String> getFormattedLocalDateTime() {
        return mLocalDateTimeFormatted;
    }

    public LiveData<String> getFormattedPts2DateTime() {
        return mPts2DateTimeFormatted;
    }

    public LiveData<String> getFirmwareInformationDateTime() {
        return mFirmwareInformationDateTimeFormatted;
    }

    public LiveData<String> getFirmwareInformationProtocols() {
        return mFirmwareInformationProtocols;
    }

    public String getFormattedDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm:ss")) : "";
    }

    public void updateLocalAndPTS2Fields(Runnable onSuccess, Consumer<String> onFail) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String localDateTimeFormatted = getFormattedDateTime(localDateTime);
        mLocalDateTimeFormatted.setValue(localDateTimeFormatted);

        boolean bRes = mPTSManager.getDateTime();

        if (!bRes) {
            onFail.accept(mResourceManager.getResourceString(R.string.date_and_time_retrieving_error));
        }

        onSuccess.run();
    }

    public void synchronizePts2TimeToLocal(Runnable onSuccess, Consumer<String> onFail) {
        try {
            sendViewModelCommandEvent(setSynchronizePts2TimeToLocalButtonEnabled.toString(), false);

            mDateTimeTaken = false;
            boolean bRes = mPTSManager.getDateTime();

            if (!bRes) {
                onFail.accept(mResourceManager.getResourceString(R.string.date_and_time_synchronization_error));
                return;
            }

            mGetDateTimeLock.lock();

            try {
                while (!mDateTimeTaken) {
                    try {
                        mGotDateTimeSettingsCondition.await(2000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        onFail.accept(mResourceManager.getResourceString(R.string.date_and_time_synchronization_error));
                        return;
                    }
                }

                mDateTimeTaken = false;
            } finally {
                mGetDateTimeLock.unlock();
            }

            if (mDateTimeSettings == null) {
                onFail.accept(mResourceManager.getResourceString(R.string.date_and_time_synchronization_error));
                return;
            }

            DateTimeSettings dateTimeSettingsToSet;

            try {
                dateTimeSettingsToSet = mDateTimeSettings.clone();
            } catch (CloneNotSupportedException e) {
                onFail.accept(mResourceManager.getResourceString(R.string.date_and_time_synchronization_error));
                return;
            }

            LocalDateTime localDateTime = LocalDateTime.now();
            dateTimeSettingsToSet.setDate(TimeHelper.convertToDateViaInstant(localDateTime));

            bRes = mPTSManager.setDateTime(dateTimeSettingsToSet);

            if (!bRes) {
                onFail.accept(mResourceManager.getResourceString(R.string.date_and_time_synchronization_error));
                return;
            }

            onSuccess.run();
        } finally {
            sendViewModelCommandEvent(setSynchronizePts2TimeToLocalButtonEnabled.toString(), true);
        }
    }

    public void updateFirmwareInformationFields(Runnable onSuccess, Consumer<String> onFail) {
        boolean bRes = mPTSManager.getFirmwareInformation();

        if (!bRes) {
            onFail.accept(mResourceManager.getResourceString(R.string.firmware_information_retrieving_error));
        }

        onSuccess.run();
    }
}