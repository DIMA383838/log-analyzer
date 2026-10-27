package academy.service.calculator;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Калькулятор статистики по размерам ответов сервера. Вычисляет средний, максимальный и 95-й процентиль размеров
 * ответов в байтах. Наследует методы расчета процентилей из BaseStatisticsCalculator.
 */
public class ResponseSizeCalculator extends BaseStatisticsCalculator {
    private static final Logger logger = LogManager.getLogger(ResponseSizeCalculator.class);

    @Override
    public void calculate(List<LogEntry> logs, LogAnalysisResult result) {
        logger.debug("Расчет статистики размеров ответов");

        if (logs.isEmpty()) {
            result.setResponseSizeInBytes(new LogAnalysisResult.ResponseSize(0, 0, 0));
            return;
        }

        List<Long> sizes = logs.stream().map(LogEntry::bodyBytesSent).sorted().collect(Collectors.toList());

        double totalSize = sizes.stream().mapToLong(Long::longValue).sum();
        double fullAverage = totalSize / sizes.size();
        double average = Math.round(fullAverage * 100.0) / 100.0;
        double max = sizes.isEmpty() ? 0 : sizes.get(sizes.size() - 1);
        double p95 = calculatePercentile(sizes, 95);

        result.setResponseSizeInBytes(new LogAnalysisResult.ResponseSize(average, max, p95));
    }
}
