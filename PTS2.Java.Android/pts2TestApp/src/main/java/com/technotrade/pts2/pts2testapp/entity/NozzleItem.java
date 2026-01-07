package com.technotrade.pts2.pts2testapp.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class NozzleItem implements Serializable {
    private int mNozzleNumber;
    private String mFuelName;
    private BigDecimal mPrice;

    public int getNozzleNumber() {
        return mNozzleNumber;
    }

    public void setNozzleNumber(int nozzleNumber) {
        mNozzleNumber = nozzleNumber;
    }

    public String getFuelName() {
        return mFuelName;
    }

    public void setFuelName(String fuelName) {
        mFuelName = fuelName;
    }
    public BigDecimal getPrice() {
        return mPrice;
    }

    public void setPrice(BigDecimal price) {
        mPrice = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NozzleItem that = (NozzleItem) o;

        return mNozzleNumber == that.mNozzleNumber &&
            mFuelName.equals(that.mFuelName) &&
            mPrice.equals(that.mPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mNozzleNumber,
            mFuelName,
            mPrice);
    }
}