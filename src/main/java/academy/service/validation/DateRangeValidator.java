package academy.service.validation;

import academy.Application;
import academy.service.exception.ParameterValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Валидатор для проверки корректности диапазона дат. Проверяет что начальная дата не позже конечной и что даты в
 * правильном формате.
 */
public class DateRangeValidator implements Validator {
    @Override
    public void validate(Application app) throws ParameterValidationException {
        String fromDate = app.getFromDate();
        String toDate = app.getToDate();

        try {
            LocalDateTime from = parseDateTime(fromDate);
            LocalDateTime to = parseDateTime(toDate);

            if (from != null && to != null && from.isAfter(to)) {
                throw new ParameterValidationException("Начальная дата не может быть позже конечной");
            }
        } catch (DateTimeParseException e) {
            throw new ParameterValidationException("Неверный формат даты. Ожидается ISO8601 (YYYY-MM-DD)");
        }
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null) return null;
        return LocalDateTime.parse(dateStr + "T00:00:00");
    }
}
