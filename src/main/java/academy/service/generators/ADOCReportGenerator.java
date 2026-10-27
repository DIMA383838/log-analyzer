package academy.service.generators;

import academy.model.LogAnalysisResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Генератор отчетов в формате AsciiDoc (ADOC). Создает структурированные отчеты с таблицами и разделами в соответствии
 * с синтаксисом AsciiDoc.
 */
public class ADOCReportGenerator extends AbstractReportGenerator {
    private static final Logger logger = LogManager.getLogger(ADOCReportGenerator.class);

    @Override
    public void generateReport(LogAnalysisResult result, String outputPath) throws IOException {
        logger.info("Генерация отчёта ADOC в {}", outputPath);

        StringBuilder adoc = new StringBuilder();

        // Заголовок и мета-информация
        adoc.append("[.text-center]%n");
        adoc.append("= Анализ логов веб-сервера%n%n");

        adoc.append("generated: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("%n");
        adoc.append("analysis-date: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .append("%n%n");

        // Введение
        adoc.append("Данный отчет содержит детальный статистический анализ логов, ")
                .append("включая информацию о запросах, ресурсах, кодах ответов и временных характеристиках.%n%n");

        // Секции отчета
        adoc.append(createGeneralInfoSection(result));
        adoc.append(createResponseSizeSection(result));
        adoc.append(createResourcesSection(result));
        adoc.append(createResponseCodesSection(result));
        adoc.append(createDateStatisticsSection(result));

        Files.write(Path.of(outputPath), String.format(adoc.toString()).getBytes());
        logger.info("ADOC отчет успешно сгенерирован: {}", outputPath);
    }

    private String createGeneralInfoSection(LogAnalysisResult result) {
        StringBuilder section = new StringBuilder();
        section.append(formatSection("Общая информация"));
        section.append("|===%n");
        section.append("| Метрика | Значение%n");
        section.append(String.format("| Анализируемые файлы | %s%n", escapeContent(formatFiles(result.getFiles()))));
        section.append(String.format("| Общее количество запросов | %,d%n", result.getTotalRequestsCount()));
        section.append(String.format(
                "| Уникальные протоколы | %s%n", escapeContent(formatProtocols(result.getUniqueProtocols()))));
        section.append("|===%n%n");
        return String.format(section.toString());
    }

    private String createResponseSizeSection(LogAnalysisResult result) {
        StringBuilder section = new StringBuilder();
        section.append(formatSection("Статистика размеров ответов"));
        section.append("|===%n");
        section.append("| Метрика | Размер (байты)%n");
        section.append(String.format(
                "| Средний размер ответа | %,.2f%n",
                result.getResponseSizeInBytes().average()));
        section.append(String.format(
                "| Максимальный размер ответа | %.0f%n",
                result.getResponseSizeInBytes().max()));
        section.append(String.format(
                "| 95-й процентиль | %,.1f%n", result.getResponseSizeInBytes().p95()));
        section.append("|===%n%n");
        return String.format(section.toString());
    }

    private String createResourcesSection(LogAnalysisResult result) {
        StringBuilder section = new StringBuilder();
        section.append(formatSection("Топ запрашиваемых ресурсов"));
        section.append("|===%n");
        section.append("| Ресурс | Количество запросов%n");

        getTopResources(result, 15)
                .forEach(resource -> section.append(String.format(
                        "| `%s` | %,d%n",
                        escapeContent(truncateResource(resource.resource(), 80)), resource.totalRequestsCount())));

        if (result.getResources().size() > 15) {
            section.append(String.format(
                    "_Показано топ-15 ресурсов из " + result.getResources().size() + "_%n"));
        }
        section.append("|===%n%n");
        return String.format(section.toString());
    }

    private String createResponseCodesSection(LogAnalysisResult result) {
        StringBuilder section = new StringBuilder();
        section.append(formatSection("Статистика по кодам ответа"));
        section.append("|===%n");
        section.append("| Код | Описание | Количество%n");

        getSortedResponseCodes(result)
                .forEach(code -> section.append(String.format(
                        "| %d | %s | %,d%n",
                        code.code(), getStatusDescription(code.code()), code.totalResponsesCount())));
        section.append("|===%n%n");
        return String.format(section.toString());
    }

    private String createDateStatisticsSection(LogAnalysisResult result) {
        StringBuilder section = new StringBuilder();
        section.append(formatSection("Распределение запросов по датам"));
        section.append("|===%n");
        section.append("| Дата | День недели | Количество запросов | Доля%n");

        getSortedRequestsPerDate(result)
                .forEach(dateStat -> section.append(String.format(
                        "| %s | %s | %,d | %.2f%%%n",
                        dateStat.date(),
                        dateStat.weekday(),
                        dateStat.totalRequestsCount(),
                        dateStat.totalRequestsPercentage())));
        section.append("|===%n%n");

        LogAnalysisResult.RequestsPerDate maxDay = getMaxRequestsDay(result);
        if (maxDay != null) {
            section.append(String.format(
                    "Наибольшая нагрузка: *%s* (%s) - %,d запросов (%.2f%%)%n%n",
                    maxDay.date(), maxDay.weekday(), maxDay.totalRequestsCount(), maxDay.totalRequestsPercentage()));
        }
        return String.format(section.toString());
    }

    @Override
    protected String escapeContent(String text) {
        if (text == null) return "";
        return text.replace("|", "\\|")
                .replace("*", "\\*")
                .replace("_", "\\_")
                .replace("`", "\\`")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("#", "\\#");
    }

    @Override
    protected String formatHeader(String title) {
        return "= " + title + "%n%n";
    }

    @Override
    protected String formatTableRow(List<String> cells) {
        return "| " + String.join(" | ", cells) + "%n";
    }

    @Override
    protected String formatSection(String title) {
        return "== " + title + "%n%n";
    }
}
