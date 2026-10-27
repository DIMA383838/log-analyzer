package academy.service.validation;

import static org.junit.jupiter.api.Assertions.*;

import academy.Application;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine;

public class ArgumentValidationTest {

    @TempDir
    Path tempDir;

    private Application app;
    private CommandLine cmd;

    @BeforeEach
    void setUp() {
        app = new Application();
        cmd = new CommandLine(app);
    }

    @Test
    @DisplayName("На вход передан несуществующий локальный файл")
    void test1() {

        String[] args = {"--path", "/nonexistent/file.log", "--output", "./result.json"};

        int exitCode = cmd.execute(args);
        assertNotEquals(0, exitCode);
    }

    @Test
    @DisplayName("На вход передан несуществующий удаленный файл")
    void test2() {

        String[] args = {"--path", "http://nonexistent-domain.com/logs/file.log", "--output", "./result.json"};

        int exitCode = cmd.execute(args);
        assertNotEquals(0, exitCode);
    }

    @ParameterizedTest
    @MethodSource("test6ArgumentsSource")
    @DisplayName("По пути в аргументе --output указан файл с некоректным расширением")
    void test6(String format, String output) {
        String[] args = {"--path", "./access.log", "--output", output, "--format", format};

        int exitCode = cmd.execute(args);
        assertEquals(2, exitCode);
    }

    @Test
    @DisplayName("По пути в аргументе --output уже существует файл")
    void test7() throws Exception {
        Path existingFile = tempDir.resolve("existing.json");
        Files.createFile(existingFile);

        String[] args = {"--path", "./access.log", "--output", existingFile.toString()};

        int exitCode = cmd.execute(args);
        assertEquals(2, exitCode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"--path", "--output"})
    @DisplayName("На вход не передан обязательный параметр \"{0}\"")
    void test8(String argument) {

        // Пропускаем обязательный параметр
        String[] args = {"--format", "json"};

        int exitCode = cmd.execute(args);
        assertEquals(2, exitCode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"--input", "--filter", "--verbose"})
    @DisplayName("На вход передан неподдерживаемый параметр \"{0}\"")
    void test9(String argument) {

        String[] args = {"--path", "./access.log", "--output", "./result.json", argument, "value"};

        int exitCode = cmd.execute(args);
        assertNotEquals(0, exitCode); // Должен вернуть ошибку
    }

    @Test
    @DisplayName("Значение параметра --from больше, чем значение параметра --to")
    void test10() {
        String[] args = {
            "--path", "./access.log",
            "--output", "./result.json",
            "--from", "2024-01-02",
            "--to", "2024-01-01"
        };

        int exitCode = cmd.execute(args);
        assertEquals(2, exitCode);
    }

    private static Stream<Arguments> test6ArgumentsSource() {
        return Stream.of(
                Arguments.of("markdown", "./results.txt"),
                Arguments.of("json", "./results.md"),
                Arguments.of("adoc", "./results.ad1"));
    }
}
