package com.technotrade.pts2.pts2testapp.entity;

import androidx.lifecycle.MutableLiveData;

import java.util.LinkedList;
import java.util.Queue;

public class EventLiveData extends MutableLiveData<Queue<Event<EventCommand<?>>>> {

    public void postEvent(EventCommand<?> eventCommand) {
        Queue<Event<EventCommand<?>>> queue = getValue();
        if (queue == null) {
            queue = new LinkedList<>();
        }
        queue.add(new Event<>(eventCommand, true));
        postValue(queue);
    }
}
