package academy.service.generators;

import academy.model.LogAnalysisResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Генератор отчетов в формате Markdown. Создает отчеты с таблицами и заголовками в синтаксисе Markdown, подходящие для
 * чтения в GitHub и других Markdown-редакторах.
 */
public class MarkdownReportGenerator extends AbstractReportGenerator {
    private static final Logger logger = LogManager.getLogger(MarkdownReportGenerator.class);

    @Override
    public void generateReport(LogAnalysisResult result, String outputPath) throws IOException {
        logger.info("Генерация Markdown отчета в: {}", outputPath);

        StringBuilder markdown = new StringBuilder();

        // Заголовок
        markdown.append("# Анализ логов веб-сервера%n%n");

        // Общая информация
        markdown.append(formatSection("Общая информация"));
        markdown.append(createGeneralInfoTable(result));

        // Статистика размеров ответов
        markdown.append(formatSection("Статистика размеров ответов"));
        markdown.append(createResponseSizeTable(result));

        // Запрашиваемые ресурсы
        markdown.append(formatSection("Запрашиваемые ресурсы"));
        markdown.append(createResourcesTable(result));

        // Коды ответа
        markdown.append(formatSection("Коды ответа"));
        markdown.append(createResponseCodesTable(result));

        // Статистика по датам
        markdown.append(formatSection("Распределение запросов по датам"));
        markdown.append(createDateStatisticsTable(result));

        Files.write(Path.of(outputPath), String.format(markdown.toString()).getBytes());
    }

    private String createGeneralInfoTable(LogAnalysisResult result) {
        StringBuilder table = new StringBuilder();
        table.append("| Метрика | Значение |%n");
        table.append("|:--------|---------:|%n");
        table.append(String.format("| Файл(-ы) | %s |%n", escapeContent(formatFiles(result.getFiles()))));
        table.append(String.format("| Количество запросов | %,d |%n", result.getTotalRequestsCount()));
        table.append(String.format(
                "| Уникальные протоколы | %s |%n", escapeContent(formatProtocols(result.getUniqueProtocols()))));
        table.append("%n");
        return String.format(table.toString());
    }

    private String createResponseSizeTable(LogAnalysisResult result) {
        StringBuilder table = new StringBuilder();
        table.append("| Метрика | Размер (байты) |%n");
        table.append("|:--------|---------------:|%n");
        table.append(String.format(
                "| Средний размер ответа | %.2f |%n",
                result.getResponseSizeInBytes().average()));
        table.append(String.format(
                "| Максимальный размер ответа | %.0f |%n",
                result.getResponseSizeInBytes().max()));
        table.append(String.format(
                "| 95-й процентиль | %.0f |%n", result.getResponseSizeInBytes().p95()));
        table.append("%n");
        return String.format(table.toString());
    }

    private String createResourcesTable(LogAnalysisResult result) {
        StringBuilder table = new StringBuilder();
        table.append("| Ресурс | Количество |%n");
        table.append("|:-------|-----------:|%n");

        getTopResources(result, 10)
                .forEach(resource -> table.append(String.format(
                        "| `%s` | %,d |%n",
                        escapeContent(truncateResource(resource.resource(), 60)), resource.totalRequestsCount())));
        table.append("%n");
        return String.format(table.toString());
    }

    private String createResponseCodesTable(LogAnalysisResult result) {
        StringBuilder table = new StringBuilder();
        table.append("| Код | Описание | Количество |%n");
        table.append("|:---:|:---------|-----------:|%n");

        getSortedResponseCodes(result)
                .forEach(code -> table.append(String.format(
                        "| %d | %s | %,d |%n",
                        code.code(), getStatusDescription(code.code()), code.totalResponsesCount())));
        table.append("%n");
        return String.format(table.toString());
    }

    private String createDateStatisticsTable(LogAnalysisResult result) {
        StringBuilder table = new StringBuilder();
        table.append("| Дата | День недели | Количество | Доля |%n");
        table.append("|:-----|:------------|-----------:|-----:|%n");

        getSortedRequestsPerDate(result)
                .forEach(dateStat -> table.append(String.format(
                        "| %s | %s | %,d | %.2f%% |%n",
                        dateStat.date(),
                        dateStat.weekday(),
                        dateStat.totalRequestsCount(),
                        dateStat.totalRequestsPercentage())));

        LogAnalysisResult.RequestsPerDate maxDay = getMaxRequestsDay(result);
        if (maxDay != null) {
            table.append(String.format(
                    "%n**Наибольшая нагрузка:** %s (%s) - %,d запросов (%.2f%%)%n",
                    maxDay.date(), maxDay.weekday(), maxDay.totalRequestsCount(), maxDay.totalRequestsPercentage()));
        }
        table.append("%n");
        return String.format(table.toString());
    }

    @Override
    protected String escapeContent(String text) {
        if (text == null) return "";
        return text.replace("|", "\\|").replace("*", "\\*").replace("_", "\\_").replace("`", "\\`");
    }

    @Override
    protected String formatHeader(String title) {
        return "# " + title + "%n%n";
    }

    @Override
    protected String formatTableRow(List<String> cells) {
        return "| " + String.join(" | ", cells) + " |%n";
    }

    @Override
    protected String formatSection(String title) {
        return "## " + title + "%n%n";
    }
}
