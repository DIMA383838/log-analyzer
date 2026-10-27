package academy.service.calculator;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Калькулятор статистики по запрашиваемым ресурсам. Анализирует какие ресурсы (URL) наиболее популярны, сортирует их по
 * количеству запросов и выбирает топ-10.
 */
public class ResourceStatisticsCalculator implements StatisticsCalculator {
    private static final Logger logger = LogManager.getLogger(ResourceStatisticsCalculator.class);

    @Override
    public void calculate(List<LogEntry> logs, LogAnalysisResult result) {
        logger.debug("Расчет статистики ресурсов");

        Map<String, Long> resourceCounts =
                logs.stream().collect(Collectors.groupingBy(LogEntry::getResource, Collectors.counting()));

        List<LogAnalysisResult.ResourceStat> topResources = resourceCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> new LogAnalysisResult.ResourceStat(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        result.setResources(topResources);
    }
}
