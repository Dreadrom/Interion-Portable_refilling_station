package com.technotrade.pts2.pts2testapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.technotrade.pts2.Device;
import com.technotrade.pts2.Settings;
import com.technotrade.pts2.datastructs.DateTimeSettings;
import com.technotrade.pts2.datastructs.Pump;
import com.technotrade.pts2.datastructs.PumpAuthorizeData;
import com.technotrade.pts2.datastructs.PumpsConfiguration;
import com.technotrade.pts2.enumeration.AuthenticationType;
import com.technotrade.pts2.enumeration.NozzleOrFuelIdSelector;
import com.technotrade.pts2.enumeration.ProtocolSecurityType;
import com.technotrade.pts2.enumeration.PumpAuthorizeType;
import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.network.IRequest;
import com.technotrade.pts2.pts2testapp.entity.ErrorInfo;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.helper.ValidationHelper;
import com.technotrade.pts2.util.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manager class that is responsible for work with PTS2 device
 */
public class PTSManager {
    private final WeakReference<Context> mContextRef;
    private final Condition mWaitStatusRequestCondition;
    private final Lock mWaitStatusRequestConditionLock;
    private final Device mDevice;
    private final DataStorage mDataStorage;
    private final DataObserver mDataObserver;

    // Scheduler for polling device
    private ScheduledExecutorService mPollerScheduler;

    public PTSManager(Context context) {
        Context appContext = context.getApplicationContext();
        mContextRef = new WeakReference<>(appContext);
        mDevice = new Device(appContext);
        mDataStorage = new DataStorage();
        mDataObserver = new DataObserver(mDataStorage);
        mWaitStatusRequestConditionLock = new ReentrantLock();
        mWaitStatusRequestCondition = mWaitStatusRequestConditionLock.newCondition();
    }

    public synchronized Settings getSettings() {
        return mDevice.getSettings();
    }

    public synchronized void setSettings(Settings settings) {
        mDevice.setSettings(settings);
    }

    public DataStorage getDataStorage() {
        return mDataStorage;
    }

    public boolean open() {
        if (isOpened()) close();

        loadPTSSettings();

        Result result = mDevice.open();
        if (result != Result.SUCCESS) {
            handleError(result);
            return false;
        }

        mDevice.removeObservers();
        mDevice.registerObserver(mDataObserver);

        // Start polling immediately
        startPolling();

        // Wait for first status response
        final long timeout = getSettings().getTimeoutSeconds() * 1000L;
        final long discreteTimeout = 10L;
        long discreteTimeoutTotal = 0L;
        boolean signaled = false;

        while (discreteTimeoutTotal <= timeout) {
            try {
                mWaitStatusRequestConditionLock.lock();
                signaled = mWaitStatusRequestCondition.await(discreteTimeout, TimeUnit.MILLISECONDS);
                if (signaled) {
                    Logger.info("waitStatusRequestCondition signaled");
                    break;
                }
                discreteTimeoutTotal += discreteTimeout;
            } catch (InterruptedException e) {
                if (!mDevice.isOpened()) break;
            } finally {
                mWaitStatusRequestConditionLock.unlock();
            }
        }

        if (!signaled) {
            Logger.error("Error: Timed out");
            close();
        }

        getDataThatNeedAtStart();
        mDataStorage.setOpened(true);

        return signaled;
    }

    public void close() {
        try {
            stopPolling();
            mDevice.removeObservers();
            mDataStorage.setOpened(false);
        } catch (Exception e) {
            Log.e("PTS2", "Error closing PTSManager", e);
        }

        mDevice.close();
    }

    public boolean isOpened() {
        return mDevice.isOpened();
    }

    public boolean isConnected() {
        return mDevice.isConnected();
    }

    public void signalAboutStatusRequest() {
        mWaitStatusRequestConditionLock.lock();
        try {
            mWaitStatusRequestCondition.signal();
        } finally {
            mWaitStatusRequestConditionLock.unlock();
        }
    }

    // ---------------- Polling logic ----------------

    public void startPolling() {
        if (mPollerScheduler != null && !mPollerScheduler.isShutdown()) return;

        mPollerScheduler = Executors.newSingleThreadScheduledExecutor();
        mPollerScheduler.scheduleWithFixedDelay(() -> {
            try {
                if (isOpened()) threadPollerMethod();
            } catch (Exception e) {
                Log.e("PTS2", "Exception during polling", e);
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public void stopPolling() {
        if (mPollerScheduler != null) {
            mPollerScheduler.shutdownNow();
            mPollerScheduler = null;
        }
    }

    public synchronized void threadPollerMethod() {
        try {
            Result result = askStatusAndAnotherPeriodicalData();

            if (result == Result.SUCCESS) {
                mDataStorage.setConnected(true);
                signalAboutStatusRequest();
            } else {
                mDataStorage.setConnected(false);
                handleError(result);
            }
        } catch (Exception e) {
            Log.e("PTS2", "Exception in threadPollerMethod", e);
            mDataStorage.setConnected(false);
        }
    }

    public synchronized Result askStatusAndAnotherPeriodicalData() {
        Result result = mDevice.clearRequestsQueue();

        PumpsConfiguration pumpsConfiguration = mDataStorage.getPumpsConfiguration();
        if (pumpsConfiguration != null) {
            ArrayList<Pump> pumps = pumpsConfiguration.getPumps();
            for (Pump pump : pumps) {
                if (result == Result.SUCCESS) result = mDevice.pumpGetStatus(pump.getId());
            }
        }

        if (result == Result.SUCCESS) result = mDevice.getConfigurationIdentifier();
        if (result == Result.SUCCESS) result = mDevice.executeRequestsQueue();

        if (result != Result.SUCCESS) handleError(result);

        return result;
    }

    // ---------------- Error handling ----------------

    public synchronized void handleError(Result result) {
        handleError(result, errorInfo -> {});
    }

    public synchronized void handleError(Result result, java.util.function.Consumer<ErrorInfo> errorInfoCallback) {
        String error = "Error: " + result.getCode() + "\r\n" + " message: " + result.getDescription();
        Logger.error(error);

        ErrorInfo errorInfo = new ErrorInfo("", result.getCode(), result.getDescription());

        if (result == Result.AT_LAST_ONE_REQUEST_IN_SEQUENCE_FAILED_ERROR) {
            ArrayList<IRequest<?>> requestsQueue = mDevice.getRequestsQueue();
            List<ErrorInfo> innerErrorsInfo = new ArrayList<>();

            for (IRequest<?> request : requestsQueue) {
                if (request.isError()) {
                    innerErrorsInfo.add(new ErrorInfo(request.getRequestName(), request.getErrorCode(), request.getErrorMessage()));
                }
            }

            errorInfo.setInnerErrorsInfo(innerErrorsInfo);
        }

        errorInfoCallback.accept(errorInfo);
    }

    public void notOpened() {
        Logger.error("Error: Device is not opened");
    }

    // ---------------- Data fetching ----------------

    public synchronized boolean getDataThatNeedAtStart() {
        if (!isOpened()) {
            notOpened();
            return false;
        }

        Result result = mDevice.clearRequestsQueue();

        if (result == Result.SUCCESS) result = mDevice.getConfigurationIdentifier();
        if (result == Result.SUCCESS) result = mDevice.getDateTime();
        if (result == Result.SUCCESS) result = mDevice.getMeasurementUnits();
        if (result == Result.SUCCESS) result = mDevice.getPumpsConfiguration();
        if (result == Result.SUCCESS) result = mDevice.getFuelGradesConfiguration();
        if (result == Result.SUCCESS) result = mDevice.getPumpNozzlesConfiguration();
        if (result == Result.SUCCESS) result = mDevice.executeRequestsQueue();

        if (result != Result.SUCCESS) handleError(result);

        return result == Result.SUCCESS;
    }

    // ---------------- Operations ----------------

    public synchronized Result pumpAuthorize(Order order) {
        if (!isOpened()) {
            notOpened();
            return Result.INIT_ERROR;
        }

        if (order == null) {
            return Result.INPUT_DATA_IS_WRONG;
        }

        if (!order.isPumpSet() || !order.isNozzleSet()) {
            return Result.INPUT_DATA_IS_WRONG;
        }

        PumpAuthorizeData pumpAuthorizeData = new PumpAuthorizeData();

        pumpAuthorizeData.setAutoCloseTransaction(true);
        pumpAuthorizeData.setPump(order.getPump().getNumber());
        pumpAuthorizeData.setNozzle(order.getNozzle().getNozzleNumber());
        pumpAuthorizeData.setNozzleOrFuelIdSelector(NozzleOrFuelIdSelector.NOZZLE);

        if (order.isQuantitySet()) {
            pumpAuthorizeData.setType(PumpAuthorizeType.VOLUME);
            pumpAuthorizeData.setDose(order.getQuantity());
        } else if (order.isAmountSet()) {
            pumpAuthorizeData.setType(PumpAuthorizeType.AMOUNT);
            pumpAuthorizeData.setDose(order.getAmount());
        } else {
            pumpAuthorizeData.setType(PumpAuthorizeType.FULLTANK);
        }

        Result result = mDevice.clearRequestsQueue();

        if (result == Result.SUCCESS) {
            mDevice.pumpAuthorize(pumpAuthorizeData);
        }

        if (result == Result.SUCCESS) {
            result = mDevice.executeRequestsQueue();
        }

        if (result != Result.SUCCESS) {
            handleError(result);
        }

        return result;
    }

    public synchronized Result pumpStop(Order order) {
        if (!isOpened()) {
            notOpened();
            return Result.INIT_ERROR;
        }

        if (order == null) {
            return Result.INPUT_DATA_IS_WRONG;
        }

        if (!order.isPumpSet()) {
            return Result.INPUT_DATA_IS_WRONG;
        }

        Result result = mDevice.clearRequestsQueue();

        if (result == Result.SUCCESS) {
            mDevice.pumpStop(order.getPump().getNumber());
        }

        if (result == Result.SUCCESS) {
            result = mDevice.executeRequestsQueue();
        }

        if (result != Result.SUCCESS) {
            handleError(result);
        }
        
        return result;
    }

    public synchronized Result pumpStopWithNumber(int pump) {
        if (!isOpened()) {
            notOpened();
            return Result.INIT_ERROR;
        }

        Result result = mDevice.clearRequestsQueue();

        if (result == Result.SUCCESS) {
            mDevice.pumpStop(pump);
        }

        if (result == Result.SUCCESS) {
            result = mDevice.executeRequestsQueue();
        }

        if (result != Result.SUCCESS) {
            handleError(result);
        }

        return result;
    }

    public synchronized Result pumpSuspend(Order order) {
        if (!isOpened()) {
            notOpened();
            return Result.INIT_ERROR;
        }

        if (order == null) {
            return Result.INPUT_DATA_IS_WRONG;
        }

        if (!order.isPumpSet()) {
            return Result.INPUT_DATA_IS_WRONG;
        }

        Result result = mDevice.clearRequestsQueue();

        if (result == Result.SUCCESS) {
            mDevice.pumpSuspend(order.getPump().getNumber());
        }

        if (result == Result.SUCCESS) {
            result = mDevice.executeRequestsQueue();
        }

        if (result != Result.SUCCESS) {
            handleError(result);
        }
        
        return result;
    }

    public synchronized Result pumpResume(Order order) {
        if (!isOpened()) {
            notOpened();
            return Result.INIT_ERROR;
        }

        if (order == null) {
            return Result.INPUT_DATA_IS_WRONG;
        }

        if (!order.isPumpSet()) {
            return Result.INPUT_DATA_IS_WRONG;
        }

        Result result = mDevice.clearRequestsQueue();

        if (result == Result.SUCCESS) {
            mDevice.pumpResume(order.getPump().getNumber());
        }

        if (result == Result.SUCCESS) {
            result = mDevice.executeRequestsQueue();
        }

        if (result != Result.SUCCESS) {
            handleError(result);
        }

        return result;
    }

    /// <summary>
    /// Return the date and time from PTS2 device
    /// </summary>
    /// <returns>boolean</returns>
    public synchronized boolean getDateTime() {
        if (!isOpened()) {
            notOpened();
            return false;
        }

        Result result = mDevice.clearRequestsQueue();

        if (result == Result.SUCCESS) {
            result = mDevice.getDateTime();
        }

        if (result == Result.SUCCESS) {
            result = mDevice.executeRequestsQueue();
        }

        if (result != Result.SUCCESS) {
            handleError(result);
        }

        return result == Result.SUCCESS;
    }

    /// <summary>
    /// Return a firmware information from PTS2 device
    /// </summary>
    /// <returns>boolean</returns>
    public synchronized boolean getFirmwareInformation() {
        if (!isOpened()) {
            notOpened();
            return false;
        }

        Result result = mDevice.clearRequestsQueue();

        if (result == Result.SUCCESS) {
            result = mDevice.getFirmwareInformation();
        }

        if (result == Result.SUCCESS) {
            result = mDevice.executeRequestsQueue();
        }

        if (result != Result.SUCCESS) {
            handleError(result);
        }

        return result == Result.SUCCESS;
    }

    /// <summary>
    /// Return the date and time from PTS2 device
    /// </summary>
    /// <returns>boolean</returns>
    public synchronized boolean setDateTime(DateTimeSettings dateTimeSettings) {
        if (!isOpened()) {
            notOpened();
            return false;
        }

        Result result = mDevice.clearRequestsQueue();

        if (result == Result.SUCCESS) {
            result = mDevice.setDateTime(dateTimeSettings);
        }

        if (result == Result.SUCCESS) {
            result = mDevice.executeRequestsQueue();
        }

        if (result != Result.SUCCESS) {
            handleError(result);
        }
        
        return result == Result.SUCCESS;
    }

    /// <summary>
    /// Running some PTS2 requests
    /// </summary>
    /// <returns>boolean</returns>
    public synchronized boolean runTest() {
        if (!isOpened()) {
            notOpened();
            return false;
        }

        Result result = mDevice.clearRequestsQueue();

        if (result == Result.SUCCESS) {
            result = mDevice.getBatteryVoltage();
        }

        if (result == Result.SUCCESS) {
            result = mDevice.getCpuTemperature();
        }

        if (result == Result.SUCCESS) {
            result = mDevice.getDateTime();
        }

        if (result == Result.SUCCESS) {
            result = mDevice.getFirmwareInformation();
        }

        if (result == Result.SUCCESS) {
            result = mDevice.getMeasurementUnits();
        }

        if (result == Result.SUCCESS) {
            result = mDevice.executeRequestsQueue();
        }

        if (result != Result.SUCCESS) {
            handleError(result);
        }

        return result == Result.SUCCESS;
    }

    public int getStatusColor(PumpStatus pumpStatus) {
        int resource = -1;

        switch (pumpStatus) {
            case OFFLINE:
                resource = R.color.grey_dimgray;
                break;
            case IDLE:
                resource = R.color.blue;
                break;
            case FILLING:
                resource = R.color.teal_700;
                break;
            case NOZZLE:
                resource = R.color.purple_700;
                break;
            case NONE:
            default:
                resource = R.color.red_dark;
        }

        return ContextCompat.getColor(mContextRef.get(), resource);
    }

    public int getStatusBackgroundColor(PumpStatus pumpStatus) {
        int resource = -1;

        switch (pumpStatus) {
            case OFFLINE:
                resource = R.color.grey;
                break;
            case IDLE:
                resource = R.color.skyblue;
                break;
            case FILLING:
                resource = R.color.teal_200;
                break;
            case NOZZLE:
                resource = R.color.purple_200;
                break;
            case NONE:
            default:
                resource = R.color.red_lightcoral;
        }

        return ContextCompat.getColor(mContextRef.get(), resource);
    }

    /// <summary>
    /// Loading PTS2 settings using SharedPreferences
    /// </summary>
    public synchronized void loadPTSSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContextRef.get());
        Settings settings = getSettings();

        String login = preferences.getString("login", "admin");
        settings.setLogin(login);

        String password = preferences.getString("password", "admin");
        settings.setPassword(password);

        String hostDomain = preferences.getString("host_domain", "192.168.1.117");
        settings.setHost(hostDomain);

        int defaultHttpPortNumber = 80;
        int httpPortNumber;

        String httpPortNumberString = preferences.getString("http_port_number", String.valueOf(defaultHttpPortNumber));
        httpPortNumber = Integer.parseInt(httpPortNumberString);

        if (!ValidationHelper.isValidPortNumber(httpPortNumber)) {
            httpPortNumber = defaultHttpPortNumber;
        }

        settings.setHttpPort((short) httpPortNumber);

        int defaultHttpsPortNumber = 443;
        int httpsPortNumber;

        String httpsPortNumberString = preferences.getString("https_port_number", String.valueOf(defaultHttpsPortNumber));
        httpsPortNumber = Integer.parseInt(httpsPortNumberString);

        if (!ValidationHelper.isValidPortNumber(httpsPortNumber)) {
            httpsPortNumber = defaultHttpsPortNumber;
        }

        settings.setHttpsPort((short) httpsPortNumber);

        String protocol = preferences.getString("protocol_preference", "https");
        if ("https".equals(protocol)) {
            settings.setProtocolSecurityType(ProtocolSecurityType.HTTPS);
        } else {
            settings.setProtocolSecurityType(ProtocolSecurityType.HTTP);
        }

        boolean digest = preferences.getBoolean("digest_auth_switch", true);
        if(digest) {
            settings.setAuthenticationType(AuthenticationType.DIGEST);
        }
        else {
            settings.setAuthenticationType(AuthenticationType.BASIC);
        }

        mDevice.setSettings(settings);

        Logger.info("PTS settings loaded");
    }
}