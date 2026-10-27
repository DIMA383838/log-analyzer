package academy.service.validation;

import academy.Application;
import academy.model.OutputFormat;
import academy.service.exception.ParameterValidationException;

/** Валидатор для проверки формата выходного файла. Проверяет что указанный формат поддерживается приложением. */
public class OutputFormatValidator implements Validator {
    @Override
    public void validate(Application app) throws ParameterValidationException {
        String format = app.getFormat();
        if (format == null) {
            throw new ParameterValidationException("Формат вывода не указан");
        }

        try {
            OutputFormat.fromString(format);
        } catch (ParameterValidationException e) {
            throw new ParameterValidationException("Неподдерживаемый формат: " + format);
        }
    }
}
