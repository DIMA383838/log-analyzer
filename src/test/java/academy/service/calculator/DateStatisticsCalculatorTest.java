package academy.service.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import academy.service.TestDataHelper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DateStatisticsCalculatorTest {

    private DateStatisticsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DateStatisticsCalculator();
    }

    @Test
    void calculate_shouldHandleEmptyLogs() {
        // given
        List<LogEntry> logs = List.of();
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getRequestsPerDate()).isEmpty();
    }

    @Test
    void calculate_shouldGroupByDateCorrectly() {
        // given
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 15, 14, 0);
        LocalDateTime time3 = LocalDateTime.of(2024, 1, 16, 10, 0);

        List<LogEntry> logs = List.of(
                TestDataHelper.createLogEntry("1.1.1.1", "/test", 200, 100L, "HTTP/1.1", time1),
                TestDataHelper.createLogEntry("1.1.1.1", "/test", 200, 100L, "HTTP/1.1", time2),
                TestDataHelper.createLogEntry("1.1.1.1", "/test", 200, 100L, "HTTP/1.1", time3));
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getRequestsPerDate()).hasSize(2);
        assertThat(result.getRequestsPerDate().get(0).date()).isEqualTo("2024-01-15");
        assertThat(result.getRequestsPerDate().get(0).totalRequestsCount()).isEqualTo(2);
        assertThat(result.getRequestsPerDate().get(0).totalRequestsPercentage()).isEqualTo(66.67);
        assertThat(result.getRequestsPerDate().get(1).date()).isEqualTo("2024-01-16");
        assertThat(result.getRequestsPerDate().get(1).totalRequestsCount()).isEqualTo(1);
        assertThat(result.getRequestsPerDate().get(1).totalRequestsPercentage()).isEqualTo(33.33);
    }

    @Test
    void calculate_shouldSortByDateAscending() {
        // given
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 16, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime time3 = LocalDateTime.of(2024, 1, 17, 10, 0);

        List<LogEntry> logs = List.of(
                TestDataHelper.createLogEntry("1.1.1.1", "/test", 200, 100L, "HTTP/1.1", time1),
                TestDataHelper.createLogEntry("1.1.1.1", "/test", 200, 100L, "HTTP/1.1", time2),
                TestDataHelper.createLogEntry("1.1.1.1", "/test", 200, 100L, "HTTP/1.1", time3));
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getRequestsPerDate().get(0).date()).isEqualTo("2024-01-15");
        assertThat(result.getRequestsPerDate().get(1).date()).isEqualTo("2024-01-16");
        assertThat(result.getRequestsPerDate().get(2).date()).isEqualTo("2024-01-17");
    }
}
