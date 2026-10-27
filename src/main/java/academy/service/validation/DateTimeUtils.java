package academy.service.validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Утилитарный класс для работы с датами и временем. Предоставляет методы парсинга строк в формате ISO8601 в
 * LocalDateTime.
 */
public class DateTimeUtils {
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null) return null;

        try {
            // Пробуем разные форматы
            if (dateStr.contains("T")) {
                return LocalDateTime.parse(dateStr, ISO_DATE_TIME_FORMATTER);
            } else {
                return LocalDateTime.parse(dateStr + "T00:00:00", ISO_DATE_TIME_FORMATTER);
            }
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(
                    "Неверный формат даты. Ожидается ISO8601 (YYYY-MM-DD или YYYY-MM-DDTHH:MM:SS)",
                    dateStr,
                    e.getErrorIndex());
        }
    }
}
