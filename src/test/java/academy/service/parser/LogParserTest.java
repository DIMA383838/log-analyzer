package academy.service.parser;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.LogEntry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LogParserTest {

    private LogParser logParser;

    @BeforeEach
    void setUp() {
        logParser = new LogParser();
    }

    @Test
    void parseLine_shouldParseValidNginxLogLine() {
        // given
        String validLine =
                "192.168.1.1 - john [17/May/2024:10:30:45 +0000] \"GET /index.html HTTP/1.1\" 200 1500 \"http://example.com\" \"Mozilla/5.0\"";

        // when
        LogEntry result = logParser.parseLine(validLine);

        // then
        assertThat(result).isNotNull();
        assertThat(result.remoteAddr()).isEqualTo("192.168.1.1");
        assertThat(result.remoteUser()).isEqualTo("john");
        assertThat(result.timeLocal()).isEqualTo(LocalDateTime.parse("17/May/2024:10:30:45 +0000", dateFormatter));
        assertThat(result.request()).isEqualTo("GET /index.html HTTP/1.1");
        assertThat(result.status()).isEqualTo(200);
        assertThat(result.bodyBytesSent()).isEqualTo(1500);
        assertThat(result.httpReferer()).isEqualTo("http://example.com");
        assertThat(result.httpUserAgent()).isEqualTo("Mozilla/5.0");
    }

    @Test
    void parseLine_shouldParseLineWithEmptyFields() {
        // given
        String lineWithEmptyFields =
                "192.168.1.1 - - [17/May/2024:10:30:45 +0000] \"GET /index.html HTTP/1.1\" 200 1500 \"-\" \"-\"";

        // when
        LogEntry result = logParser.parseLine(lineWithEmptyFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result.remoteAddr()).isEqualTo("192.168.1.1");
        assertThat(result.remoteUser()).isEmpty();
        assertThat(result.httpReferer()).isEmpty();
        assertThat(result.httpUserAgent()).isEmpty();
    }

    @Test
    void parseLine_shouldParseLineWithIPv6Address() {
        // given
        String ipv6Line =
                "2001:db8::1 - user [17/May/2024:10:30:45 +0000] \"POST /api/data HTTP/1.1\" 201 2500 \"http://app.com\" \"Custom Client\"";

        // when
        LogEntry result = logParser.parseLine(ipv6Line);

        // then
        assertThat(result).isNotNull();
        assertThat(result.remoteAddr()).isEqualTo("2001:db8::1");
        assertThat(result.status()).isEqualTo(201);
        assertThat(result.bodyBytesSent()).isEqualTo(2500);
    }

    @Test
    void parseLine_shouldParseLineWithDifferentHttpMethods() {
        // given
        String postLine =
                "192.168.1.1 - admin [17/May/2024:10:30:45 +0000] \"POST /api/users HTTP/1.1\" 201 500 \"http://app.com\" \"API Client\"";
        String putLine =
                "192.168.1.1 - admin [17/May/2024:10:30:45 +0000] \"PUT /api/users/1 HTTP/1.1\" 200 300 \"http://app.com\" \"API Client\"";
        String deleteLine =
                "192.168.1.1 - admin [17/May/2024:10:30:45 +0000] \"DELETE /api/users/1 HTTP/1.1\" 204 0 \"http://app.com\" \"API Client\"";

        // when & then
        assertThat(logParser.parseLine(postLine)).isNotNull();
        assertThat(logParser.parseLine(putLine)).isNotNull();
        assertThat(logParser.parseLine(deleteLine)).isNotNull();
    }

    @Test
    void parseLine_shouldParseLineWithDifferentTimezones() {
        // given
        String utcLine = "192.168.1.1 - user [17/May/2024:10:30:45 +0000] \"GET /test HTTP/1.1\" 200 1000 \"-\" \"-\"";
        String mskLine = "192.168.1.1 - user [17/May/2024:13:30:45 +0300] \"GET /test HTTP/1.1\" 200 1000 \"-\" \"-\"";
        String estLine = "192.168.1.1 - user [17/May/2024:05:30:45 -0500] \"GET /test HTTP/1.1\" 200 1000 \"-\" \"-\"";

        // when & then
        assertThat(logParser.parseLine(utcLine)).isNotNull();
        assertThat(logParser.parseLine(mskLine)).isNotNull();
        assertThat(logParser.parseLine(estLine)).isNotNull();
    }

    @Test
    void parseLine_shouldReturnNullForNullInput() {
        // when
        LogEntry result = logParser.parseLine(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void parseLine_shouldReturnNullForEmptyLine() {
        // when & then
        assertThat(logParser.parseLine("")).isNull();
        assertThat(logParser.parseLine("   ")).isNull();
        assertThat(logParser.parseLine("\t")).isNull();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "invalid log line",
                "192.168.1.1 - user", // неполная строка
                "192.168.1.1 - user [invalid-date] \"GET /\" 200 1000 \"-\" \"-\"", // неверная дата
                "192.168.1.1 - user [17/May/2024:10:30:45 +0000] \"GET /\" invalid 1000 \"-\" \"-\"", // неверный статус
                "192.168.1.1 - user [17/May/2024:10:30:45 +0000] \"GET /\" 200 invalid \"-\" \"-\"", // неверный размер
                "192.168.1.1 user [17/May/2024:10:30:45 +0000] \"GET /\" 200 1000 \"-\" \"-\"", // отсутствует
                // разделитель
            })
    void parseLine_shouldReturnNullForInvalidFormats(String invalidLine) {
        // when
        LogEntry result = logParser.parseLine(invalidLine);

        // then
        assertThat(result).isNull();
    }

    @Test
    void parseLine_shouldHandleMalformedRequestField() {
        // given
        String lineWithMalformedRequest =
                "192.168.1.1 - - [17/May/2024:10:30:45 +0000] \"GET /test\" 200 1000 \"-\" \"-\"";

        // when
        LogEntry result = logParser.parseLine(lineWithMalformedRequest);

        // then - парсер должен принять строку, даже если request неполный
        assertThat(result).isNotNull();
        assertThat(result.request()).isEqualTo("GET /test");
    }

    // Форматтер для проверки дат
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("d/MMM/yyyy:HH:mm:ss Z").withLocale(java.util.Locale.ENGLISH);
}
