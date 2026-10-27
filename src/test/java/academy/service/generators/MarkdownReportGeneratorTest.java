package academy.service.generators;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.LogAnalysisResult;
import academy.service.TestDataHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MarkdownReportGeneratorTest {

    private final MarkdownReportGenerator generator = new MarkdownReportGenerator();

    @TempDir
    Path tempDir;

    @Test
    void generateReport_shouldHandleEmptyResult() throws IOException {
        // given
        LogAnalysisResult result = TestDataHelper.createEmptyResult();
        Path outputFile = tempDir.resolve("empty.md");

        // when
        generator.generateReport(result, outputFile.toString());

        // then
        assertThat(outputFile).exists();

        String content = Files.readString(outputFile);
        assertThat(content).contains("Количество запросов | 0 |");
    }

    @Test
    void escapeContent_shouldEscapeMarkdownCharacters() {
        // when & then
        assertThat(generator.escapeContent("text|with|pipes")).isEqualTo("text\\|with\\|pipes");

        assertThat(generator.escapeContent("**bold** _italic_")).isEqualTo("\\*\\*bold\\*\\* \\_italic\\_");

        assertThat(generator.escapeContent("`code`")).isEqualTo("\\`code\\`");
    }
}
