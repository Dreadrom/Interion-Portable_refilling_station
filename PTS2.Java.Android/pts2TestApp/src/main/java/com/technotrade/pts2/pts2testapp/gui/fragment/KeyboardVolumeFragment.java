package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.technotrade.pts2.pts2testapp.databinding.FragmentKeyboardVolumeBinding;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.KeyboardViewModel;

public class KeyboardVolumeFragment extends BaseFragment<KeyboardViewModel> implements IKeyboard {
    private FragmentKeyboardVolumeBinding mBinding = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentKeyboardVolumeBinding.inflate(inflater, container, false);

        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    protected Class<KeyboardViewModel> getViewModelClass() {
        return KeyboardViewModel.class;
    }

    @Override
    protected boolean execModelViewCommand(EventCommand<?> eventCommand) {
        return false;
    }

    @Override
    public void clear() {
        if(mViewModel != null) {
            mViewModel.clear();
        }
    }
}