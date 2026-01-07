package com.technotrade.pts2.pts2testapp.statemachine.states;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showError;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.PTSManager;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;
import com.technotrade.pts2.pts2testapp.helper.LogHelper;
import com.technotrade.pts2.pts2testapp.statemachine.BaseState;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;

import java.util.ArrayList;
import java.util.List;

public class AuthorizingState extends BaseState {
	public static final String NAME = "AuthorizingState";

	public AuthorizingState() {
		super(NAME);
	}

	@Override
	public boolean onStart(BaseState stateFrom, StateData stateData) {
		boolean bRes = super.onStart(stateFrom, stateData);
		if (!bRes) {
			return bRes;
		}

		BaseViewModel viewModel = stateData.getViewModel();
		PumpItem pumpItem = stateData.getPumpItem();

		if(pumpItem.getStatus() == PumpStatus.FILLING) {
			String sError = getResourceString(R.string.the_pump_is_working_now);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
		Order order = orderManager.getConstructingOrder();

		// checking that pump is not in busy state
		Order formedOrderIfExist = orderManager.getFormedOrderForPump(order.getPump());

		if(formedOrderIfExist != null) {
			String sError = getResourceString(R.string.the_pump_is_already_processing_an_order);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		if (!order.isPumpSet() || !order.isNozzleSet()) {
			String sError = getResourceString(R.string.select_the_pump_and_nozzle_first);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		// if selected pump and nozzle only - its a full tank mode.

		PTSManager ptsManager = ApplicationFacade.getInstance().getPTSManager();

		Result result = ptsManager.pumpAuthorize(order);

		if(result != Result.SUCCESS) {
			String sError = Result.getDescription(result);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

		orderManager.addFormedOrder(order);

		pumpItem.setProgress(0);

		return true;
	}

	@Override
	public boolean onEnd(BaseState stateTo, StateData stateData) {
		boolean bRes = super.onEnd(stateTo, stateData);
		if (!bRes) {
			return bRes;
		}

		return true;
	}

	@Override
	public List<String> getPossibleNextStates() {
		List<String> possibleNextStates = new ArrayList<>();
		possibleNextStates.add(PumpNotSelectedState.NAME);
		possibleNextStates.add(PumpSelectedState.NAME);
		possibleNextStates.add(NozzleSelectedState.NAME);
		possibleNextStates.add(FuelingState.NAME);
		possibleNextStates.add(StoppingState.NAME);

		return possibleNextStates;
	}
}
