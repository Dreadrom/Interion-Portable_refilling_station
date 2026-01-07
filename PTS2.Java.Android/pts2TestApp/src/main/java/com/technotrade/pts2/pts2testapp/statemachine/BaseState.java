package com.technotrade.pts2.pts2testapp.statemachine;

import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.ResourceManager;

import java.util.List;

public class BaseState {
	private final String mStateName;
	private StateData mStateData;
	protected ResourceManager mResourceManager;

	public BaseState(String stateName) {
		mStateName = stateName;

		mResourceManager = ApplicationFacade.getInstance().getResourceManager();
	}

	public boolean onStart(BaseState stateFrom, StateData stateData) {
		mStateData = stateData;

		return true;
	}

	public boolean onEnd(BaseState stateTo, StateData stateData) {
		List<String> possibleNextStates = getPossibleNextStates();
		if (possibleNextStates != null && !possibleNextStates.isEmpty()) {
			return possibleNextStates.contains(stateTo.getName());
		}

		return true;
	}

	public String getName() {
		return mStateName;
	}

	/// <summary>
	/// Returns the list of possible states to move
	/// </summary>
	/// <param name="request">IRequest<?> instance of this request</param>
	/// <returns>List of names of states.
	//  If list is empty - any state is possible
	//  If list is null - there is no possibility for this state to switch forward but only backward</returns>
	public List<String> getPossibleNextStates() {
		return null;
	}

	protected String getResourceString(int resId) {
		return mResourceManager.getResourceString(resId);
	}
}
