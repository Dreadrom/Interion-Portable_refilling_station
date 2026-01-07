package com.technotrade.pts2.pts2testapp.entity;

public class EventCommand<T> {
    private String mCommand;
    private T mData;

    public EventCommand(String command, T data) {
        mCommand = command;
        mData = data;
    }

    public String getCommand() {
        return mCommand;
    }

    public void setCommand(String command) {
        mCommand = command;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }
}
