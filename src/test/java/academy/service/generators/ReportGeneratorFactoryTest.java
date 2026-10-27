package academy.service.generators;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.OutputFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ReportGeneratorFactoryTest {

    @ParameterizedTest
    @EnumSource(OutputFormat.class)
    void createGenerator_shouldCreateGeneratorForAllFormats(OutputFormat format) {
        // when
        ReportGenerator generator = ReportGeneratorFactory.createGenerator(format);

        // then
        assertThat(generator).isNotNull();
    }

    @Test
    void createGenerator_shouldCreateCorrectGeneratorTypes() {
        // when & then
        assertThat(ReportGeneratorFactory.createGenerator(OutputFormat.JSON)).isInstanceOf(JsonReportGenerator.class);

        assertThat(ReportGeneratorFactory.createGenerator(OutputFormat.MARKDOWN))
                .isInstanceOf(MarkdownReportGenerator.class);

        assertThat(ReportGeneratorFactory.createGenerator(OutputFormat.ADOC)).isInstanceOf(ADOCReportGenerator.class);
    }
}
