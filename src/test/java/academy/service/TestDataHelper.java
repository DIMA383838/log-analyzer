package academy.service;

import academy.model.LogAnalysisResult;
import academy.model.LogEntry;
import java.time.LocalDateTime;
import java.util.List;

public class TestDataHelper {

    public static LogEntry createLogEntry(
            String remoteAddr, String resource, int status, long bodyBytes, String protocol, LocalDateTime time) {
        String request = "GET " + resource + " " + protocol;
        return new LogEntry(remoteAddr, "user", time, request, status, bodyBytes, "http://referer.com", "Mozilla");
    }

    public static List<LogEntry> createSampleLogs() {
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        return List.of(
                createLogEntry("192.168.1.1", "/index.html", 200, 1500L, "HTTP/1.1", baseTime),
                createLogEntry("192.168.1.2", "/api/data", 201, 800L, "HTTP/1.1", baseTime.plusHours(1)),
                createLogEntry("192.168.1.3", "/index.html", 200, 1200L, "HTTP/1.0", baseTime.plusDays(1)),
                createLogEntry("192.168.1.4", "/not-found", 404, 350L, "HTTP/1.1", baseTime.plusDays(1)),
                createLogEntry("192.168.1.5", "/api/data", 500, 2200L, "HTTP/2.0", baseTime.plusDays(2)));
    }

    public static List<LogEntry> createLogsForPercentile() {
        LocalDateTime time = LocalDateTime.now();
        return List.of(
                createLogEntry("192.168.1.1", "/test", 200, 100L, "HTTP/1.1", time),
                createLogEntry("192.168.1.2", "/test", 200, 200L, "HTTP/1.1", time),
                createLogEntry("192.168.1.3", "/test", 200, 300L, "HTTP/1.1", time),
                createLogEntry("192.168.1.4", "/test", 200, 400L, "HTTP/1.1", time),
                createLogEntry("192.168.1.5", "/test", 200, 500L, "HTTP/1.1", time));
    }

    public static LogAnalysisResult createSampleResult() {
        LogAnalysisResult result = new LogAnalysisResult();

        // Базовая информация
        result.setFiles(List.of("access.log", "error.log"));
        result.setTotalRequestsCount(150);

        // Размеры ответов
        result.setResponseSizeInBytes(new LogAnalysisResult.ResponseSize(1250.5, 5000.0, 4500.0));

        // Ресурсы
        result.setResources(List.of(
                new LogAnalysisResult.ResourceStat("/index.html", 50),
                new LogAnalysisResult.ResourceStat("/api/data", 30),
                new LogAnalysisResult.ResourceStat("/about", 20),
                new LogAnalysisResult.ResourceStat("/contact", 15)));

        // Коды ответа
        result.setResponseCodes(List.of(
                new LogAnalysisResult.ResponseCodeStat(200, 120),
                new LogAnalysisResult.ResponseCodeStat(404, 20),
                new LogAnalysisResult.ResponseCodeStat(500, 10)));

        // Статистика по датам
        result.setRequestsPerDate(List.of(
                new LogAnalysisResult.RequestsPerDate("2024-05-17", "FRIDAY", 80, 53.33),
                new LogAnalysisResult.RequestsPerDate("2024-05-18", "SATURDAY", 70, 46.67)));

        // Протоколы
        result.setUniqueProtocols(List.of("HTTP/1.1", "HTTP/2.0"));

        return result;
    }

    public static LogAnalysisResult createEmptyResult() {
        LogAnalysisResult result = new LogAnalysisResult();
        result.setFiles(List.of());
        result.setTotalRequestsCount(0);
        result.setResponseSizeInBytes(new LogAnalysisResult.ResponseSize(0, 0, 0));
        result.setResources(List.of());
        result.setResponseCodes(List.of());
        result.setRequestsPerDate(List.of());
        result.setUniqueProtocols(List.of());
        return result;
    }
}
