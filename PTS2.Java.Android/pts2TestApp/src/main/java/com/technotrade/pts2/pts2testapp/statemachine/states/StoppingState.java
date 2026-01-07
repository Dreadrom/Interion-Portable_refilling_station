package com.technotrade.pts2.pts2testapp.statemachine.states;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showError;

import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.PTSManager;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;
import com.technotrade.pts2.pts2testapp.helper.LogHelper;
import com.technotrade.pts2.pts2testapp.statemachine.BaseState;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;

import java.util.ArrayList;
import java.util.List;

public class StoppingState extends BaseState {
	public static final String NAME = "StoppingState";

	public StoppingState() {
		super(NAME);
	}

	@Override
	public boolean onStart(BaseState stateFrom, StateData stateData) {
		boolean bRes = super.onStart(stateFrom, stateData);
		if (!bRes) {
			return bRes;
		}

		PumpItem pumpItem = stateData.getPumpItem();

		if(pumpItem.getState().getName().equals(IdleState.NAME)) {
			return false;
		}

		BaseViewModel viewModel = stateData.getViewModel();
		PTSManager ptsManager = ApplicationFacade.getInstance().getPTSManager();
		OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
		Order order = orderManager.getFormedOrderForPump(stateData.getPumpItem());

		if(order == null) {
			order = new Order();
			PumpItem orderPumpItem = new PumpItem();
			orderPumpItem.setNumber(stateData.getPumpItem().getNumber());
			order.setPump(orderPumpItem);
		}

		Result result = ptsManager.pumpStop(order);

		if(result != Result.SUCCESS) {
			String sError = Result.getDescription(result);
			LogHelper.logError(sError);
			viewModel.sendViewModelCommandEvent(showError.toString(), sError);
			return false;
		}

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
		possibleNextStates.add(IdleState.NAME);
		possibleNextStates.add(FuelingState.NAME);

		return possibleNextStates;
	}
}
