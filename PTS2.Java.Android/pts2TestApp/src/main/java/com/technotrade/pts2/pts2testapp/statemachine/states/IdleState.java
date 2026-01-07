package com.technotrade.pts2.pts2testapp.statemachine.states;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.statemachine.BaseState;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;

import java.util.ArrayList;
import java.util.List;

public class IdleState extends BaseState {
	public static final String NAME = "Idle";

	public IdleState() {
		super(NAME);
	}

	@Override
	public boolean onStart(BaseState stateFrom, StateData stateData) {
		boolean bRes = super.onStart(stateFrom, stateData);
		if (!bRes) {
			return bRes;
		}

		PumpItem pumpItem = stateData.getPumpItem();

		OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
		Order formedOrderIfExist = orderManager.getFormedOrderForPump(pumpItem);

		if(formedOrderIfExist != null) {
			orderManager.closeOrderForPump(pumpItem);
		}

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
		possibleNextStates.add(IdleState.NAME);
		possibleNextStates.add(PumpNotSelectedState.NAME);
		possibleNextStates.add(PumpSelectedState.NAME);
		possibleNextStates.add(NozzleSelectedState.NAME);
		possibleNextStates.add(QuantitySelectedState.NAME);
		possibleNextStates.add(CurrencySelectedState.NAME);
		possibleNextStates.add(AuthorizingState.NAME);
		possibleNextStates.add(FuelingState.NAME);
		possibleNextStates.add(StoppingState.NAME);

		return possibleNextStates;
	}
}
