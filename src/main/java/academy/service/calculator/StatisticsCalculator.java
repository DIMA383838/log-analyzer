package academy.service.calculator;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.util.List;

/**
 * Интерфейс для всех калькуляторов статистики. Определяет контракт для расчета различных метрик на основе списка
 * лог-записей и заполнения объекта результата.
 */
public interface StatisticsCalculator {
    void calculate(List<LogEntry> logs, LogAnalysisResult result);
}
