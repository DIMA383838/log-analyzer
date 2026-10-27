package academy.service.validation;

import academy.Application;
import academy.service.exception.ParameterValidationException;
import java.util.ArrayList;
import java.util.List;

/** Сервис валидации параметров приложения. Координирует работу всех валидаторов и собирает все ошибки валидации. */
public class ValidationService {
    private final List<Validator> validators;

    public ValidationService() {
        this.validators = List.of(
                new OutputFormatValidator(),
                new InputPathValidator(),
                new OutputPathValidator(),
                new DateRangeValidator());
    }

    public void validateParameters(Application app) {
        List<String> errors = new ArrayList<>();

        for (Validator validator : validators) {
            try {
                validator.validate(app);
            } catch (ParameterValidationException e) {
                errors.add(e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            throw new ParameterValidationException("Ошибки валидации:\n- " + String.join("\n- ", errors));
        }
    }
}
