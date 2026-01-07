package com.technotrade.pts2.pts2testapp;

import com.technotrade.pts2.IObserver;
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
import com.technotrade.pts2.network.IRequest;

import java.util.ArrayList;

/// <summary>
/// Observer class to handle a request execution result data
/// </summary>
public class DataObserver implements IObserver {
    private final DataStorage mDataStorage;

    public DataObserver(DataStorage dataStorage) {
        mDataStorage = dataStorage;
    }

    /// <summary>
    /// Callback method for PumpIdleStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpIdleStatusCallback(IRequest<?> request, PumpIdleStatus result) {
        mDataStorage.setPumpIdleStatus(result);
    }

    /// <summary>
    /// Callback method for PumpFillingStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpFillingStatusCallback(IRequest<?> request, PumpFillingStatus result) {
        mDataStorage.setPumpFillingStatus(result);
    }

    /// <summary>
    /// Callback method for PumpEndOfTransactionStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpEndOfTransactionStatusCallback(IRequest<?> request, PumpEndOfTransactionStatus result) {
        mDataStorage.setPumpEndOfTransactionStatus(result);
    }

    /// <summary>
    /// Callback method for PumpOfflineStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpOfflineStatusCallback(IRequest<?> request, PumpOfflineStatus result) {
        mDataStorage.setPumpOfflineStatus(result);
    }

    /// <summary>
    /// Callback method for PumpTotals status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpTotalsStatusCallback(IRequest<?> request, PumpTotals result) {
        mDataStorage.setPumpTotals(result);
    }

    /// <summary>
    /// Callback method for PumpPrices status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpPricesStatusCallback(IRequest<?> request, PumpPrices result) {
        mDataStorage.setPumpPrices(result);
    }

    /// <summary>p
    /// Callback method for PumpTag status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpTagStatusCallback(IRequest<?> request, PumpTag result) {
        mDataStorage.setPumpTag(result);
    }

    /// <summary>
    /// Callback method for PumpDisplayData status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpDisplayDataStatusCallback(IRequest<?> request, PumpDisplayData result) {
        mDataStorage.setPumpDisplayData(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getBatteryVoltageCallback(IRequest<?> request, int result) {
        mDataStorage.setBatteryVoltage(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getCpuTemperatureCallback(IRequest<?> request, int result) {
        mDataStorage.setCpuTemperature(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getUniqueIdentifierCallback(IRequest<?> request, String result) {
        mDataStorage.setUniqueIdentifier(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getFirmwareInformationCallback(IRequest<?> request, FirmwareInformation result) {
        mDataStorage.setFirmwareInformation(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getMeasurementUnitsCallback(IRequest<?> request, MeasurementUnits result) {
        mDataStorage.setMeasurementUnits(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void restartCallback(IRequest<?> request, boolean result) {
        mDataStorage.setRestart(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getConfigurationIdentifierCallback(IRequest<?> request, String result) {
        mDataStorage.setConfigurationIdentifier(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getDateTimeCallback(IRequest<?> request, DateTimeSettings result) {
        mDataStorage.setDateTime(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setDateTimeCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetDateTime(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPtsNetworkSettingsCallback(IRequest<?> request, PtsNetworkSettings result) {
        mDataStorage.setPtsNetworkSettings(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPtsNetworkSettingsCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetPtsNetworkSettings(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getSystemDecimalDigitsCallback(IRequest<?> request, SystemDecimalDigits result) {
        mDataStorage.setSystemDecimalDigits(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setSystemDecimalDigitsCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetSystemDecimalDigits(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getParameterCallback(IRequest<?> request, Parameter result) {
        mDataStorage.setParameter(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setParameterCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetParameter(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPumpsConfigurationCallback(IRequest<?> request, PumpsConfiguration result) {
        mDataStorage.setPumpsConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPumpsConfigurationCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetPumpsConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getProbesConfigurationCallback(IRequest<?> request, ProbesConfiguration result) {
        mDataStorage.setProbesConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setProbesConfigurationCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetProbesConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getReadersConfigurationCallback(IRequest<?> request, ReadersConfiguration result) {
        mDataStorage.setReadersConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="result">True if successfully</param>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setReadersConfigurationCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetReadersConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPriceBoardsConfigurationCallback(IRequest<?> request, PriceBoardsConfiguration result) {
        mDataStorage.setPriceBoardsConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPriceBoardsConfigurationCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetPriceBoardsConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getFuelGradesConfigurationCallback(IRequest<?> request, ArrayList<FuelGrade> result) {
        mDataStorage.setFuelGradesConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setFuelGradesConfigurationCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetFuelGradesConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPumpNozzlesConfigurationCallback(IRequest<?> request, ArrayList<PumpNozzles> result) {
        mDataStorage.setPumpNozzlesConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPumpNozzlesConfigurationCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetPumpNozzlesConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTanksConfigurationCallback(IRequest<?> request, ArrayList<Tank> result) {
        mDataStorage.setTanksConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setTanksConfigurationCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetTanksConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getUsersConfigurationCallback(IRequest<?> request, ArrayList<User> result) {
        mDataStorage.setUsersConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setUsersConfigurationCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetUsersConfiguration(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetStatusCallback(IRequest<?> request, PumpStatusBase result) {
        mDataStorage.setPumpGetStatus(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpAuthorizeCallback(IRequest<?> request, PumpAuthorizeConfirmation result) {
        mDataStorage.setPumpAuthorize(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetTransactionInformationCallback(IRequest<?> request, PumpTransactionInformation result) {
        mDataStorage.setPumpGetTransactionInformation(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpStopCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpStop(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpEmergencyStopCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpEmergencyStop(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSuspendCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpSuspend(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpResumeCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpResume(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpCloseTransactionCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpCloseTransaction(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetTotalsCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpGetTotals(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetPricesCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpGetPrices(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSetPricesCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpSetPrices(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetDisplayDataCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpGetDisplayData(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetTagCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpGetTag(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTagInformationCallback(IRequest<?> request, TagInformation result) {
        mDataStorage.setTagInformation(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTagsListCallback(IRequest<?> request, ArrayList<TagInformation> result) {
        mDataStorage.setTagsList(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setTagsListCallback(IRequest<?> request, boolean result) {
        mDataStorage.setSetTagsList(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void addTagsToListCallback(IRequest<?> request, boolean result) {
        mDataStorage.setAddTagsToList(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTagsTotalNumberCallback(IRequest<?> request, Integer result) {
        mDataStorage.setTagsTotalNumber(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void readerGetTagCallback(IRequest<?> request, ReaderTag result) {
        mDataStorage.setReaderGetTag(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSetLightsCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpSetLights(result);
    }

    /// <summary>
    /// Callback method for command
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetAutomaticOperationCallback(IRequest<?> request, PumpAutomaticOperation result) {
        mDataStorage.setPumpGetAutomaticOperation(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSetAutomaticOperationCallback(IRequest<?> request, boolean result) {
        mDataStorage.setPumpSetAutomaticOperation(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void probeGetMeasurementsCallback(IRequest<?> request, ProbeMeasurements result) {
        mDataStorage.setProbeGetMeasurements(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void probeGetTankVolumeForHeightCallback(IRequest<?> request, ProbeTankVolumeForHeight result) {
        mDataStorage.setProbeGetTankVolumeForHeight(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getGpsDataCallback(IRequest<?> request, GpsData result) {
        mDataStorage.setGpsData(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void reportGetPumpTransactionsCallback(IRequest<?> request, ArrayList<ReportPumpTransaction> result) {
        mDataStorage.setReportGetPumpTransactions(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void reportGetTankMeasurementsCallback(IRequest<?> request, ArrayList<ReportTankMeasurement> result) {
        mDataStorage.setReportGetTankMeasurements(result);
    }

    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void reportGetInTankDeliveriesCallback(IRequest<?> request, ArrayList<ReportInTankDelivery> result) {
        mDataStorage.setReportGetInTankDeliveries(result);
    }
}