package com.technotrade.pts2.pts2testapp.gui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.technotrade.pts2.pts2testapp.entity.Event;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.gui.viewmodel.BaseViewModel;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public abstract class BaseActivity<T extends BaseViewModel> extends AppCompatActivity {

    protected T mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider.Factory factory = getDefaultViewModelProviderFactory();
        mViewModel = new ViewModelProvider(this, factory).get(getViewModelClass());

        mViewModel.getViewModelCommandEvent().observe(this, queue -> {
            if (queue == null) return;

            // Defensive copy so LiveData value isn’t mutated during iteration
            Queue<Event<EventCommand<?>>> copy = new LinkedList<>();
            synchronized (queue) {
                copy.addAll(queue);
            }

            while (!copy.isEmpty()) {
                EventCommand<?> eventCommand = Objects.requireNonNull(copy.poll()).getContent();
                if (eventCommand == null) continue;

                execModelViewCommand(eventCommand);
            }
        });
    }

    protected abstract boolean execModelViewCommand(EventCommand<?> eventCommand);

    protected abstract Class<T> getViewModelClass();
}