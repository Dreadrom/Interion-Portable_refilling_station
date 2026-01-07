package com.technotrade.pts2.pts2testapp.gui.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.dialog.ConfirmationViewModel;

public class DialogConfirmation extends DialogBase {
    private final ConfirmationViewModel mViewModel;

    public DialogConfirmation(ConfirmationViewModel viewModel, String title, String message) {
        super(title, message);

        mViewModel = viewModel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder
            .setTitle(mTitle)
            .setMessage(mMessage)
            .setIcon(R.drawable.icon_question)
            .setPositiveButton("Yes", (dialog, id) -> mViewModel.onYesClicked())
            .setNegativeButton("No", (dialog, id) -> mViewModel.onNoClicked());

        return builder.create();
    }
}