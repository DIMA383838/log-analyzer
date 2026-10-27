package academy.service.processor;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс для чтения лог-файлов из различных источников. Определяет контракт для чтения строк логов из локальных или
 * удаленных источников.
 */
public interface LogReader {
    List<String> readLines(String source) throws IOException;
}
