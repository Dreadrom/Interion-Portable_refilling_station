package com.technotrade.pts2.pts2testapp.helper;

public class ValidationHelper {

    public static <T extends Comparable<T>> boolean isNumericInRange(T value, T min, T max) {
        if (value == null || min == null || max == null) {
            return false;
        }
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    public static boolean isNumericFloat(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean isNumericFloatInRange(String value, float min, float max) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        try {
            float number = Float.parseFloat(value.trim());
            return number >= min && number <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumericInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean isNumericIntegerInRange(String value, int min, int max) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        try {
            int number = Integer.parseInt(value.trim());
            return number >= min && number <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPortNumber(String portNumberStr) {
        return isNumericIntegerInRange(portNumberStr, 0, 65535);
    }

    public static boolean isValidPortNumber(int portNumber) {
        return isNumericInRange(portNumber, 0, 65535);
    }
}