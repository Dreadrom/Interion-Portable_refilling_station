package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.technotrade.pts2.pts2testapp.StringProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final StringProvider stringProvider;

    public ViewModelFactory(StringProvider stringProvider) {
    this.stringProvider = stringProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(stringProvider);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}