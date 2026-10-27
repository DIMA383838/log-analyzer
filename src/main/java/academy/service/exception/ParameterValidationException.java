package academy.service.exception;
/**
 * Специализированное исключение для ошибок валидации параметров. Используется для обозначения ошибок, связанных с
 * некорректными входными параметрами командной строки или конфигурации.
 */
public class ParameterValidationException extends RuntimeException {
    public ParameterValidationException(String message) {
        super(message);
    }
}
