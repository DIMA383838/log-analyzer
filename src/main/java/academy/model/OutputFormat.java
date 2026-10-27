package academy.model;

import academy.service.exception.ParameterValidationException;

/**
 * Перечисление поддерживаемых форматов вывода отчетов. Определяет соответствие между названием формата и расширением
 * файла.
 */
public enum OutputFormat {
    JSON("json", "json"),
    MARKDOWN("markdown", "md"),
    ADOC("adoc", "adoc");

    private final String name;
    private final String fileExtension;

    OutputFormat(String name, String fileExtension) {
        this.name = name;
        this.fileExtension = fileExtension;
    }

    public String getName() {
        return name;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public static OutputFormat fromString(String format) throws ParameterValidationException {
        for (OutputFormat f : values()) {
            if (f.name.equalsIgnoreCase(format)) {
                return f;
            }
        }
        throw new ParameterValidationException("Invalid output format: " + format);
    }
}
