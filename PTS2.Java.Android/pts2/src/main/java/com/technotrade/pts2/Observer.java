package com.technotrade.pts2;

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
public class Observer implements IObserver {
    /// <summary>
    /// Callback method for PumpIdleStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpIdleStatusCallback(IRequest<?> request, PumpIdleStatus result) {}
    /// <summary>
    /// Callback method for PumpFillingStatus status request
    /// </summary>
    /// <param name="request">IRequest<?><?> instance of this request</param>
    @Override
    public void pumpFillingStatusCallback(IRequest<?> request, PumpFillingStatus result) {}
    /// <summary>
    /// Callback method for PumpEndOfTransactionStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpEndOfTransactionStatusCallback(IRequest<?> request, PumpEndOfTransactionStatus result) {}
    /// <summary>
    /// Callback method for PumpOfflineStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpOfflineStatusCallback(IRequest<?> request, PumpOfflineStatus result) {}
    /// <summary>
    /// Callback method for PumpTotals status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpTotalsStatusCallback(IRequest<?> request, PumpTotals result) {}
    /// <summary>
    /// Callback method for PumpPrices status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpPricesStatusCallback(IRequest<?> request, PumpPrices result) {}
    /// <summary>p
    /// Callback method for PumpTag status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpTagStatusCallback(IRequest<?> request, PumpTag result) {}
    /// <summary>
    /// Callback method for PumpDisplayData status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpDisplayDataStatusCallback(IRequest<?> request, PumpDisplayData result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getBatteryVoltageCallback(IRequest<?> request, int result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getCpuTemperatureCallback(IRequest<?> request, int result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getUniqueIdentifierCallback(IRequest<?> request, String result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getFirmwareInformationCallback(IRequest<?> request, FirmwareInformation result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getMeasurementUnitsCallback(IRequest<?> request, MeasurementUnits result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void restartCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getConfigurationIdentifierCallback(IRequest<?> request, String result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getDateTimeCallback(IRequest<?> request, DateTimeSettings result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setDateTimeCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPtsNetworkSettingsCallback(IRequest<?> request, PtsNetworkSettings result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPtsNetworkSettingsCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getSystemDecimalDigitsCallback(IRequest<?> request, SystemDecimalDigits result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setSystemDecimalDigitsCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getParameterCallback(IRequest<?> request, Parameter result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setParameterCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPumpsConfigurationCallback(IRequest<?> request, PumpsConfiguration result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPumpsConfigurationCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getProbesConfigurationCallback(IRequest<?> request, ProbesConfiguration result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setProbesConfigurationCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getReadersConfigurationCallback(IRequest<?> request, ReadersConfiguration result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="result">True if successfully</param>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setReadersConfigurationCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPriceBoardsConfigurationCallback(IRequest<?> request, PriceBoardsConfiguration result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPriceBoardsConfigurationCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getFuelGradesConfigurationCallback(IRequest<?> request, ArrayList<FuelGrade> result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setFuelGradesConfigurationCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPumpNozzlesConfigurationCallback(IRequest<?> request, ArrayList<PumpNozzles> result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPumpNozzlesConfigurationCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTanksConfigurationCallback(IRequest<?> request, ArrayList<Tank> result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setTanksConfigurationCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getUsersConfigurationCallback(IRequest<?> request, ArrayList<User> result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setUsersConfigurationCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetStatusCallback(IRequest<?> request, PumpStatusBase result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpAuthorizeCallback(IRequest<?> request, PumpAuthorizeConfirmation result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetTransactionInformationCallback(IRequest<?> request, PumpTransactionInformation result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpStopCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpEmergencyStopCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSuspendCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpResumeCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpCloseTransactionCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetTotalsCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetPricesCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSetPricesCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetDisplayDataCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetTagCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTagInformationCallback(IRequest<?> request, TagInformation result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTagsListCallback(IRequest<?> request, ArrayList<TagInformation> result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setTagsListCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void addTagsToListCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTagsTotalNumberCallback(IRequest<?> request, Integer result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void readerGetTagCallback(IRequest<?> request, ReaderTag result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSetLightsCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetAutomaticOperationCallback(IRequest<?> request, PumpAutomaticOperation result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSetAutomaticOperationCallback(IRequest<?> request, boolean result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void probeGetMeasurementsCallback(IRequest<?> request, ProbeMeasurements result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void probeGetTankVolumeForHeightCallback(IRequest<?> request, ProbeTankVolumeForHeight result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getGpsDataCallback(IRequest<?> request, GpsData result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void reportGetPumpTransactionsCallback(IRequest<?> request, ArrayList<ReportPumpTransaction> result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void reportGetTankMeasurementsCallback(IRequest<?> request, ArrayList<ReportTankMeasurement> result) {}
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void reportGetInTankDeliveriesCallback(IRequest<?> request, ArrayList<ReportInTankDelivery> result) {}
}