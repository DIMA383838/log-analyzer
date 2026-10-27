package academy.service.generators;

import academy.model.LogAnalysisResult;
import academy.model.LogAnalysisResult.RequestsPerDate;
import academy.model.LogAnalysisResult.ResourceStat;
import academy.model.LogAnalysisResult.ResponseCodeStat;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Абстрактный базовый класс для генераторов отчетов. Содержит общую логику форматирования данных и методы-помощники для
 * работы со статистикой логов.
 */
public abstract class AbstractReportGenerator implements ReportGenerator {

    protected String formatFiles(List<String> files) {
        return String.join(
                ", ",
                files.stream()
                        .map(file -> {
                            if (file.startsWith("http") || file.startsWith("https")) {
                                return "Веб-источник";
                            } else {
                                return Path.of(file).getFileName().toString();
                            }
                        })
                        .collect(Collectors.toList()));
    }

    protected String formatProtocols(List<String> protocols) {
        return String.join(", ", protocols);
    }

    protected String truncateResource(String resource, int maxLength) {
        if (resource == null) return "";
        if (resource.length() > maxLength) {
            return resource.substring(0, maxLength - 3) + "...";
        }
        return resource;
    }

    protected String getStatusDescription(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 201:
                return "Created";
            case 204:
                return "No Content";
            case 301:
                return "Moved Permanently";
            case 302:
                return "Found";
            case 304:
                return "Not Modified";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 405:
                return "Method Not Allowed";
            case 500:
                return "Internal Server Error";
            case 502:
                return "Bad Gateway";
            case 503:
                return "Service Unavailable";
            default:
                return "Unknown";
        }
    }

    protected List<ResourceStat> getTopResources(LogAnalysisResult result, int limit) {
        return result.getResources().stream()
                .sorted(Comparator.comparingLong(ResourceStat::totalRequestsCount)
                        .reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    protected List<ResponseCodeStat> getSortedResponseCodes(LogAnalysisResult result) {
        return result.getResponseCodes().stream()
                .sorted(Comparator.comparingInt(ResponseCodeStat::code))
                .collect(Collectors.toList());
    }

    protected List<RequestsPerDate> getSortedRequestsPerDate(LogAnalysisResult result) {
        return result.getRequestsPerDate().stream()
                .sorted(Comparator.comparing(RequestsPerDate::date))
                .collect(Collectors.toList());
    }

    protected RequestsPerDate getMaxRequestsDay(LogAnalysisResult result) {
        return result.getRequestsPerDate().stream()
                .max(Comparator.comparingLong(RequestsPerDate::totalRequestsCount))
                .orElse(null);
    }

    // Абстрактные методы для специфичной логики форматов
    protected abstract String escapeContent(String text);

    protected abstract String formatHeader(String title);

    protected abstract String formatTableRow(List<String> cells);

    protected abstract String formatSection(String title);
}
