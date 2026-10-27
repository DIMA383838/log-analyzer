package academy.service.calculator;

import static academy.service.TestDataHelper.createLogEntry;
import static academy.service.TestDataHelper.createSampleLogs;
import static org.assertj.core.api.Assertions.assertThat;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseCodeCalculatorTest {

    private ResponseCodeCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ResponseCodeCalculator();
    }

    @Test
    void calculate_shouldHandleEmptyLogs() {
        // given
        List<LogEntry> logs = List.of();
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getResponseCodes()).isEmpty();
    }

    @Test
    void calculate_shouldCountResponseCodesCorrectly() {
        // given
        List<LogEntry> logs = createSampleLogs();
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getResponseCodes()).hasSize(4);
        assertThat(result.getResponseCodes().get(0).code()).isEqualTo(200);
        assertThat(result.getResponseCodes().get(0).totalResponsesCount()).isEqualTo(2);
        assertThat(result.getResponseCodes().get(1).code()).isEqualTo(500);
        assertThat(result.getResponseCodes().get(1).totalResponsesCount()).isEqualTo(1);
    }

    @Test
    void calculate_shouldSortByCountDescending() {
        // given
        List<LogEntry> logs = List.of(
                createLogEntry("1.1.1.1", "/test", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/test", 200, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/test", 404, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/test", 500, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/test", 500, 100L, "HTTP/1.1", null),
                createLogEntry("1.1.1.1", "/test", 500, 100L, "HTTP/1.1", null));
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getResponseCodes().get(0).code()).isEqualTo(500);
        assertThat(result.getResponseCodes().get(0).totalResponsesCount()).isEqualTo(3);
        assertThat(result.getResponseCodes().get(1).code()).isEqualTo(200);
        assertThat(result.getResponseCodes().get(1).totalResponsesCount()).isEqualTo(2);
        assertThat(result.getResponseCodes().get(2).code()).isEqualTo(404);
        assertThat(result.getResponseCodes().get(2).totalResponsesCount()).isEqualTo(1);
    }
}
