package com.technotrade.pts2.pts2testapp.statemachine.states;

import com.technotrade.pts2.pts2testapp.statemachine.BaseState;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;

import java.util.ArrayList;
import java.util.List;

public class FuelingState extends BaseState {
	public static final String NAME = "FuelingState";

	public FuelingState() {
		super(NAME);
	}

	@Override
	public boolean onStart(BaseState stateFrom, StateData stateData) {
		boolean bRes = super.onStart(stateFrom, stateData);
		if (!bRes) {
			return bRes;
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
		possibleNextStates.add(PumpNotSelectedState.NAME);
		possibleNextStates.add(PumpSelectedState.NAME);
		possibleNextStates.add(FuelingState.NAME);
		possibleNextStates.add(StoppingState.NAME);

		return possibleNextStates;
	}
}
