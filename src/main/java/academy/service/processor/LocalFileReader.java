package academy.service.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Реализация LogReader для чтения локальных файлов. Читает файлы с локальной файловой системы. */
public class LocalFileReader implements LogReader {
    @Override
    public List<String> readLines(String filePath) throws IOException {
        return Files.readAllLines(Path.of(filePath));
    }
}
