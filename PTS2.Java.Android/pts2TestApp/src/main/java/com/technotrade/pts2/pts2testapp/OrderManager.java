package com.technotrade.pts2.pts2testapp;

import com.technotrade.pts2.datastructs.MeasurementUnits;
import com.technotrade.pts2.pts2testapp.entity.Order;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/// <summary>
/// Manager class that responsible for orders
/// </summary>
public class OrderManager implements Serializable {
    private List<Order> mFormedOrders;
    private Order mConstructingOrder;
    private final ResourceManager mResourceManager;

    public OrderManager() {
        mFormedOrders = new ArrayList<>();
        ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
        mResourceManager = applicationFacade.getResourceManager();
    }

    public synchronized Order getConstructingOrder() {
        return mConstructingOrder;
    }

    private synchronized void setConstructingOrder(Order order) {
        mConstructingOrder = order;
    }

    public synchronized List<Order> getFormedOrders() {
        // Return an empty list if mFormedOrders is null
        return mFormedOrders != null ? mFormedOrders : Collections.emptyList();
    }

    public synchronized Order getFormedOrderForPump(PumpItem pumpItem) {
        if (pumpItem == null) {
            return null; // Defensive: null pump item can't match any order
        }

        List<Order> orders = getFormedOrdersForPumpWithNumber(pumpItem.getNumber());
        return orders.isEmpty() ? null : orders.get(0);
    }

    public synchronized Order getFormedOrderForPumpWithNumber(int pump) {
        List<Order> orders = getFormedOrdersForPumpWithNumber(pump);
        return orders.isEmpty() ? null : orders.get(0);
    }

    public synchronized List<Order> getFormedOrdersForPumpWithNumber(int pump) {
        if (mFormedOrders == null || mFormedOrders.isEmpty()) {
            return Collections.emptyList();
        }

        return mFormedOrders.stream()
            .filter(obj -> obj != null // defensive: avoid NPE if malformed order in list
                && obj.isPumpSet()
                && obj.getPump() != null
                && obj.getPump().getNumber() == pump
                && obj.isFormedSet()
                && obj.getFormed())
            .collect(Collectors.toList());
    }

    public synchronized void setFormedOrders(ArrayList<Order> orders) {
        for(int i = 0; i < orders.size(); ++i) {
            orders.get(i).setFormed(true);
        }

        mFormedOrders = orders;
    }

    public synchronized void addFormedOrder(Order order) {
        order.setFormed(true);
        mFormedOrders.add(order);
    }

    public synchronized Order createConstructingFuelOrder(PumpItem pump) {
        Order order = new Order();
        order.setPump(pump);
        setConstructingOrder(order);

        return order;
    }

    public synchronized void closeOrderForPump(PumpItem pumpItem) {
        if (pumpItem == null) return;

        mFormedOrders.removeIf(order ->
            order != null &&
            order.getPump() != null &&
            order.getPump().getNumber() == pumpItem.getNumber()
        );

        updateOrderProgressIndicator(pumpItem);
    }

    public String getOrderValue() {
        Order order = getConstructingOrder();
        String result = "";

        if(order.isFullTankSet()) {
            result = mResourceManager.getResourceString(R.string.full_tank);
        }
        else if(order.isQuantitySet()) {
            BigDecimal quantity = order.getQuantity();
            BigDecimal roundedValue = round(quantity, RoundingMode.HALF_EVEN);
            result += roundedValue;
        }
        else if(order.isAmountSet()) {
            BigDecimal quantity = order.getAmount();
            BigDecimal roundedValue = round(quantity, RoundingMode.HALF_EVEN);
            result += roundedValue;
        }

        return result;
    }

    public String getOrderUnit() {
        Order order = getConstructingOrder();
        String result = "";

        if(order.isFullTankSet()) {
            result = "";
        }
        else if(order.isQuantitySet()) {
            MeasurementUnits measurementUnits = ApplicationFacade.getInstance().getPTSManager().getDataStorage().getMeasurementUnits();
            result = measurementUnits.getVolume();
        }
        else if(order.isAmountSet()) {
            Settings settings = ApplicationFacade.getInstance().getSettings();
            result = settings.getCurrency();
        }

        return result;
    }

    public synchronized void updateOrderProgressIndicator(PumpItem pumpItem) {
        List<Order> formedOrders = getFormedOrders();

        // find order for this pump
        Order formedOrder = formedOrders.stream()
            .filter(o -> o.getPump() != null && o.getPump().getNumber() == pumpItem.getNumber())
            .findFirst()
            .orElse(null);

        if (formedOrder == null) {
            pumpItem.setProgress(0);
            return;
        }

        if (formedOrder.isQuantitySet()) {
            BigDecimal quantity = formedOrder.getQuantity();
            if (isNullOrZero(quantity)) {
                pumpItem.setProgress(0);
                return;
            }

            pumpItem.setProgress(
                calculateProgressSafe(pumpItem.getDispensedVolume(), quantity)
            );
        }
        else if (formedOrder.isAmountSet()) {
            BigDecimal amount = formedOrder.getAmount();
            if (isNullOrZero(amount)) {
                pumpItem.setProgress(0);
                return;
            }

            pumpItem.setProgress(
                calculateProgressSafe(pumpItem.getDispensedAmount(), amount)
            );
        }
        else if (formedOrder.isFullTankSet()) {
            pumpItem.setProgress(100);
        }
        else {
            pumpItem.setProgress(0);
        }
    }

    public synchronized void updateOrdersProgressIndicators(List<PumpItem> pumpItems) {
        for (PumpItem pumpItem : pumpItems) {
            updateOrderProgressIndicator(pumpItem);
        }
    }

    /**
     * Calculate fueling progress percentage.
     * Protects against division by zero, rounding spikes, and early false 100%.
     *
     * @param currentStr current value as String (volume or amount)
     * @param target target value (quantity or amount)
     * @return progress percentage (0–100)
     */
    private int calculateProgressSafe(String currentStr, BigDecimal target) {
        if (currentStr == null || target == null) return 0;

        BigDecimal current;
        try {
            current = new BigDecimal(currentStr.trim());
        } catch (Exception e) {
            return 0; // invalid number string
        }

        if (current.compareTo(BigDecimal.ZERO) <= 0 || target.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        // (current / target) * 100
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal ratio = current.divide(target, 4, RoundingMode.HALF_UP); // high precision ratio
        BigDecimal progress = ratio.multiply(hundred);

        // round properly (not truncate)
        int percent = progress.setScale(0, RoundingMode.HALF_UP).intValue();

        // clamp safely
        if (percent >= 100 && current.compareTo(target) < 0) {
            // avoid false 100 if still fueling but just rounding up
            percent = 99;
        }

        return Math.max(0, Math.min(100, percent));
    }

    private boolean isNullOrZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal round(BigDecimal value, RoundingMode mode) {
        return value.setScale(2, mode);
    }

    public BigDecimal round(String value, RoundingMode mode) throws ParseException {
        DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getInstance(Locale.US);
        decimalFormat.setParseBigDecimal(true);
        BigDecimal bigDecimal = (BigDecimal)decimalFormat.parse(value);
        assert bigDecimal != null;

        return round(bigDecimal, mode);
    }
}