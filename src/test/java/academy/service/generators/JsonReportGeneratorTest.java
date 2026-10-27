package academy.service.generators;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.LogAnalysisResult;
import academy.service.TestDataHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonReportGeneratorTest {

    private final JsonReportGenerator generator = new JsonReportGenerator();

    @TempDir
    Path tempDir;

    @Test
    void generateReport_shouldCreateJsonFile() throws IOException {
        // given
        LogAnalysisResult result = TestDataHelper.createSampleResult();
        Path outputFile = tempDir.resolve("report.json");

        // when
        generator.generateReport(result, outputFile.toString());

        // then
        assertThat(outputFile).exists();

        String content = Files.readString(outputFile);
        assertThat(content).contains("\"totalRequestsCount\" : 150");
        assertThat(content).contains("\"files\" : [");
        assertThat(content).contains("\"resources\" : [");
    }

    @Test
    void generateReport_shouldFormatWebSources() throws IOException {
        // given
        LogAnalysisResult result = TestDataHelper.createSampleResult();
        result.setFiles(List.of("http://example.com/logs", "local.log"));
        Path outputFile = tempDir.resolve("web_report.json");

        // when
        generator.generateReport(result, outputFile.toString());

        // then
        String content = Files.readString(outputFile);
        assertThat(content).contains("web_logs");
        assertThat(content).contains("local.log");
    }

    @Test
    void generateReport_shouldHandleEmptyResult() throws IOException {
        // given
        LogAnalysisResult result = TestDataHelper.createEmptyResult();
        Path outputFile = tempDir.resolve("empty.json");

        // when
        generator.generateReport(result, outputFile.toString());

        // then
        assertThat(outputFile).exists();

        String content = Files.readString(outputFile);
        assertThat(content).contains("\"totalRequestsCount\" : 0");
        assertThat(content).contains("\"files\" : [ ]");
    }
}
