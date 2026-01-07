package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.technotrade.pts2.pts2testapp.entity.Event;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Base ViewModel that manages command events safely.
 */
public class BaseViewModel extends ViewModel {

    private final Queue<Event<EventCommand<?>>> eventQueue = new LinkedList<>();
    private final MutableLiveData<Queue<Event<EventCommand<?>>>> mCommandEvent = new MutableLiveData<>();

    public void onStart() {
    }

    public void onStop() {
    }

    public void sendViewModelCommandEvent(String command, Object data) {
        EventCommand<?> eventCommand = new EventCommand<>(command, data);
        sendViewModelCommandEvent(eventCommand);
    }

    public void sendViewModelCommandEvent(EventCommand<?> eventCommand) {
        eventQueue.offer(new Event<>(eventCommand, true)); // or false depending on behavior
        publishSnapshot();
    }

    public void sendViewModelCommandEvents(List<EventCommand<?>> commands) {
        for (EventCommand<?> cmd : commands) {
            eventQueue.offer(new Event<>(cmd, true)); // or false
        }
        publishSnapshot();
    }

    public LiveData<Queue<Event<EventCommand<?>>>> getViewModelCommandEvent() {
        return mCommandEvent;
    }

    /**
     * Remove the head of the queue after consumer processed it.
     */
    public void consumeNextEvent() {
        eventQueue.poll(); // remove first
        publishSnapshot();
    }

    /**
     * Clears all events.
     */
    public void clear() {
        eventQueue.clear();
        publishSnapshot();
    }

    private void publishSnapshot() {
        // Expose immutable copy
        mCommandEvent.postValue(new LinkedList<>(eventQueue));
    }
}