package com.technotrade.pts2.pts2testapp.entity;

import android.graphics.Color;

import com.technotrade.pts2.enumeration.PumpStatus;
import com.technotrade.pts2.pts2testapp.statemachine.BaseState;
import com.technotrade.pts2.pts2testapp.statemachine.states.PumpNotSelectedState;

import java.util.Objects;

public class PumpItem {
    private int mNumber;
    private PumpStatus mStatus; // pump PTS status
    private BaseState mState = new PumpNotSelectedState(); // pump application state
    private String mStateDescription;
    private int mStateColor = Color.BLACK;
    private int mStateBackgroundColor = Color.TRANSPARENT;
    private String mFuelName;
    private String mPrice;
    private String mVolume;
    private String mAmount;
    private int mNozzle;
    private int mProgress;
    private String mDispensedVolume;
    private String mDispensedAmount;

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public String getFuelName() {
        return mFuelName;
    }

    public void setFuelName(String fuelName) {
        mFuelName = fuelName;
    }

    public String getStateDescription() {
        return mStateDescription;
    }

    public void setStateDescription(String stateDescription) {
        mStateDescription = stateDescription;
    }

    public PumpStatus getStatus() {
        return mStatus;
    }

    public void setStatus(PumpStatus status) {
        mStatus = status;
    }

    public BaseState getState() {
        return mState;
    }

    public void setState(BaseState state) {
        mState = state;
    }

    public int getStateColor() {
        return mStateColor;
    }

    public void setStateColor(int stateColor) {
        mStateColor = stateColor;
    }

    public int getStateBackgroundColor() {
        return mStateBackgroundColor;
    }

    public void setStateBackgroundColor(int stateBackgroundColor) {
        mStateBackgroundColor = stateBackgroundColor;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getVolume() { return mVolume; }

    public void setVolume(String volume) {
        mVolume = volume;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }

    public int getNozzle() {
        return mNozzle;
    }

    public void setNozzle(int nozzle) {
        mNozzle = nozzle;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    public String getDispensedVolume() { return mDispensedVolume; }

    public void setDispensedVolume(String dispensedVolume) {
        mDispensedVolume = dispensedVolume;
    }

    public String getDispensedAmount() {
        return mDispensedAmount;
    }

    public void setDispensedAmount(String dispensedAmount) {
        mDispensedAmount = dispensedAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PumpItem that = (PumpItem) o;

        return mNumber == that.mNumber &&
            mStatus.getCode() == that.mStatus.getCode() &&
            mState.getName().equals(that.mState.getName()) &&
            mStateDescription.equals(that.mStateDescription) &&
            mStateColor == that.mStateColor &&
            mFuelName.equals(that.mFuelName) &&
            mVolume.equals(that.mVolume) &&
            mPrice.equals(that.mPrice) &&
            mAmount.equals(that.mAmount) &&
            mNozzle == that.mNozzle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mNumber,
            mStatus.getCode(),
            mState.getName(),
            mStateDescription,
            mStateColor,
            mFuelName,
            mVolume,
            mPrice,
            mAmount,
            mNozzle);
    }
}