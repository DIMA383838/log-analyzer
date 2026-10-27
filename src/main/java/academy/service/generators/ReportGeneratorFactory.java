package academy.service.generators;

import academy.model.OutputFormat;
import academy.service.exception.ParameterValidationException;

/**
 * Фабрика для создания генераторов отчетов. Выбирает соответствующую реализацию ReportGenerator на основе указанного
 * формата вывода.
 */
public class ReportGeneratorFactory {
    public static ReportGenerator createGenerator(OutputFormat outputFormat) {
        switch (outputFormat) {
            case JSON:
                return new JsonReportGenerator();
            case MARKDOWN:
                return new MarkdownReportGenerator();
            case ADOC:
                return new ADOCReportGenerator();
            default:
                throw new ParameterValidationException("Неподдерживаемый формат: " + outputFormat);
        }
    }
}
