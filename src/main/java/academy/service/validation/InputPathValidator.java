package academy.service.validation;

import academy.Application;
import academy.model.InputFormat;
import academy.service.exception.ParameterValidationException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Валидатор для проверки входных путей к файлам логов. Проверяет существование файлов, поддерживаемые расширения и
 * корректность URL.
 */
public class InputPathValidator implements Validator {
    private static final Set<String> SUPPORTED_EXTENSIONS = InputFormat.getAllSupportedExtensions();

    @Override
    public void validate(Application app) throws ParameterValidationException {
        String[] inputPaths = app.getInputPaths();

        if (inputPaths == null || inputPaths.length == 0) {
            throw new ParameterValidationException("Не указаны входные файлы");
        }

        for (String inputPath : inputPaths) {
            validateSinglePath(inputPath);
        }
    }

    private void validateSinglePath(String inputPath) {
        // Для URL пропускаем проверку расширения
        if (isUrl(inputPath)) {
            return;
        }

        Path path = Path.of(inputPath);
        validateFileExists(path);
        validateFileExtension(path);
    }

    private boolean isUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }

    private void validateFileExists(Path path) {
        if (!Files.exists(path)) {
            throw new ParameterValidationException("Файл не существует: " + path);
        }
    }

    private void validateFileExtension(Path path) {
        Path fileNamePath = path.getFileName();
        if (fileNamePath == null) {
            throw new ParameterValidationException("Недопустимый путь к файлу: " + path);
        }

        String fileName = fileNamePath.toString();
        String extension = getFileExtension(fileName).toLowerCase();

        if (!SUPPORTED_EXTENSIONS.contains(extension)) {
            throw new ParameterValidationException(String.format(
                    "Неподдерживаемый формат файла: %s. Поддерживаемые форматы: %s",
                    extension, String.join(", ", SUPPORTED_EXTENSIONS)));
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }
}
