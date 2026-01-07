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
import java.util.List;

/// <summary>
/// Observer main listener class to handle a request execution result data
/// </summary>
public class ObserverMainListener implements IObserver {

    private final List<IObserver> mObservers;

    ObserverMainListener(List<IObserver> observers) {
        mObservers = observers;
    }
    /// <summary>
    /// Callback method for PumpIdleStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpIdleStatusCallback(IRequest<?> request, PumpIdleStatus result) {
        for (IObserver observer : mObservers) {
            observer.pumpIdleStatusCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for PumpFillingStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpFillingStatusCallback(IRequest<?> request, PumpFillingStatus result) {
        for (IObserver observer : mObservers) {
            observer.pumpFillingStatusCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for PumpEndOfTransactionStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpEndOfTransactionStatusCallback(IRequest<?> request, PumpEndOfTransactionStatus result) {
        for (IObserver observer : mObservers) {
            observer.pumpEndOfTransactionStatusCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for PumpOfflineStatus status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpOfflineStatusCallback(IRequest<?> request, PumpOfflineStatus result) {
        for (IObserver observer : mObservers) {
            observer.pumpOfflineStatusCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for PumpTotals status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpTotalsStatusCallback(IRequest<?> request, PumpTotals result) {
        for (IObserver observer : mObservers) {
            observer.pumpTotalsStatusCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for PumpPrices status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpPricesStatusCallback(IRequest<?> request, PumpPrices result) {
        for (IObserver observer : mObservers) {
            observer.pumpPricesStatusCallback(request, result);
        }
    }
    /// <summary>p
    /// Callback method for PumpTag status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpTagStatusCallback(IRequest<?> request, PumpTag result) {
        for (IObserver observer : mObservers) {
            observer.pumpTagStatusCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for PumpDisplayData status request
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpDisplayDataStatusCallback(IRequest<?> request, PumpDisplayData result) {
        for (IObserver observer : mObservers) {
            observer.pumpDisplayDataStatusCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getBatteryVoltageCallback(IRequest<?> request, int result) {
        for (IObserver observer : mObservers) {
            observer.getBatteryVoltageCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getCpuTemperatureCallback(IRequest<?> request, int result) {
        for (IObserver observer : mObservers) {
            observer.getCpuTemperatureCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getUniqueIdentifierCallback(IRequest<?> request, String result) {
        for (IObserver observer : mObservers) {
            observer.getUniqueIdentifierCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getFirmwareInformationCallback(IRequest<?> request, FirmwareInformation result) {
        for (IObserver observer : mObservers) {
            observer.getFirmwareInformationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getMeasurementUnitsCallback(IRequest<?> request, MeasurementUnits result) {
        for (IObserver observer : mObservers) {
            observer.getMeasurementUnitsCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void restartCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.restartCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getConfigurationIdentifierCallback(IRequest<?> request, String result) {
        for (IObserver observer : mObservers) {
            observer.getConfigurationIdentifierCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getDateTimeCallback(IRequest<?> request, DateTimeSettings result) {
        for (IObserver observer : mObservers) {
            observer.getDateTimeCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setDateTimeCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setDateTimeCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPtsNetworkSettingsCallback(IRequest<?> request, PtsNetworkSettings result) {
        for (IObserver observer : mObservers) {
            observer.getPtsNetworkSettingsCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPtsNetworkSettingsCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setPtsNetworkSettingsCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getSystemDecimalDigitsCallback(IRequest<?> request, SystemDecimalDigits result) {
        for (IObserver observer : mObservers) {
            observer.getSystemDecimalDigitsCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setSystemDecimalDigitsCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setSystemDecimalDigitsCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getParameterCallback(IRequest<?> request, Parameter result) {
        for (IObserver observer : mObservers) {
            observer.getParameterCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setParameterCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setParameterCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPumpsConfigurationCallback(IRequest<?> request, PumpsConfiguration result) {
        for (IObserver observer : mObservers) {
            observer.getPumpsConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPumpsConfigurationCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setPumpsConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getProbesConfigurationCallback(IRequest<?> request, ProbesConfiguration result) {
        for (IObserver observer : mObservers) {
            observer.getProbesConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setProbesConfigurationCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setProbesConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getReadersConfigurationCallback(IRequest<?> request, ReadersConfiguration result) {
        for (IObserver observer : mObservers) {
            observer.getReadersConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="result">True if successfully</param>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setReadersConfigurationCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setReadersConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPriceBoardsConfigurationCallback(IRequest<?> request, PriceBoardsConfiguration result) {
        for (IObserver observer : mObservers) {
            observer.getPriceBoardsConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPriceBoardsConfigurationCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setPriceBoardsConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getFuelGradesConfigurationCallback(IRequest<?> request, ArrayList<FuelGrade> result) {
        for (IObserver observer : mObservers) {
            observer.getFuelGradesConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setFuelGradesConfigurationCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setFuelGradesConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getPumpNozzlesConfigurationCallback(IRequest<?> request, ArrayList<PumpNozzles> result) {
        for (IObserver observer : mObservers) {
            observer.getPumpNozzlesConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setPumpNozzlesConfigurationCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setPumpNozzlesConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTanksConfigurationCallback(IRequest<?> request, ArrayList<Tank> result) {
        for (IObserver observer : mObservers) {
            observer.getTanksConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setTanksConfigurationCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setTanksConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getUsersConfigurationCallback(IRequest<?> request, ArrayList<User> result) {
        for (IObserver observer : mObservers) {
            observer.getUsersConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setUsersConfigurationCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setUsersConfigurationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetStatusCallback(IRequest<?> request, PumpStatusBase result) {
        for (IObserver observer : mObservers) {
            observer.pumpGetStatusCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpAuthorizeCallback(IRequest<?> request, PumpAuthorizeConfirmation result) {
        for (IObserver observer : mObservers) {
            observer.pumpAuthorizeCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetTransactionInformationCallback(IRequest<?> request, PumpTransactionInformation result) {
        for (IObserver observer : mObservers) {
            observer.pumpGetTransactionInformationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpStopCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpStopCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpEmergencyStopCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpEmergencyStopCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSuspendCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpSuspendCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpResumeCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpResumeCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpCloseTransactionCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpCloseTransactionCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetTotalsCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpGetTotalsCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetPricesCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpGetPricesCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSetPricesCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpSetPricesCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetDisplayDataCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpGetDisplayDataCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetTagCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpGetTagCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTagInformationCallback(IRequest<?> request, TagInformation result) {
        for (IObserver observer : mObservers) {
            observer.getTagInformationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTagsListCallback(IRequest<?> request, ArrayList<TagInformation> result) {
        for (IObserver observer : mObservers) {
            observer.getTagsListCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void setTagsListCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.setTagsListCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void addTagsToListCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.addTagsToListCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getTagsTotalNumberCallback(IRequest<?> request, Integer result) {
        for (IObserver observer : mObservers) {
            observer.getTagsTotalNumberCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void readerGetTagCallback(IRequest<?> request, ReaderTag result) {
        for (IObserver observer : mObservers) {
            observer.readerGetTagCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSetLightsCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpSetLightsCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpGetAutomaticOperationCallback(IRequest<?> request, PumpAutomaticOperation result) {
        for (IObserver observer : mObservers) {
            observer.pumpGetAutomaticOperationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void pumpSetAutomaticOperationCallback(IRequest<?> request, boolean result) {
        for (IObserver observer : mObservers) {
            observer.pumpSetAutomaticOperationCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void probeGetMeasurementsCallback(IRequest<?> request, ProbeMeasurements result) {
        for (IObserver observer : mObservers) {
            observer.probeGetMeasurementsCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void probeGetTankVolumeForHeightCallback(IRequest<?> request, ProbeTankVolumeForHeight result) {
        for (IObserver observer : mObservers) {
            observer.probeGetTankVolumeForHeightCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void getGpsDataCallback(IRequest<?> request, GpsData result) {
        for (IObserver observer : mObservers) {
            observer.getGpsDataCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void reportGetPumpTransactionsCallback(IRequest<?> request, ArrayList<ReportPumpTransaction> result) {
        for (IObserver observer : mObservers) {
            observer.reportGetPumpTransactionsCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void reportGetTankMeasurementsCallback(IRequest<?> request, ArrayList<ReportTankMeasurement> result) {
        for (IObserver observer : mObservers) {
            observer.reportGetTankMeasurementsCallback(request, result);
        }
    }
    /// <summary>
    /// Callback method for command
    /// </summary>
    /// <param name="request">IRequest<?> instance of this request</param>
    @Override
    public void reportGetInTankDeliveriesCallback(IRequest<?> request, ArrayList<ReportInTankDelivery> result) {
        for (IObserver observer : mObservers) {
            observer.reportGetInTankDeliveriesCallback(request, result);
        }
    }
}