package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionInflater;

import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.databinding.FragmentPumpsBinding;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.PumpsViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand;
import com.technotrade.pts2.pts2testapp.helper.DialogHelper;

public class PumpsFragment extends BaseFragment<PumpsViewModel> {
    private FragmentPumpsBinding mBinding = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSharedElementEnterTransition(TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_element_transition));

        setSharedElementReturnTransition(TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_element_transition));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentPumpsBinding.inflate(inflater, container, false);

        mBinding.setViewModel(getViewModel());
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mBinding.rvPumps.setLayoutManager(layoutManager);

        return mBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener(
            "SETTINGS_RESULT",
            getViewLifecycleOwner(),
            (requestKey, result) -> {
                boolean changed = result.getBoolean("SETTINGS_CHANGED", false);
                if (changed) {
                    ApplicationFacade.getInstance().loadSettings();
                }
            }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mViewModel != null) {
            mViewModel.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mViewModel != null) {
            mViewModel.onStop();
        }
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

        switch(ViewModelCommand.valueOf(command)) {
            case navigateToPump:
                navigateToPump((PumpItem)data);
                return true;
            case showNozzleIsNotPickedUpDialog:
                showNozzleIsNotPickedUpDialog((PumpItem)data);
                return true;
            default:
                return false;
        }
    }

    public void navigateToPump(PumpItem pumpItem) {
        if(pumpItem == null) {
            return;
        }

        if (mViewModel == null) {
            return;
        }

        int position = mViewModel.getIndexOfPumpItem(pumpItem);

        if (position == -1) {
            return;
        }

        if(!ApplicationFacade.getInstance().getPTSManager().isOpened()
            || !ApplicationFacade.getInstance().getPTSManager().isConnected()) {
            showNoConnectionToPTSDialog();
            return;
        }

        String transitionName = "pumpTransition" + position;

        View view = mBinding.rvPumps.getChildAt(position);

        if (view == null) {
            return;
        }

        view.setTransitionName(transitionName);

        Bundle args = new Bundle();
        args.putString("TRANSITION_NAME", transitionName);

        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
            .addSharedElement(view, transitionName)
            .build();

        safeNavigateTo(R.id.action_pumpsFragment_to_pumpFragment, args, null, extras);
    }

    public void showNozzleIsNotPickedUpDialog(PumpItem pumpItem) {
        String template = getMainActivity().getResources().getString(R.string.the_nozzle_on_pump_d_has_not_been_picked_up);
        String message = String.format(template, pumpItem.getNumber());
        DialogHelper.showDialogWarningForAWhile(this,
            this,
            getResources().getString(R.string.error),
            message,
            4000);
    }

    public void showNoConnectionToPTSDialog() {
        String message = getMainActivity().getResources().getString(R.string.no_connection_to_the_controller_the_connection_is_not_open_or_has_been_lost_connect_the_controller_before_continuing);
        DialogHelper.showDialogWarningForAWhile(this,
            this,
            getResources().getString(R.string.error),
            message,
            4000);
    }
}