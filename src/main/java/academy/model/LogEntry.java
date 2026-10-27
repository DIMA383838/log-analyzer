package academy.model;

import java.time.LocalDateTime;

/**
 * Модель представляет одну запись в лог-файле NGINX. Содержит методы для извлечения ресурса и протокола из строки
 * запроса.
 */
public record LogEntry(
        String remoteAddr,
        String remoteUser,
        LocalDateTime timeLocal,
        String request,
        int status,
        long bodyBytesSent,
        String httpReferer,
        String httpUserAgent) {
    // Метод для извлечения ресурса из запроса
    public String getResource() {
        if (request == null) return "";
        String[] parts = request.split(" ");
        if (parts.length >= 2) {
            return parts[1];
        }
        return "";
    }

    // Метод для извлечения протокола из запроса
    public String getProtocol() {
        if (request == null) return "";
        String[] parts = request.split(" ");
        if (parts.length >= 3) {
            return parts[2];
        }
        return "";
    }
}
