package academy;

import academy.model.LogAnalysisResult;
import academy.model.OutputFormat;
import academy.service.exception.ParameterValidationException;
import academy.service.generators.ReportGenerator;
import academy.service.generators.ReportGeneratorFactory;
import academy.service.processor.LogProcessor;
import academy.service.validation.ValidationService;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Основной класс приложения для анализа NGINX логов. Реализует интерфейс Callable для работы с командной строкой через
 * picocli. Обрабатывает параметры командной строки, валидирует их, выполняет анализ логов и генерирует отчет в
 * указанном формате.
 */
@Command(name = "log-analyzer", version = "1.0", mixinStandardHelpOptions = true, description = "NGINX log analyzer")
public class Application implements Callable<Integer> {
    private static final Logger logger = LogManager.getLogger(Application.class);

    @Option(
            names = {"--path", "-p"},
            required = true,
            description = "Path to NGINX log files (local or remote)",
            arity = "1..*")
    private String[] paths;

    @Option(
            names = {"--format", "-f"},
            description = "Output format: json, markdown, adoc")
    private String format;

    @Option(
            names = {"--output", "-o"},
            required = true,
            description = "Path to save the result")
    private String outputPath;

    @Option(
            names = {"--from"},
            description = "Start date in ISO8601 format")
    private String fromDate;

    @Option(
            names = {"--to"},
            description = "End date in ISO8601 format")
    private String toDate;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            logger.info("Starting log analyzer");
            // Валидация параметров
            ValidationService validationService = new ValidationService();
            validationService.validateParameters(this);

            // Загрузка и обработка логов
            LogProcessor processor = new LogProcessor();
            LogAnalysisResult result = processor.processLogs(paths, fromDate, toDate);

            // Генерация отчета
            ReportGenerator generator = ReportGeneratorFactory.createGenerator(getOutputFormat());
            generator.generateReport(result, outputPath);

            logger.info("Analysis completed successfully. Result saved to: {}", outputPath);
            System.out.println(0);
            return 0;

        } catch (ParameterValidationException e) {
            logger.error("Parameter validation error: {}", e.getMessage());
            System.out.println(2);
            return 2;
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            System.out.println(1);
            return 1;
        }
    }

    public String[] getInputPaths() {
        return paths;
    }

    public String getFormat() {
        return format;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public OutputFormat getOutputFormat() {
        return OutputFormat.fromString(format);
    }
}
