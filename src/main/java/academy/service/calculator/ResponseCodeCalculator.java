package academy.service.calculator;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Калькулятор статистики по кодам HTTP ответов. Группирует запросы по кодам ответа (200, 404, 500 и т.д.), подсчитывает
 * количество каждого кода и сортирует по убыванию.
 */
public class ResponseCodeCalculator implements StatisticsCalculator {
    private static final Logger logger = LogManager.getLogger(ResponseCodeCalculator.class);

    @Override
    public void calculate(List<LogEntry> logs, LogAnalysisResult result) {
        logger.debug("Расчет статистики кодов ответа");

        Map<Integer, Long> codeCounts =
                logs.stream().collect(Collectors.groupingBy(LogEntry::status, Collectors.counting()));

        List<LogAnalysisResult.ResponseCodeStat> codeStats = codeCounts.entrySet().stream()
                .map(entry -> new LogAnalysisResult.ResponseCodeStat(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(LogAnalysisResult.ResponseCodeStat::totalResponsesCount)
                        .reversed())
                .collect(Collectors.toList());

        result.setResponseCodes(codeStats);
    }
}
