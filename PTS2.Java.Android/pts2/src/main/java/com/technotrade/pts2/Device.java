package com.technotrade.pts2;

import android.content.Context;

import com.technotrade.pts2.datastructs.DateTimeSettings;
import com.technotrade.pts2.datastructs.FirmwareInformation;
import com.technotrade.pts2.datastructs.FuelGrade;
import com.technotrade.pts2.datastructs.GpsData;
import com.technotrade.pts2.datastructs.MeasurementUnits;
import com.technotrade.pts2.datastructs.Parameter;
import com.technotrade.pts2.datastructs.PriceBoardsConfiguration;
import com.technotrade.pts2.datastructs.Probe;
import com.technotrade.pts2.datastructs.ProbeMeasurements;
import com.technotrade.pts2.datastructs.ProbeTankVolumeForHeight;
import com.technotrade.pts2.datastructs.ProbesConfiguration;
import com.technotrade.pts2.datastructs.PtsNetworkSettings;
import com.technotrade.pts2.datastructs.PumpAuthorizeConfirmation;
import com.technotrade.pts2.datastructs.PumpAuthorizeData;
import com.technotrade.pts2.datastructs.PumpAutomaticOperation;
import com.technotrade.pts2.datastructs.PumpDisplayData;
import com.technotrade.pts2.datastructs.PumpEndOfTransactionStatus;
import com.technotrade.pts2.datastructs.PumpFillingStatus;
import com.technotrade.pts2.datastructs.PumpIdleStatus;
import com.technotrade.pts2.datastructs.PumpNozzles;
import com.technotrade.pts2.datastructs.PumpOfflineStatus;
import com.technotrade.pts2.datastructs.PumpPrices;
import com.technotrade.pts2.datastructs.PumpTag;
import com.technotrade.pts2.datastructs.PumpTotals;
import com.technotrade.pts2.datastructs.PumpTransactionInformation;
import com.technotrade.pts2.datastructs.PumpsConfiguration;
import com.technotrade.pts2.datastructs.ReaderTag;
import com.technotrade.pts2.datastructs.ReadersConfiguration;
import com.technotrade.pts2.datastructs.ReportInTankDelivery;
import com.technotrade.pts2.datastructs.ReportPumpTransaction;
import com.technotrade.pts2.datastructs.ReportTankMeasurement;
import com.technotrade.pts2.datastructs.SystemDecimalDigits;
import com.technotrade.pts2.datastructs.TagInformation;
import com.technotrade.pts2.datastructs.Tank;
import com.technotrade.pts2.datastructs.User;
import com.technotrade.pts2.enumeration.NozzleOrFuelIdSelector;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.network.BaseClientManager;
import com.technotrade.pts2.network.IRequest;
import com.technotrade.pts2.network.http.AddTagsToList;
import com.technotrade.pts2.network.http.GetBatteryVoltage;
import com.technotrade.pts2.network.http.GetConfigurationIdentifier;
import com.technotrade.pts2.network.http.GetCpuTemperature;
import com.technotrade.pts2.network.http.GetDateTime;
import com.technotrade.pts2.network.http.GetFirmwareInformation;
import com.technotrade.pts2.network.http.GetFuelGradesConfiguration;
import com.technotrade.pts2.network.http.GetGpsData;
import com.technotrade.pts2.network.http.GetMeasurementUnits;
import com.technotrade.pts2.network.http.GetParameter;
import com.technotrade.pts2.network.http.GetPriceBoardsConfiguration;
import com.technotrade.pts2.network.http.GetProbesConfiguration;
import com.technotrade.pts2.network.http.GetPtsNetworkSettings;
import com.technotrade.pts2.network.http.GetPumpNozzlesConfiguration;
import com.technotrade.pts2.network.http.GetPumpsConfiguration;
import com.technotrade.pts2.network.http.GetReadersConfiguration;
import com.technotrade.pts2.network.http.GetSystemDecimalDigits;
import com.technotrade.pts2.network.http.GetTagInformation;
import com.technotrade.pts2.network.http.GetTagsList;
import com.technotrade.pts2.network.http.GetTagsTotalNumber;
import com.technotrade.pts2.network.http.GetTanksConfiguration;
import com.technotrade.pts2.network.http.GetUniqueIdentifier;
import com.technotrade.pts2.network.http.GetUsersConfiguration;
import com.technotrade.pts2.network.http.HttpClientManager;
import com.technotrade.pts2.network.http.ProbeGetMeasurements;
import com.technotrade.pts2.network.http.ProbeGetTankVolumeForHeight;
import com.technotrade.pts2.network.http.PumpAuthorize;
import com.technotrade.pts2.network.http.PumpCloseTransaction;
import com.technotrade.pts2.network.http.PumpEmergencyStop;
import com.technotrade.pts2.network.http.PumpGetAutomaticOperation;
import com.technotrade.pts2.network.http.PumpGetDisplayData;
import com.technotrade.pts2.network.http.PumpGetPrices;
import com.technotrade.pts2.network.http.PumpGetStatus;
import com.technotrade.pts2.network.http.PumpGetTag;
import com.technotrade.pts2.network.http.PumpGetTotals;
import com.technotrade.pts2.network.http.PumpGetTransactionInformation;
import com.technotrade.pts2.network.http.PumpResume;
import com.technotrade.pts2.network.http.PumpSetAutomaticOperation;
import com.technotrade.pts2.network.http.PumpSetLights;
import com.technotrade.pts2.network.http.PumpSetPrices;
import com.technotrade.pts2.network.http.PumpStop;
import com.technotrade.pts2.network.http.PumpSuspend;
import com.technotrade.pts2.network.http.ReaderGetTag;
import com.technotrade.pts2.network.http.ReportGetInTankDeliveries;
import com.technotrade.pts2.network.http.ReportGetPumpTransactions;
import com.technotrade.pts2.network.http.ReportGetTankMeasurements;
import com.technotrade.pts2.network.http.Restart;
import com.technotrade.pts2.network.http.SetDateTime;
import com.technotrade.pts2.network.http.SetFuelGradesConfiguration;
import com.technotrade.pts2.network.http.SetParameter;
import com.technotrade.pts2.network.http.SetPriceBoardsConfiguration;
import com.technotrade.pts2.network.http.SetProbesConfiguration;
import com.technotrade.pts2.network.http.SetPtsNetworkSettings;
import com.technotrade.pts2.network.http.SetPumpNozzlesConfiguration;
import com.technotrade.pts2.network.http.SetPumpsConfiguration;
import com.technotrade.pts2.network.http.SetReadersConfiguration;
import com.technotrade.pts2.network.http.SetSystemDecimalDigits;
import com.technotrade.pts2.network.http.SetTagsList;
import com.technotrade.pts2.network.http.SetTanksConfiguration;
import com.technotrade.pts2.network.http.SetUsersConfiguration;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/// <summary>
/// The PTS2 device facade
/// </summary>
public class Device {
    public Settings mSettings;
    public BaseClientManager mClientManager;
    private boolean mIsOpened;
    private boolean mIsConnected;
    private final ObserverMainListener mObserverMainListener;
    private final List<IObserver> mObservers;

	public Device(Context context) {
        Context appContext = context.getApplicationContext();
        WeakReference<Context> mContextRef = new WeakReference<>(appContext);
        mClientManager = new HttpClientManager(mContextRef);
        mSettings = new Settings();
        mIsOpened = false;
        mIsConnected = false;
        mObservers = new ArrayList<>();
        mObserverMainListener = new ObserverMainListener(mObservers);
    }
    /// <summary>
    /// Creates an instance
    /// <param name="context">Activity context</param>
    /// </summary>
    public static Device create(Context context) {
        return new Device(context);
    }
    /// <summary>
    /// The function for registering an observer object.
    /// </summary>
    /// <param name="observer">observer object</param>
    public void registerObserver(IObserver observer) {
        mObservers.add(observer);
    }
    /// <summary>
    /// The function for deregister an observer object
    /// </summary>
    /// <param name="observer">observer object</param>
    public void deregisterObserver(Observer observer) {
        mObservers.remove(observer);
    }
    /// <summary>
    /// The function for removing observers objects
    /// </summary>
    public void removeObservers() {
        mObservers.clear();
    }
    /// <summary>
    /// Settings getter
    /// </summary>
    /// <returns>Settings</returns>
    public Settings getSettings() {
        return mSettings;
    }
    /// <summary>
    /// Settings setter
    /// </summary>
    /// <returns>Settings</returns>
    public void setSettings(Settings settings) {
        mSettings = settings;
    }
    /// <summary>
    /// Opens PTS2 device
    /// </summary>
    /// <returns>Result</returns>
    public Result open() {
        close();

        mClientManager.setSettings(mSettings);
        Result result = mClientManager.open();

        if (result != Result.SUCCESS) {
            mClientManager.close();
            return result;
        }

        clearListeners();
        setOnPumpIdleStatusListener(mObserverMainListener::pumpIdleStatusCallback);
        setOnPumpFillingStatusListener(mObserverMainListener::pumpFillingStatusCallback);
        setOnPumpEndOfTransactionStatusListener(mObserverMainListener::pumpEndOfTransactionStatusCallback);
        setOnPumpOfflineStatusListener(mObserverMainListener::pumpOfflineStatusCallback);
        setOnPumpTotalsListener(mObserverMainListener::pumpTotalsStatusCallback);
        setOnPumpPricesListener(mObserverMainListener::pumpPricesStatusCallback);
        setOnPumpDisplayDataListener(mObserverMainListener::pumpDisplayDataStatusCallback);
        setOnGetBatteryVoltageListener(mObserverMainListener::getBatteryVoltageCallback);
        setOnGetCpuTemperatureListener(mObserverMainListener::getCpuTemperatureCallback);
        setOnGetUniqueIdentifierListener(mObserverMainListener::getUniqueIdentifierCallback);
        setOnGetFirmwareInformationListener(mObserverMainListener::getFirmwareInformationCallback);
        setOnGetMeasurementUnitsListener(mObserverMainListener::getMeasurementUnitsCallback);
        setOnRestartListener(mObserverMainListener::restartCallback);
        setOnGetConfigurationIdentifierListener(mObserverMainListener::getConfigurationIdentifierCallback);
        setOnGetDateTimeListener(mObserverMainListener::getDateTimeCallback);
        setOnSetDateTimeListener(mObserverMainListener::setDateTimeCallback);
        setOnGetPtsNetworkSettingsListener(mObserverMainListener::getPtsNetworkSettingsCallback);
        setOnSetPtsNetworkSettingsListener(mObserverMainListener::setPtsNetworkSettingsCallback);
        setOnGetSystemDecimalDigitsListener(mObserverMainListener::getSystemDecimalDigitsCallback);
        setOnSetSystemDecimalDigitsListener(mObserverMainListener::setSystemDecimalDigitsCallback);
        setOnGetParameterListener(mObserverMainListener::getParameterCallback);
        setOnSetParameterListener(mObserverMainListener::setParameterCallback);
        setOnGetPumpsConfigurationListener(mObserverMainListener::getPumpsConfigurationCallback);
        setOnSetPumpsConfigurationListener(mObserverMainListener::setPumpsConfigurationCallback);
        setOnGetProbesConfigurationListener(mObserverMainListener::getProbesConfigurationCallback);
        setOnSetProbesConfigurationListener(mObserverMainListener::setProbesConfigurationCallback);
        setOnGetReadersConfigurationListener(mObserverMainListener::getReadersConfigurationCallback);
        setOnSetReadersConfigurationListener(mObserverMainListener::setReadersConfigurationCallback);
        setOnGetPriceBoardsConfigurationListener(mObserverMainListener::getPriceBoardsConfigurationCallback);
        setOnSetPriceBoardsConfigurationListener(mObserverMainListener::setPriceBoardsConfigurationCallback);
        setOnGetFuelGradesConfigurationListener(mObserverMainListener::getFuelGradesConfigurationCallback);
        setOnSetFuelGradesConfigurationListener(mObserverMainListener::setFuelGradesConfigurationCallback);
        setOnGetPumpNozzlesConfigurationListener(mObserverMainListener::getPumpNozzlesConfigurationCallback);
        setOnSetPumpNozzlesConfigurationListener(mObserverMainListener::setPumpNozzlesConfigurationCallback);
        setOnGetTanksConfigurationListener(mObserverMainListener::getTanksConfigurationCallback);
        setOnSetTanksConfigurationListener(mObserverMainListener::setTanksConfigurationCallback);
        setOnGetUsersConfigurationListener(mObserverMainListener::getUsersConfigurationCallback);
        setOnSetUsersConfigurationListener(mObserverMainListener::setUsersConfigurationCallback);
        setOnPumpAuthorizeListener(mObserverMainListener::pumpAuthorizeCallback);
        setOnPumpGetTransactionInformationListener(mObserverMainListener::pumpGetTransactionInformationCallback);
        setOnPumpStopListener(mObserverMainListener::pumpStopCallback);
        setOnPumpEmergencyStopListener(mObserverMainListener::pumpEmergencyStopCallback);
        setOnPumpSuspendListener(mObserverMainListener::pumpSuspendCallback);
        setOnPumpResumeListener(mObserverMainListener::pumpResumeCallback);
        setOnPumpCloseTransactionListener(mObserverMainListener::pumpCloseTransactionCallback);
        setOnPumpGetTotalsListener(mObserverMainListener::pumpGetTotalsCallback);
        setOnPumpGetPricesListener(mObserverMainListener::pumpGetPricesCallback);
        setOnPumpSetPricesListener(mObserverMainListener::pumpSetPricesCallback);
        setOnPumpGetDisplayDataListener(mObserverMainListener::pumpGetDisplayDataCallback);
        setOnPumpGetTagListener(mObserverMainListener::pumpGetTagCallback);
        setOnGetTagInformationListener(mObserverMainListener::getTagInformationCallback);
        setOnGetTagsListListener(mObserverMainListener::getTagsListCallback);
        setOnSetTagsListListener(mObserverMainListener::setTagsListCallback);
        setOnAddTagsToListListener(mObserverMainListener::addTagsToListCallback);
        setOnGetTagsTotalNumberListener(mObserverMainListener::getTagsTotalNumberCallback);
        setOnReaderGetTagListener(mObserverMainListener::readerGetTagCallback);
        setOnPumpSetLightsListener(mObserverMainListener::pumpSetLightsCallback);
        setOnPumpGetAutomaticOperationListener(mObserverMainListener::pumpGetAutomaticOperationCallback);
        setOnPumpSetAutomaticOperationListener(mObserverMainListener::pumpSetAutomaticOperationCallback);
        setOnProbeGetMeasurementsListener(mObserverMainListener::probeGetMeasurementsCallback);
        setOnProbeGetTankVolumeForHeightListener(mObserverMainListener::probeGetTankVolumeForHeightCallback);
        setOnGetGpsDataListener(mObserverMainListener::getGpsDataCallback);
        setOnReportGetPumpTransactionsListener(mObserverMainListener::reportGetPumpTransactionsCallback);
        setOnReportGetTankMeasurementsListener(mObserverMainListener::reportGetTankMeasurementsCallback);
        setOnReportGetInTankDeliveriesListener(mObserverMainListener::reportGetInTankDeliveriesCallback);

        mIsOpened = true;

        return result;
    }
    /// <summary>
    /// Closes PTS2 device
    /// </summary>
    public void close() {
        clearListeners();
        mClientManager.close();

        mIsOpened = false;
        mIsConnected = false;
    }
    /// <summary>
    /// Checks that PTS2 device is opened
    /// </summary>
    /// <returns>True if opened</returns>
    public boolean isOpened() {
        return mIsOpened;
    }
    /// <summary>
    /// Checks that PTS2 device is connected
    /// </summary>
    /// <returns>True if connected</returns>
    public boolean isConnected() {
        return mIsConnected;
    }
    /// <summary>
    /// Executes a requests queue
    /// </summary>
    /// <returns>Result</returns>
    public Result executeRequestsQueue() {
        if (!mIsOpened) { return Result.INIT_ERROR; }

        Result result = mClientManager.executeRequestsQueue();
        mIsConnected = (result == Result.SUCCESS);

        return result;
    }
    /// <summary>
    /// Requests queue getter
    /// </summary>
    /// <param name="requests">Requests queue</param>
    /// <returns>Result</returns>
    public ArrayList<IRequest<?>> getRequestsQueue() {
        return mClientManager.getRequestsQueue();
    }
    /// <summary>
    /// Clears requests queue
    /// </summary>
    /// <returns>Result</returns>
    public Result clearRequestsQueue() {
        return mClientManager.clearRequestsQueue();
    }
    /// <summary>
    /// Loads PTS2 configuration from the device
    /// </summary>
    /// <returns>Result</returns>
    public synchronized Result loadConfiguration() {
        Result result = clearRequestsQueue();

        if (result == Result.SUCCESS) {
            result = getConfigurationIdentifier();
        }

        if (result == Result.SUCCESS) {
            result = executeRequestsQueue();
        }

        return result;
    }
    /// <summary>
    /// Synchronizes date and time to local PC date and time
    /// </summary>
    /// <returns>Result</returns>
    public synchronized Result synchronizePTSDateTimetoLocal() {
        Result result = clearRequestsQueue();

        if (result== Result.SUCCESS) {
            DateTimeSettings dateTimeSettings = new DateTimeSettings();
            Date date = new Date();
            dateTimeSettings.setDate(date);
            result = setDateTime(dateTimeSettings);
        }

        if (result == Result.SUCCESS) {
            result = executeRequestsQueue();
        }

        return result;
    }
    /// <summary>
    /// Returns FuelGrade related to Probe
    /// </summary>
    /// <param name="pumpNozzlesConfiguration">Nozzles configuration</param>
    /// <param name="fuelGradesConfiguration">Fuel grades configuration</param>
    /// <param name="probe">Probe input object</param>
    /// <returns>FuelGrade</returns>
    public FuelGrade getFuelGradeByProbe(ArrayList<PumpNozzles> pumpNozzlesConfiguration, ArrayList<FuelGrade> fuelGradesConfiguration, Probe probe) {
        for (int p = 0; p < pumpNozzlesConfiguration.size(); ++p) {
            PumpNozzles pumpNozzles = pumpNozzlesConfiguration.get(p);
            ArrayList<Integer> tankIds = pumpNozzles.getTankIds();

            int probeIndex = tankIds.indexOf(probe.getId());
            if (probeIndex == -1) {
                continue;
            }

            ArrayList<Integer> fuelGradeIds = pumpNozzles.getFuelGradeIds();
            if (fuelGradeIds.size() <= probeIndex) {
                continue;
            }

            int fuelGradeId = fuelGradeIds.get(probeIndex);

            for (int f = 0; f < fuelGradesConfiguration.size(); ++f) {
                if (fuelGradesConfiguration.get(f).getId() == fuelGradeId) {
                    return fuelGradesConfiguration.get(f);
                }
            }
        }

        return null;
    }
    /// <summary>
    /// GetBatteryVoltage request
    /// </summary>
    /// <returns>Result</returns>
    public Result getBatteryVoltage() {
        return mClientManager.addRequest(new GetBatteryVoltage());
    }
    /// <summary>
    /// GetCpuTemperature request
    /// </summary>
    /// <returns>Result</returns>
    public Result getCpuTemperature() {
        return mClientManager.addRequest(new GetCpuTemperature());
    }
    /// <summary>
    /// GetUniqueIdentifier request
    /// </summary>
    /// <returns>Result</returns>
    public Result getUniqueIdentifier() {
        return mClientManager.addRequest(new GetUniqueIdentifier());
    }
    /// <summary>
    /// GetFirmwareInformation request
    /// </summary>
    /// <returns>Result</returns>
    public Result getFirmwareInformation() {
        return mClientManager.addRequest(new GetFirmwareInformation());
    }
    /// <summary>
    /// GetMeasurementUnits request
    /// </summary>
    /// <returns>Result</returns>
    public Result getMeasurementUnits() {
        return mClientManager.addRequest(new GetMeasurementUnits());
    }
    /// <summary>
    /// Restart request
    /// </summary>
    /// <returns>Result</returns>
    public Result restart() {
        return mClientManager.addRequest(new Restart());
    }
    /// <summary>
    /// GetConfigurationIdentifier request
    /// </summary>
    /// <returns>Result</returns>
    public Result getConfigurationIdentifier() {
        return mClientManager.addRequest(new GetConfigurationIdentifier());
    }
    /// <summary>
    /// GetDateTime request
    /// </summary>
    /// <returns>Result</returns>
    public Result getDateTime() {
        return mClientManager.addRequest(new GetDateTime());
    }
    /// <summary>
    /// SetDateTime request
    /// </summary>
    /// <param name="dateTimeSettings">Date and time</param>
    /// <returns>Result</returns>
    public Result setDateTime(DateTimeSettings dateTimeSettings) {
        return mClientManager.addRequest(new SetDateTime(dateTimeSettings));
    }
    /// <summary>
    /// GetPtsNetworkSettings request
    /// </summary>
    /// <returns>Result</returns>
    public Result getPtsNetworkSettings() {
        return mClientManager.addRequest(new GetPtsNetworkSettings());
    }
    /// <summary>
    /// SetPtsNetworkSettings request
    /// </summary>
    /// <param name="ptsNetworkSettings">Network settings</param>
    /// <returns>Result</returns>
    public Result setPtsNetworkSettings(PtsNetworkSettings ptsNetworkSettings) {
        return mClientManager.addRequest(new SetPtsNetworkSettings(ptsNetworkSettings));
    }
    /// <summary>
    /// GetSystemDecimalDigits request
    /// </summary>
    /// <returns>Result</returns>
    public Result getSystemDecimalDigits() {
        return mClientManager.addRequest(new GetSystemDecimalDigits());
    }
    /// <summary>
    /// SetSystemDecimalDigits request
    /// </summary>
    /// <param name="systemDecimalDigits">Decimal digits configuration</param>
    /// <returns>Result</returns>
    public Result setSystemDecimalDigits(SystemDecimalDigits systemDecimalDigits) {
        return mClientManager.addRequest(new SetSystemDecimalDigits(systemDecimalDigits));
    }
    /// <summary>
    /// GetParameter request
    /// </summary>
    /// <param name="parameter">Parameter</param>
    /// <returns>Result</returns>
    public Result getParameter(Parameter parameter) {
        return mClientManager.addRequest(new GetParameter(parameter));
    }
    /// <summary>
    /// SetParameter request
    /// </summary>
    /// <param name="parameter">Parameter</param>
    /// <returns>Result</returns>
    public Result setParameter(Parameter parameter) {
        return mClientManager.addRequest(new SetParameter(parameter));
    }
    /// <summary>
    /// GetPumpsConfiguration request
    /// </summary>
    /// <returns>Result</returns>
    public Result getPumpsConfiguration() {
        return mClientManager.addRequest(new GetPumpsConfiguration());
    }
    /// <summary>
    /// SetPumpsConfiguration request
    /// </summary>
    /// <param name="pumpsConfiguration">Pumps configuration</param>
    /// <returns>Result</returns>
    public Result setPumpsConfiguration(PumpsConfiguration pumpsConfiguration) {
        return mClientManager.addRequest(new SetPumpsConfiguration(pumpsConfiguration));
    }
    /// <summary>
    /// GetProbesConfiguration request
    /// </summary>
    /// <returns>Result</returns>
    public Result getProbesConfiguration() {
        return mClientManager.addRequest(new GetProbesConfiguration());
    }
    /// <summary>
    /// SetProbesConfiguration request
    /// </summary>
    /// <param name="probesConfiguration">Probes configuration</param>
    /// <returns>Result</returns>
    public Result setProbesConfiguration(ProbesConfiguration probesConfiguration) {
        return mClientManager.addRequest(new SetProbesConfiguration(probesConfiguration));
    }
    /// <summary>
    /// GetReadersConfiguration request
    /// </summary>
    /// <returns>Result</returns>
    public Result getReadersConfiguration() {
        return mClientManager.addRequest(new GetReadersConfiguration());
    }
    /// <summary>
    /// SetReadersConfiguration request
    /// </summary>
    /// <param name="readersConfiguration">Readers configuration</param>
    /// <returns>Result</returns>
    public Result setReadersConfiguration(ReadersConfiguration readersConfiguration) {
        return mClientManager.addRequest(new SetReadersConfiguration(readersConfiguration));
    }
    /// <summary>
    /// GetPriceBoardsConfiguration request
    /// </summary>
    /// <returns>Result</returns>
    public Result getPriceBoardsConfiguration() {
        return mClientManager.addRequest(new GetPriceBoardsConfiguration());
    }
    /// <summary>
    /// SetPriceBoardsConfiguration request
    /// </summary>
    /// <param name="priceBoardsConfiguration">Price boards configuration</param>
    /// <returns>Result</returns>
    public Result setPriceBoardsConfiguration(PriceBoardsConfiguration priceBoardsConfiguration) {
        return mClientManager.addRequest(new SetPriceBoardsConfiguration(priceBoardsConfiguration));
    }
    /// <summary>
    /// GetFuelGradesConfiguration request
    /// </summary>
    /// <returns>Result</returns>
    public Result getFuelGradesConfiguration() {
        return mClientManager.addRequest(new GetFuelGradesConfiguration());
    }
    /// <summary>
    /// SetFuelGradesConfiguration request
    /// </summary>
    /// <param name="fuelGrades">List of fuel grades</param>
    /// <returns>Result</returns>
    public Result setFuelGradesConfiguration(ArrayList<FuelGrade> fuelGrades) {
        return mClientManager.addRequest(new SetFuelGradesConfiguration(fuelGrades));
    }
    /// <summary>
    /// GetPumpNozzlesConfiguration request
    /// </summary>
    /// <returns>Result</returns>
    public Result getPumpNozzlesConfiguration() {
        return mClientManager.addRequest(new GetPumpNozzlesConfiguration());
    }
    /// <summary>
    /// SetPumpNozzlesConfiguration request
    /// </summary>
    /// <param name="pumpNozzlesConfiguration">Pump nozzled configuration</param>
    /// <returns>Result</returns>
    public Result setPumpNozzlesConfiguration(ArrayList<PumpNozzles> pumpNozzlesConfiguration) {
        return mClientManager.addRequest(new SetPumpNozzlesConfiguration(pumpNozzlesConfiguration));
    }
    /// <summary>
    /// GetTanksConfiguration request
    /// </summary>
    /// <returns>Result</returns>
    public Result getTanksConfiguration() {
        return mClientManager.addRequest(new GetTanksConfiguration());
    }
    /// <summary>
    /// SetTanksConfiguration request
    /// </summary>
    /// <param name="tanks">List of tank configurations</param>
    /// <returns>Result</returns>
    public Result setTanksConfiguration(ArrayList<Tank> tanks) {
        return mClientManager.addRequest(new SetTanksConfiguration(tanks));
    }
    /// <summary>
    /// GetUsersConfiguration request
    /// </summary>
    /// <returns>Result</returns>
    public Result getUsersConfiguration() {
        return mClientManager.addRequest(new GetUsersConfiguration());
    }
    /// <summary>
    /// SetUsersConfiguration request
    /// </summary>
    /// <param name="users">List of user configurations</param>
    /// <returns>Result</returns>
    public Result setUsersConfiguration(ArrayList<User> users) {
        return mClientManager.addRequest(new SetUsersConfiguration(users));
    }
    /// <summary>
    /// PumpGetStatus request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <returns>Result</returns>
    public Result pumpGetStatus(int pump) {
        return mClientManager.addRequest(new PumpGetStatus(pump));
    }
    /// <summary>
    /// PumpAuthorize request
    /// </summary>
    /// <param name="pumpAuthorizeData">Pump authorization data</param>
    /// <returns>Result</returns>
    public Result pumpAuthorize(PumpAuthorizeData pumpAuthorizeData) {
        return mClientManager.addRequest(new PumpAuthorize(pumpAuthorizeData));
    }
    /// <summary>
    /// PumpGetTransactionInformation request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <param name="transaction">Transaction number</param>
    /// <returns>Result</returns>
    public Result pumpGetTransactionInformation(int pump, int transaction) {
        return mClientManager.addRequest(new PumpGetTransactionInformation(pump, transaction));
    }
    /// <summary>
    /// PumpStop request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <returns>Result</returns>
    public Result pumpStop(int pump) {

        return mClientManager.addRequest(new PumpStop(pump));
    }
    /// <summary>
    /// PumpEmergencyStop request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <returns>Result</returns>
    public Result pumpEmergencyStop(int pump) {
        return mClientManager.addRequest(new PumpEmergencyStop(pump));
    }
    /// <summary>
    /// PumpSuspend request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <returns>Result</returns>
    public Result pumpSuspend(int pump) {
        return mClientManager.addRequest(new PumpSuspend(pump));
    }
    /// <summary>
    /// PumpResume request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <returns>Result</returns>
    public Result pumpResume(int pump) {
        return mClientManager.addRequest(new PumpResume(pump));
    }
    /// <summary>
    /// PumpCloseTransaction request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <param name="transaction">Transaction number</param>
    /// <returns>Result</returns>
    public Result pumpCloseTransaction(int pump, int transaction) {
        return mClientManager.addRequest(new PumpCloseTransaction(pump, transaction));
    }
    /// <summary>
    /// PumpGetTotals request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <param name="nozzle">Nozzle number</param>
    /// <param name="fuelGradeId">Fuel grade Id</param>
    /// <param name="nozzleOrFielIdSelector">Nozzle or fielId selector. Determines field that will be used</param>
    /// <returns>Result</returns>
    public Result pumpGetTotals(int pump, int nozzle, int fuelGradeId, NozzleOrFuelIdSelector nozzleOrFielIdSelector) {
        return mClientManager.addRequest(new PumpGetTotals(pump, nozzle, fuelGradeId, nozzleOrFielIdSelector));
    }
    /// <summary>
    /// PumpGetPrices request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <returns>Result</returns>
    public Result pumpGetPrices(int pump) {
        return mClientManager.addRequest(new PumpGetPrices(pump));
    }
    /// <summary>
    /// PumpSetPrices request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <param name="prices">List of prices for each nozzle</param>
    /// <returns>Result</returns>
    public Result pumpSetPrices(int pump, ArrayList<BigDecimal> prices) {
        return mClientManager.addRequest(new PumpSetPrices(pump, prices));
    }
    /// <summary>
    /// PumpGetDisplayData request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <returns>Result</returns>
    public Result pumpGetDisplayData(int pump) {
        return mClientManager.addRequest(new PumpGetDisplayData(pump));
    }
    /// <summary>
    /// PumpGetTag request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <param name="nozzle">Nozzle number</param>
    /// <returns>Result</returns>
    public Result pumpGetTag(int pump, int nozzle) {
        return mClientManager.addRequest(new PumpGetTag(pump, nozzle));
    }
    /// <summary>
    /// GetTagInformation request
    /// </summary>
    /// <param name="tag">Tag</param>
    /// <returns>Result</returns>
    public Result getTagInformation(String tag) {
        return mClientManager.addRequest(new GetTagInformation(tag));
    }
    /// <summary>
    /// GetTagsList request
    /// </summary>
    /// <returns>Result</returns>
    public Result getTagsList() {
        return mClientManager.addRequest(new GetTagsList());
    }
    /// <summary>
    /// GetTagsList request that allow to choose a range
    /// </summary>
    /// <param name="startNumber">starting number of tag in list to read (integer, range from 1 and higher)</param>
    /// <param name="totalNumber">total number of tags to read (integer, range from 1 and till 200)</param>
    /// <returns>Result</returns>
    public Result getTagsList(int startNumber, int totalNumber) {
        return mClientManager.addRequest(new GetTagsList(startNumber, totalNumber));
    }
    /// <summary>
    /// SetTagsList request
    /// </summary>
    /// <param name="tagInformations">List of TagInformation instances</param>
    /// <returns>Result</returns>
    public Result setTagsList(ArrayList<TagInformation> tagInformations) {
        return mClientManager.addRequest(new SetTagsList(tagInformations));
    }
    /// <summary>
    /// AddTagsToList request
    /// </summary>
    /// <param name="tagInformations">List of TagInformation instances</param>
    /// <returns>Result</returns>
    public Result addTagsToList(ArrayList<TagInformation> tagInformations) {
        return mClientManager.addRequest(new AddTagsToList(tagInformations));
    }
    /// <summary>
    /// GetTagsTotalNumber request that returns total number of configured tags
    /// </summary>
    /// <returns>Result</returns>
    public Result getTagsTotalNumber() {
        return mClientManager.addRequest(new GetTagsTotalNumber());
    }
    /// <summary>
    /// ReaderGetTag request
    /// </summary>
    /// <param name="reader">Reader number</param>
    /// <returns>Result</returns>
    public Result readerGetTag(int reader) {
        return mClientManager.addRequest(new ReaderGetTag(reader));
    }
    /// <summary>
    /// PumpSetLights request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <param name="lightsEnabled">True to enable, False to disable</param>
    /// <returns>Result</returns>
    public Result pumpSetLights(int pump, boolean lightsEnabled) {
        return mClientManager.addRequest(new PumpSetLights(pump, lightsEnabled));
    }
    /// <summary>
    /// PumpGetAutomaticOperation request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <returns>Result</returns>
    public Result pumpGetAutomaticOperation(int pump) {
        return mClientManager.addRequest(new PumpGetAutomaticOperation(pump));
    }
    /// <summary>
    /// PumpSetAutomaticOperation request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <param name="automaticPumpAuthorization">True for automatic authorization, False for via CloseTransaction</param>
    /// <returns>Result</returns>
    public Result pumpSetAutomaticOperation(int pump, boolean automaticPumpAuthorization) {
        return mClientManager.addRequest(new PumpSetAutomaticOperation(pump, automaticPumpAuthorization));
    }
    /// <summary>
    /// ProbeGetMeasurements request
    /// </summary>
    /// <param name="probe">Probe number</param>
    /// <returns>Result</returns>
    public Result probeGetMeasurements(int probe) {
        return mClientManager.addRequest(new ProbeGetMeasurements(probe));
    }
    /// <summary>
    /// ProbeGetTankVolumeForHeight request
    /// </summary>
    /// <param name="probe">Probe number</param>
    /// <param name="height">Height</param>
    /// <returns>Result</returns>
    public Result probeGetTankVolumeForHeight(int probe, int height) {
        return mClientManager.addRequest(new ProbeGetTankVolumeForHeight(probe, height));
    }
    /// <summary>
    /// GetGpsData request
    /// </summary>
    /// <returns>Result</returns>
    public Result getGpsData() {
        return mClientManager.addRequest(new GetGpsData());
    }
    /// <summary>
    /// ReportGetPumpTransactions request
    /// </summary>
    /// <param name="pump">Pump number</param>
    /// <param name="dateTimeStart">Date and time as a start point to search</param>
    /// <param name="dateTimeEnd">Date and time as a finish point to search</param>
    /// <returns>Result</returns>
    public Result reportGetPumpTransactions(int pump, Date dateTimeStart, Date dateTimeEnd) {
        return mClientManager.addRequest(new ReportGetPumpTransactions(pump, dateTimeStart, dateTimeEnd));
    }
    /// <summary>
    /// ReportGetTankMeasurements request
    /// </summary>
    /// <param name="tank">Tank number</param>
    /// <param name="dateTimeStart">Date and time as a start point to search</param>
    /// <param name="dateTimeEnd">Date and time as a finish point to search</param>
    /// <returns>Result</returns>
    public Result reportGetTankMeasurements(int tank, Date dateTimeStart, Date dateTimeEnd) {
        return mClientManager.addRequest(new ReportGetTankMeasurements(tank, dateTimeStart, dateTimeEnd));
    }
    /// <summary>
    /// ReportGetInTankDeliveries request
    /// </summary>
    /// <param name="tank">Tank number</param>
    /// <param name="dateTimeStart">Date and time as a start point to search</param>
    /// <param name="dateTimeEnd">Date and time as a finish point to search</param>
    /// <returns>Result</returns>
    public Result reportGetInTankDeliveries(int tank, Date dateTimeStart, Date dateTimeEnd) {
        return mClientManager.addRequest(new ReportGetInTankDeliveries(tank, dateTimeStart, dateTimeEnd));
    }
    /// <summary>
    /// Clears the listeners request
    /// </summary>
    public void clearListeners() {
        mClientManager.clearCallbacks();
    }
    /// <summary>
    /// PumpIdleStatus callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpIdleStatusListener(RequestCallback<PumpIdleStatus> callback) {
        mClientManager.addCallback(PumpGetStatus.PUMP_IDLE_STATUS, callback);
    }
    /// <summary>
    /// PumpFillingStatus callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpFillingStatusListener(RequestCallback<PumpFillingStatus> callback) {
        mClientManager.addCallback(PumpGetStatus.PUMP_FILLING_STATUS, callback);
    }
    /// <summary>
    /// PumpEndOfTransactionStatus callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpEndOfTransactionStatusListener(RequestCallback<PumpEndOfTransactionStatus> callback) {
        mClientManager.addCallback(PumpGetStatus.PUMP_END_OF_TRANSACTION_STATUS, callback);
    }
    /// <summary>
    /// PumpOfflineStatus callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpOfflineStatusListener(RequestCallback<PumpOfflineStatus> callback) {
        mClientManager.addCallback(PumpGetStatus.PUMP_OFFLINE_STATUS, callback);
    }
    /// <summary>
    /// PumpTotals callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpTotalsListener(RequestCallback<PumpTotals> callback) {
        mClientManager.addCallback(PumpGetStatus.PUMP_TOTALS, callback);
    }
    /// <summary>
    /// PumpPrices callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpPricesListener(RequestCallback<PumpPrices> callback) {
        mClientManager.addCallback(PumpGetStatus.PUMP_PRICES, callback);
    }
    /// <summary>
    /// PumpTag callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpTagListiner(RequestCallback<PumpTag> callback) {
        mClientManager.addCallback(PumpGetStatus.PUMP_TAG, callback);
    }
    /// <summary>
    /// PumpDisplayData callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpDisplayDataListener(RequestCallback<PumpDisplayData> callback) {
        mClientManager.addCallback(PumpGetStatus.PUMP_DISPLAY_DATA, callback);
    }
    /// <summary>
    /// GetBatteryVoltage callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetBatteryVoltageListener(RequestCallback<Integer> callback) {
        mClientManager.addCallback(GetBatteryVoltage.KEY, callback);
    }
    /// <summary>
    /// GetCpuTemperature callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetCpuTemperatureListener(RequestCallback<Integer> callback) {
        mClientManager.addCallback(GetCpuTemperature.KEY, callback);
    }
    /// <summary>
    /// GetUniqueIdentifier callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetUniqueIdentifierListener(RequestCallback<String> callback) {
        mClientManager.addCallback(GetUniqueIdentifier.KEY, callback);
    }
    /// <summary>
    /// GetFirmwareInformation callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetFirmwareInformationListener(RequestCallback<FirmwareInformation> callback) {
        mClientManager.addCallback(GetFirmwareInformation.KEY, callback);
    }
    /// <summary>
    /// GetMeasurementUnits callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetMeasurementUnitsListener(RequestCallback<MeasurementUnits> callback) {
        mClientManager.addCallback(GetMeasurementUnits.KEY, callback);
    }
    /// <summary>
    /// Restart callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnRestartListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(Restart.KEY, callback);
    }
    /// <summary>
    /// GetConfigurationIdentifier callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetConfigurationIdentifierListener(RequestCallback<String> callback) {
        mClientManager.addCallback(GetConfigurationIdentifier.KEY, callback);
    }
    /// <summary>
    /// GetDateTime callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetDateTimeListener(RequestCallback<DateTimeSettings> callback) {
        mClientManager.addCallback(GetDateTime.KEY, callback);
    }
    /// <summary>
    /// SetDateTime callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetDateTimeListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetDateTime.KEY, callback);
    }
    /// <summary>
    /// GetPtsNetworkSettings callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetPtsNetworkSettingsListener(RequestCallback<PtsNetworkSettings> callback) {
        mClientManager.addCallback(GetPtsNetworkSettings.KEY, callback);
    }
    /// <summary>
    /// SetPtsNetworkSettings callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetPtsNetworkSettingsListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetPtsNetworkSettings.KEY, callback);
    }
    /// <summary>
    /// GetSystemDecimalDigits callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetSystemDecimalDigitsListener(RequestCallback<SystemDecimalDigits> callback) {
        mClientManager.addCallback(GetSystemDecimalDigits.KEY, callback);
    }
    /// <summary>
    /// SetSystemDecimalDigits callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetSystemDecimalDigitsListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetSystemDecimalDigits.KEY, callback);
    }
    /// <summary>
    /// GetParameter callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetParameterListener(RequestCallback<Parameter> callback) {
        mClientManager.addCallback(GetParameter.KEY, callback);
    }
    /// <summary>
    /// SetParameter callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetParameterListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetParameter.KEY, callback);
    }
    /// <summary>
    /// GetPumpsConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetPumpsConfigurationListener(RequestCallback<PumpsConfiguration> callback) {
        mClientManager.addCallback(GetPumpsConfiguration.KEY, callback);
    }
    /// <summary>
    /// SetPumpsConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetPumpsConfigurationListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetPumpsConfiguration.KEY, callback);
    }
    /// <summary>
    /// GetProbesConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetProbesConfigurationListener(RequestCallback<ProbesConfiguration> callback) {
        mClientManager.addCallback(GetProbesConfiguration.KEY, callback);
    }
    /// <summary>
    /// SetProbesConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetProbesConfigurationListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetProbesConfiguration.KEY, callback);
    }
    /// <summary>
    /// GetReadersConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetReadersConfigurationListener(RequestCallback<ReadersConfiguration> callback) {
        mClientManager.addCallback(GetReadersConfiguration.KEY, callback);
    }
    /// <summary>
    /// SetReadersConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetReadersConfigurationListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetReadersConfiguration.KEY, callback);
    }
    /// <summary>
    /// GetPriceBoardsConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetPriceBoardsConfigurationListener(RequestCallback<PriceBoardsConfiguration> callback) {
        mClientManager.addCallback(GetPriceBoardsConfiguration.KEY, callback);
    }
    /// <summary>
    /// SetPriceBoardsConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetPriceBoardsConfigurationListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetPriceBoardsConfiguration.KEY, callback);
    }
    /// <summary>
    /// GetFuelGradesConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetFuelGradesConfigurationListener(RequestCallback<ArrayList<FuelGrade>> callback) {
        mClientManager.addCallback(GetFuelGradesConfiguration.KEY, callback);
    }
    /// <summary>
    /// SetFuelGradesConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetFuelGradesConfigurationListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetFuelGradesConfiguration.KEY, callback);
    }
    /// <summary>
    /// GetPumpNozzlesConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetPumpNozzlesConfigurationListener(RequestCallback<ArrayList<PumpNozzles>> callback) {
        mClientManager.addCallback(GetPumpNozzlesConfiguration.KEY, callback);
    }
    /// <summary>
    /// SetPumpNozzlesConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetPumpNozzlesConfigurationListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetPumpNozzlesConfiguration.KEY, callback);
    }
    /// <summary>
    /// GetTanksConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetTanksConfigurationListener(RequestCallback<ArrayList<Tank>> callback) {
        mClientManager.addCallback(GetTanksConfiguration.KEY, callback);
    }
    /// <summary>
    /// SetTanksConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetTanksConfigurationListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetTanksConfiguration.KEY, callback);
    }
    /// <summary>
    /// GetUsersConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetUsersConfigurationListener(RequestCallback<ArrayList<User>> callback) {
        mClientManager.addCallback(GetUsersConfiguration.KEY, callback);
    }
    /// <summary>
    /// SetUsersConfiguration callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetUsersConfigurationListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetUsersConfiguration.KEY, callback);
    }
    /// <summary>
    /// PumpAuthorize callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpAuthorizeListener(RequestCallback<PumpAuthorizeConfirmation> callback) {
        mClientManager.addCallback(PumpAuthorize.KEY, callback);
    }
    /// <summary>
    /// PumpGetTransactionInformation callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpGetTransactionInformationListener(RequestCallback<PumpTransactionInformation> callback) {
        mClientManager.addCallback(PumpGetTransactionInformation.KEY, callback);
    }
    /// <summary>
    /// PumpStop callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpStopListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpStop.KEY, callback);
    }
    /// <summary>
    /// PumpEmergencyStop callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpEmergencyStopListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpEmergencyStop.KEY, callback);
    }
    /// <summary>
    /// PumpSuspend callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpSuspendListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpSuspend.KEY, callback);
    }
    /// <summary>
    /// PumpResume callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpResumeListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpResume.KEY, callback);
    }
    /// <summary>
    /// PumpCloseTransaction callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpCloseTransactionListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpCloseTransaction.KEY, callback);
    }
    /// <summary>
    /// PumpGetTotals callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpGetTotalsListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpGetTotals.KEY, callback);
    }
    /// <summary>
    /// PumpGetPrices callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpGetPricesListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpGetPrices.KEY, callback);
    }
    /// <summary>
    /// PumpSetPrices callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpSetPricesListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpSetPrices.KEY, callback);
    }
    /// <summary>
    /// PumpGetDisplayData callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpGetDisplayDataListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpGetDisplayData.KEY, callback);
    }
    /// <summary>
    /// PumpGetTag callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpGetTagListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpGetTag.KEY, callback);
    }
    /// <summary>
    /// GetTagInformation callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetTagInformationListener(RequestCallback<TagInformation> callback) {
        mClientManager.addCallback(GetTagInformation.KEY, callback);
    }
    /// <summary>
    /// GetTagsList callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetTagsListListener(RequestCallback<ArrayList<TagInformation>> callback) {
        mClientManager.addCallback(GetTagsList.KEY, callback);
    }
    /// <summary>
    /// SetTagsList callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnSetTagsListListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(SetTagsList.KEY, callback);
    }
    /// <summary>
    /// AddTagsToList callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnAddTagsToListListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(AddTagsToList.KEY, callback);
    }
    /// <summary>
    /// GetTagsTotalNumber callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetTagsTotalNumberListener(RequestCallback<Integer> callback) {
        mClientManager.addCallback(GetTagsTotalNumber.KEY, callback);
    }
    /// <summary>
    /// ReaderGetTag callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnReaderGetTagListener(RequestCallback<ReaderTag> callback) {
        mClientManager.addCallback(ReaderGetTag.KEY, callback);
    }
    /// <summary>
    /// PumpSetLights callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpSetLightsListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpSetLights.KEY, callback);
    }
    /// <summary>
    /// PumpGetAutomaticOperation callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpGetAutomaticOperationListener(RequestCallback<PumpAutomaticOperation> callback) {
        mClientManager.addCallback(PumpGetAutomaticOperation.KEY, callback);
    }
    /// <summary>
    /// PumpSetAutomaticOperation callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnPumpSetAutomaticOperationListener(RequestCallback<Boolean> callback) {
        mClientManager.addCallback(PumpSetAutomaticOperation.KEY, callback);
    }
    /// <summary>
    /// ProbeGetMeasurements callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnProbeGetMeasurementsListener(RequestCallback<ProbeMeasurements> callback) {
        mClientManager.addCallback(ProbeGetMeasurements.KEY, callback);
    }
    /// <summary>
    /// ProbeGetTankVolumeForHeight callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnProbeGetTankVolumeForHeightListener(RequestCallback<ProbeTankVolumeForHeight> callback) {
        mClientManager.addCallback(ProbeGetTankVolumeForHeight.KEY, callback);
    }
    /// <summary>
    /// GetGpsData callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnGetGpsDataListener(RequestCallback<GpsData> callback) {
        mClientManager.addCallback(GetGpsData.KEY, callback);
    }
    /// <summary>
    /// ReportGetPumpTransactions callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnReportGetPumpTransactionsListener(RequestCallback<ArrayList<ReportPumpTransaction>> callback) {
        mClientManager.addCallback(ReportGetPumpTransactions.KEY, callback);
    }
    /// <summary>
    /// ReportGetTankMeasurements callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnReportGetTankMeasurementsListener(RequestCallback<ArrayList<ReportTankMeasurement>> callback) {
        mClientManager.addCallback(ReportGetTankMeasurements.KEY, callback);
    }
    /// <summary>
    /// ReportGetInTankDeliveries callback setter
    /// </summary>
    /// <param name="callback">Callback function</param>
    public void setOnReportGetInTankDeliveriesListener(RequestCallback<ArrayList<ReportInTankDelivery>> callback) {
        mClientManager.addCallback(ReportGetInTankDeliveries.KEY, callback);
    }
}