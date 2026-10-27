package academy.service.generators;

import academy.model.LogAnalysisResult;
import java.io.IOException;

/**
 * Интерфейс для всех генераторов отчетов. Определяет контракт для генерации отчетов в различных форматах на основе
 * результатов анализа логов.
 */
public interface ReportGenerator {
    void generateReport(LogAnalysisResult result, String outputPath) throws IOException;
}
