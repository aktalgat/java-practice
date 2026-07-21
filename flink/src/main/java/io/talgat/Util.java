package io.talgat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Util {

    public static String formatTime(long timestamp) {
        return LocalDateTime
                .ofInstant(
                        java.time.Instant.ofEpochMilli(timestamp),
                        ZoneOffset.UTC
                )
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static String formatWatermark(long watermark) {
        if (watermark == Long.MIN_VALUE) {
            return "NOT_STARTED";
        }

        return formatTime(watermark);
    }
}
