package com.technotrade.pts2.pts2testapp.gui;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.recyclerview.widget.RecyclerView;

import com.technotrade.pts2.pts2testapp.adapter.NozzlesRecyclerViewAdapter;
import com.technotrade.pts2.pts2testapp.adapter.PumpsRecyclerViewAdapter;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.listener.OnNozzleItemClickListener;
import com.technotrade.pts2.pts2testapp.listener.OnPumpItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class BindingAdapters {

    @BindingAdapter("pumps")
    public static void bindRecyclerViewSetPumps(RecyclerView recyclerView, List<PumpItem> pumpItems) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();

        if (adapter instanceof PumpsRecyclerViewAdapter) {
            PumpsRecyclerViewAdapter pumpsRecyclerViewAdapter = (PumpsRecyclerViewAdapter) adapter;
            pumpsRecyclerViewAdapter.setDataList((ArrayList<PumpItem>) pumpItems);
        }
    }

    @BindingAdapter("nozzles")
    public static void bindRecyclerViewSetNozzles(RecyclerView recyclerView, List<NozzleItem> nozzleItems) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();

        if (adapter instanceof NozzlesRecyclerViewAdapter) {
            NozzlesRecyclerViewAdapter nozzlesRecyclerViewAdapter = (NozzlesRecyclerViewAdapter) adapter;
            nozzlesRecyclerViewAdapter.setDataList((ArrayList<NozzleItem>) nozzleItems);
        }
    }

    @BindingAdapter("selectedPump")
    public static void bindRecyclerViewSetSelectedPump(RecyclerView recyclerView, PumpItem pumpItem) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();

        if(pumpItem == null) {
            return;
        }

        if (adapter instanceof PumpsRecyclerViewAdapter) {
            PumpsRecyclerViewAdapter pumpsRecyclerViewAdapter = (PumpsRecyclerViewAdapter) adapter;
            pumpsRecyclerViewAdapter.setSelectedPump(pumpItem);
        }
    }

    @InverseBindingAdapter(attribute = "selectedPump", event = "selectedPumpChanged")
    public static PumpItem bindRecyclerViewGetSelectedPump(RecyclerView recyclerView) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();

        if (adapter instanceof PumpsRecyclerViewAdapter) {
            PumpsRecyclerViewAdapter pumpsRecyclerViewAdapter = (PumpsRecyclerViewAdapter) adapter;
            return pumpsRecyclerViewAdapter.getSelectedPump();
        }

        return null;
    }

    @BindingAdapter("selectedPumpChanged")
    public static void addSelectedPumpListener(RecyclerView recyclerView, final InverseBindingListener inverseBindingListener) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();

        if (adapter instanceof PumpsRecyclerViewAdapter) {
            PumpsRecyclerViewAdapter pumpsRecyclerViewAdapter = (PumpsRecyclerViewAdapter) adapter;

            final OnPumpItemClickListener onPumpItemClickListener = new OnPumpItemClickListener() {
                @Override
                public void onPumpItemClick(PumpItem item) {
                    if (inverseBindingListener != null) {
                        inverseBindingListener.onChange();
                    }
                }
            };

            pumpsRecyclerViewAdapter.addOnPumpItemClickListener(onPumpItemClickListener);
        }
    }

    @BindingAdapter("selectedNozzle")
    public static void bindRecyclerViewSetSelectedNozzle(RecyclerView recyclerView, NozzleItem nozzleItem) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();

        if(nozzleItem == null) {
            return;
        }

        if (adapter instanceof NozzlesRecyclerViewAdapter) {
            NozzlesRecyclerViewAdapter nozzlesRecyclerViewAdapter = (NozzlesRecyclerViewAdapter) adapter;
            nozzlesRecyclerViewAdapter.setSelectedNozzle(nozzleItem);
        }
    }

    @InverseBindingAdapter(attribute = "selectedNozzle", event = "selectedNozzleChanged")
    public static NozzleItem bindRecyclerViewGetSelectedNozzle(RecyclerView recyclerView) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();

        if (adapter instanceof NozzlesRecyclerViewAdapter) {
            NozzlesRecyclerViewAdapter nozzlesRecyclerViewAdapter = (NozzlesRecyclerViewAdapter) adapter;
            return nozzlesRecyclerViewAdapter.getSelectedNozzle();
        }

        return null;
    }

    @BindingAdapter("selectedNozzleChanged")
    public static void addSelectedNozzleListener(RecyclerView recyclerView, final InverseBindingListener inverseBindingListener) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();

        if (adapter instanceof NozzlesRecyclerViewAdapter) {
            NozzlesRecyclerViewAdapter nozzlesRecyclerViewAdapter = (NozzlesRecyclerViewAdapter) adapter;

            final OnNozzleItemClickListener onNozzleItemClickListener = new OnNozzleItemClickListener() {
                @Override
                public void onNozzleItemClick(NozzleItem item) {
                    if (inverseBindingListener != null) {
                        inverseBindingListener.onChange();
                    }
                }
            };

            nozzlesRecyclerViewAdapter.addOnNozzleItemClickListener(onNozzleItemClickListener);
        }
    }
}