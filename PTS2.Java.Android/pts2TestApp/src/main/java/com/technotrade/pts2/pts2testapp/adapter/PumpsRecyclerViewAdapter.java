package com.technotrade.pts2.pts2testapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.databinding.PumpCardRecyclerItemBinding;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.listener.OnPumpItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PumpsRecyclerViewAdapter extends RecyclerView.Adapter<PumpsRecyclerViewAdapter.PumpViewHolder> {

    public static class PumpViewHolder extends RecyclerView.ViewHolder {
        PumpCardRecyclerItemBinding mBinding;

        public PumpViewHolder(PumpCardRecyclerItemBinding binding) {
            super(binding.getRoot());

            this.mBinding = binding;
        }

        public void setItem(PumpItem pumpItem) {
            mBinding.setItem(pumpItem);
        }
    }

    private List<PumpItem> mItems;
    private PumpItem mSelectedPump;
    private final List<OnPumpItemClickListener> mOnPumpItemClickListeners;

    public PumpsRecyclerViewAdapter() {
        mItems = new ArrayList<>();
        mOnPumpItemClickListeners = new CopyOnWriteArrayList<>();
    }

    @NonNull
    @Override
    public PumpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PumpCardRecyclerItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.pump_card_recycler_item, parent, false);

        return new PumpViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(PumpViewHolder viewHolder, int position) {
        PumpItem pumpItem = mItems.get(position);
        viewHolder.mBinding.setItem(pumpItem);
        viewHolder.mBinding.executePendingBindings();

        viewHolder.itemView.setOnClickListener(view -> {
            setSelectedPump(viewHolder.mBinding.getItem());

            for (OnPumpItemClickListener listener : getOnPumpItemClickListeners()) {
                if (listener != null) {
                    listener.onPumpItemClick(viewHolder.mBinding.getItem());

                    if(listener.getFireOnce()) {
                        removeOnPumpItemClickListener(listener);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataList(ArrayList<PumpItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public PumpItem getSelectedPump() {
        return mSelectedPump;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedPump(PumpItem selectedPump) {
        this.mSelectedPump = selectedPump;
        notifyDataSetChanged();
    }

    public List<OnPumpItemClickListener> getOnPumpItemClickListeners() {
        return mOnPumpItemClickListeners;
    }

    public void addOnPumpItemClickListener(OnPumpItemClickListener listener) {
        mOnPumpItemClickListeners.add(listener);
    }

    public void removeOnPumpItemClickListener(OnPumpItemClickListener listener) {
        mOnPumpItemClickListeners.remove(listener);
    }

    public int findPositionWithPumpItem(PumpItem pumpItem) {
        return mItems != null && pumpItem != null ? mItems.indexOf(pumpItem) : -1;
    }
}