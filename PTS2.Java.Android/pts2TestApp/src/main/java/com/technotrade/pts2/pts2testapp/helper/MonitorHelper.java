package com.technotrade.pts2.pts2testapp.helper;

import com.technotrade.pts2.datastructs.Pump;
import com.technotrade.pts2.datastructs.PumpNozzles;
import com.technotrade.pts2.datastructs.PumpsConfiguration;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.ResourceManager;
import com.technotrade.pts2.pts2testapp.helper.converter.MonitorInputRepresentation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MonitorHelper {
    @FunctionalInterface
    public interface CompletionHandler<T> {
        void onComplete(boolean result, String sError, T parsedValue);
    }

    private final ResourceManager mResourceManager;

    public MonitorHelper() {
        ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
        mResourceManager = applicationFacade.getResourceManager();
    }

    public boolean parsePump(String monitorInput, CompletionHandler<Integer> handler) {

        MonitorInputRepresentation<Integer> monitorInputRepresentation = new MonitorInputRepresentation<Integer>(monitorInput, Integer.class);
        boolean bRes = monitorInputRepresentation.representValue();
        if (!bRes) {
            String sError = mResourceManager.getResourceString(R.string.the_input_must_be_an_integer_value_during_pump_selection);
            LogHelper.logDebug(sError);
            handler.onComplete(false, sError, null);

            return false;
        }

        int pumpNumber = monitorInputRepresentation.getRepresentedInput();

        if (pumpNumber == 0) {
            String sError = mResourceManager.getResourceString(R.string.the_pump_with_entered_number_not_configured);
            LogHelper.logDebug(sError);
            handler.onComplete(false, sError, pumpNumber);

            return false;
        }

        PumpsConfiguration pumpsConfiguration = ApplicationFacade.getInstance().getPTSManager().getDataStorage().getPumpsConfiguration();
        ArrayList<Pump> pumps = pumpsConfiguration.getPumps();

        List<Pump> found = pumps.stream()
            .filter(obj -> obj.getId() == pumpNumber)
            .collect(Collectors.toList());

        if (found.isEmpty()) {
            String sError = mResourceManager.getResourceString(R.string.the_pump_with_entered_number_not_configured);
            LogHelper.logDebug(sError);
            handler.onComplete(false, sError, null);

            return false;
        }
        else {
            handler.onComplete(true, null, pumpNumber);

            return true;
        }
    }

    public boolean parseNozzleForPump(String monitorInput, Pump pump, CompletionHandler<Integer> handler) {

        MonitorInputRepresentation<Integer> monitorInputRepresentation = new MonitorInputRepresentation<Integer>(monitorInput, Integer.class);
        boolean bRes = monitorInputRepresentation.representValue();
        if (!bRes) {
            String sError = mResourceManager.getResourceString(R.string.the_input_must_be_an_integer_value_during_nozzle_selection);
            LogHelper.logDebug(sError);
            handler.onComplete(false, sError, null);

            return false;
        }

        int nozzleNumber = monitorInputRepresentation.getRepresentedInput();

        if (nozzleNumber == 0) {
            String sError = mResourceManager.getResourceString(R.string.the_chosen_nozzle_is_not_configured);
            LogHelper.logDebug(sError);
            handler.onComplete(false, sError, nozzleNumber);

            return false;
        }

        List<PumpNozzles> pumpNozzlesConfiguration = ApplicationFacade.getInstance().getPTSManager().getDataStorage().getPumpNozzlesConfiguration();

        Optional<PumpNozzles> foundPumpNozzles = pumpNozzlesConfiguration.stream()
            .filter(item -> item.getPumpId() == pump.getId())
            .findFirst();

        if (foundPumpNozzles.isEmpty()) {
            String sError = mResourceManager.getResourceString(R.string.the_chosen_nozzle_is_not_configured);
            LogHelper.logDebug(sError);
            handler.onComplete(false, sError, null);

            return false;
        }

        PumpNozzles pumpNozzles = foundPumpNozzles.get();
        List<Integer> fuelGradeIds = pumpNozzles.getFuelGradeIds();

        if (fuelGradeIds.size() <= nozzleNumber || fuelGradeIds.get(nozzleNumber - 1) == 0) {
            String sError = mResourceManager.getResourceString(R.string.the_chosen_nozzle_is_not_configured);
            LogHelper.logDebug(sError);
            handler.onComplete(false, sError, null);

            return false;
        }

        handler.onComplete(true, null, nozzleNumber);

        return true;
    }

    public boolean parseQuantity(String monitorInput, CompletionHandler<Integer> handler) {

        MonitorInputRepresentation<Integer> monitorInputRepresentation = new MonitorInputRepresentation<Integer>(monitorInput, Integer.class);
        boolean bRes = monitorInputRepresentation.representValue();
        if (!bRes) {
            String sError = mResourceManager.getResourceString(R.string.the_input_must_be_an_integer_value_during_quantity_selection);
            LogHelper.logDebug(sError);
            handler.onComplete(false, sError, null);

            return false;
        }

        int quantity = monitorInputRepresentation.getRepresentedInput();

        handler.onComplete(true, null, quantity);

        return true;
    }

    public boolean parseCurrency(String monitorInput, CompletionHandler<BigDecimal> handler) {

        MonitorInputRepresentation<BigDecimal> monitorInputRepresentation = new MonitorInputRepresentation<BigDecimal>(monitorInput, BigDecimal.class);
        boolean bRes = monitorInputRepresentation.representValue();
        if (!bRes) {
            String sError = mResourceManager.getResourceString(R.string.the_input_must_be_an_decimal_value_during_amount_selection);
            LogHelper.logDebug(sError);
            handler.onComplete(false, sError, null);

            return false;
        }

        BigDecimal amount = monitorInputRepresentation.getRepresentedInput();

        handler.onComplete(true, null, amount);

        return true;
    }
}