// academy/service/processor/LogAnalysisOrchestratorTest.java
package academy.service.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import academy.service.calculator.StatisticsCalculator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogAnalysisOrchestratorTest {

    @Mock
    private StatisticsCalculator calculator1;

    @Mock
    private StatisticsCalculator calculator2;

    @Test
    void calculateStatistics_shouldCallAllCalculators() {
        // given
        List<StatisticsCalculator> calculators = List.of(calculator1, calculator2);
        LogAnalysisOrchestrator orchestrator = new LogAnalysisOrchestrator(calculators);

        List<LogEntry> logs = List.of();
        List<String> files = List.of("test.log");

        // when
        LogAnalysisResult result = orchestrator.calculateStatistics(logs, files);

        // then
        verify(calculator1).calculate(logs, result);
        verify(calculator2).calculate(logs, result);

        assertThat(result.getFiles()).isEqualTo(files);
        assertThat(result.getTotalRequestsCount()).isEqualTo(0);
    }
}
