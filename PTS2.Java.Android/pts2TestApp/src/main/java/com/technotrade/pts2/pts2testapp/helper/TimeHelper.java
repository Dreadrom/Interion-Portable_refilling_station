package com.technotrade.pts2.pts2testapp.helper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeHelper {
    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

    public static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date
            .from(dateToConvert.atZone(ZoneId.systemDefault())
                .toInstant());
    }
}