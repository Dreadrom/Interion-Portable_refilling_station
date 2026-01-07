package com.technotrade.pts2.pts2testapp.entity;

public class Event<T> {
    private final T mContent;
    private boolean mHasBeenHandled;
    private final boolean mIsAutoReset;

    public Event(T content, boolean isAutoReset) {
        mContent = content;
        mHasBeenHandled = false;
        mIsAutoReset = isAutoReset;
    }

    public T getContent() {
        if (!mHasBeenHandled) {
            if (mIsAutoReset) {
                mHasBeenHandled = true;
            }

            return mContent;
        }

        return null;
    }

    public T peekContent() {
        return mContent;
    }

    public boolean isHandled() {
        return mHasBeenHandled;
    }

    public void markAsHandled() {
        mHasBeenHandled = true;
    }

    public void resetHandledState() {
        mHasBeenHandled = false;
    }
}
