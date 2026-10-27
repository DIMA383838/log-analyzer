package academy.service.calculator;

import static academy.service.TestDataHelper.createLogEntry;
import static academy.service.TestDataHelper.createSampleLogs;
import static org.assertj.core.api.Assertions.assertThat;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResourceStatisticsCalculatorTest {

    private ResourceStatisticsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ResourceStatisticsCalculator();
    }

    @Test
    void calculate_shouldHandleEmptyLogs() {
        // given
        List<LogEntry> logs = List.of();
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getResources()).isEmpty();
    }

    @Test
    void calculate_shouldCountResourcesCorrectly() {
        // given
        List<LogEntry> logs = createSampleLogs();
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getResources()).hasSize(3);
        assertThat(result.getResources().get(0).resource()).isEqualTo("/api/data");
        assertThat(result.getResources().get(0).totalRequestsCount()).isEqualTo(2);
        assertThat(result.getResources().get(1).resource()).isEqualTo("/index.html");
        assertThat(result.getResources().get(1).totalRequestsCount()).isEqualTo(2);
    }

    @Test
    void calculate_shouldLimitToTop10Resources() {
        // given
        List<LogEntry> logs = List.of(
                createLogEntry("1.1.1.1", "/res1", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res1", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res2", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res3", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res4", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res5", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res6", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res7", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res8", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res9", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res10", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/res11", 200, 100L, "HTTP/1.1", null));
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getResources()).hasSize(10);
    }
}
