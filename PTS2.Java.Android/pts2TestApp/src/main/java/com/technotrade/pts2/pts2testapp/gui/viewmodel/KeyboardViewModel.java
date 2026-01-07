package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showOrderConfirmation;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.PTSManager;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;
import com.technotrade.pts2.pts2testapp.statemachine.StateMachine;
import com.technotrade.pts2.pts2testapp.statemachine.states.CurrencySelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.QuantitySelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.StoppingState;

public class KeyboardViewModel extends BaseViewModel {

    private final MutableLiveData<String> mMonitor;
    private final StateMachine mStateMachine;
    private final PTSManager mPTSManager;

    public KeyboardViewModel() {
        mMonitor = new MutableLiveData<>("0");
        mPTSManager = ApplicationFacade.getInstance().getPTSManager();
        mStateMachine = ApplicationFacade.getInstance().getStateMachine();
    }

    public MutableLiveData<String> getMonitor() {
        return mMonitor;
    }

    public void onButtonFullTankClicked(View view) {
        StateData stateData = prepareStateData();
        stateData.setFullTank(true);

        boolean bRes = mStateMachine.transition(new QuantitySelectedState(), stateData);

        if (bRes) {
            clearMonitor();
            sendViewModelCommandEvent(showOrderConfirmation.toString(), null);
        }
    }

    public void onButtonEnterVolumeClicked(View view) {
        StateData stateData = prepareStateData();

        boolean bRes = mStateMachine.transition(new QuantitySelectedState(), stateData);

        if (bRes) {
            clearMonitor();
            sendViewModelCommandEvent(showOrderConfirmation.toString(), null);
        }
    }

    public void onButtonEnterAmountClicked(View view) {
        StateData stateData = prepareStateData();

        boolean bRes = mStateMachine.transition(new CurrencySelectedState(), stateData);

        if (bRes) {
            clearMonitor();
            sendViewModelCommandEvent(showOrderConfirmation.toString(), null);
        }
    }

    public void onButtonClearClicked(View view) {
        StateData stateData = prepareStateData();

        boolean bRes = mStateMachine.transition(new StoppingState(), stateData);

        clearMonitor();
    }

    public void stopSelectedPump(PumpItem pumpItem) {
        StateData stateData = prepareStateData();
        stateData.setPumpItem(pumpItem);

        boolean bRes = mStateMachine.transition(new StoppingState(), stateData);

        if (bRes) {
            clearMonitor();
        }
    }

    public void onButtonOneClicked(View view) {
        addCharacter('1');
    }

    public void onButtonTwoClicked(View view) {
        addCharacter('2');
    }

    public void onButtonThreeClicked(View view) {
        addCharacter('3');
    }

    public void onButtonFourClicked(View view) {
        addCharacter('4');
    }

    public void onButtonFiveClicked(View view) {
        addCharacter('5');
    }

    public void onButtonSixClicked(View view) {
        addCharacter('6');
    }

    public void onButtonSevenClicked(View view) {
        addCharacter('7');
    }

    public void onButtonEightClicked(View view) {
        addCharacter('8');
    }

    public void onButtonNineClicked(View view) {
        addCharacter('9');
    }

    public void onButtonZeroClicked(View view) {
        addCharacter('0');
    }

    public void onButtonTwoZerosClicked(View view) {
        addCharacters("00");
    }

    public void onButtonDotClicked(View view) {
        addCharacter('.');
    }

    public void onButtonBackspaceClicked(View view) {
        clearLastCharacter();
    }

    private void addCharacter(char charakter) {
        if (!"0123456789.".contains(String.valueOf(charakter))) {
            return;
        }

        String curText = mMonitor.getValue();
        assert curText != null;

        if (charakter == '.' && curText.contains(".")) {
            return;
        }

        if (curText.equals(String.valueOf(0))) {
            curText = "";
        }

        curText += charakter;
        mMonitor.setValue(curText);
    }

    private void addCharacters(String charakters) {
        for (int i = 0; i < charakters.length(); ++i) {
            addCharacter(charakters.charAt(i));
        }
    }

    private void clearLastCharacter() {
        String curText = mMonitor.getValue();
        assert curText != null;

        curText = curText.substring(0, curText.length() - 1);

        if (curText.isEmpty()) {
            curText = String.valueOf(0);
        }

        mMonitor.postValue(curText);
    }

    public void clearMonitor() {
        mMonitor.postValue(String.valueOf(0));
    }

    private StateData prepareStateData() {
        StateData stateData = new StateData();
        stateData.setText(mMonitor.getValue());
        stateData.setViewModel(this);
        stateData.setPumpItem(mPTSManager.getDataStorage().getSelectedPump());

        return stateData;
    }

    @Override
    public void clear() {
        clearMonitor();
    }
}