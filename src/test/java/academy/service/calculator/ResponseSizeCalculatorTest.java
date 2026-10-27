package academy.service.calculator;

import static academy.service.TestDataHelper.createLogsForPercentile;
import static academy.service.TestDataHelper.createSampleLogs;
import static org.assertj.core.api.Assertions.assertThat;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseSizeCalculatorTest {

    private ResponseSizeCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ResponseSizeCalculator();
    }

    @Test
    void calculate_shouldHandleEmptyLogs() {
        // given
        List<LogEntry> logs = List.of();
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getResponseSizeInBytes().average()).isEqualTo(0);
        assertThat(result.getResponseSizeInBytes().max()).isEqualTo(0);
        assertThat(result.getResponseSizeInBytes().p95()).isEqualTo(0);
    }

    @Test
    void calculate_shouldCalculateCorrectStatistics() {
        // given
        List<LogEntry> logs = createSampleLogs();
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then
        assertThat(result.getResponseSizeInBytes().average()).isEqualTo(1210.0);
        assertThat(result.getResponseSizeInBytes().max()).isEqualTo(2200.0);
        assertThat(result.getResponseSizeInBytes().p95()).isBetween(2000.0, 2300.0);
    }

    @Test
    void calculate_shouldCalculatePercentileCorrectly() {
        // given
        List<LogEntry> logs = createLogsForPercentile();
        LogAnalysisResult result = new LogAnalysisResult();

        // when
        calculator.calculate(logs, result);

        // then - для 5 элементов 95 перцентиль должен быть близок к максимальному
        assertThat(result.getResponseSizeInBytes().p95()).isBetween(450.0, 500.0);
    }
}
