package academy.service.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class BaseStatisticsCalculatorTest {

    // Тестируем через конкретную реализацию
    private ResponseSizeCalculator calculator = new ResponseSizeCalculator();

    @Test
    void calculatePercentile_shouldHandleEmptyList() {
        // when
        double result = calculator.calculatePercentile(List.of(), 95);

        // then
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void calculatePercentile_shouldCalculateCorrectly() {
        // given
        List<Long> values = List.of(1L, 2L, 3L, 4L, 5L);

        // when & then
        assertThat(calculator.calculatePercentile(values, 0)).isEqualTo(1.0);
        assertThat(calculator.calculatePercentile(values, 50)).isEqualTo(3.0);
        assertThat(calculator.calculatePercentile(values, 100)).isEqualTo(5.0);
    }

    @Test
    void calculatePercentile_shouldHandleSingleElement() {
        // given
        List<Long> values = List.of(42L);

        // when
        double result = calculator.calculatePercentile(values, 95);

        // then
        assertThat(result).isEqualTo(42.0);
    }
}
