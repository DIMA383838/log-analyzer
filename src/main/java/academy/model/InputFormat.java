package academy.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** Перечисление поддерживаемых форматов входных файлов. Определяет допустимые расширения файлов для обработки логов. */
public enum InputFormat {
    TXT("txt", "txt"),
    LOG("log", "log");

    private final String name;
    private final String fileExtension;

    InputFormat(String name, String fileExtension) {
        this.name = name;
        this.fileExtension = fileExtension;
    }

    public String getName() {
        return name;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public static Set<String> getAllSupportedExtensions() {
        return Arrays.stream(values()).map(InputFormat::getFileExtension).collect(Collectors.toSet());
    }
}
