package academy.service.generators;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ADOCReportGeneratorTest {

    private final ADOCReportGenerator generator = new ADOCReportGenerator();

    @TempDir
    Path tempDir;

    @Test
    void escapeContent_shouldEscapeAdocCharacters() {
        // when & then
        assertThat(generator.escapeContent("text|with|pipes")).isEqualTo("text\\|with\\|pipes");

        assertThat(generator.escapeContent("*bold* _italic_")).isEqualTo("\\*bold\\* \\_italic\\_");

        assertThat(generator.escapeContent("`code` [link] #header")).isEqualTo("\\`code\\` \\[link\\] \\#header");
    }
}
