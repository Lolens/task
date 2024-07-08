package ru.clevertec.check;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

    public static String getDateTimeString() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return currentDateTime.format(formatter);
    }
}
