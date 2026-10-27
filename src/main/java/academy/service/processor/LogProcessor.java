package academy.service.processor;

import static academy.service.validation.DateTimeUtils.parseDateTime;

import academy.model.*;
import academy.service.exception.ParameterValidationException;
import academy.service.parser.LogParser;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Основной процессор для обработки лог-файлов. Читает файлы из различных источников, парсит записи, применяет фильтры и
 * вычисляет статистику.
 */
public class LogProcessor {
    private static final Logger logger = LogManager.getLogger(LogProcessor.class);

    private final LogParser logParser;
    private final LogAnalysisOrchestrator statisticsCalculator;
    private final LogReaderFactory readerFactory;

    // Конструктор для production
    public LogProcessor() {
        this(new LogParser(), new LogAnalysisOrchestrator(), new LogReaderFactory());
    }

    // Конструктор для тестирования
    public LogProcessor(LogParser parser, LogAnalysisOrchestrator calculator, LogReaderFactory factory) {
        this.logParser = parser;
        this.statisticsCalculator = calculator;
        this.readerFactory = factory;
    }

    public LogAnalysisResult processLogs(String[] paths, String fromDate, String toDate) {
        DateFilter dateFilter = createDateFilter(fromDate, toDate);
        List<LogEntry> allLogs = new ArrayList<>();
        List<String> processedFiles = new ArrayList<>();

        for (String path : paths) {
            try {
                logger.info("Обработка файла: {}", path);
                List<LogEntry> fileLogs = processSource(path, dateFilter);
                allLogs.addAll(fileLogs);
                processedFiles.add(path);
            } catch (Exception e) {
                logger.error("Ошибка обработки файла {}: {}", path, e.getMessage());
                throw new ParameterValidationException("Не удалось обработать файл: " + path);
            }
        }

        return statisticsCalculator.calculateStatistics(allLogs, processedFiles);
    }

    private List<LogEntry> processSource(String source, DateFilter dateFilter) throws IOException {
        LogReader reader = readerFactory.createReader(source);
        List<String> lines = reader.readLines(source);

        return lines.stream()
                .map(logParser::parseLine)
                .filter(Objects::nonNull)
                .filter(dateFilter::shouldInclude)
                .collect(Collectors.toList());
    }

    private DateFilter createDateFilter(String fromDate, String toDate) {
        LocalDateTime from = parseDateTime(fromDate);
        LocalDateTime to = parseDateTime(toDate);
        return new DateFilter(from, to);
    }
}
