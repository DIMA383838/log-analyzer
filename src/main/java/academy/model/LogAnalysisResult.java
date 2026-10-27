package academy.model;

import java.util.List;

/**
 * Класс-результат анализа логов. Содержит всю собранную статистику: общее количество запросов, размер ответов,
 * статистику по ресурсам, кодам ответов, запросам по датам и используемым протоколам.
 */
public class LogAnalysisResult {
    private List<String> files;
    private int totalRequestsCount;
    private ResponseSize responseSizeInBytes;
    private List<ResourceStat> resources;
    private List<ResponseCodeStat> responseCodes;
    private List<RequestsPerDate> requestsPerDate;
    private List<String> uniqueProtocols;

    public record ResponseSize(double average, double max, double p95) {}

    public record ResourceStat(String resource, long totalRequestsCount) {}

    public record ResponseCodeStat(int code, long totalResponsesCount) {}

    public record RequestsPerDate(
            String date, String weekday, long totalRequestsCount, double totalRequestsPercentage) {}

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public int getTotalRequestsCount() {
        return totalRequestsCount;
    }

    public void setTotalRequestsCount(int totalRequestsCount) {
        this.totalRequestsCount = totalRequestsCount;
    }

    public ResponseSize getResponseSizeInBytes() {
        return responseSizeInBytes;
    }

    public void setResponseSizeInBytes(ResponseSize responseSizeInBytes) {
        this.responseSizeInBytes = responseSizeInBytes;
    }

    public List<ResourceStat> getResources() {
        return resources;
    }

    public void setResources(List<ResourceStat> resources) {
        this.resources = resources;
    }

    public List<ResponseCodeStat> getResponseCodes() {
        return responseCodes;
    }

    public void setResponseCodes(List<ResponseCodeStat> responseCodes) {
        this.responseCodes = responseCodes;
    }

    public List<RequestsPerDate> getRequestsPerDate() {
        return requestsPerDate;
    }

    public void setRequestsPerDate(List<RequestsPerDate> requestsPerDate) {
        this.requestsPerDate = requestsPerDate;
    }

    public List<String> getUniqueProtocols() {
        return uniqueProtocols;
    }

    public void setUniqueProtocols(List<String> uniqueProtocols) {
        this.uniqueProtocols = uniqueProtocols;
    }
}
