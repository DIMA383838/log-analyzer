package academy.service.validation;

import academy.Application;
import academy.model.OutputFormat;
import academy.service.exception.ParameterValidationException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Валидатор для проверки пути сохранения результата. Проверяет что файл не существует, директория доступна для записи и
 * расширение файла соответствует выбранному формату.
 */
public class OutputPathValidator implements Validator {
    @Override
    public void validate(Application app) throws ParameterValidationException {
        String outputPath = app.getOutputPath();
        String format = app.getFormat();

        if (outputPath == null || outputPath.trim().isEmpty()) {
            throw new ParameterValidationException("Путь для сохранения не указан");
        }

        Path path = Path.of(outputPath);
        validateFileDoesNotExist(path);
        validateParentDirectoryWritable(path);
        validateFileExtension(path, format);
    }

    private void validateFileDoesNotExist(Path path) {
        if (Files.exists(path)) {
            throw new ParameterValidationException("Файл уже существует: " + path);
        }
    }

    private void validateParentDirectoryWritable(Path path) {
        Path parent = path.getParent();
        if (parent != null && !Files.isWritable(parent)) {
            throw new ParameterValidationException("Директория недоступна для записи: " + parent);
        }
    }

    private void validateFileExtension(Path path, String format) {
        if (format == null) return;

        Path fileNamePath = path.getFileName();
        if (fileNamePath == null) {
            throw new ParameterValidationException("Недопустимый путь для сохранения: " + path);
        }

        String expectedExtension = OutputFormat.fromString(format).getFileExtension();
        String actualExtension = getFileExtension(fileNamePath.toString());

        if (!expectedExtension.equalsIgnoreCase(actualExtension)) {
            throw new ParameterValidationException(
                    String.format("Ожидаемое расширение для формата %s: .%s", format, expectedExtension));
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }
}
