package academy.service.processor;
/**
 * Фабрика для создания соответствующих ридеров в зависимости от источника. Определяет какой тип ридера использовать на
 * основе анализа пути к файлу.
 */
public class LogReaderFactory {
    public static LogReader createReader(String source) {
        if (source.startsWith("http://") || source.startsWith("https://")) {
            return new RemoteFileReader();
        }
        return new LocalFileReader();
    }
}
