package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.technotrade.pts2.pts2testapp.StringProvider;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.dialog.ConfirmationViewModel;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.dialog.ExclamationViewModel;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final StringProvider mStringProvider;

    public MainViewModelFactory(StringProvider stringProvider) {
        mStringProvider = stringProvider;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(mStringProvider);
        } else if (modelClass.isAssignableFrom(ExtraViewModel.class)) {
            return (T) new ExtraViewModel();
        } else if (modelClass.isAssignableFrom(PumpsViewModel.class)) {
            return (T) new PumpsViewModel();
        } else if (modelClass.isAssignableFrom(KeyboardViewModel.class)) {
            return (T) new KeyboardViewModel();
        } else if (modelClass.isAssignableFrom(ConfirmationViewModel.class)) {
            return (T) new ConfirmationViewModel();
        } else if (modelClass.isAssignableFrom(ExclamationViewModel.class)) {
            return (T) new ExclamationViewModel();
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass);
        }
    }
}