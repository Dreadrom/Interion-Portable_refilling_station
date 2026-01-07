package com.technotrade.pts2.pts2testapp.statemachine.states;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showError;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.DataStorage;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;
import com.technotrade.pts2.pts2testapp.helper.LogHelper;
import com.technotrade.pts2.pts2testapp.helper.MonitorHelper;
import com.technotrade.pts2.pts2testapp.statemachine.BaseState;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PumpSelectedState extends BaseState {
	public static final String NAME = "PumpSelectedState";

	public PumpSelectedState() {
		super(NAME);
	}

	@Override
	public boolean onStart(BaseState stateFrom, StateData stateData) {
		boolean bRes = super.onStart(stateFrom, stateData);
		if (!bRes) {
			return bRes;
		}

		final String sMonitorText = stateData.getText();
		final BaseViewModel viewModel = stateData.getViewModel();
		final DataStorage dataStorage = ApplicationFacade.getInstance().getPTSManager().getDataStorage();
		final MonitorHelper monitorHelper = new MonitorHelper();
		final AtomicInteger pump = new AtomicInteger();

		CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() ->
			monitorHelper.parsePump(sMonitorText, (result, sError, parsedValue) -> {
				if (!result) {
					viewModel.sendViewModelCommandEvent(showError.toString(), sError);
				} else {
					pump.set(parsedValue);
				}
			}
		));

		try {
			bRes = future.get();
		} catch (InterruptedException | ExecutionException e) {
			bRes = false;
		}

		if (!bRes) {
			return bRes;
		}

		List<PumpItem> pumpItems = dataStorage.getPumpItems();
		assert pumpItems != null;
		List<PumpItem> found = pumpItems.stream()
			.filter(obj -> obj.getNumber() == pump.get())
			.collect(Collectors.toList());

		if (found.isEmpty()) {
			String sError = getResourceString(R.string.state_machine_error);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
		}

		PumpItem pumpItem = found.get(0);

		if (pumpItem.getStatus() == PumpStatus.OFFLINE) {
			String sError = getResourceString(R.string.the_pump_is_offline);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		if (pumpItem.getStatus() == PumpStatus.NONE) {
			String sError = getResourceString(R.string.the_pump_is_undefined);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
		orderManager.createConstructingFuelOrder(pumpItem);

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

		if(stateTo.getName().equals(PumpNotSelectedState.NAME)) {
			order.resetPump();
		}

		order.resetNozzle();
		order.resetQuantity();
		order.resetAmount();
		order.resetFullTank();

		return true;
	}

	@Override
	public List<String> getPossibleNextStates() {
		List<String> possibleNextStates = new ArrayList<>();
		possibleNextStates.add(IdleState.NAME);
		possibleNextStates.add(PumpNotSelectedState.NAME);
		possibleNextStates.add(PumpSelectedState.NAME);
		possibleNextStates.add(NozzleSelectedState.NAME);
		possibleNextStates.add(StoppingState.NAME);
		possibleNextStates.add(FuelingState.NAME);

		return possibleNextStates;
	}
}
