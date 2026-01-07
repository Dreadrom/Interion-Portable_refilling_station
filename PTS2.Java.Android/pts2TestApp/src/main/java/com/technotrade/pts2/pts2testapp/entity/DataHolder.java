package com.technotrade.pts2.pts2testapp.entity;

import com.technotrade.pts2.pts2testapp.listener.DataChangeListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataHolder<T> {
    private T mData;

    private List<DataChangeListener<T>> mListeners = new CopyOnWriteArrayList<>();

    public DataHolder() {}
    public DataHolder(T data) {mData = data;}

    public synchronized void addOnDataChangeListener(DataChangeListener<T> listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public synchronized void removeOnDataChangeListener(DataChangeListener<T> listener) {
        mListeners.remove(listener);
    }

    public synchronized void clearOnDataChangeListeners() {
        mListeners.clear();
    }

    public synchronized T getData() {
        return mData;
    }

    public synchronized void setData(T data) {
        this.mData = data;

        for (DataChangeListener<T> listener : mListeners) {
            listener.onDataChanged(this.mData);
        }
    }

    public synchronized void clear()
    {
        clearOnDataChangeListeners();
        mData = null;
    }
}