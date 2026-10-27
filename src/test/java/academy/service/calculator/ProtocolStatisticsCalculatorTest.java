package academy.service.calculator;

import static academy.service.TestDataHelper.createLogEntry;
import static academy.service.TestDataHelper.createSampleLogs;
import static org.assertj.core.api.Assertions.assertThat;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProtocolStatisticsCalculatorTest {

    private ProtocolStatisticsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ProtocolStatisticsCalculator();
    }

    @Test
    void calculate_shouldHandleEmptyLogs() {
        // given
        List<LogEntry> logs = List.of();
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getUniqueProtocols()).isEmpty();
    }

    @Test
    void calculate_shouldExtractUniqueProtocols() {
        // given
        List<LogEntry> logs = createSampleLogs();
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getUniqueProtocols()).hasSize(3);
        assertThat(result.getUniqueProtocols()).containsExactlyInAnyOrder("HTTP/1.1", "HTTP/1.0", "HTTP/2.0");
    }

    @Test
    void calculate_shouldFilterEmptyProtocols() {
        // given
        List<LogEntry> logs = List.of(
                createLogEntry("1.1.1.1", "/test", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/test", 200, 100L, "", null),
                createLogEntry("1.1.1.1", "/test", 200, 100L, null, null),
                createLogEntry("1.1.1.1", "/test", 200, 100L, "HTTP/2.0", null));
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getUniqueProtocols()).hasSize(3);
        assertThat(result.getUniqueProtocols()).containsExactlyInAnyOrder("null", "HTTP/1.1", "HTTP/2.0");
    }
}
