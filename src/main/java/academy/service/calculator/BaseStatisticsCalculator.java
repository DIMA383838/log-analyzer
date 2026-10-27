// academy/service/processor/BaseStatisticsCalculator.java
package academy.service.calculator;

import java.util.List;

/**
 * Абстрактный базовый класс для калькуляторов статистики. Содержит общие методы для статистических расчетов, такие как
 * вычисление процентилей с линейной интерполяцией.
 */
public abstract class BaseStatisticsCalculator implements StatisticsCalculator {

    protected double calculatePercentile(List<Long> sortedValues, double percentile) {
        if (sortedValues.isEmpty()) {
            return 0.0;
        }

        double position = percentile / 100.0 * (sortedValues.size() - 1) + 1;

        int lowerIndex = (int) Math.floor(position) - 1;
        int upperIndex = (int) Math.ceil(position) - 1;

        double weight = position - Math.floor(position);

        if (upperIndex >= sortedValues.size()) {
            return sortedValues.get(sortedValues.size() - 1);
        }

        // Линейная интерполяция
        return sortedValues.get(lowerIndex) * (1 - weight) + sortedValues.get(upperIndex) * weight;
    }
}
