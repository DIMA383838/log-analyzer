package academy.service.processor;

import academy.model.LogEntry;
import java.time.LocalDateTime;

/** Фильтр для отбора лог-записей по диапазону дат. Используется для фильтрации записей между указанными датами. */
public class DateFilter {
    private final LocalDateTime from;
    private final LocalDateTime to;

    public DateFilter(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    public boolean shouldInclude(LogEntry entry) {
        if (from != null && entry.timeLocal().isBefore(from)) {
            return false;
        }
        if (to != null && entry.timeLocal().isAfter(to)) {
            return false;
        }
        return true;
    }
}
