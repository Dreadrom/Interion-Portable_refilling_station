package com.technotrade.pts2.pts2testapp.statemachine.states;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showError;

import com.technotrade.pts2.datastructs.Pump;
import com.technotrade.pts2.datastructs.PumpsConfiguration;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;
import com.technotrade.pts2.pts2testapp.helper.LogHelper;
import com.technotrade.pts2.pts2testapp.helper.MonitorHelper;
import com.technotrade.pts2.pts2testapp.statemachine.BaseState;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NozzleSelectedState extends BaseState {
	public static final String NAME = "ChooseNozzleState";

	public NozzleSelectedState() {
		super(NAME);
	}

	@Override
	public boolean onStart(BaseState stateFrom, StateData stateData) {
		boolean bRes = super.onStart(stateFrom, stateData);
		if (!bRes) {
			return false;
		}

		String sMonitorText = stateData.getText();
		BaseViewModel viewModel = stateData.getViewModel();

		OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
		Order order = orderManager.getConstructingOrder();

		// Check pump is set
		if (order == null || !order.isPumpSet() || order.getPump() == null) {
			String sError = getResourceString(R.string.select_the_pump_first);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		PumpItem pumpItem = order.getPump();
		PumpsConfiguration pumpsConfiguration =
			ApplicationFacade.getInstance().getPTSManager().getDataStorage().getPumpsConfiguration();

		Pump pump = null;
		if (pumpsConfiguration != null && pumpsConfiguration.getPumps() != null) {
			for (Pump candidate : pumpsConfiguration.getPumps()) {
				if (candidate != null && candidate.getId() == pumpItem.getNumber()) {
					if (pump != null) {
						// found more than one
						pump = null;
						break;
					}
					pump = candidate;
				}
			}
		}

		if (pump == null) {
			String sError = getResourceString(R.string.wrong_pump_during_choosing_a_nozzle);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		MonitorHelper monitorHelper = new MonitorHelper();
		AtomicInteger nozzleNumber = new AtomicInteger(-1);
		AtomicBoolean parseOk = new AtomicBoolean(false);

		monitorHelper.parseNozzleForPump(sMonitorText, pump, (result, sError, parsedValue) -> {
			if (!result) {
				viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			} else {
				nozzleNumber.set(parsedValue);
				parseOk.set(true);
			}
		});

		if (!parseOk.get()) {
			return false;
		}

		List<NozzleItem> nozzleItems =
			ApplicationFacade.getInstance().getPTSManager().getDataStorage().getNozzleItems();

		if (nozzleItems == null || nozzleItems.isEmpty()) {
			String sError = getResourceString(R.string.the_chosen_nozzle_is_not_configured);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		NozzleItem nozzleItem = null;
		for (NozzleItem candidate : nozzleItems) {
			if (candidate != null && candidate.getNozzleNumber() == nozzleNumber.get()) {
				nozzleItem = candidate;
				break;
			}
		}

		if (nozzleItem == null) {
			String sError = getResourceString(R.string.the_chosen_nozzle_is_not_configured);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		order.setNozzle(nozzleItem);

		return true;
	}

	@Override
	public boolean onEnd(BaseState stateTo, StateData stateData) {
		boolean bRes = super.onEnd(stateTo, stateData);
		if (!bRes) {
			return bRes;
		}

		OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
		Order order = orderManager.getConstructingOrder();

		if(stateTo.getName().equals(PumpSelectedState.NAME)) {
			order.resetNozzle();
		}

		order.resetQuantity();
		order.resetAmount();
		order.resetFullTank();

		return true;
	}

	@Override
	public List<String> getPossibleNextStates() {
		List<String> possibleNextStates = new ArrayList<>();
		possibleNextStates.add(PumpNotSelectedState.NAME);
		possibleNextStates.add(PumpSelectedState.NAME);
		possibleNextStates.add(NozzleSelectedState.NAME);
		possibleNextStates.add(QuantitySelectedState.NAME);
		possibleNextStates.add(CurrencySelectedState.NAME);
		possibleNextStates.add(StoppingState.NAME);
		possibleNextStates.add(IdleState.NAME);
		possibleNextStates.add(FuelingState.NAME);

		return possibleNextStates;
	}
}
