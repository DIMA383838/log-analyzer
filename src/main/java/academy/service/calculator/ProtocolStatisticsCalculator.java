package academy.service.calculator;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Калькулятор статистики по используемым протоколам. Извлекает и анализирует протоколы из запросов (HTTP/1.0, HTTP/1.1,
 * HTTP/2) и определяет уникальные протоколы в логах.
 */
public class ProtocolStatisticsCalculator implements StatisticsCalculator {
    private static final Logger logger = LogManager.getLogger(ProtocolStatisticsCalculator.class);

    @Override
    public void calculate(List<LogEntry> logs, LogAnalysisResult result) {
        logger.debug("Расчет статистики протоколов");

        Set<String> protocols = logs.stream()
                .map(LogEntry::getProtocol)
                .filter(protocol -> protocol != null && !protocol.isEmpty())
                .collect(Collectors.toSet());

        result.setUniqueProtocols(new ArrayList<>(protocols));
    }
}
