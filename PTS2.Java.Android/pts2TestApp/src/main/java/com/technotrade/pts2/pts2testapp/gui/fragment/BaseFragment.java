package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.gui.activity.MainActivity;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand;
import com.technotrade.pts2.pts2testapp.helper.DialogHelper;

public abstract class BaseFragment<T extends BaseViewModel> extends Fragment {
    protected T mViewModel;
    protected NavController mNavController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(getViewModelClass());
        mNavController = NavHostFragment.findNavController(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        getViewModel().getViewModelCommandEvent().observe(getViewLifecycleOwner(), queue -> {
            if (queue == null) return;

            while (!queue.isEmpty()) {
                EventCommand<?> eventCommand = queue.poll().getContent();
                if (eventCommand == null) continue;

                boolean handled = handleCommonCommands(eventCommand);
                if (!handled) {
                    handled = execModelViewCommand(eventCommand);
                }
                if (!handled) {
                    forwardToParent(eventCommand);
                }
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Handle framework-wide commands (like dialogs, navigation, progress).
     * Return true if the command is consumed.
     */
    protected boolean handleCommonCommands(EventCommand<?> eventCommand) {
        String command = eventCommand.getCommand();
        Object data = eventCommand.getData();

        if (command == null) return false;

        try {
            switch (ViewModelCommand.valueOf(command)) {
                case showError:
                    DialogHelper.showDialogWarning(
                        this,
                        this,
                        requireContext().getString(R.string.error),
                        (String) data
                    );
                    return true;

                case navigateUp:
                    navigateUp();
                    return true;

                case setProgressVisible:
                    if ((boolean) data) {
                        DialogHelper.showDialogProgress(this);
                    } else {
                        DialogHelper.hideDialogProgress();
                    }
                    return true;

                default:
                    return false;
            }
        } catch (IllegalArgumentException e) {
            // command not in ViewModelCommand enum
            return false;
        }
    }

    /**
     * Forward unhandled commands to parent fragment if it is also a BaseFragment.
     */
    protected void forwardToParent(EventCommand<?> eventCommand) {
        Fragment parent = getParentFragment();
        if (parent instanceof BaseFragment) {
            ((BaseFragment<?>) parent).getViewModel().sendViewModelCommandEvent(eventCommand);
        }
    }

    protected abstract Class<T> getViewModelClass();

    protected T getViewModel() {
        return mViewModel;
    }

    /**
     * For child fragments to handle their specific commands.
     * Return true if consumed, false if should bubble up.
     */
    protected abstract boolean execModelViewCommand(EventCommand<?> eventCommand);

    protected MainActivity getMainActivity() {
        return (MainActivity) requireActivity();
    }

    protected boolean onBackPressed() {
        return true;
    }

    protected void navigateUp() {
        if (mNavController != null) {
            mNavController.navigateUp();
        }
    }

    protected void navigateTo(int actionId) {
        if (mNavController != null) {
            mNavController.navigate(actionId);
        }
    }

    protected void navigateTo(int actionId, Bundle args) {
        if (mNavController != null) {
            mNavController.navigate(actionId, args);
        }
    }

    protected void navigateTo(int actionId, Bundle args, NavOptions navOptions) {
        if (mNavController != null) {
            mNavController.navigate(actionId, args, navOptions);
        }
    }

    protected void navigateTo(int actionId, Bundle args, NavOptions navOptions, FragmentNavigator.Extras extras) {
        if (mNavController != null) {
            mNavController.navigate(actionId, args, navOptions, extras);
        }
    }

    protected void safeNavigateTo(int actionId) {
        safeNavigateTo(actionId, null, null, null);
    }

    protected void safeNavigateTo(int actionId, Bundle args) {
        safeNavigateTo(actionId, args, null, null);
    }

    protected void safeNavigateTo(int actionId, Bundle args, NavOptions navOptions) {
        safeNavigateTo(actionId, args, navOptions, null);
    }

    protected void safeNavigateTo(int actionId, Bundle args, NavOptions navOptions, FragmentNavigator.Extras extras) {
        if (mNavController == null) return;
        if (!isAdded()) return; // ensure fragment is attached

        try {
            int currentDestId = mNavController.getCurrentDestination() != null
                ? mNavController.getCurrentDestination().getId()
                : -1;

            // Guard against navigating from wrong state or twice
            if (currentDestId == 0 || currentDestId == actionId) {
                return; // already there or invalid
            }

            if (extras != null) {
                mNavController.navigate(actionId, args, navOptions, extras);
            } else if (navOptions != null) {
                mNavController.navigate(actionId, args, navOptions);
            } else if (args != null) {
                mNavController.navigate(actionId, args);
            } else {
                mNavController.navigate(actionId);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}