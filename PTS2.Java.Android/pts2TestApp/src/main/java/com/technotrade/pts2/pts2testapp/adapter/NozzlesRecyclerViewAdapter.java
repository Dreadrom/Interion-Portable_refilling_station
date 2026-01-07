package com.technotrade.pts2.pts2testapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.technotrade.pts2.pts2testapp.R;
import com.technotrade.pts2.pts2testapp.databinding.NozzleRecyclerItemBinding;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.listener.OnNozzleItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NozzlesRecyclerViewAdapter extends RecyclerView.Adapter<NozzlesRecyclerViewAdapter.NozzleViewHolder> {

    public static class NozzleViewHolder extends RecyclerView.ViewHolder {
        final NozzleRecyclerItemBinding mBinding;

        NozzleViewHolder(NozzleRecyclerItemBinding binding) {
            super(binding.getRoot());

            this.mBinding = binding;
        }

        public void setItem(NozzleItem nozzleItem) {
            mBinding.setItem(nozzleItem);
        }
    }

    private List<NozzleItem> mItems;
    private NozzleItem mSelectedNozzle;
    private final List<OnNozzleItemClickListener> mOnNozzleItemClickListeners;

    public NozzlesRecyclerViewAdapter() {
        mItems = new ArrayList<>();
        mOnNozzleItemClickListeners = new CopyOnWriteArrayList<>();
    }

    @NonNull
    @Override
    public NozzleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        NozzleRecyclerItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.nozzle_recycler_item, parent, false);

        return new NozzleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(NozzleViewHolder viewHolder, int position) {
        NozzleItem nozzleItem = mItems.get(position);
        viewHolder.mBinding.setItem(nozzleItem);
        viewHolder.mBinding.executePendingBindings();

        viewHolder.itemView.setOnClickListener(view -> {
            setSelectedNozzle(viewHolder.mBinding.getItem());

            for (OnNozzleItemClickListener listener : getOnNozzleItemClickListeners()) {
                if (listener != null) {
                    listener.onNozzleItemClick(viewHolder.mBinding.getItem());

                    if(listener.getFireOnce()) {
                        removeOnNozzleItemClickListener(listener);
                    }
                }
            }
        });

        viewHolder.itemView.setSelected(getSelectedNozzle() == nozzleItem);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataList(ArrayList<NozzleItem> items) {
        mItems = items;

        if(mItems != null && !mItems.contains(mSelectedNozzle) && !mItems.isEmpty()) {
            setSelectedNozzle(mItems.get(0));
        }

        notifyDataSetChanged();
    }

    public NozzleItem getSelectedNozzle() {
        return mSelectedNozzle;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedNozzle(NozzleItem selectedNozzle) {
        this.mSelectedNozzle = selectedNozzle;
        notifyDataSetChanged();
    }

    public List<OnNozzleItemClickListener> getOnNozzleItemClickListeners() {
        return mOnNozzleItemClickListeners;
    }

    public void addOnNozzleItemClickListener(OnNozzleItemClickListener listener) {
        mOnNozzleItemClickListeners.add(listener);
    }

    public void removeOnNozzleItemClickListener(OnNozzleItemClickListener listener) {
        mOnNozzleItemClickListeners.remove(listener);
    }

    public int findPositionWithNozzleItem(NozzleItem nozzleItem) {
        return mItems != null && nozzleItem != null  ? mItems.indexOf(nozzleItem) : -1;
    }
}