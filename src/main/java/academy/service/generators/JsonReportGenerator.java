package academy.service.generators;

import academy.model.LogAnalysisResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Генератор отчетов в формате JSON. Использует Jackson ObjectMapper для сериализации объекта результата в красиво
 * отформатированный JSON файл.
 */
class JsonReportGenerator implements ReportGenerator {
    private static final Logger logger = LogManager.getLogger(JsonReportGenerator.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void generateReport(LogAnalysisResult result, String outputPath) throws IOException {
        logger.info("Генерация JSON отчета в: {}", outputPath);
        if (result.getFiles() != null) {
            List<String> fileNames = result.getFiles().stream()
                    .map(path -> {
                        if (path.startsWith("http") || path.startsWith("https")) {
                            return "web_logs";
                        } else {
                            return Path.of(path).getFileName().toString();
                        }
                    })
                    .collect(Collectors.toList());
            result.setFiles(fileNames);
        }

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        Files.write(Path.of(outputPath), json.getBytes());
    }
}
