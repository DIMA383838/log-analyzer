package academy.service.validation;

import academy.Application;
import academy.service.exception.ParameterValidationException;

/**
 * Интерфейс для всех валидаторов параметров приложения. Определяет контракт для проверки параметров командной строки.
 */
public interface Validator {
    void validate(Application app) throws ParameterValidationException;
}
