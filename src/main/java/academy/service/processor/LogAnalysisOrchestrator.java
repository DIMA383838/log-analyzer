package academy.service.processor;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import academy.service.calculator.DateStatisticsCalculator;
import academy.service.calculator.ProtocolStatisticsCalculator;
import academy.service.calculator.ResourceStatisticsCalculator;
import academy.service.calculator.ResponseCodeCalculator;
import academy.service.calculator.ResponseSizeCalculator;
import academy.service.calculator.StatisticsCalculator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Оркестратор расчета статистики логов. Координирует работу различных калькуляторов статистики и собирает результаты в
 * единый объект LogAnalysisResult.
 */
public class LogAnalysisOrchestrator {
    private static final Logger logger = LogManager.getLogger(LogAnalysisOrchestrator.class);

    private final List<StatisticsCalculator> calculators;

    public LogAnalysisOrchestrator() {
        this.calculators = List.of(
                new ResponseSizeCalculator(),
                new ResourceStatisticsCalculator(),
                new ResponseCodeCalculator(),
                new DateStatisticsCalculator(),
                new ProtocolStatisticsCalculator());
    }

    // Конструктор для тестирования
    public LogAnalysisOrchestrator(List<StatisticsCalculator> calculators) {
        this.calculators = calculators;
    }

    public LogAnalysisResult calculateStatistics(List<LogEntry> logs, List<String> files) {
        logger.info("Расчет статистики для {} записей логов", logs.size());

        LogAnalysisResult result = new LogAnalysisResult();
        result.setFiles(files);
        result.setTotalRequestsCount(logs.size());

        calculators.forEach(calculator -> calculator.calculate(logs, result));

        return result;
    }
}
