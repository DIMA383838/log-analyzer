package academy.service.calculator;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Калькулятор статистики по датам запросов. Группирует запросы по датам, вычисляет количество запросов в день и их
 * процентное соотношение от общего числа.
 */
public class DateStatisticsCalculator implements StatisticsCalculator {
    private static final Logger logger = LogManager.getLogger(DateStatisticsCalculator.class);

    @Override
    public void calculate(List<LogEntry> logs, LogAnalysisResult result) {
        logger.debug("Расчет статистики по датам");

        Map<LocalDate, Long> dateCounts = logs.stream()
                .collect(Collectors.groupingBy(entry -> entry.timeLocal().toLocalDate(), Collectors.counting()));

        long totalRequests = logs.size();
        List<LogAnalysisResult.RequestsPerDate> requestsPerDate = dateCounts.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    long count = entry.getValue();
                    double percentage =
                            totalRequests > 0 ? Math.round(count * 100.0 / totalRequests * 100.0) / 100.0 : 0.0;
                    String weekday = String.valueOf(date.getDayOfWeek());

                    return new LogAnalysisResult.RequestsPerDate(
                            date.format(DateTimeFormatter.ISO_DATE), weekday, count, percentage);
                })
                .sorted(Comparator.comparing(LogAnalysisResult.RequestsPerDate::date))
                .collect(Collectors.toList());

        result.setRequestsPerDate(requestsPerDate);
    }
}
