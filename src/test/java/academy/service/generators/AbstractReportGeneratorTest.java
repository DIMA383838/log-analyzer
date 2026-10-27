package academy.service.generators;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.LogAnalysisResult;
import academy.service.TestDataHelper;
import java.util.List;
import org.junit.jupiter.api.Test;

class AbstractReportGeneratorTest {

    private final AbstractReportGenerator generator = new TestReportGenerator();

    // Тестовый генератор для тестирования абстрактного класса
    private static class TestReportGenerator extends AbstractReportGenerator {
        @Override
        public void generateReport(LogAnalysisResult result, String outputPath) {
            // Не реализовано для тестов
        }

        @Override
        protected String escapeContent(String text) {
            return text != null ? text.replace("|", "\\|") : "";
        }

        @Override
        protected String formatHeader(String title) {
            return "# " + title + "\n";
        }

        @Override
        protected String formatTableRow(List<String> cells) {
            return "| " + String.join(" | ", cells) + " |\n";
        }

        @Override
        protected String formatSection(String title) {
            return "## " + title + "\n";
        }
    }

    @Test
    void formatFiles_shouldFormatLocalFiles() {
        // given
        List<String> files = List.of("/var/log/access.log", "error.log");

        // when
        String result = generator.formatFiles(files);

        // then
        assertThat(result).isEqualTo("access.log, error.log");
    }

    @Test
    void formatFiles_shouldFormatWebSources() {
        // given
        List<String> files = List.of("http://example.com/logs", "https://site.com/access.log");

        // when
        String result = generator.formatFiles(files);

        // then
        assertThat(result).isEqualTo("Веб-источник, Веб-источник");
    }

    @Test
    void getStatusDescription_shouldReturnCorrectDescriptions() {
        // when & then
        assertThat(generator.getStatusDescription(200)).isEqualTo("OK");
        assertThat(generator.getStatusDescription(404)).isEqualTo("Not Found");
        assertThat(generator.getStatusDescription(500)).isEqualTo("Internal Server Error");
        assertThat(generator.getStatusDescription(999)).isEqualTo("Unknown");
    }

    @Test
    void getTopResources_shouldReturnLimitedResources() {
        // given
        LogAnalysisResult result = TestDataHelper.createSampleResult();

        // when
        var topResources = generator.getTopResources(result, 2);

        // then
        assertThat(topResources).hasSize(2);
        assertThat(topResources.get(0).resource()).isEqualTo("/index.html");
        assertThat(topResources.get(1).resource()).isEqualTo("/api/data");
    }
}
