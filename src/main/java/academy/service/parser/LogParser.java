package academy.service.parser;

import academy.model.LogEntry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Парсер для преобразования строк логов NGINX в объекты LogEntry. Использует регулярные выражения для разбора строк и
 * преобразования дат из формата NGINX в LocalDateTime.
 */
public class LogParser {
    private static final Logger logger = LogManager.getLogger(LogParser.class);

    private static final String LOG_PATTERN =
            "^(\\S+) - (\\S+) \\[(.+?)\\] \"(.+?)\" (\\d{3}) (\\d+) \"(.+?)\" \"(.+?)\"$";
    private static final Pattern PATTERN = Pattern.compile(LOG_PATTERN);

    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("d/MMM/yyyy:HH:mm:ss Z")
            .toFormatter(Locale.ENGLISH);

    public LogEntry parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        Matcher matcher = PATTERN.matcher(line);

        if (!matcher.matches()) {

            logger.warn("Строка лога не соответствует формату: {}", line);
            return null;
        }

        try {
            String remoteAddr = matcher.group(1);
            String remoteUser = matcher.group(2).equals("-") ? "" : matcher.group(2);
            String timeLocalStr = matcher.group(3);
            String request = matcher.group(4);
            int status = Integer.parseInt(matcher.group(5));
            long bodyBytesSent = Long.parseLong(matcher.group(6));
            String httpReferer = matcher.group(7).equals("-") ? "" : matcher.group(7);
            String httpUserAgent = matcher.group(8).equals("-") ? "" : matcher.group(8);

            LocalDateTime timeLocal = parseDateTime(timeLocalStr);

            return new LogEntry(
                    remoteAddr, remoteUser, timeLocal, request, status, bodyBytesSent, httpReferer, httpUserAgent);

        } catch (Exception e) {
            logger.warn("Ошибка парсинга строки лога: {}. Причина: {}", line, e.getMessage());
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);
        } catch (Exception e) {
            logger.warn("Неверный формат даты: {}", dateTimeStr);
            throw e;
        }
    }
}
