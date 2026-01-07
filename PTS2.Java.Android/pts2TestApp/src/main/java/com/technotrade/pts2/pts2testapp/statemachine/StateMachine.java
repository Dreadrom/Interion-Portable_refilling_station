package com.technotrade.pts2.pts2testapp.statemachine;

import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.util.Logger;

/// <summary>
/// State machine class
/// </summary>
public class StateMachine {
    public StateMachine() {

    }

    public boolean transition(BaseState stateTo, StateData stateData) {

        PumpItem pumpItem = stateData.getPumpItem();

        if(pumpItem == null) {
            return false;
        }

        BaseState stateFrom = pumpItem.getState();

        boolean bres = stateFrom.getPossibleNextStates().contains(stateTo.getName());
        if (!bres) {
            return bres;
        }

        bres = stateFrom.onEnd(stateTo, stateData);
        if (!bres) {
            return bres;
        }

        bres = stateTo.onStart(stateFrom, stateData);
        if (!bres) {
            return bres;
        }

        pumpItem.setState(stateTo);

        Logger.debug("Switched to: " + stateTo.getName());

        return true;
    }
}