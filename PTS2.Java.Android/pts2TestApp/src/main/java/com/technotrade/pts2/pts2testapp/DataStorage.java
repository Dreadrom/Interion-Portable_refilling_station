package com.technotrade.pts2.pts2testapp;

import com.technotrade.pts2.datastructs.DateTimeSettings;
import com.technotrade.pts2.datastructs.FirmwareInformation;
import com.technotrade.pts2.datastructs.FuelGrade;
import com.technotrade.pts2.datastructs.GpsData;
import com.technotrade.pts2.datastructs.MeasurementUnits;
import com.technotrade.pts2.datastructs.Parameter;
import com.technotrade.pts2.datastructs.PriceBoardsConfiguration;
import com.technotrade.pts2.datastructs.ProbeMeasurements;
import com.technotrade.pts2.datastructs.ProbeTankVolumeForHeight;
import com.technotrade.pts2.datastructs.ProbesConfiguration;
import com.technotrade.pts2.datastructs.PtsNetworkSettings;
import com.technotrade.pts2.datastructs.PumpAuthorizeConfirmation;
import com.technotrade.pts2.datastructs.PumpAutomaticOperation;
import com.technotrade.pts2.datastructs.PumpDisplayData;
import com.technotrade.pts2.datastructs.PumpEndOfTransactionStatus;
import com.technotrade.pts2.datastructs.PumpFillingStatus;
import com.technotrade.pts2.datastructs.PumpIdleStatus;
import com.technotrade.pts2.datastructs.PumpNozzles;
import com.technotrade.pts2.datastructs.PumpOfflineStatus;
import com.technotrade.pts2.datastructs.PumpPrices;
import com.technotrade.pts2.datastructs.PumpStatusBase;
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
import com.technotrade.pts2.pts2testapp.entity.DataHolder;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// Data storage class for storing data that came from PTS2 device
/// </summary>
public class DataStorage {
    // custom data section
    private final DataHolder<Boolean> mOpened;
    private final DataHolder<Boolean> mConnected;
    private final DataHolder<List<PumpItem>> mPumpItems;
    private final DataHolder<List<NozzleItem>> mNozzleItems;
    private final DataHolder<PumpItem> mSelectedPump;
    private final DataHolder<NozzleItem> mSelectedNozzle;

    // section with data that relates to API
    private final DataHolder<PumpIdleStatus> mPumpIdleStatus;
    private final DataHolder<PumpFillingStatus> mPumpFillingStatus;
    private final DataHolder<PumpEndOfTransactionStatus> mPumpEndOfTransactionStatus;
    private final DataHolder<PumpOfflineStatus> mPumpOfflineStatus;
    private final DataHolder<PumpTotals> mPumpTotals;
    private final DataHolder<PumpPrices> mPumpPrices;
    private final DataHolder<PumpTag> mPumpTag;
    private final DataHolder<PumpDisplayData> mPumpDisplayData;
    private final DataHolder<Integer> mBatteryVoltage;
    private final DataHolder<Integer> mCpuTemperature;
    private final DataHolder<String> mUniqueIdentifier;
    private final DataHolder<FirmwareInformation> mFirmwareInformation;
    private final DataHolder<MeasurementUnits> mMeasurementUnits;
    private final DataHolder<Boolean> mRestart;
    private final DataHolder<String> mConfigurationIdentifier;
    private final DataHolder<String> mConfigurationIdentifierPrevious;
    private final DataHolder<DateTimeSettings> mDateTime;
    private final DataHolder<Boolean> mSetDateTime;
    private final DataHolder<PtsNetworkSettings> mPtsNetworkSettings;
    private final DataHolder<Boolean> mSetPtsNetworkSettings;
    private final DataHolder<SystemDecimalDigits> mSystemDecimalDigits;
    private final DataHolder<Boolean> mSetSystemDecimalDigit;
    private final DataHolder<Parameter> mParameter;
    private final DataHolder<Boolean> mSetParameter;
    private final DataHolder<PumpsConfiguration> mPumpsConfiguration;
    private final DataHolder<Boolean> mSetPumpsConfiguration;
    private final DataHolder<ProbesConfiguration> mProbesConfiguration;
    private final DataHolder<Boolean> mSetProbesConfiguration;
    private final DataHolder<ReadersConfiguration> mReadersConfiguration;
    private final DataHolder<Boolean> mSetReadersConfiguration;
    private final DataHolder<PriceBoardsConfiguration> mPriceBoardsConfiguration;
    private final DataHolder<Boolean> mSetPriceBoardsConfiguration;
    private final DataHolder<ArrayList<FuelGrade>> mFuelGradesConfiguration;
    private final DataHolder<Boolean> mSetFuelGradesConfiguration;
    private final DataHolder<ArrayList<PumpNozzles>> mPumpNozzlesConfiguration;
    private final DataHolder<Boolean> mSetPumpNozzlesConfiguration;
    private final DataHolder<ArrayList<Tank>> mTanksConfiguration;
    private final DataHolder<Boolean> mSetTanksConfiguration;
    private final DataHolder<ArrayList<User>> mUsersConfiguration;
    private final DataHolder<Boolean> mSetUsersConfiguration;
    private final DataHolder<PumpStatusBase> mPumpGetStatus;
    private final DataHolder<PumpAuthorizeConfirmation> mPumpAuthorize;
    private final DataHolder<PumpTransactionInformation> mPumpGetTransactionInformation;
    private final DataHolder<Boolean> mPumpStop;
    private final DataHolder<Boolean> mPumpEmergencyStop;
    private final DataHolder<Boolean> mPumpSuspend;
    private final DataHolder<Boolean> mPumpResume;
    private final DataHolder<Boolean> mPumpCloseTransaction;
    private final DataHolder<Boolean> mPumpGetTotals;
    private final DataHolder<Boolean> mPumpGetPrices;
    private final DataHolder<Boolean> mPumpSetPrices;
    private final DataHolder<Boolean> mPumpGetDisplayData;
    private final DataHolder<Boolean> mPumpGetTag;
    private final DataHolder<TagInformation> mTagInformation;
    private final DataHolder<ArrayList<TagInformation>> mTagsList;
    private final DataHolder<Boolean> mSetTagsList;
    private final DataHolder<Boolean> mAddTagsToList;
    private final DataHolder<Integer> mTagsTotalNumber;
    private final DataHolder<ReaderTag> mReaderGetTag;
    private final DataHolder<Boolean> mPumpSetLights;
    private final DataHolder<PumpAutomaticOperation> mPumpGetAutomaticOperation;
    private final DataHolder<Boolean> mPumpSetAutomaticOperation;
    private final DataHolder<ProbeMeasurements> mProbeGetMeasurements;
    private final DataHolder<ProbeTankVolumeForHeight> mProbeGetTankVolumeForHeight;
    private final DataHolder<GpsData> mGpsData;
    private final DataHolder<ArrayList<ReportPumpTransaction>> mReportGetPumpTransactions;
    private final DataHolder<ArrayList<ReportTankMeasurement>> mReportGetTankMeasurements;
    private final DataHolder<ArrayList<ReportInTankDelivery>> mReportGetInTankDeliveries;

    public DataStorage() {
        mOpened = new DataHolder<Boolean>(false);
        mConnected = new DataHolder<Boolean>(false);
        mPumpItems = new DataHolder<List<PumpItem>>();
        mNozzleItems = new DataHolder<List<NozzleItem>>();
        mSelectedPump = new DataHolder<PumpItem>();
        mSelectedNozzle = new DataHolder<NozzleItem>();

        mPumpIdleStatus = new DataHolder<PumpIdleStatus>();
        mPumpFillingStatus = new DataHolder<PumpFillingStatus>();
        mPumpEndOfTransactionStatus = new DataHolder<PumpEndOfTransactionStatus>();
        mPumpOfflineStatus = new DataHolder<PumpOfflineStatus>();
        mPumpTotals = new DataHolder<PumpTotals>();
        mPumpPrices = new DataHolder<PumpPrices>();
        mPumpTag = new DataHolder<PumpTag>();
        mPumpDisplayData = new DataHolder<PumpDisplayData>();
        mBatteryVoltage = new DataHolder<Integer>();
        mCpuTemperature = new DataHolder<Integer>();
        mUniqueIdentifier = new DataHolder<String>();
        mFirmwareInformation = new DataHolder<FirmwareInformation>();
        mMeasurementUnits = new DataHolder<MeasurementUnits>();
        mRestart = new DataHolder<Boolean>();
        mConfigurationIdentifier = new DataHolder<String>();
        mConfigurationIdentifierPrevious = new DataHolder<String>();
        mDateTime = new DataHolder<DateTimeSettings>();
        mSetDateTime = new DataHolder<Boolean>();
        mPtsNetworkSettings = new DataHolder<PtsNetworkSettings>();
        mSetPtsNetworkSettings = new DataHolder<Boolean>();
        mSystemDecimalDigits = new DataHolder<SystemDecimalDigits>();
        mSetSystemDecimalDigit = new DataHolder<Boolean>();
        mParameter = new DataHolder<Parameter>();
        mSetParameter = new DataHolder<Boolean>();
        mPumpsConfiguration = new DataHolder<PumpsConfiguration>();
        mSetPumpsConfiguration = new DataHolder<Boolean>();
        mProbesConfiguration = new DataHolder<ProbesConfiguration>();
        mSetProbesConfiguration = new DataHolder<Boolean>();
        mReadersConfiguration = new DataHolder<ReadersConfiguration>();
        mSetReadersConfiguration = new DataHolder<Boolean>();
        mPriceBoardsConfiguration = new DataHolder<PriceBoardsConfiguration>();
        mSetPriceBoardsConfiguration = new DataHolder<Boolean>();
        mFuelGradesConfiguration = new DataHolder<ArrayList<FuelGrade>>();
        mSetFuelGradesConfiguration = new DataHolder<Boolean>();
        mPumpNozzlesConfiguration = new DataHolder<ArrayList<PumpNozzles>>();
        mSetPumpNozzlesConfiguration = new DataHolder<Boolean>();
        mTanksConfiguration = new DataHolder<ArrayList<Tank>>();
        mSetTanksConfiguration = new DataHolder<Boolean>();
        mUsersConfiguration = new DataHolder<ArrayList<User>>();
        mSetUsersConfiguration = new DataHolder<Boolean>();
        mPumpGetStatus = new DataHolder<PumpStatusBase>();
        mPumpAuthorize = new DataHolder<PumpAuthorizeConfirmation>();
        mPumpGetTransactionInformation = new DataHolder<PumpTransactionInformation>();
        mPumpStop = new DataHolder<Boolean>();
        mPumpEmergencyStop = new DataHolder<Boolean>();
        mPumpSuspend = new DataHolder<Boolean>();
        mPumpResume = new DataHolder<Boolean>();
        mPumpCloseTransaction = new DataHolder<Boolean>();
        mPumpGetTotals = new DataHolder<Boolean>();
        mPumpGetPrices = new DataHolder<Boolean>();
        mPumpSetPrices = new DataHolder<Boolean>();
        mPumpGetDisplayData = new DataHolder<Boolean>();
        mPumpGetTag = new DataHolder<Boolean>();
        mTagInformation = new DataHolder<TagInformation>();
        mTagsList = new DataHolder<ArrayList<TagInformation>>();
        mSetTagsList = new DataHolder<Boolean>();
        mAddTagsToList = new DataHolder<Boolean>();
        mTagsTotalNumber = new DataHolder<Integer>();
        mReaderGetTag = new DataHolder<ReaderTag>();
        mPumpSetLights = new DataHolder<Boolean>();
        mPumpGetAutomaticOperation = new DataHolder<PumpAutomaticOperation>();
        mPumpSetAutomaticOperation = new DataHolder<Boolean>();
        mProbeGetMeasurements = new DataHolder<ProbeMeasurements>();
        mProbeGetTankVolumeForHeight = new DataHolder<ProbeTankVolumeForHeight>();
        mGpsData = new DataHolder<GpsData>();
        mReportGetPumpTransactions = new DataHolder<ArrayList<ReportPumpTransaction>>();
        mReportGetTankMeasurements = new DataHolder<ArrayList<ReportTankMeasurement>>();
        mReportGetInTankDeliveries = new DataHolder<ArrayList<ReportInTankDelivery>>();
    }

    public void clear() {
        mOpened.clear();
        mConnected.clear();
        mPumpItems.clear();
        mNozzleItems.clear();
        mSelectedPump.clear();
        mSelectedNozzle.clear();

        mPumpIdleStatus.clear();
        mPumpFillingStatus.clear();
        mPumpEndOfTransactionStatus.clear();
        mPumpOfflineStatus.clear();
        mPumpTotals.clear();
        mPumpPrices.clear();
        mPumpTag.clear();
        mPumpDisplayData.clear();
        mBatteryVoltage.clear();
        mCpuTemperature.clear();
        mUniqueIdentifier.clear();
        mFirmwareInformation.clear();
        mMeasurementUnits.clear();
        mRestart.clear();
        mConfigurationIdentifier.clear();
        mConfigurationIdentifierPrevious.clear();
        mDateTime.clear();
        mSetDateTime.clear();
        mPtsNetworkSettings.clear();
        mSetPtsNetworkSettings.clear();
        mSystemDecimalDigits.clear();
        mSetSystemDecimalDigit.clear();
        mParameter.clear();
        mSetParameter.clear();
        mPumpsConfiguration.clear();
        mSetPumpsConfiguration.clear();
        mProbesConfiguration.clear();
        mSetProbesConfiguration.clear();
        mReadersConfiguration.clear();
        mSetReadersConfiguration.clear();
        mPriceBoardsConfiguration.clear();
        mSetPriceBoardsConfiguration.clear();
        mFuelGradesConfiguration.clear();
        mSetFuelGradesConfiguration.clear();
        mPumpNozzlesConfiguration.clear();
        mSetPumpNozzlesConfiguration.clear();
        mTanksConfiguration.clear();
        mSetTanksConfiguration.clear();
        mUsersConfiguration.clear();
        mSetUsersConfiguration.clear();
        mPumpGetStatus.clear();
        mPumpAuthorize.clear();
        mPumpGetTransactionInformation.clear();
        mPumpStop.clear();
        mPumpEmergencyStop.clear();
        mPumpSuspend.clear();
        mPumpResume.clear();
        mPumpCloseTransaction.clear();
        mPumpGetTotals.clear();
        mPumpGetPrices.clear();
        mPumpSetPrices.clear();
        mPumpGetDisplayData.clear();
        mPumpGetTag.clear();
        mTagInformation.clear();
        mTagsList.clear();
        mSetTagsList.clear();
        mAddTagsToList.clear();
        mTagsTotalNumber.clear();
        mReaderGetTag.clear();
        mPumpSetLights.clear();
        mPumpGetAutomaticOperation.clear();
        mPumpSetAutomaticOperation.clear();
        mProbeGetMeasurements.clear();
        mProbeGetTankVolumeForHeight.clear();
        mGpsData.clear();
        mReportGetPumpTransactions.clear();
        mReportGetTankMeasurements.clear();
        mReportGetInTankDeliveries.clear();
    }

    /// <summary>
    /// Opened getter and setter
    /// </summary>
    public Boolean getOpened() {
        return mOpened.getData();
    }

    public void setOpened(Boolean opened) {
        mOpened.setData(opened);
    }

    public DataHolder<Boolean> getOpenedDataHolder() {
        return mOpened;
    }

    /// <summary>
    /// Connected getter and setter
    /// </summary>
    public Boolean getConnected() {
        return mConnected.getData();
    }

    public void setConnected(Boolean connected) {
        mConnected.setData(connected);
    }

    public DataHolder<Boolean> getConnectedDataHolder() {
        return mConnected;
    }

    /// <summary>
    /// PumpItems getter and setter
    /// </summary>
    public List<PumpItem> getPumpItems() {
        return mPumpItems.getData();
    }

    public void setPumpItems(List<PumpItem> pumpItems) {
        mPumpItems.setData(pumpItems);
    }

    public DataHolder<List<PumpItem>> getPumpItemsDataHolder() {
        return mPumpItems;
    }

    /// <summary>
    /// NozzleItems getter and setter
    /// </summary>
    public List<NozzleItem> getNozzleItems() {
        return mNozzleItems.getData();
    }

    public void setNozzleItems(List<NozzleItem> nozzleItems) {
        mNozzleItems.setData(nozzleItems);
    }

    public DataHolder<List<NozzleItem>> getNozzleItemsDataHolder() {
        return mNozzleItems;
    }

    /// <summary>
    /// Selected pump getter and setter
    /// </summary>
    public PumpItem getSelectedPump() {
        return mSelectedPump.getData();
    }

    public void setSelectedPump(PumpItem selectedPump) {
        mSelectedPump.setData(selectedPump);
    }

    public DataHolder<PumpItem> getSelectedPumpDataHolder() {
        return mSelectedPump;
    }

    /// <summary>
    /// Selected nozzle getter and setter
    /// </summary>
    public NozzleItem getSelectedNozzle() {
        return mSelectedNozzle.getData();
    }

    public void setSelectedNozzle(NozzleItem selectedNozzle) {
        mSelectedNozzle.setData(selectedNozzle);
    }

    public DataHolder<NozzleItem> getSelectedNozzleDataHolder() {
        return mSelectedNozzle;
    }

    /// <summary>
    /// Pump IdleStatus getter and setter
    /// </summary>
    public PumpIdleStatus getPumpIdleStatus() {
        return mPumpIdleStatus.getData();
    }

    public void setPumpIdleStatus(PumpIdleStatus pumpIdleStatus) {
        mPumpIdleStatus.setData(pumpIdleStatus);
    }

    public DataHolder<PumpIdleStatus> getPumpIdleStatusDataHolder() {
        return mPumpIdleStatus;
    }

    /// <summary>
    /// Pump FillingStatus getter and setter
    /// </summary>
    public PumpFillingStatus getPumpFillingStatus() {
        return mPumpFillingStatus.getData();
    }

    public void setPumpFillingStatus(PumpFillingStatus pumpFillingStatus) {
        mPumpFillingStatus.setData(pumpFillingStatus);
    }

    public DataHolder<PumpFillingStatus> getPumpFillingStatusDataHolder() {
        return mPumpFillingStatus;
    }

    /// <summary>
    /// Pump EndOfTransactionStatus getter and setter
    /// </summary>
    public PumpEndOfTransactionStatus getPumpEndOfTransactionStatus() {
        return mPumpEndOfTransactionStatus.getData();
    }

    public void setPumpEndOfTransactionStatus(PumpEndOfTransactionStatus pumpEndOfTransactionStatus) {
        mPumpEndOfTransactionStatus.setData(pumpEndOfTransactionStatus);
    }

    public DataHolder<PumpEndOfTransactionStatus> getPumpEndOfTransactionStatusDataHolder() {
        return mPumpEndOfTransactionStatus;
    }

    /// <summary>
    /// Pump OfflineStatus getter and setter
    /// </summary>
    public PumpOfflineStatus getPumpOfflineStatus() {
        return mPumpOfflineStatus.getData();
    }

    public void setPumpOfflineStatus(PumpOfflineStatus pumpOfflineStatus) {
        mPumpOfflineStatus.setData(pumpOfflineStatus);
    }

    public DataHolder<PumpOfflineStatus> getPumpOfflineStatusDataHolder() {
        return mPumpOfflineStatus;
    }

    /// <summary>
    /// Pump totals getter and setter
    /// </summary>
    public PumpTotals getPumpTotals() {
        return mPumpTotals.getData();
    }

    public void setPumpTotals(PumpTotals pumpTotals) {
        mPumpTotals.setData(pumpTotals);
    }

    public DataHolder<PumpTotals> getPumpTotalsDataHolder() {
        return mPumpTotals;
    }

    /// <summary>
    /// Pump prices getter and setter
    /// </summary>
    public PumpPrices getPumpPrices() {
        return mPumpPrices.getData();
    }

    public void setPumpPrices(PumpPrices pumpPrices) {
        mPumpPrices.setData(pumpPrices);
    }

    public DataHolder<PumpPrices> getPumpPricesDataHolder() {
        return mPumpPrices;
    }

    /// <summary>
    /// Pump tag getter and setter
    /// </summary>
    public PumpTag getPumpTag() {
        return mPumpTag.getData();
    }

    public void setPumpTag(PumpTag pumpTag) {
        mPumpTag.setData(pumpTag);
    }

    public DataHolder<PumpTag> getPumpTagDataHolder() {
        return mPumpTag;
    }

    /// <summary>
    /// Pump display data getter and setter
    /// </summary>
    public PumpDisplayData getPumpDisplayData() {
        return mPumpDisplayData.getData();
    }

    public void setPumpDisplayData(PumpDisplayData pumpDisplayData) {
        mPumpDisplayData.setData(pumpDisplayData);
    }

    public DataHolder<PumpDisplayData> getPumpDisplayDataDataHolder() {
        return mPumpDisplayData;
    }

    /// <summary>
    /// BatteryVoltage getter and setter
    /// </summary>
    public Integer getBatteryVoltage() {
        return mBatteryVoltage.getData();
    }

    public void setBatteryVoltage(Integer batteryVoltage) {
        mBatteryVoltage.setData(batteryVoltage);
    }

    public DataHolder<Integer> getBatteryVoltageDataHolder() {
        return mBatteryVoltage;
    }

    /// <summary>
    /// CpuTemperature getter and setter
    /// </summary>
    public Integer getCpuTemperature() {
        return mCpuTemperature.getData();
    }

    public void setCpuTemperature(Integer cpuTemperature) {
        mCpuTemperature.setData(cpuTemperature);
    }

    public DataHolder<Integer> getCpuTemperatureDataHolder() {
        return mCpuTemperature;
    }

    /// <summary>
    /// UniqueIdentifier getter and setter
    /// </summary>
    public String getUniqueIdentifier() {
        return mUniqueIdentifier.getData();
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        mUniqueIdentifier.setData(uniqueIdentifier);
    }

    public DataHolder<String> getUniqueIdentifierDataHolder() {
        return mUniqueIdentifier;
    }

    /// <summary>
    /// FirmwareInformation getter and setter
    /// </summary>
    public FirmwareInformation getFirmwareInformation() {
        return mFirmwareInformation.getData();
    }

    public void setFirmwareInformation(FirmwareInformation firmwareInformation) {
        mFirmwareInformation.setData(firmwareInformation);
    }

    public DataHolder<FirmwareInformation> getFirmwareInformationDataHolder() {
        return mFirmwareInformation;
    }

    /// <summary>
    /// MeasurementUnits getter and setter
    /// </summary>
    public MeasurementUnits getMeasurementUnits() {
        return mMeasurementUnits.getData();
    }

    public void setMeasurementUnits(MeasurementUnits measurementUnits) {
        mMeasurementUnits.setData(measurementUnits);
    }

    public DataHolder<MeasurementUnits> getMeasurementUnitsDataHolder() {
        return mMeasurementUnits;
    }

    /// <summary>
    /// Restart getter and setter
    /// </summary>
    public Boolean getRestart() {
        return mRestart.getData();
    }

    public void setRestart(Boolean measurementUnits) {
        mRestart.setData(measurementUnits);
    }

    public DataHolder<Boolean> getRestartDataHolder() {
        return mRestart;
    }

    /// <summary>
    /// Configuration identifier getter and setter
    /// </summary>
    public String getConfigurationIdentifier() {
        return mConfigurationIdentifier.getData();
    }

    public void setConfigurationIdentifier(String configurationIdentifier) {
        mConfigurationIdentifier.setData(configurationIdentifier);
    }

    public DataHolder<String> getConfigurationIdentifierDataHolder() {
        return mConfigurationIdentifier;
    }

    /// <summary>
    /// Previous configuration identifier getter and setter
    /// </summary>
    public String getConfigurationIdentifierPrevious() {
        return mConfigurationIdentifierPrevious.getData();
    }

    public void setConfigurationIdentifierPrevious(String configurationIdentifierPrevious) {
        mConfigurationIdentifierPrevious.setData(configurationIdentifierPrevious);
    }

    public DataHolder<String> getConfigurationIdentifierPreviousDataHolder() {
        return mConfigurationIdentifierPrevious;
    }

    /// <summary>
    /// DateTime getter and setter
    /// </summary>
    public DateTimeSettings getDateTime() {
        return mDateTime.getData();
    }

    public void setDateTime(DateTimeSettings dateTime) {
        mDateTime.setData(dateTime);
    }

    public DataHolder<DateTimeSettings> getDateTimeDataHolder() {
        return mDateTime;
    }

    /// <summary>
    /// SetDateTime getter and setter
    /// </summary>
    public Boolean getSetDateTime() {
        return mSetDateTime.getData();
    }

    public void setSetDateTime(Boolean setDateTime) {
        mSetDateTime.setData(setDateTime);
    }

    public DataHolder<Boolean> getSetDateTimeDataHolder() {
        return mSetDateTime;
    }

    /// <summary>
    /// PtsNetworkSettings getter and setter
    /// </summary>
    public PtsNetworkSettings getPtsNetworkSettings() {
        return mPtsNetworkSettings.getData();
    }

    public void setPtsNetworkSettings(PtsNetworkSettings ptsNetworkSettings) {
        mPtsNetworkSettings.setData(ptsNetworkSettings);
    }

    public DataHolder<PtsNetworkSettings> getPtsNetworkSettingsDataHolder() {
        return mPtsNetworkSettings;
    }

    /// <summary>
    ///SetPtsNetworkSettings getter and setter
    /// </summary>
    public Boolean getSetPtsNetworkSettings() {
        return mSetPtsNetworkSettings.getData();
    }

    public void setSetPtsNetworkSettings(Boolean setPtsNetworkSettings) {
        mSetPtsNetworkSettings.setData(setPtsNetworkSettings);
    }

    public DataHolder<Boolean> getSetPtsNetworkSettingsDataHolder() {
        return mSetPtsNetworkSettings;
    }

    /// <summary>
    ///SystemDecimalDigits getter and setter
    /// </summary>
    public SystemDecimalDigits getSystemDecimalDigits() {
        return mSystemDecimalDigits.getData();
    }

    public void setSystemDecimalDigits(SystemDecimalDigits systemDecimalDigits) {
        mSystemDecimalDigits.setData(systemDecimalDigits);
    }

    public DataHolder<SystemDecimalDigits> getSystemDecimalDigitsDataHolder() {
        return mSystemDecimalDigits;
    }

    /// <summary>
    ///SetSystemDecimalDigits getter and setter
    /// </summary>
    public Boolean getSetSystemDecimalDigits() {
        return mSetSystemDecimalDigit.getData();
    }

    public void setSetSystemDecimalDigits(Boolean setSystemDecimalDigits) {
        mSetSystemDecimalDigit.setData(setSystemDecimalDigits);
    }

    public DataHolder<Boolean> getSetSystemDecimalDigitsDataHolder() {
        return mSetSystemDecimalDigit;
    }

    /// <summary>
    ///Parameter getter and setter
    /// </summary>
    public Parameter getParameter() {
        return mParameter.getData();
    }

    public void setParameter(Parameter parameter) {
        mParameter.setData(parameter);
    }

    public DataHolder<Parameter> getParameterDataHolder() {
        return mParameter;
    }

    /// <summary>
    ///SetParameter getter and setter
    /// </summary>
    public Boolean getSetParameter() {
        return mSetParameter.getData();
    }

    public void setSetParameter(Boolean setSetParameter) {
        mSetParameter.setData(setSetParameter);
    }

    public DataHolder<Boolean> getSetParameterDataHolder() {
        return mSetParameter;
    }

    /// <summary>
    /// Pumps configuration getter and setter
    /// </summary>
    public PumpsConfiguration getPumpsConfiguration() {
        return mPumpsConfiguration.getData();
    }

    public void setPumpsConfiguration(PumpsConfiguration pumpsConfiguration) {
        mPumpsConfiguration.setData(pumpsConfiguration);
    }

    public DataHolder<PumpsConfiguration> getPumpsConfigurationDataHolder() {
        return mPumpsConfiguration;
    }

    /// <summary>
    /// SetPumpsConfiguration configuration getter and setter
    /// </summary>
    public Boolean getSetPumpsConfiguration() {
        return mSetPumpsConfiguration.getData();
    }

    public void setSetPumpsConfiguration(Boolean setPumpsConfiguration) {
        mSetPumpsConfiguration.setData(setPumpsConfiguration);
    }

    public DataHolder<Boolean> getSetPumpsConfigurationDataHolder() {
        return mSetPumpsConfiguration;
    }

    /// <summary>
    /// Probes configuration getter and setter
    /// </summary>
    public ProbesConfiguration getProbesConfiguration() {
        return mProbesConfiguration.getData();
    }

    public void setProbesConfiguration(ProbesConfiguration probesConfiguration) {
        mProbesConfiguration.setData(probesConfiguration);
    }

    public DataHolder<ProbesConfiguration> getProbesConfigurationDataHolder() {
        return mProbesConfiguration;
    }

    /// <summary>
    /// SetProbesConfiguration getter and setter
    /// </summary>
    public Boolean getSetProbesConfiguration() {
        return mSetProbesConfiguration.getData();
    }

    public void setSetProbesConfiguration(Boolean setProbesConfiguration) {
        mSetProbesConfiguration.setData(setProbesConfiguration);
    }

    public DataHolder<Boolean> getSetProbesConfigurationDataHolder() {
        return mSetProbesConfiguration;
    }

    /// <summary>
    /// ReadersConfiguration getter and setter
    /// </summary>
    public ReadersConfiguration getReadersConfiguration() {
        return mReadersConfiguration.getData();
    }

    public void setReadersConfiguration(ReadersConfiguration readersConfiguration) {
        mReadersConfiguration.setData(readersConfiguration);
    }

    public DataHolder<ReadersConfiguration> getReadersConfigurationDataHolder() {
        return mReadersConfiguration;
    }

    /// <summary>
    /// SetReadersConfiguration getter and setter
    /// </summary>
    public Boolean getSetReadersConfiguration() {
        return mSetReadersConfiguration.getData();
    }

    public void setSetReadersConfiguration(Boolean setReadersConfiguration) {
        mSetReadersConfiguration.setData(setReadersConfiguration);
    }

    public DataHolder<Boolean> getSetReadersConfigurationDataHolder() {
        return mSetReadersConfiguration;
    }

    /// <summary>
    /// PriceBoardsConfiguration getter and setter
    /// </summary>
    public PriceBoardsConfiguration getPriceBoardsConfiguration() {
        return mPriceBoardsConfiguration.getData();
    }

    public void setPriceBoardsConfiguration(PriceBoardsConfiguration priceBoardsConfiguration) {
        mPriceBoardsConfiguration.setData(priceBoardsConfiguration);
    }

    public DataHolder<PriceBoardsConfiguration> getPriceBoardsConfigurationDataHolder() {
        return mPriceBoardsConfiguration;
    }

    /// <summary>
    /// SetPriceBoardsConfiguration getter and setter
    /// </summary>
    public Boolean getSetPriceBoardsConfiguration() {
        return mSetPriceBoardsConfiguration.getData();
    }

    public void setSetPriceBoardsConfiguration(Boolean setPriceBoardsConfiguration) {
        mSetPriceBoardsConfiguration.setData(setPriceBoardsConfiguration);
    }

    public DataHolder<Boolean> getSetPriceBoardsConfigurationDataHolder() {
        return mSetPriceBoardsConfiguration;
    }

    /// <summary>
    /// FuelGradesConfiguration getter and setter
    /// </summary>
    public ArrayList<FuelGrade> getFuelGradesConfiguration() {
        return mFuelGradesConfiguration.getData();
    }

    public void setFuelGradesConfiguration(ArrayList<FuelGrade> fuelGradesConfiguration) {
        mFuelGradesConfiguration.setData(fuelGradesConfiguration);
    }

    public DataHolder<ArrayList<FuelGrade>> getFuelGradesConfigurationDataHolder() {
        return mFuelGradesConfiguration;
    }

    /// <summary>
    /// SetFuelGradesConfiguration getter and setter
    /// </summary>
    public Boolean getSetFuelGradesConfiguration() {
        return mSetFuelGradesConfiguration.getData();
    }

    public void setSetFuelGradesConfiguration(Boolean setFuelGradesConfiguration) {
        mSetFuelGradesConfiguration.setData(setFuelGradesConfiguration);
    }

    public DataHolder<Boolean> getSetFuelGradesConfigurationDataHolder() {
        return mSetFuelGradesConfiguration;
    }

    /// <summary>
    /// Pump nozzles configuration getter and setter
    /// </summary>
    public ArrayList<PumpNozzles> getPumpNozzlesConfiguration() {
        return mPumpNozzlesConfiguration.getData();
    }

    public void setPumpNozzlesConfiguration(ArrayList<PumpNozzles> pumpNozzlesConfiguration) {
        mPumpNozzlesConfiguration.setData(pumpNozzlesConfiguration);
    }

    public DataHolder<ArrayList<PumpNozzles>> getPumpNozzlesConfigurationDataHolder() {
        return mPumpNozzlesConfiguration;
    }

    /// <summary>
    /// SetPumpNozzlesConfiguration getter and setter
    /// </summary>
    public Boolean getSetPumpNozzlesConfiguration() {
        return mSetPumpNozzlesConfiguration.getData();
    }

    public void setSetPumpNozzlesConfiguration(Boolean setPumpNozzlesConfiguration) {
        mSetPumpNozzlesConfiguration.setData(setPumpNozzlesConfiguration);
    }

    public DataHolder<Boolean> getSetPumpNozzlesConfigurationDataHolder() {
        return mSetPumpNozzlesConfiguration;
    }

    /// <summary>
    /// TanksConfiguration getter and setter
    /// </summary>
    public ArrayList<Tank> getTanksConfiguration() {
        return mTanksConfiguration.getData();
    }

    public void setTanksConfiguration(ArrayList<Tank> tanksConfiguration) {
        mTanksConfiguration.setData(tanksConfiguration);
    }

    public DataHolder<ArrayList<Tank>> getTanksConfigurationDataHolder() {
        return mTanksConfiguration;
    }

    /// <summary>
    /// SetTanksConfiguration getter and setter
    /// </summary>
    public Boolean getSetTanksConfiguration() {
        return mSetTanksConfiguration.getData();
    }

    public void setSetTanksConfiguration(Boolean setTanksConfiguration) {
        mSetTanksConfiguration.setData(setTanksConfiguration);
    }

    public DataHolder<Boolean> getSetTanksConfigurationDataHolder() {
        return mSetTanksConfiguration;
    }

    /// <summary>
    /// UsersConfiguration getter and setter
    /// </summary>
    public ArrayList<User> getUsersConfiguration() {
        return mUsersConfiguration.getData();
    }

    public void setUsersConfiguration(ArrayList<User> usersConfiguration) {
        mUsersConfiguration.setData(usersConfiguration);
    }

    public DataHolder<ArrayList<User>> getUsersConfigurationDataHolder() {
        return mUsersConfiguration;
    }

    /// <summary>
    /// SetUsersConfiguration getter and setter
    /// </summary>
    public Boolean getSetUsersConfiguration() {
        return mSetUsersConfiguration.getData();
    }

    public void setSetUsersConfiguration(Boolean setUsersConfiguration) {
        mSetUsersConfiguration.setData(setUsersConfiguration);
    }

    public  DataHolder<Boolean> getSetUsersConfigurationDataHolder() {
        return mSetUsersConfiguration;
    }

    /// <summary>
    /// PumpGetStatus getter and setter
    /// </summary>
    public PumpStatusBase getPumpGetStatus() {
        return mPumpGetStatus.getData();
    }

    public void setPumpGetStatus(PumpStatusBase pumpStatusBase) {
        mPumpGetStatus.setData(pumpStatusBase);
    }

    public DataHolder<PumpStatusBase> getPumpGetStatusDataHolder() {
        return mPumpGetStatus;
    }

    /// <summary>
    /// PumpAuthorize getter and setter
    /// </summary>
    public PumpAuthorizeConfirmation getPumpAuthorize() {
        return mPumpAuthorize.getData();
    }

    public void setPumpAuthorize(PumpAuthorizeConfirmation pumpAuthorize) {
        mPumpAuthorize.setData(pumpAuthorize);
    }

    public DataHolder<PumpAuthorizeConfirmation> getPumpAuthorizeDataHolder() {
        return mPumpAuthorize;
    }

    /// <summary>
    /// PumpGetTransactionInformation getter and setter
    /// </summary>
    public PumpTransactionInformation getPumpGetTransactionInformation() {
        return mPumpGetTransactionInformation.getData();
    }

    public void setPumpGetTransactionInformation(PumpTransactionInformation pumpGetTransactionInformation) {
        mPumpGetTransactionInformation.setData(pumpGetTransactionInformation);
    }

    public DataHolder<PumpTransactionInformation> getPumpGetTransactionInformationDataHolder() {
        return mPumpGetTransactionInformation;
    }

    /// <summary>
    /// PumpStop getter and setter
    /// </summary>
    public Boolean getPumpStop() {
        return mPumpStop.getData();
    }

    public void setPumpStop(Boolean pumpStop) {
        mPumpStop.setData(pumpStop);
    }

    public DataHolder<Boolean> getPumpStopDataHolder() {
        return mPumpStop;
    }

    /// <summary>
    /// PumpEmergencyStop getter and setter
    /// </summary>
    public Boolean getPumpEmergencyStop() {
        return mPumpEmergencyStop.getData();
    }

    public void setPumpEmergencyStop(Boolean pumpEmergencyStop) {
        mPumpEmergencyStop.setData(pumpEmergencyStop);
    }

    public DataHolder<Boolean> getPumpEmergencyStopDataHolder() {
        return mPumpEmergencyStop;
    }

    /// <summary>
    /// PumpSuspend getter and setter
    /// </summary>
    public Boolean getPumpSuspend() {
        return mPumpSuspend.getData();
    }

    public void setPumpSuspend(Boolean pumpSuspend) {
        mPumpSuspend.setData(pumpSuspend);
    }

    public DataHolder<Boolean> getPumpSuspendDataHolder() {
        return mPumpSuspend;
    }

    /// <summary>
    /// PumpResume getter and setter
    /// </summary>
    public Boolean getPumpResume() {
        return mPumpResume.getData();
    }

    public void setPumpResume(Boolean pumpResume) {
        mPumpResume.setData(pumpResume);
    }

    public DataHolder<Boolean> getPumpResumeDataHolder() {
        return mPumpResume;
    }

    /// <summary>
    /// PumpCloseTransaction getter and setter
    /// </summary>
    public Boolean getPumpCloseTransaction() {
        return mPumpCloseTransaction.getData();
    }

    public void setPumpCloseTransaction(Boolean pumpCloseTransaction) {
        mPumpCloseTransaction.setData(pumpCloseTransaction);
    }

    public DataHolder<Boolean> getPumpCloseTransactionDataHolder() {
        return mPumpCloseTransaction;
    }

    /// <summary>
    /// PumpGetTotals getter and setter
    /// </summary>
    public Boolean getPumpGetTotals() {
        return mPumpGetTotals.getData();
    }

    public void setPumpGetTotals(Boolean pumpGetTotals) {
        mPumpGetTotals.setData(pumpGetTotals);
    }

    public DataHolder<Boolean> getPumpGetTotalsDataHolder() {
        return mPumpGetTotals;
    }

    /// <summary>
    /// PumpGetPrices getter and setter
    /// </summary>
    public Boolean getPumpGetPrices() {
        return mPumpGetPrices.getData();
    }

    public void setPumpGetPrices(Boolean pumpGetPrices) {
        mPumpGetPrices.setData(pumpGetPrices);
    }

    public DataHolder<Boolean> getPumpGetPricesDataHolder() {
        return mPumpGetPrices;
    }

    /// <summary>
    /// PumpSetPrices getter and setter
    /// </summary>
    public Boolean getPumpSetPrices() {
        return mPumpSetPrices.getData();
    }

    public void setPumpSetPrices(Boolean pumpSetPrices) {
        mPumpSetPrices.setData(pumpSetPrices);
    }

    public DataHolder<Boolean> getPumpSetPricesDataHolder() {
        return mPumpSetPrices;
    }

    /// <summary>
    /// PumpSetPrices getter and setter
    /// </summary>
    public Boolean getPumpGetDisplayData() {
        return mPumpGetDisplayData.getData();
    }

    public void setPumpGetDisplayData(Boolean pumpSetPrices) {
        mPumpGetDisplayData.setData(pumpSetPrices);
    }

    public DataHolder<Boolean> getPumpGetDisplayDataDataHolder() {
        return mPumpGetDisplayData;
    }

    /// <summary>
    /// PumpGetTag getter and setter
    /// </summary>
    public Boolean getPumpGetTag() {
        return mPumpGetTag.getData();
    }

    public void setPumpGetTag(Boolean pumpGetTag) {
        mPumpGetTag.setData(pumpGetTag);
    }

    public DataHolder<Boolean> getPumpGetTagDataHolder() {
        return mPumpGetTag;
    }

    /// <summary>
    /// TagInformation getter and setter
    /// </summary>
    public TagInformation getTagInformation() {
        return mTagInformation.getData();
    }

    public void setTagInformation(TagInformation tagInformation) {
        mTagInformation.setData(tagInformation);
    }

    public DataHolder<TagInformation> getTagInformationDataHolder() {
        return mTagInformation;
    }

    /// <summary>
    /// TagsList getter and setter
    /// </summary>
    public ArrayList<TagInformation> getTagsList() {
        return mTagsList.getData();
    }

    public void setTagsList(ArrayList<TagInformation> tagsList) {
        mTagsList.setData(tagsList);
    }

    public DataHolder<ArrayList<TagInformation>> getTagsListDataHolder() {
        return mTagsList;
    }

    /// <summary>
    /// SetTagsList getter and setter
    /// </summary>
    public Boolean getSetTagsList() {
        return mSetTagsList.getData();
    }

    public void setSetTagsList(Boolean setTagsList) {
        mSetTagsList.setData(setTagsList);
    }

    public DataHolder<Boolean> getSetTagsListDataHolder() {
        return mSetTagsList;
    }

    /// <summary>
    /// AddTagsToList getter and setter
    /// </summary>
    public Boolean getAddTagsToList() {
        return mAddTagsToList.getData();
    }

    public void setAddTagsToList(Boolean addTagsToList) {
        mAddTagsToList.setData(addTagsToList);
    }

    public DataHolder<Boolean> getAddTagsToListDataHolder() {
        return mAddTagsToList;
    }

    /// <summary>
    /// TagsTotalNumber getter and setter
    /// </summary>
    public Integer getTagsTotalNumber() {
        return mTagsTotalNumber.getData();
    }

    public void setTagsTotalNumber(Integer tagsTotalNumber) {
        mTagsTotalNumber.setData(tagsTotalNumber);
    }

    public DataHolder<Integer> getTagsTotalNumberDataHolder() {
        return mTagsTotalNumber;
    }

    /// <summary>
    /// ReaderGetTag getter and setter
    /// </summary>
    public ReaderTag getReaderGetTag() {
        return mReaderGetTag.getData();
    }

    public void setReaderGetTag(ReaderTag readerGetTag) {
        mReaderGetTag.setData(readerGetTag);
    }

    public DataHolder<ReaderTag> getReaderGetTagDataHolder() {
        return mReaderGetTag;
    }

    /// <summary>
    /// PumpSetLights getter and setter
    /// </summary>
    public Boolean getPumpSetLights() {
        return mPumpSetLights.getData();
    }

    public void setPumpSetLights(Boolean pumpSetLights) {
        mPumpSetLights.setData(pumpSetLights);
    }

    public DataHolder<Boolean> getPumpSetLightsDataHolder() {
        return mPumpSetLights;
    }

    /// <summary>
    /// PumpGetAutomaticOperation getter and setter
    /// </summary>
    public PumpAutomaticOperation getPumpGetAutomaticOperation() {
        return mPumpGetAutomaticOperation.getData();
    }

    public void setPumpGetAutomaticOperation(PumpAutomaticOperation pumpGetAutomaticOperation) {
        mPumpGetAutomaticOperation.setData(pumpGetAutomaticOperation);
    }

    public DataHolder<PumpAutomaticOperation> getPumpGetAutomaticOperationDataHolder() {
        return mPumpGetAutomaticOperation;
    }

    /// <summary>
    /// PumpSetAutomaticOperation getter and setter
    /// </summary>
    public Boolean getPumpSetAutomaticOperation() {
        return mPumpSetAutomaticOperation.getData();
    }

    public void setPumpSetAutomaticOperation(Boolean pumpSetAutomaticOperation) {
        mPumpSetAutomaticOperation.setData(pumpSetAutomaticOperation);
    }

    public DataHolder<Boolean> getPumpSetAutomaticOperationDataHolder() {
        return mPumpSetAutomaticOperation;
    }

    /// <summary>
    /// ProbeGetMeasurements getter and setter
    /// </summary>
    public ProbeMeasurements getProbeGetMeasurements() {
        return mProbeGetMeasurements.getData();
    }

    public void setProbeGetMeasurements(ProbeMeasurements probeGetMeasurements) {
        mProbeGetMeasurements.setData(probeGetMeasurements);
    }

    public DataHolder<ProbeMeasurements> getProbeGetMeasurementsDataHolder() {
        return mProbeGetMeasurements;
    }

    /// <summary>
    /// ProbeGetTankVolumeForHeight getter and setter
    /// </summary>
    public ProbeTankVolumeForHeight getProbeGetTankVolumeForHeight() {
        return mProbeGetTankVolumeForHeight.getData();
    }

    public void setProbeGetTankVolumeForHeight(ProbeTankVolumeForHeight probeGetTankVolumeForHeight) {
        mProbeGetTankVolumeForHeight.setData(probeGetTankVolumeForHeight);
    }

    public DataHolder<ProbeTankVolumeForHeight> getProbeGetTankVolumeForHeightDataHolder() {
        return mProbeGetTankVolumeForHeight;
    }

    /// <summary>
    /// GpsData getter and setter
    /// </summary>
    public GpsData getGpsData() {
        return mGpsData.getData();
    }

    public void setGpsData(GpsData gpsData) {
        mGpsData.setData(gpsData);
    }

    public DataHolder<GpsData> getGpsDataDataHolder() {
        return mGpsData;
    }

    /// <summary>
    /// ReportGetPumpTransactions getter and setter
    /// </summary>
    public ArrayList<ReportPumpTransaction> getReportGetPumpTransactions() {
        return mReportGetPumpTransactions.getData();
    }

    public void setReportGetPumpTransactions(ArrayList<ReportPumpTransaction> reportGetPumpTransactions) {
        mReportGetPumpTransactions.setData(reportGetPumpTransactions);
    }

    public DataHolder<ArrayList<ReportPumpTransaction>> getReportGetPumpTransactionsDataHolder() {
        return mReportGetPumpTransactions;
    }

    /// <summary>
    /// ReportGetTankMeasurements getter and setter
    /// </summary>
    public ArrayList<ReportTankMeasurement> getReportGetTankMeasurements() {
        return mReportGetTankMeasurements.getData();
    }

    public void setReportGetTankMeasurements(ArrayList<ReportTankMeasurement> reportGetTankMeasurements) {
        mReportGetTankMeasurements.setData(reportGetTankMeasurements);
    }

    public DataHolder<ArrayList<ReportTankMeasurement>> getReportGetTankMeasurementsDataHolder() {
        return mReportGetTankMeasurements;
    }

    /// <summary>
    /// ReportGetInTankDeliveries getter and setter
    /// </summary>
    public ArrayList<ReportInTankDelivery> getReportGetInTankDeliveries() {
        return mReportGetInTankDeliveries.getData();
    }

    public void setReportGetInTankDeliveries(ArrayList<ReportInTankDelivery> reportGetInTankDeliveries) {
        mReportGetInTankDeliveries.setData(reportGetInTankDeliveries);
    }

    public DataHolder<ArrayList<ReportInTankDelivery>> getReportGetInTankDeliveriesDataHolder() {
        return mReportGetInTankDeliveries;
    }
}