package com.technotrade.pts2.pts2testapp.gui.dialog;

import android.os.CountDownTimer;

import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public abstract class DialogBase extends DialogFragment {

    protected final String mTitle;
    protected final String mMessage;
    private long mAutoCloseDelayMs = 0;
    private long mRemainingAutoCloseTimeMs = 0;
    private CountDownTimer mAutoCloseCountDownTimer;

    public DialogBase() {
        mTitle = "";
        mMessage = "";
    }

    public DialogBase(String title, String message) {
        mTitle = title;
        mMessage = message;
    }

    public void setAutoCloseDelay(long autoCloseDelayMs) {
        this.mAutoCloseDelayMs = autoCloseDelayMs;
        this.mRemainingAutoCloseTimeMs = autoCloseDelayMs;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setDimAmount(0.5f);
        }

        if (mAutoCloseDelayMs > 0) {
            startAutoCloseCountDownTimer(mRemainingAutoCloseTimeMs);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelAutoCloseCountDownTimer();
    }

    private void startAutoCloseCountDownTimer(long millisUntilFinished) {
        cancelAutoCloseCountDownTimer();

        mAutoCloseCountDownTimer = new CountDownTimer(millisUntilFinished, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                mRemainingAutoCloseTimeMs = millisUntilFinished;
                // Optionally update UI with remaining time
                //onTimerTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (isAdded() && getDialog() != null && getDialog().isShowing()) {
                    dismiss();
                }
            }
        }.start();
    }

    private void cancelAutoCloseCountDownTimer() {
        if (mAutoCloseCountDownTimer != null) {
            mAutoCloseCountDownTimer.cancel();
            mAutoCloseCountDownTimer = null;
        }
    }
}