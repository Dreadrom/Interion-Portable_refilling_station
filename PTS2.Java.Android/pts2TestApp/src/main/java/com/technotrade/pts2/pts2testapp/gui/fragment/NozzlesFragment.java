package com.technotrade.pts2.pts2testapp.gui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.technotrade.pts2.pts2testapp.databinding.FragmentNozzlesBinding;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.PumpsViewModel;

import java.io.Serializable;

public class NozzlesFragment extends BaseFragment<PumpsViewModel> {

	private FragmentNozzlesBinding mBinding;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mBinding = FragmentNozzlesBinding.inflate(inflater, container, false);
		mBinding.setViewModel(getViewModel());
		mBinding.setLifecycleOwner(getViewLifecycleOwner());
		mBinding.rvNozzles.setLayoutManager(new LinearLayoutManager(getContext()));

        mNavController = NavHostFragment.findNavController(this);

		Bundle args = getArguments();
		if (args != null) {
			NozzleItem nozzleItem;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				nozzleItem = args.getSerializable("NOZZLE_ITEM", NozzleItem.class);
			} else {
				@SuppressWarnings("deprecation")
				Serializable serializable = args.getSerializable("NOZZLE_ITEM");
				nozzleItem = (NozzleItem) serializable;
			}

			if (nozzleItem != null) {
				getViewModel().getNozzlesRecyclerViewAdapter().setSelectedNozzle(nozzleItem);
			}
		}

		return mBinding.getRoot();
	}

	@Override
	protected Class<PumpsViewModel> getViewModelClass() {
		return PumpsViewModel.class;
	}

	@Override
	protected boolean execModelViewCommand(EventCommand<?> eventCommand) {
		return false;
	}
}