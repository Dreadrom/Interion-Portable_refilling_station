package com.technotrade.pts2.pts2testapp.helper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.technotrade.pts2.pts2testapp.gui.dialog.DialogConfirmation;
import com.technotrade.pts2.pts2testapp.gui.dialog.DialogExclamation;
import com.technotrade.pts2.pts2testapp.gui.dialog.DialogProgress;
import com.technotrade.pts2.pts2testapp.gui.dialog.DialogWarning;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.dialog.ConfirmationViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.dialog.ExclamationViewModel;

public class DialogHelper {
    private static DialogProgress mDialogProgress = null;

    private static void safeShow(Fragment fragment, final androidx.fragment.app.DialogFragment dialog, final String tag) {
        if (fragment == null || dialog == null) return;

        fragment.requireActivity().runOnUiThread(() -> {
            FragmentManager fm = fragment.getParentFragmentManager();
            if (!dialog.isAdded() && fragment.isAdded() && !fragment.isStateSaved()) {
                dialog.show(fm, tag);
            }
        });
    }

    public static void showDialogExclamation(LifecycleOwner owner, Fragment fragment, String title, String message) {
        ExclamationViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) owner)
            .get(ExclamationViewModel.class);
        DialogExclamation dialog = new DialogExclamation(viewModel, title, message);

        safeShow(fragment, dialog, "DialogExclamation");

        viewModel.getAcknowledgement().observe(owner, event -> {
            Boolean isYes = event.getContent();
            if (isYes != null && isYes) {
                dialog.dismissNow();
                viewModel.getAcknowledgement().removeObservers(owner);
                viewModel.onDialogDismissed();
            }
        });
    }

    public static void showDialogWarning(LifecycleOwner owner, Fragment fragment, String title, String message) {
        showDialogWarningCommon(owner, fragment, title, message, 0);
    }

    public static void showDialogWarningForAWhile(LifecycleOwner owner, Fragment fragment, String title, String message, long autoCloseDelayMs) {
        showDialogWarningCommon(owner, fragment, title, message, autoCloseDelayMs);
    }

    private static void showDialogWarningCommon(LifecycleOwner owner, Fragment fragment, String title, String message, long autoCloseDelayMs) {
        ExclamationViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) owner)
            .get(ExclamationViewModel.class);
        DialogWarning dialog = new DialogWarning(viewModel, title, message);
        dialog.setAutoCloseDelay(autoCloseDelayMs);

        safeShow(fragment, dialog, "DialogWarning");

        viewModel.getAcknowledgement().observe(owner, event -> {
            Boolean isYes = event.getContent();
            if (isYes != null && isYes) {
                dialog.dismissNow();
                viewModel.getAcknowledgement().removeObservers(owner);
                viewModel.onDialogDismissed();
            }
        });
    }

    public static void showConfirmationDialog(LifecycleOwner owner, Fragment fragment, String title, String message,
                                              Runnable onYes, Runnable onNo) {
        ConfirmationViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) owner)
            .get(ConfirmationViewModel.class);
        DialogConfirmation dialog = new DialogConfirmation(viewModel, title, message);

        safeShow(fragment, dialog, "DialogConfirmation");

        viewModel.getConfirmation().observe(owner, event -> {
            Boolean isYes = event.getContent();
            if (isYes != null) {
                if (isYes) onYes.run(); else onNo.run();
                dialog.dismissNow();
                viewModel.getConfirmation().removeObservers(owner);
                viewModel.onDialogDismissed();
            }
        });
    }

    public static void showDialogProgress(Fragment fragment) {
        if (fragment == null) return;
        fragment.requireActivity().runOnUiThread(() -> {
            if (mDialogProgress != null) hideDialogProgress();
            mDialogProgress = new DialogProgress();
            safeShow(fragment, mDialogProgress, "DialogProgress");
        });
    }

    public static void showDialogProgress(AppCompatActivity activity) {
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            if (mDialogProgress != null) hideDialogProgress();
            mDialogProgress = new DialogProgress();
            safeShow(null, mDialogProgress, "DialogProgress"); // fallback for activity
        });
    }

    public static void hideDialogProgress() {
        if (mDialogProgress != null && mDialogProgress.isAdded()) {
            mDialogProgress.dismissNow();
            mDialogProgress = null;
        }
    }
}