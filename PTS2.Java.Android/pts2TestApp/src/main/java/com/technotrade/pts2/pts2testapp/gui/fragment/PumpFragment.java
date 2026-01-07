package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentContainerView;
import androidx.transition.TransitionInflater;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.databinding.FragmentPumpBinding;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.enumeration.KeyboardType;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.PumpsViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand;
import com.technotrade.pts2.pts2testapp.helper.KeyboardHelper;
import com.technotrade.pts2.pts2testapp.statemachine.states.FuelingState;

public class PumpFragment extends BaseFragment<PumpsViewModel> {
    private FragmentPumpBinding mBinding = null;
    private KeyboardHelper mKeyboardHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentPumpBinding.inflate(inflater, container, false);

        mBinding.setViewModel(getViewModel());
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        assert getArguments() != null;
        String transitionName = getArguments().getString("TRANSITION_NAME");
        mBinding.cvPump.setTransitionName(transitionName);

        return mBinding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mKeyboardHelper = ApplicationFacade.getInstance().getKeyboardHelper();

        requireActivity().getOnBackPressedDispatcher().addCallback(
            this,  // LifecycleOwner
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {

                    if(mKeyboardHelper.getStackSize() == 1) {
                        // Last keyboard fragment left
                        // remove it and go back at outer fragments stack
                        mKeyboardHelper.popKeyboardFragment();
                        remove();
                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    }
                    else {
                        mKeyboardHelper.popKeyboardFragment();
                    }
                }
            }
        );

        setSharedElementEnterTransition(TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_element_transition));

        setSharedElementReturnTransition(TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_element_transition));
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentContainerView fKeyboard = mBinding.fKeyboard;

        fKeyboard.setAlpha(0f);

        ObjectAnimator fadeInKeyboard = ObjectAnimator.ofFloat(fKeyboard, "alpha", 0f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeInKeyboard);
        animatorSet.setDuration(300);
        animatorSet.start();

        PumpItem pumpItem = ApplicationFacade.getInstance().getPTSManager().getDataStorage().getSelectedPump();

        if(pumpItem.getStatus() == PumpStatus.FILLING) {
            mKeyboardHelper.showKeyboardFragment(R.id.fKeyboard, getChildFragmentManager(), KeyboardType.ORDERED);
        }
        else {
            mKeyboardHelper.showKeyboardFragment(R.id.fKeyboard, getChildFragmentManager(), KeyboardType.ORDER_IN_FORMATION);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected Class<PumpsViewModel> getViewModelClass() {
        return PumpsViewModel.class;
    }

    @Override
    protected boolean execModelViewCommand(EventCommand<?> eventCommand) {
        if (eventCommand == null) {
            return false;
        }

        String command = eventCommand.getCommand();
        Object data = eventCommand.getData();

        switch (ViewModelCommand.valueOf(command)) {
            case chooseNozzleWithList:
                chooseNozzleWithList();
                return true;
            case updateNozzlesForSelectedPumpItem:
                if (data instanceof PumpItem) {
                    getViewModel().updateNozzlesForSelectedPumpItem((PumpItem) data);
                    return true;
                }
                return false;
            case chooseVolumeWithKeyboard:
                chooseVolumeWithKeyboard();
                return true;
            case chooseAmountWithKeyboard:
                chooseAmountWithKeyboard();
                return true;
            case showOrderConfirmation:
                showOrderConfirmation();
                return true;
            case clearKeyboardStack:
                clearKeyboardStack();
                return true;
            case showOrdered:
                showOrdered();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected boolean onBackPressed() {
        getViewModel().setSelectedPump(null);

        return true;
    }

    public void chooseNozzleWithList() {
        ApplicationFacade.getInstance().getKeyboardHelper().hideAndRemoveKeyboard(getChildFragmentManager());

        Bundle args = new Bundle();
        NozzleItem nozzleItem = getViewModel().getTwoWayFields().getSelectedNozzle().getValue();

        if(nozzleItem != null) {
            args.putSerializable("NOZZLE_ITEM", nozzleItem);
        }

        mNavController.navigate(R.id.action_pumpFragment_to_nozzlesFragment, args);
    }

    public void chooseVolumeWithKeyboard() {
        mKeyboardHelper.showKeyboardFragment(R.id.fKeyboard, getChildFragmentManager(), KeyboardType.VOLUME);
    }

    public void chooseAmountWithKeyboard() {
        mKeyboardHelper.showKeyboardFragment(R.id.fKeyboard, getChildFragmentManager(), KeyboardType.AMOUNT);
    }

    public void showOrderConfirmation() {
        mKeyboardHelper.showKeyboardFragment(R.id.fKeyboard, getChildFragmentManager(), KeyboardType.ORDER_CONFIRMATION);
    }

    public void clearKeyboardStack() {
        mKeyboardHelper.clearStack();
    }

    public void showOrdered() {
        mKeyboardHelper.showKeyboardFragment(R.id.fKeyboard, getChildFragmentManager(), KeyboardType.ORDERED);
    }
}