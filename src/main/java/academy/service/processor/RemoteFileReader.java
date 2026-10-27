package academy.service.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/** Реализация LogReader для чтения удаленных файлов по HTTP/HTTPS. Читает файлы из интернета по указанному URL. */
public class RemoteFileReader implements LogReader {
    @Override
    public List<String> readLines(String url) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            return reader.lines().collect(Collectors.toList());
        }
    }
}
