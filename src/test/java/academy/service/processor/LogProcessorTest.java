// academy/service/processor/LogProcessorTest.java
package academy.service.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.model.LogAnalysisResult;
import academy.service.exception.ParameterValidationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LogProcessorTest {

    private LogProcessor logProcessor;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        logProcessor = new LogProcessor();
    }

    @Test
    void processLogs_shouldProcessLocalFileSuccessfully() throws Exception {
        // given
        List<String> logLines = createSampleLogLines();
        String filePath = createTestLogFile(logLines);
        String[] paths = {filePath};

        // when
        LogAnalysisResult result = logProcessor.processLogs(paths, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFiles()).containsExactly(filePath);
        assertThat(result.getTotalRequestsCount()).isEqualTo(3);
    }

    @Test
    void processLogs_shouldHandleEmptyFile() throws Exception {
        // given
        String filePath = createTestLogFile(List.of());
        String[] paths = {filePath};

        // when
        LogAnalysisResult result = logProcessor.processLogs(paths, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalRequestsCount()).isEqualTo(0);
        assertThat(result.getFiles()).containsExactly(filePath);
    }

    @Test
    void processLogs_shouldProcessMultipleFiles() throws Exception {
        // given
        List<String> lines1 =
                List.of("192.168.1.1 - - [17/May/2024:10:30:45 +0000] \"GET /page1 HTTP/1.1\" 200 1000 \"-\" \"-\"");
        List<String> lines2 =
                List.of("192.168.1.2 - - [17/May/2024:11:30:45 +0000] \"GET /page2 HTTP/1.1\" 200 1000 \"-\" \"-\"");

        String file1 = createTestLogFile(lines1);
        String file2 = createTestLogFile(lines2);
        String[] paths = {file1, file2};

        // when
        LogAnalysisResult result = logProcessor.processLogs(paths, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalRequestsCount()).isEqualTo(2);
        assertThat(result.getFiles()).containsExactly(file1, file2);
    }

    private String createTestLogFile(List<String> lines) throws IOException {
        File file = tempDir.resolve("test.log").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        return file.getAbsolutePath();
    }

    private List<String> createSampleLogLines() {
        return List.of(
                "192.168.1.1 - john [17/May/2024:10:30:45 +0000] \"GET /index.html HTTP/1.1\" 200 1500 \"http://example.com\" \"Mozilla/5.0\"",
                "192.168.1.2 - mary [17/May/2024:11:30:45 +0000] \"POST /api/data HTTP/1.1\" 201 800 \"http://app.com\" \"Chrome\"",
                "192.168.1.3 - bob [18/May/2024:10:30:45 +0000] \"GET /about.html HTTP/1.1\" 200 1200 \"http://site.com\" \"Safari\"");
    }

    @Test
    void processLogs_shouldThrowExceptionForNonExistentFile() {
        // given
        String[] paths = {"/non/existent/file.log"};

        // when & then
        assertThatThrownBy(() -> logProcessor.processLogs(paths, null, null))
                .isInstanceOf(ParameterValidationException.class)
                .hasMessageContaining("Не удалось обработать файл");
    }

    @Test
    void processLogs_shouldHandleFilesWithInvalidLines() throws Exception {
        // given
        List<String> mixedLines = List.of(
                "192.168.1.1 - - [17/May/2024:10:30:45 +0000] \"GET /valid HTTP/1.1\" 200 1000 \"-\" \"-\"",
                "invalid log line",
                "192.168.1.2 - - [17/May/2024:11:30:45 +0000] \"GET /also-valid HTTP/1.1\" 200 1000 \"-\" \"-\"");

        String filePath = createTestLogFile(mixedLines);
        String[] paths = {filePath};

        // when
        LogAnalysisResult result = logProcessor.processLogs(paths, null, null);

        // then - должны быть обработаны только валидные строки
        assertThat(result).isNotNull();
        assertThat(result.getTotalRequestsCount()).isEqualTo(2);
    }

    @Test
    void processLogs_shouldHandleFilesWithMalformedDates() throws Exception {
        // given
        List<String> linesWithBadDate = List.of(
                "192.168.1.1 - - [invalid-date] \"GET /test HTTP/1.1\" 200 1000 \"-\" \"-\"",
                "192.168.1.2 - - [17/May/2024:10:30:45 +0000] \"GET /good HTTP/1.1\" 200 1000 \"-\" \"-\"");

        String filePath = createTestLogFile(linesWithBadDate);
        String[] paths = {filePath};

        // when
        LogAnalysisResult result = logProcessor.processLogs(paths, null, null);

        // then - строка с невалидной датой должна быть пропущена
        assertThat(result).isNotNull();
        assertThat(result.getTotalRequestsCount()).isEqualTo(1);
    }

    @Test
    void processLogs_shouldIncludeAllWhenNoDateFilter() throws Exception {
        // given
        List<String> logLines = createSampleLogLines();
        String filePath = createTestLogFile(logLines);
        String[] paths = {filePath};

        // when
        LogAnalysisResult result = logProcessor.processLogs(paths, null, null);

        // then
        assertThat(result.getTotalRequestsCount()).isEqualTo(3);
    }

    @Test
    void processLogs_shouldFilterByFromDate() throws Exception {
        // given
        List<String> logLines = createSampleLogLines();
        String filePath = createTestLogFile(logLines);
        String[] paths = {filePath};

        // Фильтруем записи начиная с 18 мая
        String fromDate = "2024-05-18";

        // when
        LogAnalysisResult result = logProcessor.processLogs(paths, fromDate, null);

        // then
        assertThat(result.getTotalRequestsCount()).isEqualTo(1); // только запись за 18 мая
    }
}
